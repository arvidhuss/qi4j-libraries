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

package org.qi4j.logging.log.assemblies;

import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.logging.trace.service.TraceServiceConfiguration;
import org.qi4j.logging.trace.service.StandardTraceServiceComposite;
import org.qi4j.logging.log.LogTypes;
import org.qi4j.logging.log.service.LoggingServiceComposite;

public class LoggingAssembler
    implements Assembler
{
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.addServices( LoggingServiceComposite.class );
        module.addComposites( LogTypes.class );
        module.on( LogTypes.class ).to().info().set( "INFO" );
        module.on( LogTypes.class ).to().warning().set( "WARNING" );
        module.on( LogTypes.class ).to().error().set( "ERROR" );
    }

}