/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: SelectLocalFilePanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.wizard.upload;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.*;
import gov.ornl.bellerophon.beam.file.CustomFileFilter;
import gov.ornl.bellerophon.beam.file.FileType;
import gov.ornl.bellerophon.beam.ui.util.*;
import info.clearthought.layout.*;

import java.awt.Dimension;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

public class SelectLocalFilePanel extends JPanel implements ActionListener{

	private File selectedLocalFile;
	private DataFile selectedDataFile;
	private JButton selectFileButton;
	private JTextArea selectedFileArea;
	private JLabel selecteFileLabel;
	
	public SelectLocalFilePanel(UploadDataFileWizard owner){

		WordWrapLabel topLabel = new WordWrapLabel(true);
		topLabel.setText("Please select an H5 file to upload to your BEAM data storage area and click <i>Continue</i>.");
		
		selectFileButton = new JButton("Browse...");
		selectFileButton.addActionListener(this);
		
		selecteFileLabel = new JLabel("Selected Local Data File:");
		
		selectedFileArea = new JTextArea();
		selectedFileArea.setEditable(false);
		selectedFileArea.setLineWrap(true);
		JScrollPane selectedFileAreaSP = new JScrollPane(selectedFileArea);
		selectedFileAreaSP.setPreferredSize(new Dimension(5000, 100));
		
		JPanel filePanel = new JPanel();
		double[] columnFile = {TableLayoutConstants.FILL};
		double[] rowFile = {TableLayoutConstants.PREFERRED
							, 10, TableLayoutConstants.FILL
							, 10, TableLayoutConstants.PREFERRED};
		filePanel.setLayout(new TableLayout(columnFile, rowFile));
		filePanel.add(selecteFileLabel, 	"0, 0, l, c");
		filePanel.add(selectedFileAreaSP, 	"0, 2, f, f");
		filePanel.add(selectFileButton, 	"0, 4, r, c");
		
		double[] column = {20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
						, 30, TableLayoutConstants.PREFERRED, 20};

		setLayout(new TableLayout(column, row));
		add(topLabel, 	"1, 1, c, c");
		add(filePanel,	"1, 3, f, f");
		
	}

	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==selectFileButton){
			JFileChooser fileDialog = PlainFileChooserFactory.createPlainFileChooser();
			fileDialog.setAcceptAllFileFilterUsed(false);
			fileDialog.setFileFilter(new CustomFileFilter(FileType.H5));
			int returnVal = fileDialog.showOpenDialog(this); 
			MainData.setAbsolutePath(fileDialog.getCurrentDirectory());
			if(returnVal==JFileChooser.APPROVE_OPTION){
				selectedLocalFile = fileDialog.getSelectedFile();
				selectedFileArea.setText(selectedLocalFile.getAbsolutePath());
			}
		}
	}
	
	public boolean goodData(){
		return selectedLocalFile!=null;
	}
	
	public boolean goodFilename(){
		boolean goodFilename = true;
		if(selectedLocalFile.getName().startsWith(".") || !selectedLocalFile.getName().matches("[\\-._a-zA-Z0-9]+")){
			goodFilename = false;
		}
		return goodFilename;
	}
	
	public void setCurrentState(File selectedLocalFile, DataFile selectedDataFile){
		this.selectedLocalFile = selectedLocalFile;
		this.selectedDataFile = selectedDataFile;
		if(selectedLocalFile!=null){
			selectedFileArea.setText(selectedLocalFile.getAbsolutePath());
		}
	}
	
	public void getCurrentState(){
		selectedDataFile.populateFromHDF5File(selectedLocalFile);
		selectedDataFile.setFile(selectedLocalFile);
		selectedDataFile.setSize(selectedLocalFile.length());
	}

	public boolean goodDataSize() {
		return (MainData.getTotalDataSize() + selectedLocalFile.length()) < MainData.MAX_DATA_SIZE;
	}
}
