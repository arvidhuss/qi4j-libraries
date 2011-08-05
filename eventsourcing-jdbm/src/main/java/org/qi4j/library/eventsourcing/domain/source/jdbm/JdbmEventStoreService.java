/**
 *
 * Copyright 2009-2010 Streamsource AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qi4j.library.eventsourcing.domain.source.jdbm;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.RecordManagerOptions;
import jdbm.btree.BTree;
import jdbm.helper.*;
import jdbm.recman.CacheRecordManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.io.*;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.Activatable;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.util.Function;
import org.qi4j.library.eventsourcing.domain.api.UnitOfWorkDomainEventsValue;
import org.qi4j.library.eventsourcing.domain.source.*;
import org.qi4j.library.fileconfig.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * JAVADOC
 */
@Mixins(JdbmEventStoreService.JdbmEventStoreMixin.class)
public interface JdbmEventStoreService
        extends EventSource, EventStore, EventStream, EventManagement, Activatable, ServiceComposite
{
    class JdbmEventStoreMixin
            extends AbstractEventStoreMixin
            implements EventManagement, EventSource
    {
        @Service
        FileConfiguration fileConfig;

        private RecordManager recordManager;
        private BTree index;
        private Serializer serializer;
        private File dataFile;

        private long currentCount;

        public void activate() throws IOException
        {
            super.activate();

            dataFile = new File( fileConfig.dataDirectory(), identity.identity() + "/events" );
            File directory = dataFile.getAbsoluteFile().getParentFile();
            directory.mkdirs();
            String name = dataFile.getAbsolutePath();
            Properties properties = new Properties();
            properties.put( RecordManagerOptions.AUTO_COMMIT, "false" );
            properties.put( RecordManagerOptions.DISABLE_TRANSACTIONS, "false" );
            initialize( name, properties );
        }

        public void passivate() throws Exception
        {
            super.passivate();
            recordManager.close();
        }

        public Output<String, IOException> restore()
        {
            // Commit every 1000 events, convert from string to value, and then store. Put a lock around the whole thing
            Output<String, IOException> map = Transforms.map( new Transforms.ProgressLog<String>( 1000 )
            {
                @Override
                protected void logProgress()
                {
                    try
                    {
                        recordManager.commit(); // Commit every 1000 transactions to avoid OutOfMemory issues
                    } catch( IOException e )
                    {
                        throw new IllegalStateException( "Could not commit data", e );
                    }
                }
            }, Transforms.map( new Function<String, UnitOfWorkDomainEventsValue>()
            {
                @Override
                public UnitOfWorkDomainEventsValue map( String item )
                {
                    try
                    {
                        JSONObject json = (JSONObject) new JSONTokener( item ).nextValue();
                        return (UnitOfWorkDomainEventsValue) eventsType.fromJSON( json, module );
                    } catch( JSONException e )
                    {
                        throw new IllegalArgumentException( e );
                    }
                }
            }, storeEvents0() ) );

            return Transforms.lock( JdbmEventStoreMixin.this.lock,
                    map );
        }

        // EventStore implementation
        public Input<UnitOfWorkDomainEventsValue, IOException> events( final long offset, long limit )
        {
            return new Input<UnitOfWorkDomainEventsValue, IOException>()
            {
               @Override
               public <ReceiverThrowableType extends Throwable> void transferTo(Output<? super UnitOfWorkDomainEventsValue, ReceiverThrowableType> output) throws IOException, ReceiverThrowableType
               {
                    output.receiveFrom( new Sender<UnitOfWorkDomainEventsValue, IOException>()
                    {
                       @Override
                       public <ReceiverThrowableType extends Throwable> void sendTo(Receiver<? super UnitOfWorkDomainEventsValue, ReceiverThrowableType> receiver) throws ReceiverThrowableType, IOException
                       {
                            // Lock datastore first
                            lock();

                            try
                            {
                                final TupleBrowser browser = index.browse( offset+1 );

                                Tuple tuple = new Tuple();

                                while (browser.getNext( tuple ))
                                {
                                    // Get next transaction
                                    UnitOfWorkDomainEventsValue domainEvents = readTransactionEvents( tuple );

                                    receiver.receive( domainEvents );
                                }
                            } catch (Exception e)
                            {
                                logger.warn( "Could not iterate events", e );
                            } finally
                            {
                                lock.unlock();
                            }

                        }
                    } );
                }
            };
        }

        public long count()
        {
            return currentCount;
        }

        @Override
        protected Output<UnitOfWorkDomainEventsValue, IOException> storeEvents0()
        {
            return new Output<UnitOfWorkDomainEventsValue, IOException>()
            {
                @Override
                public <SenderThrowableType extends Throwable> void receiveFrom( Sender<? extends UnitOfWorkDomainEventsValue, SenderThrowableType> sender ) throws IOException, SenderThrowableType
                {
                    try
                    {
                        sender.sendTo( new Receiver<UnitOfWorkDomainEventsValue, IOException>()
                        {
                            @Override
                            public void receive( UnitOfWorkDomainEventsValue item ) throws IOException
                            {
                                String jsonString = item.toJSON();
                                currentCount++;
                                index.insert( currentCount, jsonString.getBytes( "UTF-8" ), false );
                            }
                        });
                        recordManager.commit();
                    } catch( IOException e )
                    {
                        recordManager.rollback();
                        throw e;
                    } catch( Throwable e )
                    {
                        recordManager.rollback();
                        throw (SenderThrowableType) e;
                    }
                }
            };
        }

        private void initialize( String name, Properties properties )
                throws IOException
        {
            recordManager = RecordManagerFactory.createRecordManager( name, properties );
            serializer = new ByteArraySerializer();
            recordManager = new CacheRecordManager( recordManager, new MRU( 1000 ) );
            long recid = recordManager.getNamedObject( "index" );
            if (recid != 0)
            {
                index = BTree.load( recordManager, recid );

                currentCount = index.size();
            } else
            {
                LongComparator comparator = new LongComparator();
                index = BTree.createInstance( recordManager, comparator, new LongSerializer(), serializer, 16 );
                recordManager.setNamedObject( "index", index.getRecid() );
                currentCount = 0;
            }
            recordManager.commit();
        }

        private UnitOfWorkDomainEventsValue readTransactionEvents( Tuple tuple )
                throws UnsupportedEncodingException, JSONException
        {
            byte[] eventData = (byte[]) tuple.getValue();
            String eventJson = new String( eventData, "UTF-8" );
            JSONTokener tokener = new JSONTokener( eventJson );
            JSONObject transaction = (JSONObject) tokener.nextValue();
            return (UnitOfWorkDomainEventsValue) eventsType.fromJSON( transaction, module );
        }
    }
}
