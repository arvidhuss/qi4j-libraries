/*
 * Copyright 2005 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.library.alarm.impl.test;

import java.util.Date;
import java.util.Locale;
import org.qi4j.library.alarm.AlarmState;

public class NormalTestState
    implements AlarmState
{

    private Date creation;

    public NormalTestState()
    {
        creation = new Date();
    }

    public Date getCreationDate()
    {
        return creation;
    }

    public String getName()
    {
        return "Normal Test State";
    }

    public String getName( Locale locale )
    {
        return "Normal Test State";
    }

    public String getDescription()
    {
        return "Normal Test State";
    }

    public String getDescription( Locale locale )
    {
        return "Normal Test State";
    }
}
