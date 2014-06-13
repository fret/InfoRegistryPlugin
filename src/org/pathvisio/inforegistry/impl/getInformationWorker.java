package org.pathvisio.inforegistry.impl;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.bridgedb.Xref;
import org.pathvisio.inforegistry.IInfoProvider;

public class getInformationWorker extends SwingWorker<JComponent, Void> {

	  	
	  
	  private final IInfoProvider ipo;
	  private final JPanel sidePanel;
	  private final Xref xref;
	  
	  public getInformationWorker(final IInfoProvider ipo, final JPanel sidePanel, final Xref xref){
		  this.ipo = ipo;
		  this.sidePanel = sidePanel;
		  this.xref = xref;
	  }
	  
	  @Override
	  protected JComponent doInBackground() {
  		return ipo.getInformation(xref);
	  }
	  
	  protected void done() {
		  
		  try {
			JComponent jc = get();
			sidePanel.add(jc);
			sidePanel.revalidate();
			sidePanel.repaint();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
}
