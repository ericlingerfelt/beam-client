/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: BEAnalyzerPanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.imageprocessor;

import gov.ornl.bellerophon.beam.data.feature.ImageProcessorData;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning.ImageProcessorPCAImageCleaningAnalysisPanel;
import gov.ornl.bellerophon.beam.ui.util.DataFileSelectionListener;
import gov.ornl.bellerophon.beam.ui.util.FullScreenModeListener;
import info.clearthought.layout.*;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;

import javax.swing.*;

public class ImageProcessorPanel extends JPanel implements ActionListener
														, DataFileSelectionListener
														, FullScreenModeListener
														, DataExporter
														, ImageExporter
														, ImageProcessorModeListener{
	
	private ImageProcessorData d = new ImageProcessorData();
	private ImageProcessorPCAImageCleaningAnalysisPanel pcaImageCleaningAnalysisPanel;
	private ImageProcessorInputPanel inputPanel;
	private ImageProcessorDataFilePanel filePanel;
	private JPanel analysisPanel;
	public static enum Mode{PCA_IMAGE_CLEANING_ANALYSIS_MODE};
	private boolean initialized = false;
	private JSplitPane jsp;
	private Mode mode;
	
	public ImageProcessorPanel(Frame frame){

		pcaImageCleaningAnalysisPanel = new ImageProcessorPCAImageCleaningAnalysisPanel(frame, d);
		
		inputPanel = new ImageProcessorInputPanel(frame, d, this, pcaImageCleaningAnalysisPanel);
		inputPanel.setBorder(Borders.getBorder("Image Processor Functions"));
		
		filePanel = new ImageProcessorDataFilePanel(frame, d, this, inputPanel);
		filePanel.setBorder(Borders.getBorder("Image Processor Data File"));
		filePanel.setPreferredSize(new Dimension(300, 9000));
		
		inputPanel.setImageProcessorDataFilePanel(filePanel);
		
		analysisPanel = new JPanel();
		double[] colAnalysis = {TableLayoutConstants.FILL};
		double[] rowAnalysis = {TableLayoutConstants.FILL};
		analysisPanel.setLayout(new TableLayout(colAnalysis, rowAnalysis));
		analysisPanel.setBorder(Borders.getBorder("Image Processor Data Viewer"));
		
		JSplitPane jspLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, filePanel, inputPanel);
		jspLeft.setBorder(null);
		jspLeft.setResizeWeight(0.5);
		jspLeft.setDividerLocation(350);
		
		jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, jspLeft, analysisPanel);
		jsp.setBorder(null);
		jsp.setDividerLocation(400);
		
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		add(jsp, 			"1, 1, f, f");
	}
	
	public void setCurrentState(){
		if(!initialized){
			filePanel.setCurrentState();
			inputPanel.setCurrentState();
			pcaImageCleaningAnalysisPanel.setCurrentState();
			setMode(Mode.PCA_IMAGE_CLEANING_ANALYSIS_MODE);
			initialized = true;
		}
	}
	
	public void actionPerformed(ActionEvent ae){
		
	}
	
	private void setMode(Mode mode){
		
		this.mode = mode;
		
		analysisPanel.removeAll();
		switch(mode){
		
			case PCA_IMAGE_CLEANING_ANALYSIS_MODE:
				analysisPanel.add(pcaImageCleaningAnalysisPanel, 	"0, 0, f, f");
				break;
		
		}
			
		analysisPanel.validate();
		analysisPanel.repaint();
	}

	public void dataFileSelected(){
		pcaImageCleaningAnalysisPanel.setCurrentState();
	}

	public void exportCurrentImage(){
		switch(mode){
		
			case PCA_IMAGE_CLEANING_ANALYSIS_MODE:
				pcaImageCleaningAnalysisPanel.exportCurrentImage();
				break;
				
		}
	}

	public void exportCurrentData(){
		
		switch(mode){
		
			case PCA_IMAGE_CLEANING_ANALYSIS_MODE:
				pcaImageCleaningAnalysisPanel.exportCurrentData();
				break;
			
		}

	}
	
	public void enterFullScreenMode(){
		removeAll();
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		add(analysisPanel, "1, 1, f, f");
		validate();
		repaint();
	}
	
	public void exitFullScreenMode(){
		removeAll();
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		jsp.setRightComponent(analysisPanel);
		add(jsp, 			"1, 1, f, f");
		validate();
		repaint();
	}

	public void imageProcessorModeChanged(Mode mode) {
		setMode(mode);
	}

}
