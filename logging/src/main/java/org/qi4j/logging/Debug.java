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
package org.qi4j.logging;

import org.qi4j.composite.Concerns;
import org.qi4j.composite.Mixins;
import org.qi4j.library.framework.NoopMixin;
import org.qi4j.property.Property;

@Concerns( DebugConcern.class )
@Mixins( NoopMixin.class )
public interface Debug
{
    int LOWLOW = -100;
    int LOW = -50;
    int NORMAL = 0;
    int HIGH = 50;
    int HIGHHIGH = 100;

    Property<Integer> debugLevel();

    void debug( int level, String message );

    void debug( int level, String message, Object param1 );

    void debug( int level, String message, Object param1, Object param2 );

    void debug( int level, String message, Object... params );

}