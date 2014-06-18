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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
 * @author rohansaxena
 *
 */
public class InfoRegistryPlugin extends JPanel implements Plugin, SelectionListener,PathwayElementListener,
ApplicationEventListener {

	private InfoRegistry registry;
	private PvDesktop desktop;
	private JPanel sidePanel;
	private JComboBox<String> pluginList;
	private JComponent c = null;
	private JButton goButton;
	private MutableComboBoxModel<String> model;
	private Xref xref;
//	private int warning_flag;
//	private String warning_message;
//	private JLabel centerLabel;
	private JPanel northPanel;
	private JPanel centerPanel;
	private JButton okButton;
	private JButton cancelButton;
	private JLabel errorMessage;
	private getInformationWorker giw;

	
	@Override
	public void init(PvDesktop desktop) {
		
		registry = InfoRegistry.getInfoRegistry();
		this.desktop = desktop;
		
		desktop.getSwingEngine().getEngine().addApplicationEventListener(this);
		VPathway vp = desktop.getSwingEngine().getEngine().getActiveVPathway();
		if(vp != null) vp.addSelectionListener(this);
		
		addSidePanel();
	}

	
	public InfoRegistryPlugin(){

		super(new BorderLayout());

	}
	

  /*  public JPanel getSidePanel() {
    	return sidePanel;
    }
    */
	
	/**
	 * In this method a new tabbed pane called info is added. 
	 */
	private void addSidePanel() {
		
		//acts like a container
		sidePanel = new JPanel ();
		sidePanel.setLayout(new BorderLayout());
		
		northPanel = new JPanel();
		northPanel.setLayout(new GridBagLayout());
		centerPanel = new JPanel();
		
		sidePanel.add(northPanel,BorderLayout.NORTH);
		sidePanel.add(centerPanel,BorderLayout.CENTER);
		
		
		
		//sidePanel.add(new JLabel ("No node selected."), BorderLayout.CENTER);
	//	warning_flag = 1;
		
	//	centerLabel = new JLabel();

		//will contain all the registered plugins
		
        pluginList = new JComboBox<String>();
        model = (MutableComboBoxModel<String>)pluginList.getModel();
        goButton = new JButton("Go");
        cancelButton = new JButton("Cancel");
        errorMessage = new JLabel("No item selected.");

		
		GridBagConstraints con = new GridBagConstraints();
		
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 0;
		con.gridy = 0;
		//con.weightx = 1;
		con.gridwidth = 4;
		//con.gridheight = 1;
        northPanel.add(pluginList,con);
        
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 0;
		con.gridy = 1;
		con.weightx = 0.5;
		con.gridwidth = 2;
		//con.gridheight = 1;
        northPanel.add(goButton,con);
        
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 2;
		con.gridy = 1;
		con.weightx = 0.5;
		con.gridwidth = 2;
		//con.gridheight = 1;
        northPanel.add(cancelButton,con);
        
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 0;
		con.gridy = 2;
		//con.weightx = 1;
		con.gridwidth = 4;
		//con.gridheight = 1;
        northPanel.add(errorMessage,con);
        
        
        /* pluginList.addItemListener(new ItemListener() {
        	
        	@Override
           public void itemStateChanged(ItemEvent e){
      
        		
            	{	
                	if (e.getStateChange() == ItemEvent.SELECTED){
                		int flag = 0;
                        Iterator<IInfoProvider> ip = registry.registeredPlugins.iterator();
                        while(ip.hasNext()) {
                        	IInfoProvider ipo = ip.next();
                        	
                        	if( ipo.getName().equals(e.getItem().toString())){
                        		
                        		displayMessage("Connecting to internet...");
                        		getInformationWorker giw = new getInformationWorker(ipo, centerPanel, xref);
                        		flag = 1;
                        		giw.execute();        		
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
        
        */
        
        //sidePanel.add(pluginList, BorderLayout.NORTH);
        
        //goButton = new JButton("Go");
        goButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
        		
        		//System.err.println("cliky");
      
        		
        		if(pluginList.getItemCount() != 0){
        			
        			
        		
        		
                    Iterator<IInfoProvider> ip = registry.registeredPlugins.iterator();
                    while(ip.hasNext()) {
                    	IInfoProvider ipo = ip.next();
                    	
                    	if( ipo.getName().equals(pluginList.getSelectedItem().toString())){
                    		
                    		if(giw!=null && !giw.isDone()){
                    			giw.cancel(true);
                    		}
                    			displayMessage("Connecting to the plugin...");
                        		giw = new getInformationWorker(ipo, centerPanel, errorMessage, xref);
                        		giw.execute();
                    		//sidePanel.add(new JLabel ("Connecting to internet."));
                    		
                    	}
                    	
                    }
           		}
        		else {
        			displayMessage("No plugin available.");
        		}
        	}}
        );
        
        
        cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				
        		if(giw!=null && !giw.isDone()){
        			giw.cancel(true);
        			displayMessage("Query cancelled.");
        		}
        		else{
        			displayMessage("No query in progress.");
        		}
				
			}
			
        }
        );
        
        
        //sidePanel.add(goButton,BorderLayout.SOUTH);
       
        JTabbedPane sidebarTabbedPane = desktop.getSideBarTabbedPane();
        sidebarTabbedPane.add("Info", sidePanel);
	}

	@Override
	public void done() {		
		desktop.getSideBarTabbedPane().remove(sidePanel);
		// TODO Auto-generated method stub
	}

	/**
	 * updates the dropdown box depending on the pathway element
	 * selected.
	 * Displays warnings in unusual conditions.
	 */
	public void selectionEvent(SelectionEvent e) {
		// TODO Auto-generated method stub
		
        switch(e.type) {
        case SelectionEvent.OBJECT_ADDED:
        	multiSelection(e);
        	break;
        case SelectionEvent.OBJECT_REMOVED:
        	multiSelection(e);
        	break;
        case SelectionEvent.SELECTION_CLEARED:
        	emptyJComboBox(pluginList);
        	displayMessage("No node selected.");
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
	
	/**
	 * adds a new item to the model of drop down box if not present before.
	 * @param model - model to be modified
	 * @param str - item to be added
	 * @return - modified model with the new item added(if not present before)
	 */
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
	/**
	 * removes all the items from the drop down box
	 * @param jcb - drop down box to be emptied
	 */
	private void emptyJComboBox(JComboBox<String> jcb){
		pluginList.removeAllItems();
	}
	
	/**
	 * First checks if the pathway element is of the type DATANODE. If yes then
	 * updates drop down box depending on the data node type of the selected node.
	 * @param o - currently selected pathway element 
	 */
	private void sidePanelDisplayManager(VPathwayElement o){

        if(o instanceof Graphics) {
            PathwayElement pe = ((Graphics)o).getPathwayElement();
            if(pe.getObjectType()==ObjectType.DATANODE)    
            {   
            	if(isAnnotated(pe)){
            	xref = pe.getXref();            	
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
            errorMessage.setText(null);	
        	pluginList.setModel(model);
        	sidePanel.revalidate();
        	sidePanel.repaint();
            	}
            	else{
            		emptyJComboBox(pluginList);
            		displayMessage("Warning: Data node not annotated.");
            	}

        }      
}


	}
	
	/**
	 * used to display a message in the info tabbed pane
	 * @param s - Message to be displayed
	 */
	private void displayMessage(String s){
		
		errorMessage.setText(s);
       	sidePanel.revalidate();
       	sidePanel.repaint();               
	}
	
	/**
	 * checks the no. of elements selected. If more than one element selected
	 * issues a warning.
	 * @param e - current selection
	 */
	private void multiSelection(SelectionEvent e){
        if(e.selection.size() == 1){
        	//warning_flag = 0;
        	Iterator<VPathwayElement> it = e.selection.iterator();
        	sidePanelDisplayManager(it.next());
        }
        else if(e.selection.size() == 0) {
        	//warning_flag = 1;
        	emptyJComboBox(pluginList);
        	displayMessage("No node selected.");
        }
        else{
        	//warning_flag = 1;
        	emptyJComboBox(pluginList);
        	displayMessage("Warning: Multiple Elements Selected.");
        }
	}
	
	/**
	 * checks if the element is annotated or not 
	 * @param pe - element to be checked  
	 * @return - returns true if annotated else false
	 */
	private Boolean isAnnotated(PathwayElement pe){
	
		if(pe.getXref().getDataSource() == null | 
				pe.getXref().getId() == null){
			return false;
		}
		else{
			return true;	
		}
		
	}

}