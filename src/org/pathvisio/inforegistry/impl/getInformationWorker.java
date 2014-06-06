package org.pathvisio.inforegistry.impl;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.pathvisio.inforegistry.IInfoProvider;

public class getInformationWorker extends SwingWorker<JComponent, IInfoProvider> {

	  	
	  
	  private final IInfoProvider ipo;
	  private final JPanel sidePanel;
	  
	  public getInformationWorker(final IInfoProvider ipo, final JPanel sidePanel){
		  this.ipo = ipo;
		  this.sidePanel = sidePanel;
	  }
	  
	  @Override
	  protected JComponent doInBackground() {
  		sidePanel.add(ipo.getInformation(null));
  		sidePanel.revalidate();
  		sidePanel.repaint();
  		return null;
  		

	  }
}
