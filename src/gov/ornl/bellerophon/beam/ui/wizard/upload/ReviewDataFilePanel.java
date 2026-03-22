/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: ReviewDataFilePanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.wizard.upload;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.ui.format.Calendars;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;

import javax.swing.*;

public class ReviewDataFilePanel extends JPanel{

	private JLabel nameValueLabel, instrumentValueLabel, gridSizeValueLabel, experimentDateValueLabel;
	
	public ReviewDataFilePanel() {
		
		WordWrapLabel topLabel = new WordWrapLabel(true);
		topLabel.setText("Please review the following data obtained from the selected H5 file and click <i>Continue</i>.");
		
		JLabel nameLabel = new JLabel("Filename:");
		JLabel instrumentLabel = new JLabel("Instrument:");
		JLabel gridSizeLabel = new JLabel("Grid Size:");
		JLabel experimentDateLabel = new JLabel("Experiment Date:");
		
		nameValueLabel = new JLabel();
		instrumentValueLabel = new JLabel();
		gridSizeValueLabel = new JLabel();
		experimentDateValueLabel = new JLabel();
		
		JPanel valuePanel = new JPanel();
		double[] columnValue = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		double[] rowValue = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		valuePanel.setLayout(new TableLayout(columnValue, rowValue));
		valuePanel.add(nameLabel, 				"0, 0, r, c");
		valuePanel.add(nameValueLabel, 			"2, 0, l, c");
		valuePanel.add(instrumentLabel, 		"0, 2, r, c");
		valuePanel.add(instrumentValueLabel, 	"2, 2, l, c");
		valuePanel.add(gridSizeLabel, 			"0, 4, r, c");
		valuePanel.add(gridSizeValueLabel, 		"2, 4, l, c");
		valuePanel.add(experimentDateLabel, 	"0, 6, r, c");
		valuePanel.add(experimentDateValueLabel,"2, 6, l, c");
		
		double[] column = {20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
						, 30, TableLayoutConstants.PREFERRED, 20};

		setLayout(new TableLayout(column, row));
		add(topLabel, 	"1, 1, c, c");
		add(valuePanel,	"1, 3, c, c");
	}

	public void setCurrentState(DataFile selectedDataFile){
		nameValueLabel.setText(selectedDataFile.getName());
		if(selectedDataFile.getInstrument()!=null){
			instrumentValueLabel.setText(selectedDataFile.getInstrument().toString());
		}
		if(selectedDataFile.getGridSize()!=null){
			gridSizeValueLabel.setText(selectedDataFile.getGridSize().toString());
		}
		if(selectedDataFile.getExpDate()!=null){
			experimentDateValueLabel.setText(Calendars.getFormattedOutputDateString(selectedDataFile.getExpDate()));
		}
	}

}
