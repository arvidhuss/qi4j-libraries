/*
 * Copyright 2008 Niclas Hedhman. All rights Reserved.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.qi4j.library.uid.sequence;

import org.qi4j.entity.ConcurrentEntityModificationException;
import org.qi4j.entity.UnitOfWork;
import org.qi4j.entity.UnitOfWorkCompletionException;
import org.qi4j.entity.UnitOfWorkFactory;
import org.qi4j.injection.scope.Structure;
import org.qi4j.injection.scope.This;
import org.qi4j.property.Property;
import org.qi4j.service.Configuration;

public class PersistedSequencingMixin
    implements Sequencing
{
    @Structure private UnitOfWorkFactory uowf;
    @This private Configuration<Sequence> sequence;

    public Long newSequenceValue()
        throws SequencingException
    {
        synchronized( this )
        {
            ConcurrentEntityModificationException exc = null;
            UnitOfWork uow = uowf.newUnitOfWork();
            try
            {
                for( int i=0 ; i < 3 ; i++ )
                {
                    try
                    {
                        Property<Long> property = sequence.configuration().currentValue();
                        long value = property.get();
                        value = value + 1;
                        property.set( value );
                        uow.complete();
                        return value;
                    }
                    catch( ConcurrentEntityModificationException e )
                    {
                        // Ignore;
                        exc = e;
                    }
                }
                throw new SequencingException( "Unable to update sequence value.", exc );
            }
            catch( UnitOfWorkCompletionException e )
            {
                throw new SequencingException( "Unable to update sequence value.", exc );
            }
        }
    }

    public Long currentSequenceValue()
    {
        synchronized( this )
        {
            return sequence.configuration().currentValue().get();
        }
    }
}