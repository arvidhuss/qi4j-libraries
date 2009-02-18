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
package org.qi4j.library.swing.envisage.tree;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import org.qi4j.library.swing.envisage.model.descriptor.ApplicationDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.LayerDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ModuleDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.EntityDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ServiceDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ObjectDetailDescriptor;

/**
 * Helper class to build tree model as structure
 *
 * @author Tonny Kohar (tonny.kohar@gmail.com)
 */
public class StructureModelBuilder
{
    public static MutableTreeNode build( ApplicationDetailDescriptor descriptor )
    {
        StructureModelBuilder builder = new StructureModelBuilder();
        return builder.buildApplicationNode( descriptor );       
    }

    private MutableTreeNode buildApplicationNode( ApplicationDetailDescriptor descriptor )
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( descriptor );
        buildLayersNode( node, descriptor.layers() );
        return node;
    }


    private void buildLayersNode( DefaultMutableTreeNode parent, Iterable<LayerDetailDescriptor> iter )
    {
        for( LayerDetailDescriptor descriptor : iter )
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode( descriptor );
            buildModulesNode( node, descriptor.modules() );
            parent.add( node );
        }
    }

    private void buildModulesNode( DefaultMutableTreeNode parent, Iterable<ModuleDetailDescriptor> iter )
    {
        for( ModuleDetailDescriptor descriptor : iter )
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode( descriptor );
            buildServicesNode( node, descriptor.services() );
            buildEntitiesNode( node, descriptor.entities() );
            buildObjectsNode( node, descriptor.objects() );
            parent.add( node );
        }
    }

    private void buildServicesNode( DefaultMutableTreeNode parent, Iterable<ServiceDetailDescriptor> iter )
    {
        for( ServiceDetailDescriptor descriptor : iter )
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode( descriptor );
            parent.add( node );
        }
    }

    private void buildEntitiesNode( DefaultMutableTreeNode parent, Iterable<EntityDetailDescriptor> iter )
    {
        for( EntityDetailDescriptor descriptor : iter )
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode( descriptor );
            parent.add( node );
        }
    }

    private void buildObjectsNode( DefaultMutableTreeNode parent, Iterable<ObjectDetailDescriptor> iter )
    {
        for( ObjectDetailDescriptor descriptor : iter )
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode( descriptor );
            parent.add( node );
        }
    }
}