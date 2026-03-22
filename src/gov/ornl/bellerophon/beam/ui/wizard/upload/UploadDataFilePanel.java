/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: UploadDataFilePanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.wizard.upload;

import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.io.BytesWrittenListener;
import gov.ornl.bellerophon.beam.ui.dialog.CautionDialog;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import gov.ornl.bellerophon.beam.ui.worker.CreateDataFileWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.CreateDataFileListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.*;

public class UploadDataFilePanel extends JPanel implements ActionListener, BytesWrittenListener, CreateDataFileListener{

	private UploadDataFileWizard owner;
	private DataFile selectedDataFile;
	private JButton uploadButton;
	private JLabel nameValueLabel, pathValueLabel, uploadValueLabel;
	private WordWrapLabel topLabel;
	private JPanel valuePanel;
	
	private JProgressBar bar;
	private DecimalFormat format;
	private int counter = 0;
	
	public UploadDataFilePanel(UploadDataFileWizard owner) {
		
		this.owner = owner;
		
		format = new DecimalFormat("########.0");
		
		topLabel = new WordWrapLabel(true);		
		uploadValueLabel = new JLabel("");
		
		JLabel nameLabel = new JLabel("Data File Name:");
		JLabel pathLabel = new JLabel("Upload Directory:");
		JLabel uploadLabel = new JLabel("Data File Upload Status:");
		
		bar = new JProgressBar();
		bar.setStringPainted(true);
		bar.setBorderPainted(true);
		bar.setMinimum(0);
		
		nameValueLabel = new JLabel();
		pathValueLabel = new JLabel();
		
		uploadButton = new JButton("Upload Data File");
		uploadButton.addActionListener(this);
		
		valuePanel = new JPanel();
		double[] columnValue = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		double[] rowValue = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		valuePanel.setLayout(new TableLayout(columnValue, rowValue));
		valuePanel.add(nameLabel, 			"0, 0, r, c");
		valuePanel.add(nameValueLabel, 		"2, 0, l, c");
		valuePanel.add(pathLabel, 			"0, 2, r, c");
		valuePanel.add(pathValueLabel, 		"2, 2, l, c");
		valuePanel.add(uploadLabel, 		"0, 4, r, c");
		valuePanel.add(uploadValueLabel, 	"2, 4, l, c");
		
		double[] column = {20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
						, 30, TableLayoutConstants.PREFERRED
						, 30, TableLayoutConstants.PREFERRED
						, 30, TableLayoutConstants.PREFERRED, 20};

		setLayout(new TableLayout(column, row));
	
	}

	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==uploadButton){
			if(!selectedDataFile.exists()){
				remove(uploadButton);
				validate();
				CreateDataFileWorker worker = new CreateDataFileWorker(this, this, selectedDataFile, owner);
				worker.execute();
			}else{
				String string = "The selected data file already exists in the selected directory. Do you want to overwrite this data file?";
				int returnValue = CautionDialog.createCautionDialog(owner, string, "Attention!");
				if(returnValue==CautionDialog.YES){
					remove(uploadButton);
					validate();
					CreateDataFileWorker worker = new CreateDataFileWorker(this, this, selectedDataFile, owner);
					worker.execute();
				}
			}
		}
	}

	public void setCurrentState(DataFile selectedDataFile){
		this.selectedDataFile = selectedDataFile;
		nameValueLabel.setText(selectedDataFile.getName());
		pathValueLabel.setText(selectedDataFile.getPath());
		topLabel.setText("Please review the following data and click <i>Upload Data File</i> to "
							+ "transfer the file to your BEAM data storage area and enter its metadata into the BEAM database."
							+ " Uploading your file and processing its metadata may take several minutes.");
		uploadValueLabel.setText("0.0 MB out of " + format.format(selectedDataFile.getSize()/1E6) + " MB");
		bar.setMaximum(100);
		removeAll();
		add(topLabel, 		"1, 1, c, c");
		add(valuePanel,		"1, 3, c, c");
		add(bar,			"1, 5, f, c");
		add(uploadButton,	"1, 7, c, c");
		validate();
	}

	public void setBytesWritten(long bytesWritten){
		if(counter==200){
			
			bar.setValue((int) Math.ceil((((double)bytesWritten/(double)selectedDataFile.getSize())*100)));
			uploadValueLabel.setText(format.format(bytesWritten/1000000L) + " MB out of " 
									+ format.format(selectedDataFile.getSize()/1E6) + " MB");
			counter = 0;
			
		}else if(bytesWritten==selectedDataFile.getSize()){
			
			bar.setValue(100);
			uploadValueLabel.setText(format.format(selectedDataFile.getSize()/1E6) + " MB out of " 
									+ format.format(selectedDataFile.getSize()/1E6) + " MB");
			
		}else{
			counter++;
		}
	}

	public void updateAfterCreateDataFile() {
		owner.fileUploaded = true;
		remove(bar);
		topLabel.setText("The file listed below has been uploaded to your BEAM data storage area and inserted into the BEAM database.");
		validate();
		owner.addEndButtonsWithoutBackButton();
	}

	public void setContentLength(long contentLength) {
	}
	
}
