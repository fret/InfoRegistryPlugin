// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2014 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.pathvisio.inforegistry.impl;

import java.util.Iterator;

import org.bridgedb.Xref;
import org.pathvisio.core.ApplicationEvent;
import org.pathvisio.core.view.VPathway;
import org.pathvisio.core.Engine.ApplicationEventListener;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElementEvent;
import org.pathvisio.core.model.PathwayElementListener;
import org.pathvisio.core.model.StaticProperty;
import org.pathvisio.core.model.StaticPropertyType;
import org.pathvisio.core.view.Graphics;
import org.pathvisio.core.view.VPathwayElement;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.DataNodeType;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.event.*;

import javax.swing.*;

import org.pathvisio.core.view.SelectionBox.SelectionEvent;
import org.pathvisio.core.view.SelectionBox.SelectionListener;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.inforegistry.IInfoProvider;
import org.pathvisio.inforegistry.InfoRegistry;

/**
 * 
 * currently implemented as a plugin (but not shown in plugin manager)
 * creates a side tab and implements the selection listener to update the
 * drop down box with information providers for the selected data node type.
 * 
 * @author mkutmon
 *
 */
public class InfoRegistryPlugin extends JPanel implements Plugin, SelectionListener,PathwayElementListener,
ApplicationEventListener {

	private InfoRegistry registry;
	private PvDesktop desktop;
	private JPanel sidePanel;
	private JComboBox<String> pluginList;
	private JComponent c = null;
	private MutableComboBoxModel<String> model;

	
	@Override
	public void init(PvDesktop desktop) {
		
		registry = InfoRegistry.getInfoRegistry();
		this.desktop = desktop;
		
		addSidePanel();
		
		desktop.getSwingEngine().getEngine().addApplicationEventListener(this);
		VPathway vp = desktop.getSwingEngine().getEngine().getActiveVPathway();
		if(vp != null) vp.addSelectionListener(this);

	}

	
	public InfoRegistryPlugin(){

		super(new BorderLayout());

	}
	

    public JPanel getSidePanel() {
    	return sidePanel;
    }
	
	private void addSidePanel() {
		
		sidePanel = new JPanel ();
		sidePanel.setLayout (new BorderLayout());

        pluginList = new JComboBox<String>();
        //petList.setSelectedIndex(4);
        pluginList.addItemListener(new ItemListener() {
        	
        	@Override
            public void itemStateChanged(ItemEvent e){
            	
            	{	
                	if (e.getStateChange() == ItemEvent.SELECTED){
                		int flag = 0;
                        Iterator<IInfoProvider> ip = registry.registeredPlugins.iterator();
                        while(ip.hasNext()) {
                        	IInfoProvider ipo = ip.next();
                        	
                        	if( ipo.getName().equals(e.getItem().toString())){
                        		
                        		getInformationWorker giw = new getInformationWorker(ipo, sidePanel);
                        		flag = 1;
                        		giw.doInBackground();
                        		
                        	}
                        	
                        }
                        if(flag==0){
                    		if(c != null)
                        		sidePanel.remove(c);
                        		sidePanel.revalidate();
                        		sidePanel.repaint();
                        }
                	}
                    }
            }
        });
        
        JTabbedPane sidebarTabbedPane = desktop.getSideBarTabbedPane();
        sidebarTabbedPane.add("Info", sidePanel);
	}

	@Override
	public void done() {		
		desktop.getSideBarTabbedPane().remove(sidePanel);
		// TODO Auto-generated method stub
	}

	public void selectionEvent(SelectionEvent e) {
		// TODO Auto-generated method stub
		
        switch(e.type) {
        case SelectionEvent.OBJECT_ADDED:
                Iterator<VPathwayElement> it = e.selection.iterator();
                while(it.hasNext()) {
                        VPathwayElement o = it.next();                         
                        if(o instanceof Graphics) {
                                PathwayElement pe = ((Graphics)o).getPathwayElement();
                                if(pe.getObjectType()==ObjectType.DATANODE)    
                                {                   
                                	model = (MutableComboBoxModel<String>)pluginList.getModel();
                                	emptyJComboBox(pluginList);
                                    Iterator<IInfoProvider> ip = registry.registeredPlugins.iterator();
                                    while(ip.hasNext()) {
                                    	IInfoProvider ipo = ip.next();
                                		switch(pe.getDataNodeType()){
                                		case"Protein": if(ipo.getDatanodeTypes().contains(DataNodeType.PROTEIN)){
                                					   model = addToComboBoxModel(model, ipo.getName()); 
                                    	}
                                		break;
                                		case"Rna": if(ipo.getDatanodeTypes().contains(DataNodeType.RNA)){
                     					   model = addToComboBoxModel(model, ipo.getName());
                                		}
                                		break;
                                		case"GeneProduct": if(ipo.getDatanodeTypes().contains(DataNodeType.GENEPRODUCT)){
                      					   model = addToComboBoxModel(model, ipo.getName());
                                 		}
                                		break;
                                		case"Metabolite": if(ipo.getDatanodeTypes().contains(DataNodeType.METABOLITE )){
                       					   model = addToComboBoxModel(model, ipo.getName());
                                  		}
                                		break;
                                		case"Pathway": if(ipo.getDatanodeTypes().contains(DataNodeType.PATHWAY  )){
                        					   model = addToComboBoxModel(model, ipo.getName());
                                   		}
                                		break;
                                		case"Unknown": if(ipo.getDatanodeTypes().contains(DataNodeType.UNKOWN  )){
                        					   model = addToComboBoxModel(model, ipo.getName());
                                   		}
                                		break;
                                		}
                                		}
                                    	
                                	pluginList.setModel(model);
                                	sidePanel.add(pluginList,BorderLayout.NORTH);
                                	sidePanel.revalidate();
                                	sidePanel.repaint();

                                }      
                        }
                }
                break;
        case SelectionEvent.OBJECT_REMOVED:
        		{
                	sidePanel.removeAll();
                	sidePanel.revalidate();
                	sidePanel.repaint();
                }
                	break;
        case SelectionEvent.SELECTION_CLEARED:
            sidePanel.removeAll();
           	sidePanel.revalidate();
           	sidePanel.repaint();               
                break;
        }
       
}
	
	public void gmmlObjectModified(PathwayElementEvent e) {

	}
	
	public void applicationEvent(ApplicationEvent e)
	{
		switch(e.getType())
		{
		case VPATHWAY_CREATED:
			((VPathway)e.getSource()).addSelectionListener(this);
			break;
		case VPATHWAY_DISPOSED:
			((VPathway)e.getSource()).removeSelectionListener(this);

			break;
		}
	}
	
	private MutableComboBoxModel<String> addToComboBoxModel(MutableComboBoxModel<String> model, String str ){
		int index,flag = 0;
		for(index=0; index < model.getSize(); index++){
			
			if(model.getElementAt(index).equals(str)){
				flag = 1;
				break;
			}
		}
		if(flag == 1)
			return model;
		else{
			model.addElement(str);
			return model;
	}
}
	private void emptyJComboBox(JComboBox<String> jcb){
		pluginList.removeAllItems();
	}

}