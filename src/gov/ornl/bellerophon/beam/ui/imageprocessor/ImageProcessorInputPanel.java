package gov.ornl.bellerophon.beam.ui.imageprocessor;

import gov.ornl.bellerophon.beam.data.feature.ImageProcessorData;
import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.PCAImageCleaningDataSet;
import gov.ornl.bellerophon.beam.enums.AnalysisFunctionType;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import gov.ornl.bellerophon.beam.ui.format.Buttons;
import gov.ornl.bellerophon.beam.ui.format.Colors;
import gov.ornl.bellerophon.beam.ui.util.DataFileTreeNodeSelectionListener;
import gov.ornl.bellerophon.beam.ui.wizard.analysis.ExecuteAnalysisProcessWizard;
import gov.ornl.bellerophon.beam.ui.worker.GetPCAImageCleaningResultsWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetPCASResultsWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetPCAUVResultsWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetPCAImageCleaningDimsWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCAImageCleaningResultsListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCASResultsListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCAUVResultsListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCAImageCleaningDimsListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import hdf.object.HObject;
import hdf.object.h5.H5Group;
import hdf.object.h5.H5ScalarDS;

public class ImageProcessorInputPanel extends JPanel implements DataFileTreeNodeSelectionListener, 
																			ActionListener, 
																			ChangeListener, 
																			GetPCAImageCleaningDimsListener, 
																			GetPCAImageCleaningResultsListener, 
																			GetPCASResultsListener,
																			GetPCAUVResultsListener{

	private Frame frame;
	private ImageProcessorData d;
	private JTabbedPane pane;
	private JPanel pcaImageCleaningPanel;
	private enum PCAImageCleaningPanelMode {DISABLED, REAL, RESULTS};
	private PCAImageCleaningPanelMode pcaImageCleaningPanelMode;
	private String selectedInputPath, selectedDims, selectedDatasetName;
	private ImageProcessorModeListener ipml;
	private GetPCAImageCleaningResultsListener gpicrl;

	private JLabel compLabel, compLabel2, winLabel;
	private JCheckBox compBox, winBox, fastBox;
	private JTextField compField, winField;
	private int compMax, winMax;
	private ImageProcessorDataFilePanel filePanel;
	
	//PCA Panel Components
	private JButton execPCAImageCleaningButton, viewPCAImageCleaningResultsButton;
	
	public ImageProcessorInputPanel(Frame frame, ImageProcessorData d, ImageProcessorModeListener ipml, GetPCAImageCleaningResultsListener gpicrl) {
		
		this.frame = frame;
		this.d = d;
		this.ipml = ipml;
		this.gpicrl = gpicrl;
	
		//PCA Components
		compLabel = new JLabel("Component Limit:");
		compLabel2 = new JLabel("Component Limit:");
		compBox = new JCheckBox("Limit Number of Components?");
		compBox.addActionListener(this);
		compField = new JTextField();
		
		fastBox = new JCheckBox("Use Randomized (\"Fast\") PCA?");
		fastBox.addActionListener(this);
		
		winLabel = new JLabel("Window Size (in pixels):");
		winBox = new JCheckBox("Automatically Set Window Size?");
		winBox.addActionListener(this);
		winField = new JTextField();
		winField.addActionListener(this);
		
		execPCAImageCleaningButton = Buttons.getIconButton("Execute PCA Image Cleaning with HPC"
													, "icons/system-run.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 0);
		
		viewPCAImageCleaningResultsButton = Buttons.getIconButton("View PCA Image Cleaning Results"
													, "icons/system-search.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 10);
		
		pcaImageCleaningPanel = new JPanel();
		double[] columnPCA = {15, TableLayoutConstants.PREFERRED
										, 5, TableLayoutConstants.FILL
										, 5, TableLayoutConstants.FILL, 15};
		double[] rowPCA = {20, TableLayoutConstants.PREFERRED
										, 5, TableLayoutConstants.PREFERRED
										, 15, TableLayoutConstants.PREFERRED
										, 15, TableLayoutConstants.PREFERRED
										, 15, TableLayoutConstants.PREFERRED
										, 5, TableLayoutConstants.PREFERRED
										, 20, TableLayoutConstants.PREFERRED
										, 5, TableLayoutConstants.PREFERRED, 20};
		pcaImageCleaningPanel.setLayout(new TableLayout(columnPCA, rowPCA));
		pcaImageCleaningPanel.add(winBox, 								"1, 1, 5, 1, c, c");
		pcaImageCleaningPanel.add(winLabel, 							"1, 3, r, c");
		pcaImageCleaningPanel.add(winField, 							"3, 3, 5, 3, f, c");
		pcaImageCleaningPanel.add(fastBox, 								"1, 5, 5, 5, c, c");
		pcaImageCleaningPanel.add(compBox, 								"1, 7, 5, 7, c, c");
		pcaImageCleaningPanel.add(compLabel, 							"1, 9, r, c");
		pcaImageCleaningPanel.add(compField, 							"3, 9, 5, 9, f, c");
		pcaImageCleaningPanel.add(compLabel2, 							"1, 11, 5, 11, c, c");
		pcaImageCleaningPanel.add(execPCAImageCleaningButton, 			"1, 13, 5, 13, f, c");
		pcaImageCleaningPanel.add(viewPCAImageCleaningResultsButton, 	"1, 15, 5, 15, f, c");
		
		pane = new JTabbedPane();
		pane.add("PCA Image Cleaning", 		pcaImageCleaningPanel);
		pane.addChangeListener(this);
		
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		add(pane, "1, 1, f, f");
		
	}

	public void setImageProcessorDataFilePanel(ImageProcessorDataFilePanel filePanel){
		this.filePanel = filePanel;
	}
	
	public void dataFileTreeNodeSelected(DefaultMutableTreeNode node) {
		
		pcaImageCleaningPanelMode = PCAImageCleaningPanelMode.DISABLED;
		selectedInputPath = "";
		selectedDatasetName = "";
		selectedDims = "";
		
		d.getDataFile().setSelectedDataset(null);
		d.getDataFile().setSelectedTreeNode(node);
		
		if(node!=null){
			
			if(node.getUserObject() instanceof HObject){
			
				HObject hObject = (HObject) node.getUserObject();
	
				if(hObject instanceof H5ScalarDS){
					
					H5ScalarDS sDS =  (H5ScalarDS) hObject;

					if(sDS.getName().equals("Raw_Data")){
						
						pcaImageCleaningPanelMode = PCAImageCleaningPanelMode.REAL;
						selectedInputPath = sDS.getPath();
						selectedDatasetName = sDS.getName();
						d.getDataFile().setSelectedDataset(sDS);
						
						winBox.setSelected(true);

						PCAImageCleaningDataSet picds = new PCAImageCleaningDataSet();
						picds.setDataFileIndex(d.getDataFile().getIndex());
						picds.setImagePath(selectedInputPath + selectedDatasetName);
						picds.setWindowSize(0);
						d.getDataFile().setPCAImageCleaningDataSet(picds);
						
						GetPCAImageCleaningDimsWorker worker = new GetPCAImageCleaningDimsWorker(this, picds, frame);
						worker.execute();
						
					}
					
				}else if(hObject instanceof H5Group){
				
					H5Group group =  (H5Group) hObject;
					if(group.getName().contains("Image_Windows-PCA_000")){
						
						pcaImageCleaningPanelMode = PCAImageCleaningPanelMode.RESULTS;
						selectedInputPath = group.getPath();
						selectedDatasetName = group.getName();
						
					}
					
					setPCAImageCleaningPanelMode();
					
				}
			
			}else{
				
				setPCAImageCleaningPanelMode();
				
			}
				
		}else{
			
			setPCAImageCleaningPanelMode();
			
		}

	}

	private void setPCAImageCleaningPanelMode(){
		
		switch(pcaImageCleaningPanelMode){
		
			case DISABLED:
				
				winField.setText("");
				winBox.setEnabled(false);
				winBox.setSelected(false);
				winField.setEnabled(false);
				winLabel.setEnabled(false);
				
				compField.setText("");
				compBox.setEnabled(false);
				compBox.setSelected(false);
				fastBox.setEnabled(false);
				fastBox.setSelected(false);
				compField.setEnabled(false);
				compLabel.setEnabled(false);
				compLabel2.setEnabled(false);
				compLabel2.setVisible(false);
				
				execPCAImageCleaningButton.setEnabled(false);
				viewPCAImageCleaningResultsButton.setEnabled(false);
				break;
				
			case REAL:
				
				winBox.setEnabled(true);
				
				winField.setEnabled(!winBox.isSelected());
				winLabel.setEnabled(!winBox.isSelected());
				
				compBox.setEnabled(true);
				compBox.setSelected(false);
				fastBox.setEnabled(true);
				fastBox.setSelected(false);
				compField.setEnabled(false);
				compLabel.setEnabled(false);
				compLabel2.setVisible(true);
				
				execPCAImageCleaningButton.setEnabled(true);
				viewPCAImageCleaningResultsButton.setEnabled(false);
				break;
				
			case RESULTS:
				
				winBox.setEnabled(false);
				winBox.setSelected(false);
				winField.setEnabled(false);
				winLabel.setEnabled(false);
				
				compBox.setEnabled(false);
				compBox.setSelected(false);
				fastBox.setEnabled(false);
				fastBox.setSelected(false);
				compField.setEnabled(false);
				compLabel.setEnabled(false);
				compLabel2.setVisible(false);
				
				execPCAImageCleaningButton.setEnabled(false);
				viewPCAImageCleaningResultsButton.setEnabled(true);
				break;
		
		}
		
		
	}
	
	public void setCurrentState(){

		if(d.getDataFile()==null){
			pcaImageCleaningPanelMode = PCAImageCleaningPanelMode.DISABLED;
			setPCAImageCleaningPanelMode();
		}
		
	}
	
	private boolean goodData(){
		
		if(compBox.isSelected()){
		
			String error = "Please enter an integer value between 1 and " 
							+ compMax 
							+ " for Component Limit.";
			String stringValue = compField.getText();
			
			try{
				if(Integer.valueOf(stringValue)<1 || Integer.valueOf(stringValue)>compMax){
					AttentionDialog.createDialog(frame, error);
					return false;
				}
			}catch(Exception e){
				AttentionDialog.createDialog(frame, error);
				return false;
			}
		
		}
		
		if(winBox.isSelected()){
		
			String error = "Please enter an integer value between 1 and " 
					+ winMax 
					+ " for Window Size.";
			String stringValue = winField.getText();
			
			try{
				if(Integer.valueOf(stringValue)<1 || Integer.valueOf(stringValue)>winMax){
					AttentionDialog.createDialog(frame, error);
					return false;
				}
			}catch(Exception e){
				AttentionDialog.createDialog(frame, error);
				return false;
			}
		
		}
		
		return true;
		
	}
	
	private boolean goodWindowSize(){
		
		if(winBox.isSelected()){
		
			String error = "Please enter an integer value between 1 and " 
					+ winMax 
					+ " for Window Size.";
			String stringValue = winField.getText();
			
			try{
				if(Integer.valueOf(stringValue)<1 || Integer.valueOf(stringValue)>winMax){
					AttentionDialog.createDialog(frame, error);
					return false;
				}
			}catch(Exception e){
				AttentionDialog.createDialog(frame, error);
				return false;
			}
		
		}
		
		return true;
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getSource()==execPCAImageCleaningButton){

			if(goodData()){
				
				AnalysisProcess process = new AnalysisProcess();
				process.setDataFile(d.getDataFile());
				if(fastBox.isSelected()){
					process.setAnalysisFunctionType(AnalysisFunctionType.FAST_PCA_IMAGE_CLEANING);
				}else{
					process.setAnalysisFunctionType(AnalysisFunctionType.PCA_IMAGE_CLEANING);
				}
				process.setInputParameters(getPCAImageCleaningInputParameters());
				boolean processCompleted = ExecuteAnalysisProcessWizard.createExecuteAnalysisProcessWizard(frame, process);
				if(processCompleted){
					d.getDataFile().getPCAImageCleaningDataSet().setGroupPath(selectedInputPath + selectedDatasetName + "-Windowing_000/Image_Windows-PCA_000");
					GetPCASResultsWorker worker = new GetPCASResultsWorker(this, d.getDataFile().getPCAImageCleaningDataSet(), frame);
					worker.execute();
				}
			
			}
			
		}else if(ae.getSource()==viewPCAImageCleaningResultsButton){
			
			PCAImageCleaningDataSet picds = new PCAImageCleaningDataSet();
			picds.setDataFileIndex(d.getDataFile().getIndex());
			picds.setGroupPath(selectedInputPath + selectedDatasetName);
			d.getDataFile().setPCAImageCleaningDataSet(picds);
			
			GetPCASResultsWorker worker = new GetPCASResultsWorker(this, d.getDataFile().getPCAImageCleaningDataSet(), frame);
			worker.execute();

		}else if(ae.getSource()==compBox){
			
			if(compBox.isSelected()){
				
				compField.setEnabled(true);
				compLabel.setEnabled(true);
				compLabel2.setEnabled(true);
				compLabel2.setVisible(true);
				
			}else{
				
				compField.setEnabled(false);
				compLabel.setEnabled(false);
				compLabel2.setEnabled(false);
				compLabel2.setVisible(true);
				
			}
			
		}else if(ae.getSource()==winBox){
			
			if(winBox.isSelected()){
				
				winField.setEnabled(false);
				winLabel.setEnabled(false);
				
				PCAImageCleaningDataSet picds = d.getDataFile().getPCAImageCleaningDataSet();
				picds.setWindowSize(0);
				
				GetPCAImageCleaningDimsWorker worker = new GetPCAImageCleaningDimsWorker(this, picds, frame);
				worker.execute();
				
			}else{
				
				winField.setEnabled(true);
				winLabel.setEnabled(true);
				
				if(goodWindowSize()){
					
					PCAImageCleaningDataSet picds = d.getDataFile().getPCAImageCleaningDataSet();
					picds.setWindowSize(Integer.valueOf(winField.getText()));
					
					GetPCAImageCleaningDimsWorker worker = new GetPCAImageCleaningDimsWorker(this, picds, frame);
					worker.execute();
					
				}
				
			}
			
		}else if(ae.getSource()==winField){
			
			if(goodWindowSize()){
				
				PCAImageCleaningDataSet picds = d.getDataFile().getPCAImageCleaningDataSet();
				picds.setWindowSize(Integer.valueOf(winField.getText()));
				
				GetPCAImageCleaningDimsWorker worker = new GetPCAImageCleaningDimsWorker(this, picds, frame);
				worker.execute();
				
			}
			
		}
		
	}

	private String getPCAImageCleaningInputParameters(){
		
		String string = "";
		
		string += "IMAGE_DATASET_PATH=";
		string += selectedInputPath + selectedDatasetName + "\n";
		
		string += "INPUT_DATASET_PATH=";
		string += selectedInputPath + selectedDatasetName + "-Windowing_000/Image_Windows" + "\n";
		
		string += "DIMS=";
		string += selectedDims + "\n";
		
		if(pcaImageCleaningPanelMode==PCAImageCleaningPanelMode.REAL){
			
			string += "IS_COMPLEX=";
			string += "FALSE\n";
			
			string += "DATA_TYPE=";
			string += "REAL\n";
			
		}
		
		if(compBox.isSelected()){
			
			string += "NUM_COMPS=";
			string += compField.getText() + "\n";
		
		}else{

			string += "NUM_COMPS=";
			string += compMax + "\n";
			
		}
		
		string += "OUTPUT_DATASET_PATH=";
		string += selectedInputPath + selectedDatasetName + "-Windowing_000/Image_Windows-PCA_000" + "\n";
		
		if(!winBox.isSelected()){
			
			string += "WINDOW_SIZE=";
			string += winField.getText() + "\n";
		
		}else{

			string += "WINDOW_SIZE=0";
			
		}
		
		return string;
	}

	public void stateChanged(ChangeEvent ce) {
		
		if(ce.getSource()==pane){
			
			switch(pane.getSelectedIndex()){
			
				case 0:
					ipml.imageProcessorModeChanged(ImageProcessorPanel.Mode.PCA_IMAGE_CLEANING_ANALYSIS_MODE);
					break;
			
			}
			
		}
		
	}

	public void updateAfterGetPCAImageCleaningDims() {
		
		int x = d.getDataFile().getPCAImageCleaningDataSet().getXDim();
		int y = d.getDataFile().getPCAImageCleaningDataSet().getYDim();
		
		selectedDims = y + " " + x;
		compMax = (int) Math.min(x, y);
		compField.setText(String.valueOf(compMax));
		compLabel2.setText("(Enter a value between 1 and " + compMax + ")");

		winMax = (int)Math.sqrt(compMax);
		winField.setText(String.valueOf(winMax));
		
		setPCAImageCleaningPanelMode();
		
	}

	public void updateAfterGetPCAImageCleaningResults() {
		filePanel.reloadTreeModelAfterPCAImageCleaning();
		gpicrl.updateAfterGetPCAImageCleaningResults();
	}

	public void updateAfterGetPCAUVResults() {
		GetPCAImageCleaningResultsWorker worker = new GetPCAImageCleaningResultsWorker(this, d.getDataFile().getPCAImageCleaningDataSet(), frame);
		worker.execute();
	}
	
	public void updateAfterGetPCASResults() {
		d.getDataFile().getPCAImageCleaningDataSet().setMaxComponentIndex(d.getDataFile().getPCAImageCleaningDataSet().getPCADataList().size()-1);
		GetPCAUVResultsWorker worker = new GetPCAUVResultsWorker(this, d.getDataFile().getPCAImageCleaningDataSet(), frame);
		worker.execute();
	}
	
}
