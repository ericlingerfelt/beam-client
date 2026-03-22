/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: DownloadFileDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import info.clearthought.layout.*;

import java.awt.*;
import java.text.*;

import javax.swing.*;

import gov.ornl.bellerophon.beam.io.*;

import java.awt.event.*;

/**
 * The Class DownloadFileDialog.
 *
 * @author Eric J. Lingerfelt
 */
public class DownloadFileDialog extends JFrame implements BytesReadListener, ActionListener{
	
	private JProgressBar bar;
	private Window owner;
	private String filename;
	private DecimalFormat format;
	private JButton minimizeButton;
	private long contentLength;
	private int counter = 0;
	
	/**
	 * Instantiates a new download file dialog.
	 *
	 * @param owner the owner
	 * @param file the file
	 * @param customFile the custom file
	 */
	public DownloadFileDialog(Window owner, String filename){
		
		this.owner = owner;
		this.filename = filename;
		
		format = new DecimalFormat("#######0.0");
		setSize(750, 125);

		bar = new JProgressBar();
		bar.setStringPainted(true);
		bar.setBorderPainted(true);
		bar.setMaximum(100);
		bar.setMinimum(0);
		
		minimizeButton = new JButton("Minimize");
		minimizeButton.addActionListener(this);
		
		Container c = this.getContentPane();
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL
						, 10, TableLayoutConstants.PREFERRED, 10};
		c.setLayout(new TableLayout(col, row));
		c.add(bar, "1, 1, f, c");
		c.add(minimizeButton, "1, 3, r, c");
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==minimizeButton){
			setState(ICONIFIED);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.io.BytesReadListener#setBytesRead(int)
	 */
	public void setBytesRead(long bytesRead){
		if(counter==200){
			bar.setValue((int) Math.ceil((((double)bytesRead/(double)contentLength)*100)));
			setTitle("Downloading " + filename + ": " 
					+ format.format(bytesRead/1000000L) + " MB out of " 
					+ format.format(contentLength/1E6) + " MB");
			repaint();
			counter = 0;
		}else if(bytesRead==contentLength){
			bar.setValue(100);
			setTitle("Downloading Results: " 
					+ format.format(contentLength/1E6) + " MB out of " 
					+ format.format(contentLength/1E6) + " MB");
			repaint();
		}else{
			counter++;
		}
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
		setTitle("Downloading " + filename
				+ ": 0.0 MB out of " 
				+ format.format(contentLength/(long)1E6) + " MB");
		
	}
	
	/**
	 * Open.
	 */
	public void open(){
		setLocationRelativeTo(owner);
		OpenDialogWorker task = new OpenDialogWorker(this);
		task.execute();
	}
	
	/**
	 * Close.
	 */
	public void close(){
		setVisible(false);
		dispose();
	}
	
}