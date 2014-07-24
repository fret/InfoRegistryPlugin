package org.pathvisio.inforegistry.impl;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.bridgedb.Xref;
import org.pathvisio.inforegistry.IInfoProvider;

public class getInformationWorker extends SwingWorker<JComponent, Void> {

	  	
	  
	  private final IInfoProvider ipo;
	  private final JPanel centerPanel;
	  private final JLabel errorMessage;
	  private final Xref xref;
	  
	  public getInformationWorker(final IInfoProvider ipo, final JPanel centerPanel, final JLabel errorMessage, final Xref xref){
		  this.ipo = ipo;
		  this.centerPanel = centerPanel;
		  this.errorMessage = errorMessage;
		  this.xref = xref;
	  }
	  
	  @Override
	  protected JComponent doInBackground() {
  		return ipo.getInformation(xref);
	  }
	  
	  protected void done() {
		  
		  try {
			JComponent jc = get();
			errorMessage.setText(null);
			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
			centerPanel.removeAll();
			//centerPanel.add(new JLabel("Results:"));
			centerPanel.add(jc);
			centerPanel.revalidate();
			centerPanel.repaint();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  catch (CancellationException e){
		  }
	  }
}
