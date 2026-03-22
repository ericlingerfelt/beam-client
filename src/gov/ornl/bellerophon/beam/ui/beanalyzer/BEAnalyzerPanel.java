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
package gov.ornl.bellerophon.beam.ui.beanalyzer;

import gov.ornl.bellerophon.beam.data.feature.BEAnalyzerData;
import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.MeanSpectrogramDataSet;
import gov.ornl.bellerophon.beam.data.util.SHOFitDataSet;
import gov.ornl.bellerophon.beam.enums.AnalysisFunctionType;
import gov.ornl.bellerophon.beam.ui.beanalyzer.meanspectrogram.BEAnalyzerMeanSpectrogramAnalysisPanel;
import gov.ornl.bellerophon.beam.ui.beanalyzer.shofit.BEAnalyzerSHOFitAnalysisPanel;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.format.Buttons;
import gov.ornl.bellerophon.beam.ui.format.Colors;
import gov.ornl.bellerophon.beam.ui.util.DataFileSelectionListener;
import gov.ornl.bellerophon.beam.ui.util.FullScreenModeListener;
import gov.ornl.bellerophon.beam.ui.wizard.analysis.ExecuteAnalysisProcessWizard;
import gov.ornl.bellerophon.beam.ui.worker.GetMeanSpectrogramWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetSHOFitResultsWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetMeanSpectrogramListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetSHOFitResultsListener;
import info.clearthought.layout.*;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.*;

import hdf.object.Dataset;
import hdf.object.h5.H5File;

public class BEAnalyzerPanel extends JPanel implements ActionListener
														, DataFileSelectionListener
														, GetSHOFitResultsListener
														, GetMeanSpectrogramListener
														, FullScreenModeListener
														, DataExporter
														, ImageExporter{
	
	private BEAnalyzerData d = new BEAnalyzerData();
	private BEAnalyzerDataFilePanel filePanel;
	private BEAnalyzerMeanSpectrogramAnalysisPanel meanSpectrogramAnalysisPanel;
	private BEAnalyzerSHOFitAnalysisPanel shoFitAnalysisPanel;
	private JButton execShoFitButton, viewShoFitResultsButton, execMeanSpectrogramButton;
	private JPanel buttonPanel, analysisPanel;
	private Frame frame;
	private enum Mode{INITIAL_MODE, DATA_FILE_SELECTED_MODE, MEAN_SPECTROGRAM_ANALYSIS_MODE, SHO_FIT_ANALYSIS_MODE};
	private boolean initialized = false;
	private JSplitPane jsp;
	private Mode mode;
	
	public BEAnalyzerPanel(Frame frame){

		this.frame = frame;

		shoFitAnalysisPanel = new BEAnalyzerSHOFitAnalysisPanel(frame, d);
		meanSpectrogramAnalysisPanel = new BEAnalyzerMeanSpectrogramAnalysisPanel(frame, d);
		
		execMeanSpectrogramButton = Buttons.getIconButton("View Mean Spectrogram"
													, "icons/system-search.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 12);
		
		execShoFitButton = Buttons.getIconButton("Execute SHO Fit with HPC"
													, "icons/system-run.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 12);

		viewShoFitResultsButton = Buttons.getIconButton("View SHO Fit Results"
													, "icons/system-search.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 28);

		filePanel = new BEAnalyzerDataFilePanel(frame, d, this);
		filePanel.setBorder(Borders.getBorder("BE Analyzer Data File"));
		filePanel.setPreferredSize(new Dimension(300, 9000));
		
		buttonPanel = new JPanel();
		buttonPanel.setBorder(Borders.getBorder("BE Analyzer Functions"));
		buttonPanel.setPreferredSize(new Dimension(300, 9000));
		double[] columnButton = {10, TableLayoutConstants.FILL, 10};
		double[] rowButton = {10, TableLayoutConstants.FILL
								, 10, TableLayoutConstants.PREFERRED
								, 15, TableLayoutConstants.PREFERRED
								, 15, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.FILL, 10};
		buttonPanel.setLayout(new TableLayout(columnButton, rowButton));
		buttonPanel.add(new JLabel(), 					"1, 1, f, f");
		buttonPanel.add(execMeanSpectrogramButton, 	"1, 3, f, c");
		buttonPanel.add(execShoFitButton, 				"1, 5, f, c");
		buttonPanel.add(viewShoFitResultsButton, 		"1, 7, f, c");
		buttonPanel.add(new JLabel(), 					"1, 9, f, f");
		
		analysisPanel = new JPanel();
		double[] colAnalysis = {10, TableLayoutConstants.FILL, 10};
		double[] rowAnalysis = {10, TableLayoutConstants.FILL, 10};
		analysisPanel.setLayout(new TableLayout(colAnalysis, rowAnalysis));
		analysisPanel.setBorder(Borders.getBorder("BE Analyzer Data Viewer"));
		
		JSplitPane jspLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, filePanel, buttonPanel);
		jspLeft.setBorder(null);
		jspLeft.setResizeWeight(0.5);
		jspLeft.setDividerLocation(350);
		
		jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, jspLeft, analysisPanel);
		jsp.setBorder(null);
		jsp.setDividerLocation(400);
		jsp.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, 
				new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent pce){
					meanSpectrogramAnalysisPanel.getChartPanel().drawChart();
				}
		});
		
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		add(jsp, 			"1, 1, f, f");
	}

	public void setCurrentState(){
		if(!initialized){
			filePanel.setCurrentState();
			setMode(Mode.INITIAL_MODE);
			initialized = true;
		}
		meanSpectrogramAnalysisPanel.getChartPanel().drawChart();
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==execShoFitButton){
			AnalysisProcess process = new AnalysisProcess();
			process.setDataFile(d.getDataFile());
			process.setAnalysisFunctionType(AnalysisFunctionType.SHO_FIT);
			process.setInputParameters(getSHOFitInputParameters());
			boolean processCompleted = ExecuteAnalysisProcessWizard.createExecuteAnalysisProcessWizard(frame, process);
			if(processCompleted){
				SHOFitDataSet sfds = new SHOFitDataSet();
				sfds.setDataFileIndex(d.getDataFile().getIndex());
				d.getDataFile().setSHOFitDataSet(sfds);
				GetSHOFitResultsWorker worker = new GetSHOFitResultsWorker(this, sfds, frame);
				worker.execute();
			}
		}else if(ae.getSource()==viewShoFitResultsButton){
			if(d.getDataFile().getSHOFitDataSet()==null){
				SHOFitDataSet sfds = new SHOFitDataSet();
				sfds.setDataFileIndex(d.getDataFile().getIndex());
				d.getDataFile().setSHOFitDataSet(sfds);
				GetSHOFitResultsWorker worker = new GetSHOFitResultsWorker(this, sfds, frame);
				worker.execute();
			}else{
				updateAfterGetSHOFitResults();
			}
		}else if(ae.getSource()==execMeanSpectrogramButton){
			MeanSpectrogramDataSet msds = new MeanSpectrogramDataSet();
			msds.setDataFileIndex(d.getDataFile().getIndex());
			d.getDataFile().setMeanSpectrogramDataSet(msds);
			GetMeanSpectrogramWorker worker = new GetMeanSpectrogramWorker(this, msds, frame);
			worker.execute();
		}
	}
	
	private String getSHOFitInputParameters(){
		
		String string = "";
		
		try {
			
			H5File h5File = d.getDataFile().getTreeH5File();
			h5File.open();
		
			DecimalFormat df = new DecimalFormat("000");
			
			int mCounter = 0;
			String mGroupPath = "/Measurement_" + df.format(mCounter);
			while(h5File.get(mGroupPath) != null){
				
				int cCounter = 0;
				String cGroupPath = mGroupPath + "/Channel_" + df.format(cCounter);
				
				while(h5File.get(cGroupPath) != null){
					
					Dataset binFreqDS = (Dataset) h5File.get(cGroupPath + "/Bin_Frequencies");
					binFreqDS.init();
					
					Dataset rawDataDS = (Dataset) h5File.get(cGroupPath + "/Raw_Data");
					rawDataDS.init();
					
					string += getSHOFitInputParametersForPath(cGroupPath, 
																String.valueOf(binFreqDS.getDims()[0]), 
																String.valueOf(rawDataDS.getDims()[0]) + " " + String.valueOf(rawDataDS.getDims()[1]));
					
					cGroupPath = mGroupPath + "/Channel_" + df.format(++cCounter);
					
				}
			
				mGroupPath = "/Measurement_" + df.format(++mCounter);
				
			}
			
			h5File.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return string;
		
	}
	
	private String getSHOFitInputParametersForPath(String path, String binFreqDims, String rawDataDims){
		
		String string = "";
		
		string += "CHANNEL_PATH=";
		string += path + "\n";
		
		string += "BIN_FREQ_PATH=";
		string += path + "/Bin_Frequencies\n";
		
		string += "BIN_FREQ_DIMS=";
		string += binFreqDims + "\n";
		
		string += "RAW_DATA_PATH=";
		string += path + "/Raw_Data\n";
		
		string += "RAW_DATA_DIMS=";
		string += rawDataDims + "\n";
		
		string += "GUESS_PATH=";
		string += path + "/Raw_Data-SHO_Fit_000/Guess\n";
		
		string += "FIT_PATH=";
		string += path + "/Raw_Data-SHO_Fit_000/Fit\n";
		
		return string;
		
	}

	private void setMode(Mode mode){
		
		this.mode = mode;
		
		analysisPanel.removeAll();
		
		switch(mode){
		
			case INITIAL_MODE:
				execShoFitButton.setEnabled(false);
				viewShoFitResultsButton.setEnabled(false);
				execMeanSpectrogramButton.setEnabled(false);
				break;
				
			case DATA_FILE_SELECTED_MODE:
				execShoFitButton.setEnabled(true);
				viewShoFitResultsButton.setEnabled(true);
				execMeanSpectrogramButton.setEnabled(true);
				break;
				
			case MEAN_SPECTROGRAM_ANALYSIS_MODE:
				analysisPanel.add(meanSpectrogramAnalysisPanel, 	"1, 1, f, f");
				execShoFitButton.setEnabled(true);
				viewShoFitResultsButton.setEnabled(true);
				execMeanSpectrogramButton.setEnabled(false);
				break;
				
			case SHO_FIT_ANALYSIS_MODE:
				analysisPanel.add(shoFitAnalysisPanel, 	"1, 1, f, f");
				execShoFitButton.setEnabled(true);
				viewShoFitResultsButton.setEnabled(false);
				execMeanSpectrogramButton.setEnabled(true);
				break;
		
		}
			
		analysisPanel.validate();
		analysisPanel.repaint();
	}

	public void dataFileSelected(){
		setMode(Mode.DATA_FILE_SELECTED_MODE);
	}
	
	public void updateAfterGetSHOFitResults(){
		shoFitAnalysisPanel.setCurrentState();
		setMode(Mode.SHO_FIT_ANALYSIS_MODE);
	}

	public void updateAfterGetMeanSpectrogram(){
		meanSpectrogramAnalysisPanel.setCurrentState();
		setMode(Mode.MEAN_SPECTROGRAM_ANALYSIS_MODE);
	}

	public void exportCurrentImage(){
		if(mode==Mode.MEAN_SPECTROGRAM_ANALYSIS_MODE){
			meanSpectrogramAnalysisPanel.exportCurrentImage();
		}else if(mode==Mode.SHO_FIT_ANALYSIS_MODE){
			shoFitAnalysisPanel.exportCurrentImage();
		}
	}

	public void exportCurrentData(){
		if(mode==Mode.MEAN_SPECTROGRAM_ANALYSIS_MODE){
			meanSpectrogramAnalysisPanel.exportCurrentData();
		}else if(mode==Mode.SHO_FIT_ANALYSIS_MODE){
			shoFitAnalysisPanel.exportCurrentData();
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

}
