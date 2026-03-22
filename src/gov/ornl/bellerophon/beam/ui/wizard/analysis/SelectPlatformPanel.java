package gov.ornl.bellerophon.beam.ui.wizard.analysis;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.Allocation;
import gov.ornl.bellerophon.beam.data.util.AnalysisFunction;
import gov.ornl.bellerophon.beam.data.util.AnalysisPlatform;
import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.enums.AnalysisFunctionImplementation;
import gov.ornl.bellerophon.beam.enums.AnalysisFunctionType;
import gov.ornl.bellerophon.beam.enums.UserFacility;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SelectPlatformPanel extends JPanel implements ActionListener, ChangeListener{
	
	private JComboBox<AnalysisPlatform> platformBox;
	private JComboBox<Allocation> allocationBox;
	private JComboBox<AnalysisFunctionImplementation> implementationBox;
	private JSpinner numNodesSpinner, numCoresSpinner;
	private SpinnerNumberModel numNodesSpinnerModel, numCoresSpinnerModel;
	private AnalysisProcess process;
	private JLabel functionValueLabel, typeValueLabel, facilityValueLabel;
	//private JTextField numCoresField;
	private WordWrapLabel dataFileValueLabel;
	
	public SelectPlatformPanel(){

		WordWrapLabel topLabel = new WordWrapLabel(true);
		topLabel.setText("Please select options for your analysis process below and click <i>Continue</i> to execute the selected analysis process.");
		
		implementationBox = new JComboBox<AnalysisFunctionImplementation>(); 
		platformBox = new JComboBox<AnalysisPlatform>();
		allocationBox = new JComboBox<Allocation>();
		
		numNodesSpinnerModel = new SpinnerNumberModel();
		numNodesSpinner = new JSpinner(numNodesSpinnerModel);
		((JSpinner.DefaultEditor) numNodesSpinner.getEditor()).getTextField().setEditable(false);
		
		numCoresSpinnerModel = new SpinnerNumberModel();
		numCoresSpinner = new JSpinner(numCoresSpinnerModel);
		((JSpinner.DefaultEditor) numCoresSpinner.getEditor()).getTextField().setEditable(false);
		
		dataFileValueLabel = new WordWrapLabel(true);
		
		WordWrapLabel dataFileLabel = new WordWrapLabel(true);
		dataFileLabel.setText("Selected Data File:");
		
		JPanel dataFilePanel = new JPanel();
		double[] columnDataFile = {TableLayoutConstants.PREFERRED
									, 7, TableLayoutConstants.PREFERRED};
		double[] rowDataFile = {TableLayoutConstants.PREFERRED};
		dataFilePanel.setLayout(new TableLayout(columnDataFile, rowDataFile));
		dataFilePanel.add(dataFileLabel, 			"0, 0, r, c");
		dataFilePanel.add(dataFileValueLabel, 		"2, 0, l, c");
		
		JLabel functionLabel = new JLabel("Analysis Function:");
		JLabel implementationLabel = new JLabel("Function Implementation:");
		JLabel platformLabel = new JLabel("Computing Resource:");
		JLabel typeLabel = new JLabel("Computing Resource Type:");
		JLabel facilityLabel = new JLabel("User Facility:");
		JLabel allocationLabel = new JLabel("Computing Allocation:");
		JLabel numNodesLabel = new JLabel("Number of Compute Nodes:");
		JLabel numCoresLabel = new JLabel("Number of CPU Cores per Node:");
		
		functionValueLabel = new JLabel();
		typeValueLabel = new JLabel();
		facilityValueLabel = new JLabel();
		
		JPanel valuePanel = new JPanel();
		double[] columnValue = {TableLayoutConstants.FILL
								, 15, TableLayoutConstants.FILL};
		double[] rowValue = {TableLayoutConstants.PREFERRED
								, 15, TableLayoutConstants.PREFERRED
								, 15, TableLayoutConstants.PREFERRED
								, 15, TableLayoutConstants.PREFERRED
								, 15, TableLayoutConstants.PREFERRED
								, 15, TableLayoutConstants.PREFERRED
								, 15, TableLayoutConstants.PREFERRED
								, 15, TableLayoutConstants.PREFERRED};
		valuePanel.setLayout(new TableLayout(columnValue, rowValue));
		valuePanel.add(functionLabel, 		"0, 0, r, c");
		valuePanel.add(functionValueLabel, 	"2, 0, l, c");
		valuePanel.add(platformLabel, 		"0, 2, r, c");
		valuePanel.add(platformBox, 		"2, 2, f, c");
		valuePanel.add(typeLabel, 			"0, 4, r, c");
		valuePanel.add(typeValueLabel, 		"2, 4, f, c");
		valuePanel.add(facilityLabel, 		"0, 6, r, c");
		valuePanel.add(facilityValueLabel, 	"2, 6, f, c");
		valuePanel.add(allocationLabel, 	"0, 8, r, c");
		valuePanel.add(allocationBox, 		"2, 8, f, c");
		valuePanel.add(implementationLabel, "0, 10, r, c");
		valuePanel.add(implementationBox, 	"2, 10, f, c");
		valuePanel.add(numNodesLabel, 		"0, 12, r, c");
		valuePanel.add(numNodesSpinner, 	"2, 12, f, c");
		valuePanel.add(numCoresLabel, 		"0, 14, r, c");
		valuePanel.add(numCoresSpinner, 	"2, 14, f, c");
		
		double[] column = {20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
						, 20, TableLayoutConstants.PREFERRED
						, 30, TableLayoutConstants.PREFERRED, 20};

		setLayout(new TableLayout(column, row));
		add(topLabel, 		"1, 1, c, c");
		add(dataFilePanel,	"1, 3, c, c");
		add(valuePanel, 	"1, 5, c, c");
		
	}

	public void stateChanged(ChangeEvent ce){
		if(ce.getSource()==numNodesSpinner){
			//AnalysisPlatform platform = (AnalysisPlatform) platformBox.getSelectedItem();
			//numCoresField.setText(String.valueOf((int)numNodesSpinnerModel.getValue() * platform.getNumCoresPerNode()));
		}
	}
	
	public void actionPerformed(ActionEvent ae){
		
		if(ae.getSource()==platformBox){
			
			AnalysisPlatform platform = (AnalysisPlatform) platformBox.getSelectedItem();
			UserFacility facility = platform.getUserFacility();
			
			typeValueLabel.setText(platform.getAnalysisPlatformType().toString());
			facilityValueLabel.setText(facility.toString());
			
			allocationBox.removeAllItems();
			Iterator<Allocation> itr = MainData.getUser().getAllocationMap().values().iterator();
			while(itr.hasNext()){
				Allocation allocation = itr.next();
				if(facility==allocation.getUserFacility()){
					allocationBox.addItem(allocation);
				}
			}
			allocationBox.setSelectedIndex(0);
			
			ArrayList<AnalysisFunctionImplementation> implementationList = getImplementationList(process.getAnalysisFunctionType(), platform);
			implementationBox.removeAllItems();
			for(AnalysisFunctionImplementation afi: implementationList){
				implementationBox.addItem(afi);
			}
			implementationBox.setSelectedIndex(0);
			
			numNodesSpinner.removeChangeListener(this);
			numNodesSpinnerModel.setMinimum(1);
			numNodesSpinnerModel.setMaximum(platform.getNumNodesMax());
			numNodesSpinnerModel.setStepSize(1);
			numNodesSpinnerModel.setValue(1);
			numNodesSpinner.addChangeListener(this);
			
			numCoresSpinner.removeChangeListener(this);
			numCoresSpinnerModel.setMinimum(1);
			numCoresSpinnerModel.setMaximum((int)numNodesSpinnerModel.getValue() * platform.getNumCoresPerNode());
			numCoresSpinnerModel.setStepSize(1);
			numCoresSpinnerModel.setValue(1);
			numCoresSpinner.addChangeListener(this);
			
		}
	}
	
	public void setCurrentState(AnalysisProcess process){
		
		this.process = process;

		functionValueLabel.setText(process.getAnalysisFunctionType().toString());
		dataFileValueLabel.setText(process.getDataFile().getFullPath());
		
		ArrayList<AnalysisPlatform> platformList = new ArrayList<AnalysisPlatform>();
		ArrayList<AnalysisFunction> functionList = MainData.getAnalysisFunctions(process.getAnalysisFunctionType());
		for(AnalysisFunction af: functionList){
			if(!platformList.contains(af.getAnalysisPlatform())){
				platformList.add(af.getAnalysisPlatform());
			}
		}
		
		platformBox.removeActionListener(this);
		platformBox.removeAllItems();
		for(AnalysisPlatform ap: platformList){
			Iterator<Allocation> itr = MainData.getUser().getAllocationMap().values().iterator();
			while(itr.hasNext()){
				Allocation allocation = itr.next();
				if(ap.getUserFacility()==allocation.getUserFacility()){
					platformBox.addItem(ap);
					break;
				}
			}
		}
		platformBox.setSelectedIndex(0);
		platformBox.addActionListener(this);
		
		AnalysisPlatform platform = (AnalysisPlatform) platformBox.getSelectedItem();
		UserFacility facility = platform.getUserFacility();
		
		typeValueLabel.setText(platform.getAnalysisPlatformType().toString());
		facilityValueLabel.setText(facility.toString());
		
		allocationBox.removeAllItems();
		Iterator<Allocation> itr = MainData.getUser().getAllocationMap().values().iterator();
		while(itr.hasNext()){
			Allocation allocation = itr.next();
			if(facility==allocation.getUserFacility()){
				allocationBox.addItem(allocation);
			}
		}
		allocationBox.setSelectedIndex(0);
		
		ArrayList<AnalysisFunctionImplementation> implementationList = getImplementationList(process.getAnalysisFunctionType(), platform);
		implementationBox.removeAllItems();
		for(AnalysisFunctionImplementation afi: implementationList){
			implementationBox.addItem(afi);
		}
		implementationBox.setSelectedIndex(0);
		
		numNodesSpinner.removeChangeListener(this);
		numNodesSpinnerModel.setMinimum(1);
		numNodesSpinnerModel.setMaximum(platform.getNumNodesMax());
		numNodesSpinnerModel.setStepSize(1);
		numNodesSpinnerModel.setValue(1);
		numNodesSpinner.addChangeListener(this);
		
		numCoresSpinner.removeChangeListener(this);
		numCoresSpinnerModel.setMinimum(1);
		numCoresSpinnerModel.setMaximum((int)numNodesSpinnerModel.getValue() * platform.getNumCoresPerNode());
		numCoresSpinnerModel.setStepSize(1);
		numCoresSpinnerModel.setValue(1);
		numCoresSpinner.addChangeListener(this);
	}
	
	private ArrayList<AnalysisFunctionImplementation> getImplementationList(AnalysisFunctionType type, AnalysisPlatform platform){
		ArrayList<AnalysisFunctionImplementation> list = new ArrayList<AnalysisFunctionImplementation>();
		ArrayList<AnalysisFunction> functionList = MainData.getAnalysisFunctions(process.getAnalysisFunctionType());
		for(AnalysisFunction af: functionList){
			if(af.getAnalysisPlatform()==platform 
					&& af.getAnalysisFunctionType()==type
					&& !list.contains(af.getAnalysisFunctionImplementation())){
				list.add(af.getAnalysisFunctionImplementation());
			}
		}
		return list;
	}
	
	public void getCurrentState(){
		
		AnalysisPlatform platform = (AnalysisPlatform) platformBox.getSelectedItem();
		AnalysisFunctionImplementation implementation = (AnalysisFunctionImplementation) implementationBox.getSelectedItem();
		ArrayList<AnalysisFunction> functionList = MainData.getAnalysisFunctions(process.getAnalysisFunctionType());
		AnalysisFunction analysisFunction = null;
		for(AnalysisFunction af: functionList){
			if(af.getAnalysisFunctionType()==process.getAnalysisFunctionType()
					&& af.getAnalysisPlatform()==platform
					&& af.getAnalysisFunctionImplementation()==implementation){
				analysisFunction = af;
			}
		}
		process.setAnalysisFunction(analysisFunction);
		
		process.setNumNodes((int)numNodesSpinnerModel.getValue());
		process.setNumCores((int)numNodesSpinnerModel.getValue() * (int)numCoresSpinnerModel.getValue());
		
		/*if((analysisFunction.getAnalysisFunctionType()==AnalysisFunctionType.PCA || analysisFunction.getAnalysisFunctionType()==AnalysisFunctionType.FAST_PCA)
				&& analysisFunction.getAnalysisFunctionImplementation()==AnalysisFunctionImplementation.FORTRAN){
			
			int xDim = (int) process.getDataFile().getSelectedDataset().getDims()[0];
			int yDim = (int) process.getDataFile().getSelectedDataset().getDims()[1];
			int numComponents = (int)Math.min(xDim, yDim);
			
			int maxNumCores = (int)numNodesSpinnerModel.getValue() * platform.getNumCoresPerNode();
			int numCores = 1;
			for(int i=maxNumCores; i>0; i--){
				if((numComponents % i) == 0){
					numCores = i;
					break;
				}
			}
			process.setNumCoresUsed(numCores);
			
		}else if((analysisFunction.getAnalysisFunctionType()==AnalysisFunctionType.PCA_IMAGE_CLEANING || analysisFunction.getAnalysisFunctionType()==AnalysisFunctionType.FAST_PCA_IMAGE_CLEANING)
				&& analysisFunction.getAnalysisFunctionImplementation()==AnalysisFunctionImplementation.FORTRAN){
			
			int xDim = (int) process.getDataFile().getPCAImageCleaningDataSet().getXDim();
			int yDim = (int) process.getDataFile().getPCAImageCleaningDataSet().getYDim();
			int numComponents = (int) Math.min(xDim, yDim);
			
			int maxNumCores = (int)numNodesSpinnerModel.getValue() * platform.getNumCoresPerNode();
			int numCores = 1;
			for(int i=maxNumCores; i>0; i--){
				if((numComponents % i) == 0){
					numCores = i;
					break;
				}
			}
			process.setNumCoresUsed(numCores);
			
		}else{*/
			process.setNumCoresUsed((int)numNodesSpinnerModel.getValue() * (int)numCoresSpinnerModel.getValue());
		//}
		
		process.setAllocation((Allocation) allocationBox.getSelectedItem());
	}

}