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
package org.qi4j.library.framework.rdf.parse.model;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.qi4j.library.framework.rdf.Qi4jRdf;
import org.qi4j.library.framework.rdf.parse.ParseContext;
import org.qi4j.spi.composite.ParameterModel;

public class ParameterParser
{
    private final ParseContext context;

    public ParameterParser( ParseContext context )
    {
        this.context = context;
    }

    public Value parseModel( ParameterModel model )
    {
        BNode node = createParameter( model );
        return node;
    }

    private BNode createParameter( ParameterModel model )
    {
        BNode node = context.getValueFactory().createBNode( model.getClass().getName() );
        context.addType( node, Qi4jRdf.TYPE_PARAMETER );
        return node;
    }
}