/*
 * Copyright 2006 Niclas Hedhman.
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
package org.qi4j.log;

import org.qi4j.composite.Concerns;
import org.qi4j.log.service.SimpleLogConcern;

@Concerns( { SimpleLogConcern.class } )
public interface SimpleLog
{
    void info( String message );

    void info( String message, Object param1 );

    void info( String message, Object param1, Object param2 );

    void info( String message, Object... params );

    void warning( String message );

    void warning( String message, Object param1 );

    void warning( String message, Object param1, Object param2 );

    void warning( String message, Object... params );

    void error( String message );

    void error( String message, Object param1 );

    void error( String message, Object param1, Object param2 );

    void error( String message, Object... params );


}