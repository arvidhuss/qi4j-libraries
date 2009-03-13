/*  Copyright 2009 Tonny Kohar.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied.
*
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.qi4j.library.swing.envisage.graph;

import org.qi4j.library.swing.envisage.model.descriptor.ApplicationDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.CompositeDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.EntityDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ImportedServiceDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.LayerDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ModuleDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ObjectDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ServiceDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ValueDetailDescriptor;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;

/**
 * Build Qi4J application model as Prefuse Tree Graph
 *
 * @author Tonny Kohar (tonny.kohar@gmail.com)
 */
public class BoxedGraphBuilder
{
    private Graph graph = null;

    public static Graph buildGraph( ApplicationDetailDescriptor descriptor )
    {
        BoxedGraphBuilder builder = new BoxedGraphBuilder();
        return builder.buildApplicationNode( descriptor );
    }

    private BoxedGraphBuilder()
    {
        graph = new Graph( true );

        Table nodeTable = graph.getNodeTable();
        nodeTable.addColumn( GraphDisplay.NAME_LABEL, String.class );
        nodeTable.addColumn( GraphDisplay.USER_OBJECT, Object.class );

        Table edgeTable = graph.getEdgeTable();
        edgeTable.addColumn( GraphDisplay.USES_EDGES, boolean.class, false );
    }

    private Graph buildApplicationNode( ApplicationDetailDescriptor descriptor )
    {
        Node node = addChild( null, descriptor.descriptor().name(), descriptor );

        buildLayersNode( node, descriptor.layers() );
        //buildUsesNode( node, descriptor.layers() );

        return graph;
    }

    private void buildUsesNode( Node parent, Iterable<LayerDetailDescriptor> iter )
    {
        for( LayerDetailDescriptor descriptor : iter )
        {
            Node source = findNode( parent, descriptor );

            for( LayerDetailDescriptor usesDescriptor : descriptor.usedLayers() )
            {
                Node target = findNode( parent, usesDescriptor );
                if( target == null )
                {
                    continue;
                }
                Edge edge = graph.addEdge( source, target );
                edge.setBoolean( GraphDisplay.USES_EDGES, true );
            }
        }
    }

    private Node findNode( Node parent, Object userObject )
    {
        Node node = null;

        for( int i = 0; i < parent.getChildCount(); i++ )
        {
            Node tNode = parent.getChild( i );
            Object obj = tNode.get( GraphDisplay.USER_OBJECT );
            if( obj.equals( userObject ) )
            {
                node = tNode;
                break;
            }
        }

        return node;
    }

    private Node addChild( Node parent, String name, Object object )
    {
        Node childNode = graph.addNode();
        childNode.set( GraphDisplay.NAME_LABEL, name );
        childNode.set( GraphDisplay.USER_OBJECT, object );

        // check for application node
        if( parent != null )
        {
            graph.addEdge( parent, childNode );
        }
        return childNode;
    }

    private void buildLayersNode( Node parent, Iterable<LayerDetailDescriptor> iter )
    {
        for( LayerDetailDescriptor descriptor : iter )
        {
            Node childNode = addChild( parent, descriptor.descriptor().name(), descriptor );
            buildModulesNode( childNode, descriptor.modules() );
        }
    }

    private void buildModulesNode( Node parent, Iterable<ModuleDetailDescriptor> iter )
    {
        for( ModuleDetailDescriptor descriptor : iter )
        {
            Node childNode = addChild( parent, descriptor.descriptor().name(), descriptor );

            buildServicesNode( childNode, descriptor.services() );
            buildImportedServicesNode( childNode, descriptor.importedServices() );
            buildEntitiesNode( childNode, descriptor.entities() );
            buildTransientsNode( childNode, descriptor.composites() );
            buildValuesNode( childNode, descriptor.values() );
            buildObjectsNode( childNode, descriptor.objects() );
        }
    }

    private void buildServicesNode( Node parent, Iterable<ServiceDetailDescriptor> iter )
    {
        boolean first = true;
        for( ServiceDetailDescriptor descriptor : iter )
        {
            if (first)
            {
                String name = "Services";
                parent = addChild( parent, name, name );
                first = false;
            }
            addChild( parent, descriptor.descriptor().type().getSimpleName(), descriptor );
        }
    }

    private void buildImportedServicesNode( Node parent, Iterable<ImportedServiceDetailDescriptor> iter )
    {
        boolean first = true;
        for( ImportedServiceDetailDescriptor descriptor : iter )
        {
            if (first)
            {
                String name = "Imported Services";
                parent = addChild( parent, name, name );
                first = false;
            }
            addChild( parent, descriptor.descriptor().type().getSimpleName(), descriptor );
        }
    }

    private void buildEntitiesNode( Node parent, Iterable<EntityDetailDescriptor> iter )
    {
        boolean first = true;
        for( EntityDetailDescriptor descriptor : iter )
        {
            if (first)
            {
                String name = "Entities";
                parent = addChild( parent, name, name );
                first = false;
            }
            addChild( parent, descriptor.descriptor().type().getSimpleName(), descriptor );
        }
    }

    private void buildTransientsNode( Node parent, Iterable<CompositeDetailDescriptor> iter )
    {
        boolean first = true;
        for( CompositeDetailDescriptor descriptor : iter )
        {
            if (first)
            {
                String name = "Transients";
                parent = addChild( parent, name, name );
                first = false;
            }
            addChild( parent, descriptor.descriptor().type().getSimpleName(), descriptor );
        }
    }

    private void buildValuesNode( Node parent, Iterable<ValueDetailDescriptor> iter )
    {
        boolean first = true;
        for( ValueDetailDescriptor descriptor : iter )
        {
            if (first)
            {
                String name = "Values";
                parent = addChild( parent, name, name );
                first = false;
            }
            addChild( parent, descriptor.descriptor().type().getSimpleName(), descriptor );
        }
    }

    private void buildObjectsNode( Node parent, Iterable<ObjectDetailDescriptor> iter )
    {
        boolean first = true;
        for( ObjectDetailDescriptor descriptor : iter )
        {
            if (first)
            {
                String name = "Objects";
                parent = addChild( parent, name, name );
                first = false;
            }
            addChild( parent, descriptor.descriptor().type().getSimpleName(), descriptor );
        }
    }
}