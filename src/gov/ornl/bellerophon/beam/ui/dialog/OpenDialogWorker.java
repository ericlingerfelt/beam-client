/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: OpenDialogWorker.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.SwingWorker;


/**
 * The Class OpenDialogWorker.
 *
 * @author Eric J. Lingerfelt
 */
public class OpenDialogWorker extends SwingWorker<Void, Void>{
		
	private JDialog dialog;
	private Frame frame;
	
	public OpenDialogWorker(JDialog dialog){
		this.dialog = dialog;
	}
	
	public OpenDialogWorker(Frame frame){
		this.frame = frame;
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected Void doInBackground(){
		if(dialog!=null){
			dialog.setVisible(true);
		}else{
			frame.setVisible(true);
		}
		return null;
	}
}
