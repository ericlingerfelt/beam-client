/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: EnterUserDataPanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.wizard.upload;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;

import javax.swing.*;

public class EnterUserDataPanel extends JPanel{

	private DataFile selectedDataFile;
	private JTextField projectNameField, projectIdField, sampleNameField;
	private JTextArea sampleDescArea, commentsArea;
	
	public EnterUserDataPanel(UploadDataFileWizard owner) {
		
		WordWrapLabel topLabel = new WordWrapLabel(true);
		topLabel.setText("Please complete the following fields and click <i>Continue</i>. "
				+ "This metadata will be written as attributes in your data file at the root level.");
		
		JLabel projectNameLabel = new JLabel("Project Name:");
		JLabel projectIdLabel = new JLabel("Project ID:");
		JLabel sampleNameLabel = new JLabel("Sample Name:");
		JLabel sampleDescLabel = new JLabel("Sample Description:");
		JLabel commentsLabel = new JLabel("Comments:");
		
		projectNameField = new JTextField();
		projectIdField = new JTextField();
		sampleNameField = new JTextField();
		
		sampleDescArea = new JTextArea();
		sampleDescArea.setLineWrap(true);
		sampleDescArea.setWrapStyleWord(true);
		JScrollPane sampleDescSP = new JScrollPane(sampleDescArea);
		
		commentsArea = new JTextArea();
		commentsArea.setLineWrap(true);
		commentsArea.setWrapStyleWord(true);
		JScrollPane commentsAreaSP = new JScrollPane(commentsArea);
		
		JPanel valuePanel = new JPanel();
		double[] columnValue = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.FILL};
		double[] rowValue = {TableLayoutConstants.PREFERRED
								, 20, TableLayoutConstants.PREFERRED
								, 20, TableLayoutConstants.PREFERRED
								, 20, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.FILL
								, 20, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.FILL};
		valuePanel.setLayout(new TableLayout(columnValue, rowValue));
		valuePanel.add(projectNameLabel, 	"0, 0, l, c");
		valuePanel.add(projectNameField, 	"2, 0, f, c");
		valuePanel.add(projectIdLabel, 		"0, 2, l, c");
		valuePanel.add(projectIdField, 		"2, 2, f, c");
		valuePanel.add(sampleNameLabel, 	"0, 4, l, c");
		valuePanel.add(sampleNameField, 	"2, 4, f, c");
		valuePanel.add(sampleDescLabel, 	"0, 6, l, c");
		valuePanel.add(sampleDescSP, 		"0, 8, 2, 8, f, f");
		valuePanel.add(commentsLabel, 		"0, 10, l, c");
		valuePanel.add(commentsAreaSP, 		"0, 12, 2, 12, f, f");
		
		double[] column = {20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
						, 30, TableLayoutConstants.FILL, 20};

		setLayout(new TableLayout(column, row));
		add(topLabel, 	"1, 1, c, c");
		add(valuePanel,	"1, 3, f, f");
		
	}
	
	public boolean goodData(){
		return true;
	}
	
	public void setCurrentState(DataFile selectedDataFile){
		this.selectedDataFile = selectedDataFile;
		projectNameField.setText(selectedDataFile.getProjectName());
		projectIdField.setText(selectedDataFile.getProjectId());
		sampleNameField.setText(selectedDataFile.getSampleName());
		sampleDescArea.setText(selectedDataFile.getSampleDesc());
		commentsArea.setText(selectedDataFile.getComments());
	}
	
	public void getCurrentState(){
		selectedDataFile.setProjectName(projectNameField.getText().trim());
		selectedDataFile.setProjectId(projectIdField.getText().trim());
		selectedDataFile.setComments(commentsArea.getText().trim());
		selectedDataFile.setSampleName(sampleNameField.getText().trim());
		selectedDataFile.setSampleDesc(sampleDescArea.getText().trim());
	}

}
