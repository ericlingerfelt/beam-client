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
package gov.ornl.bellerophon.beam.ui.multivariateanalyzer;

import gov.ornl.bellerophon.beam.data.feature.MultivariateAnalyzerData;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.multivariateanalyzer.bayesian.MultivariateAnalyzerBayesianAnalysisPanel;
import gov.ornl.bellerophon.beam.ui.multivariateanalyzer.ica.MultivariateAnalyzerICAAnalysisPanel;
import gov.ornl.bellerophon.beam.ui.multivariateanalyzer.kmeansclustering.MultivariateAnalyzerKMeansClusteringAnalysisPanel;
import gov.ornl.bellerophon.beam.ui.multivariateanalyzer.pca.MultivariateAnalyzerPCAAnalysisPanel;
import gov.ornl.bellerophon.beam.ui.util.DataFileSelectionListener;
import gov.ornl.bellerophon.beam.ui.util.FullScreenModeListener;
import info.clearthought.layout.*;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;

import javax.swing.*;

public class MultivariateAnalyzerPanel extends JPanel implements ActionListener
																	, DataFileSelectionListener
																	, FullScreenModeListener
																	, DataExporter
																	, ImageExporter
																	, MultivariateAnalyzerModeListener{
	
	private MultivariateAnalyzerData d = new MultivariateAnalyzerData();
	private MultivariateAnalyzerPCAAnalysisPanel pcaAnalysisPanel;
	private MultivariateAnalyzerICAAnalysisPanel icaAnalysisPanel;
	private MultivariateAnalyzerBayesianAnalysisPanel bayesianAnalysisPanel;
	private MultivariateAnalyzerKMeansClusteringAnalysisPanel kmeansClusteringAnalysisPanel;
	private MultivariateAnalyzerInputPanel inputPanel;
	private MultivariateAnalyzerDataFilePanel filePanel;
	private JPanel analysisPanel;
	private boolean initialized = false;
	private JSplitPane jsp;
	private MultivariateAnalyzerMode mode;
	
	public MultivariateAnalyzerPanel(Frame frame){

		pcaAnalysisPanel = new MultivariateAnalyzerPCAAnalysisPanel(frame, d);
		icaAnalysisPanel = new MultivariateAnalyzerICAAnalysisPanel(frame, d);
		bayesianAnalysisPanel = new MultivariateAnalyzerBayesianAnalysisPanel(frame, d);
		kmeansClusteringAnalysisPanel = new MultivariateAnalyzerKMeansClusteringAnalysisPanel(frame, d);
		
		inputPanel = new MultivariateAnalyzerInputPanel(frame, d, this, 
															kmeansClusteringAnalysisPanel, 
															pcaAnalysisPanel, 
															pcaAnalysisPanel);
		inputPanel.setBorder(Borders.getBorder("Multivariate Analyzer Functions"));
		
		filePanel = new MultivariateAnalyzerDataFilePanel(frame, d, this, inputPanel);
		filePanel.setBorder(Borders.getBorder("Multivariate Analyzer Data File"));
		filePanel.setPreferredSize(new Dimension(300, 9000));
		
		inputPanel.setMultivariateAnalyzerDataFilePanel(filePanel);
		
		analysisPanel = new JPanel();
		double[] colAnalysis = {TableLayoutConstants.FILL};
		double[] rowAnalysis = {TableLayoutConstants.FILL};
		analysisPanel.setLayout(new TableLayout(colAnalysis, rowAnalysis));
		analysisPanel.setBorder(Borders.getBorder("Multivariate Analyzer Data Viewer"));
		
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
			pcaAnalysisPanel.setCurrentState();
			icaAnalysisPanel.setCurrentState();
			bayesianAnalysisPanel.setCurrentState();
			kmeansClusteringAnalysisPanel.setCurrentState();
			setMode(MultivariateAnalyzerMode.PCA_ANALYSIS_MODE);
			initialized = true;
		}
	}
	
	public void actionPerformed(ActionEvent ae){
		
	}
	
	private void setMode(MultivariateAnalyzerMode mode){
		
		this.mode = mode;
		
		filePanel.setMode(mode);
		
		analysisPanel.removeAll();
		switch(mode){
		
			case PCA_ANALYSIS_MODE:
				analysisPanel.add(pcaAnalysisPanel, 	"0, 0, f, f");
				break;
				
			case ICA_ANALYSIS_MODE:
				analysisPanel.add(icaAnalysisPanel, 	"0, 0, f, f");
				break;
				
			case BAYESIAN_ANALYSIS_MODE:
				analysisPanel.add(bayesianAnalysisPanel, 	"0, 0, f, f");
				break;
				
			case KMEANS_CLUSTERING_ANALYSIS_MODE:
				analysisPanel.add(kmeansClusteringAnalysisPanel, 	"0, 0, f, f");
				break;
		
		}
			
		analysisPanel.validate();
		analysisPanel.repaint();
	}

	public void dataFileSelected(){
		pcaAnalysisPanel.setCurrentState();
		icaAnalysisPanel.setCurrentState();
		bayesianAnalysisPanel.setCurrentState();
		kmeansClusteringAnalysisPanel.setCurrentState();
	}
	
	public void updateAfterGetPCASResults(){
		pcaAnalysisPanel.setCurrentState();
		setMode(MultivariateAnalyzerMode.PCA_ANALYSIS_MODE);
	}

	public void exportCurrentImage(){
		switch(mode){
		
			case PCA_ANALYSIS_MODE:
				pcaAnalysisPanel.exportCurrentImage();
				break;
				
			case ICA_ANALYSIS_MODE:
				icaAnalysisPanel.exportCurrentImage();
				break;
				
			case BAYESIAN_ANALYSIS_MODE:
				bayesianAnalysisPanel.exportCurrentImage();
				break;
				
			case KMEANS_CLUSTERING_ANALYSIS_MODE:
				kmeansClusteringAnalysisPanel.exportCurrentImage();
				break;
				
		}
	}

	public void exportCurrentData(){
		
		switch(mode){
		
			case PCA_ANALYSIS_MODE:
				pcaAnalysisPanel.exportCurrentData();
				break;
				
			case ICA_ANALYSIS_MODE:
				icaAnalysisPanel.exportCurrentData();
				break;
				
			case BAYESIAN_ANALYSIS_MODE:
				bayesianAnalysisPanel.exportCurrentData();
				break;
				
			case KMEANS_CLUSTERING_ANALYSIS_MODE:
				kmeansClusteringAnalysisPanel.exportCurrentData();
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

	public void multivariateAnalyzerModeChanged(MultivariateAnalyzerMode mode) {
		setMode(mode);
	}

}
