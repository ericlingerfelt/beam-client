package gov.ornl.bellerophon.beam.ui.multivariateanalyzer;

import gov.ornl.bellerophon.beam.data.feature.MultivariateAnalyzerData;
import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.KMeansClusteringDataSet;
import gov.ornl.bellerophon.beam.data.util.PCADataSet;
import gov.ornl.bellerophon.beam.enums.AnalysisFunctionType;
import gov.ornl.bellerophon.beam.enums.PCAValueType;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import gov.ornl.bellerophon.beam.ui.format.Buttons;
import gov.ornl.bellerophon.beam.ui.format.Colors;
import gov.ornl.bellerophon.beam.ui.util.DataFileTreeNodeSelectionListener;
import gov.ornl.bellerophon.beam.ui.wizard.analysis.ExecuteAnalysisProcessWizard;
import gov.ornl.bellerophon.beam.ui.worker.GetKMeansClusteringResultsWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetPCASResultsWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetPCAUVResultsWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetKMeansClusteringResultsListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCASResultsListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCAUVResultsListener;
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

import hdf.object.Datatype;
import hdf.object.HObject;
import hdf.object.h5.H5CompoundDS;
import hdf.object.h5.H5Group;
import hdf.object.h5.H5ScalarDS;

public class MultivariateAnalyzerInputPanel extends JPanel implements DataFileTreeNodeSelectionListener, 
																			ActionListener, 
																			ChangeListener, 
																			GetKMeansClusteringResultsListener,
																			GetPCASResultsListener, 
																			GetPCAUVResultsListener{

	private Frame frame;
	private MultivariateAnalyzerData d;
	private JTabbedPane pane;
	private JPanel pcaPanel, icaPanel, bayesianPanel, kmeansClusteringPanel;
	private enum PCAPanelMode {DISABLED, COMPLEX, REAL, RESULTS};
	private enum KMeansClusteringPanelMode {DISABLED, REAL, RESULTS};
	private PCAPanelMode pcaPanelMode;
	private KMeansClusteringPanelMode kmeansClusteringPanelMode;
	private String selectedInputPath, selectedDims, selectedDatasetName;
	private MultivariateAnalyzerModeListener maml;
	private GetPCASResultsListener gpsrl;
	private GetPCAUVResultsListener gpuvrl;
	private GetKMeansClusteringResultsListener gkmcrl;
	
	//PCA Panel Components
	private JLabel typeLabel, compLabel, compLabel2;
	private JComboBox<PCAValueType> typeBox;
	private JCheckBox compBox, fastBox;
	private JTextField compField;
	private JButton execPCAButton, viewPCAResultsButton;
	private int compMax;
	
	//K Means Panel Components
	private JLabel clusterLabel, compLabelKMeans, compLabelKMeans2;
	private JTextField clusterField;
	private JCheckBox compBoxKMeans;
	private JTextField compFieldKMeans;
	private int compMaxKMeans;
	private JButton execKMeansClusteringButton, viewKMeansClusteringResultsButton;
	private boolean isU;
	
	//ICA components
	private JButton execICAButton, viewICAResultsButton;
	
	//Bayesian components
	private JButton execBayesianButton, viewBayesianResultsButton;
	
	private MultivariateAnalyzerDataFilePanel filePanel;
	private MultivariateAnalyzerMode mode;
	private DefaultMutableTreeNode node;
	
	public MultivariateAnalyzerInputPanel(Frame frame, 
											MultivariateAnalyzerData d,
											MultivariateAnalyzerModeListener maml, 
											GetKMeansClusteringResultsListener gkmcrl,
											GetPCASResultsListener gpsrl, 
											GetPCAUVResultsListener gpuvrl) {
		
		
		this.frame = frame;
		this.d = d;
		this.maml = maml;
		this.gpsrl = gpsrl;
		this.gpuvrl = gpuvrl;
		this.gkmcrl = gkmcrl;
		
		mode = MultivariateAnalyzerMode.PCA_ANALYSIS_MODE;
		
		icaPanel = new JPanel();
		bayesianPanel = new JPanel();
		kmeansClusteringPanel = new JPanel();
		
		//PCA Components
		typeLabel = new JLabel("Apply PCA To:");
		compLabel = new JLabel("Component Limit:");
		compLabel2 = new JLabel("Component Limit:");
		
		typeBox = new JComboBox<PCAValueType>();
		for(PCAValueType type: PCAValueType.values()){
			typeBox.addItem(type);
		}
		
		compBox = new JCheckBox("Limit Number of Components?");
		compBox.addActionListener(this);
		
		fastBox = new JCheckBox("Use Randomized (\"Fast\") PCA?");
		fastBox.addActionListener(this);
		
		compField = new JTextField();
		
		execPCAButton = Buttons.getIconButton("Execute PCA with HPC"
													, "icons/system-run.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 12);
		
		viewPCAResultsButton = Buttons.getIconButton("View PCA Results"
													, "icons/system-search.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 28);
		
		pcaPanel = new JPanel();
		double[] columnPCA = {20, TableLayoutConstants.PREFERRED
										, 5, TableLayoutConstants.FILL
										, 5, TableLayoutConstants.FILL, 20};
		double[] rowPCA = {20, TableLayoutConstants.PREFERRED
										, 15, TableLayoutConstants.PREFERRED
										, 15, TableLayoutConstants.PREFERRED
										, 15, TableLayoutConstants.PREFERRED
										, 15, TableLayoutConstants.PREFERRED
										, 30, TableLayoutConstants.PREFERRED
										, 15, TableLayoutConstants.PREFERRED, 20};
		pcaPanel.setLayout(new TableLayout(columnPCA, rowPCA));
		pcaPanel.add(typeLabel, 			"1, 1, r, c");
		pcaPanel.add(typeBox, 				"3, 1, 5, 1, f, c");
		pcaPanel.add(fastBox, 				"1, 3, 5, 3, c, c");
		pcaPanel.add(compBox, 				"1, 5, 5, 5, c, c");
		pcaPanel.add(compLabel, 			"1, 7, r, c");
		pcaPanel.add(compField, 			"3, 7, 5, 7, f, c");
		pcaPanel.add(compLabel2, 			"1, 9, 5, 9, c, c");
		pcaPanel.add(execPCAButton, 		"1, 11, 5, 11, f, c");
		pcaPanel.add(viewPCAResultsButton, 	"1, 13, 5, 13, f, c");
		
		//K-Means Clustering Components
		clusterLabel = new JLabel("Number of Clusters:");
		clusterField = new JTextField();
		
		compLabelKMeans = new JLabel("Component Limit:");
		compLabelKMeans2 = new JLabel("Component Limit:");
		
		compBoxKMeans = new JCheckBox("Limit Number of Components?");
		compBoxKMeans.addActionListener(this);
		
		compFieldKMeans = new JTextField();
		
		execKMeansClusteringButton = Buttons.getIconButton("Execute K-Means Clustering with HPC"
													, "icons/system-run.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 12);

		viewKMeansClusteringResultsButton = Buttons.getIconButton("View K-Means Clustering Results"
													, "icons/system-search.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 28);
		
		double[] columnKMeansClustering = {20, TableLayoutConstants.PREFERRED
											, 5, TableLayoutConstants.FILL
											, 5, TableLayoutConstants.FILL, 20};
		double[] rowKMeansClustering = {20, TableLayoutConstants.PREFERRED
											, 15, TableLayoutConstants.PREFERRED
											, 15, TableLayoutConstants.PREFERRED
											, 15, TableLayoutConstants.PREFERRED
											, 30, TableLayoutConstants.PREFERRED
											, 15, TableLayoutConstants.PREFERRED, 20};
									
		kmeansClusteringPanel = new JPanel();
		kmeansClusteringPanel.setLayout(new TableLayout(columnKMeansClustering, rowKMeansClustering));
		kmeansClusteringPanel.add(clusterLabel, 						"1, 1, r, c");
		kmeansClusteringPanel.add(clusterField, 						"3, 1, 5, 1, f, c");
		kmeansClusteringPanel.add(compBoxKMeans, 						"1, 3, 5, 3, c, c");
		kmeansClusteringPanel.add(compLabelKMeans, 						"1, 5, r, c");
		kmeansClusteringPanel.add(compFieldKMeans, 						"3, 5, 5, 5, f, c");
		kmeansClusteringPanel.add(compLabelKMeans2, 					"1, 7, 5, 7, c, c");
		kmeansClusteringPanel.add(execKMeansClusteringButton, 			"1, 9, 5, 9, f, c");
		kmeansClusteringPanel.add(viewKMeansClusteringResultsButton, 	"1, 11, 5, 11, f, c");
		
		execKMeansClusteringButton.setEnabled(false);
		viewKMeansClusteringResultsButton.setEnabled(false);
		
		//ICA Components
		execICAButton = Buttons.getIconButton("Execute ICA with HPC"
													, "icons/system-run.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 12);

		viewICAResultsButton = Buttons.getIconButton("View ICA Results"
													, "icons/system-search.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 28);
		
		icaPanel = new JPanel();
		icaPanel.setLayout(new TableLayout(columnPCA, rowPCA));
		icaPanel.add(new JLabel(""), 			"1, 1, r, c");
		icaPanel.add(new JLabel(""), 			"3, 1, 5, 1, f, c");
		icaPanel.add(new JLabel(""), 			"1, 3, 5, 3, c, c");
		icaPanel.add(new JLabel(""), 			"1, 5, r, c");
		icaPanel.add(new JLabel(""), 			"3, 5, 5, 5, f, c");
		icaPanel.add(execICAButton, 		"1, 7, 5, 7, f, c");
		icaPanel.add(viewICAResultsButton, 	"1, 9, 5, 9, f, c");
		
		execICAButton.setEnabled(false);
		viewICAResultsButton.setEnabled(false);
		
		//Bayesian Components
		execBayesianButton = Buttons.getIconButton("Execute Bayesian with HPC"
													, "icons/system-run.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 12);

		viewBayesianResultsButton = Buttons.getIconButton("View Bayesian Results"
													, "icons/system-search.png"
													, Buttons.IconPosition.RIGHT
													, Colors.BLUE
													, this
													, new Dimension(225, 50)
													, 28);
									
		bayesianPanel = new JPanel();
		bayesianPanel.setLayout(new TableLayout(columnPCA, rowPCA));
		bayesianPanel.add(new JLabel(""), 			"1, 1, r, c");
		bayesianPanel.add(new JLabel(""), 			"3, 1, 5, 1, f, c");
		bayesianPanel.add(new JLabel(""), 			"1, 3, 5, 3, c, c");
		bayesianPanel.add(new JLabel(""), 			"1, 5, r, c");
		bayesianPanel.add(new JLabel(""), 			"3, 5, 5, 5, f, c");
		bayesianPanel.add(execBayesianButton, 		"1, 7, 5, 7, f, c");
		bayesianPanel.add(viewBayesianResultsButton, 	"1, 9, 5, 9, f, c");
		
		execBayesianButton.setEnabled(false);
		viewBayesianResultsButton.setEnabled(false);
		
		pane = new JTabbedPane();
		pane.add("PCA", 				pcaPanel);
		pane.add("K-Means Clustering", 	kmeansClusteringPanel);
		pane.add("ICA", 				icaPanel);
		pane.add("Bayesian", 			bayesianPanel);
		pane.addChangeListener(this);
		
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		add(pane, "1, 1, f, f");
		
	}

	public void setMultivariateAnalyzerDataFilePanel(MultivariateAnalyzerDataFilePanel filePanel){
		this.filePanel = filePanel;
	}
	
	public void dataFileTreeNodeSelected(DefaultMutableTreeNode node) {
		
		this.node = node;
		
		if(d.getDataFile()==null){
			return;
		}
		
		switch(mode){
		
			case PCA_ANALYSIS_MODE:
				
				pcaPanelMode = PCAPanelMode.DISABLED;
				
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
							
							if(sDS.getDatatype().getDatatypeClass() == Datatype.CLASS_FLOAT){
								if(sDS.getDims().length>1){
									long x = sDS.getDims()[0];
									long y = sDS.getDims()[1];
									if(x>1 && y>1){
										pcaPanelMode = PCAPanelMode.REAL;
										selectedInputPath = sDS.getPath();
										selectedDatasetName = sDS.getName();
										compMax = (int)Math.min(x, y);
										compField.setText(String.valueOf(compMax));
										compLabel2.setText("(Enter a value between 1 and " + compMax + ")");
										selectedDims = x + " " + y;
										d.getDataFile().setSelectedDataset(sDS);
									}
								}
							}
							
						}else if(hObject instanceof H5CompoundDS){
							
							H5CompoundDS cDS =  (H5CompoundDS) hObject;
							if(cDS.getDatatype().getDatatypeClass() == Datatype.CLASS_COMPOUND){
								if(cDS.getMemberNames()[0].equals("r") && cDS.getMemberNames()[1].equals("i")){
									if(cDS.getDims().length>1){
										long x = cDS.getDims()[0];
										long y = cDS.getDims()[1];
										if(x>1 && y>1){
											pcaPanelMode = PCAPanelMode.COMPLEX;
											selectedInputPath = cDS.getPath();
											selectedDatasetName = cDS.getName();
											compMax = (int)Math.min(x, y);
											compField.setText(String.valueOf(compMax));
											compLabel2.setText("(Enter a value between 1 and " + compMax + ")");
											selectedDims = x + " " + y;
											d.getDataFile().setSelectedDataset(cDS);
										}
									}
								}
							}
							
						}else if(hObject instanceof H5Group){
						
							H5Group group =  (H5Group) hObject;
							if(group.getName().contains("PCA_000")){
								
								pcaPanelMode = PCAPanelMode.RESULTS;
								selectedInputPath = group.getPath();
								selectedDatasetName = group.getName().substring(0, group.getName().length()-8);
								
							}
							
						}
					
					}
						
				}
				
				setPCAPanelMode();
				
				break;
				
			case KMEANS_CLUSTERING_ANALYSIS_MODE:
				
				kmeansClusteringPanelMode = KMeansClusteringPanelMode.DISABLED;
				
				selectedInputPath = "";
				selectedDatasetName = "";
				selectedDims = "";
				isU = false;
				
				d.getDataFile().setSelectedDataset(null);
				d.getDataFile().setSelectedTreeNode(node);
				
				if(node!=null){
					
					if(node.getUserObject() instanceof HObject){
					
						HObject hObject = (HObject) node.getUserObject();

						if(hObject instanceof H5ScalarDS){
							
							H5ScalarDS sDS =  (H5ScalarDS) hObject;
							
							if(sDS.getDatatype().getDatatypeClass() == Datatype.CLASS_FLOAT){
								if(sDS.getDims().length>1){
									long x = sDS.getDims()[0];
									long y = sDS.getDims()[1];
									if(x>1 && y>1){
										kmeansClusteringPanelMode = KMeansClusteringPanelMode.REAL;
										selectedInputPath = sDS.getPath();
										selectedDatasetName = sDS.getName();
										if(selectedDatasetName.equals("U")){
											isU = true;
										}
										compMaxKMeans = (int)Math.min(x, y);
										compFieldKMeans.setText(String.valueOf(compMaxKMeans));
										compLabelKMeans2.setText("(Enter a value between 1 and " + compMaxKMeans + ")");
										selectedDims = x + " " + y;
										d.getDataFile().setSelectedDataset(sDS);
									}
								}
							}
							
						}else if(hObject instanceof H5Group){
						
							H5Group group =  (H5Group) hObject;
							if(group.getName().contains("Cluster_000")){
								
								kmeansClusteringPanelMode = KMeansClusteringPanelMode.RESULTS;
								selectedInputPath = group.getPath();
								selectedDatasetName = group.getName().substring(0, group.getName().length()-12);
								
							}
							
						}
					
					}
						
				}
				
				setKMeansClusteringPanelMode();
				
				break;
		}

	}

	private void setPCAPanelMode(){
		
		switch(pcaPanelMode){
		
			case DISABLED:
				compField.setText("");
				typeBox.setEnabled(false);
				compBox.setEnabled(false);
				compBox.setSelected(false);
				fastBox.setEnabled(false);
				fastBox.setSelected(false);
				compField.setEnabled(false);
				compLabel.setEnabled(false);
				compLabel2.setVisible(false);
				typeLabel.setEnabled(false);
				execPCAButton.setEnabled(false);
				viewPCAResultsButton.setEnabled(false);
				break;
				
			case REAL:
				typeBox.removeAllItems();
				typeBox.addItem(PCAValueType.REAL);
				typeBox.setEnabled(true);
				compBox.setEnabled(true);
				compBox.setSelected(false);
				fastBox.setEnabled(true);
				fastBox.setSelected(false);
				compField.setEnabled(false);
				compLabel.setEnabled(false);
				compLabel2.setVisible(true);
				compLabel2.setEnabled(false);
				typeLabel.setEnabled(true);
				execPCAButton.setEnabled(true);
				viewPCAResultsButton.setEnabled(false);
				break;
				
			case COMPLEX:
				typeBox.removeAllItems();
				for(PCAValueType type: PCAValueType.values()){
					typeBox.addItem(type);
				}
				typeBox.setEnabled(true);
				compBox.setEnabled(true);
				compBox.setSelected(false);
				fastBox.setEnabled(true);
				fastBox.setSelected(false);
				compField.setEnabled(false);
				compLabel.setEnabled(false);
				compLabel2.setVisible(true);
				compLabel2.setEnabled(false);
				typeLabel.setEnabled(true);
				execPCAButton.setEnabled(true);
				viewPCAResultsButton.setEnabled(false);
				break;
				
			case RESULTS:
				typeBox.setEnabled(false);
				compBox.setEnabled(false);
				compBox.setSelected(false);
				fastBox.setEnabled(false);
				fastBox.setSelected(false);
				compField.setEnabled(false);
				compLabel.setEnabled(false);
				compLabel2.setVisible(false);
				typeLabel.setEnabled(false);
				execPCAButton.setEnabled(false);
				viewPCAResultsButton.setEnabled(true);
				break;
		
		}
		
		
	}
	
	private void setKMeansClusteringPanelMode(){
		
		switch(kmeansClusteringPanelMode){
		
			case DISABLED:
				clusterField.setEnabled(false);
				clusterLabel.setEnabled(false);
				compFieldKMeans.setText("");
				compFieldKMeans.setVisible(false);
				compBoxKMeans.setEnabled(false);
				compBoxKMeans.setVisible(false);
				compBoxKMeans.setSelected(false);
				compFieldKMeans.setVisible(false);
				compFieldKMeans.setEnabled(false);
				compLabelKMeans.setEnabled(false);
				compLabelKMeans.setVisible(false);
				compLabelKMeans2.setVisible(false);
				execKMeansClusteringButton.setEnabled(false);
				viewKMeansClusteringResultsButton.setEnabled(false);
				break;
				
			case REAL:
				clusterField.setText("15");
				clusterField.setEnabled(true);
				clusterLabel.setEnabled(true);
				compBoxKMeans.setVisible(isU);
				compBoxKMeans.setEnabled(isU);
				compBoxKMeans.setSelected(false);
				compFieldKMeans.setVisible(isU);
				compFieldKMeans.setEnabled(false);
				compLabelKMeans.setEnabled(false);
				compLabelKMeans.setVisible(isU);
				compLabelKMeans2.setVisible(isU);
				compLabelKMeans2.setEnabled(false);
				execKMeansClusteringButton.setEnabled(true);
				viewKMeansClusteringResultsButton.setEnabled(false);
				break;
				
			case RESULTS:
				clusterField.setEnabled(false);
				clusterLabel.setEnabled(false);
				compBoxKMeans.setEnabled(false);
				compBoxKMeans.setSelected(false);
				compFieldKMeans.setEnabled(false);
				compLabelKMeans.setEnabled(false);
				compLabelKMeans2.setVisible(false);
				execKMeansClusteringButton.setEnabled(false);
				viewKMeansClusteringResultsButton.setEnabled(true);
				break;
		
		}
		
		
	}
	
	public void setCurrentState(){

		if(d.getDataFile()==null){
			
			pcaPanelMode = PCAPanelMode.DISABLED;
			setPCAPanelMode();
			
			kmeansClusteringPanelMode = KMeansClusteringPanelMode.DISABLED;
			setKMeansClusteringPanelMode();
			
		}
		
	}
	
	private boolean goodPCAData(){
		
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
		
		return true;
		
	}
	
	private boolean goodKMeansClusteringData(){
		
		String error = "Please enter an integer value greater than 0 for Number of Clusters.";
		String stringValue = compFieldKMeans.getText();
		
		try{
			if(Integer.valueOf(stringValue)<1){
				AttentionDialog.createDialog(frame, error);
				return false;
			}
		}catch(Exception e){
			AttentionDialog.createDialog(frame, error);
			return false;
		}
		
		return true;
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getSource()==execPCAButton){
			
			if(goodPCAData()){
			
				AnalysisProcess process = new AnalysisProcess();
				process.setDataFile(d.getDataFile());
				if(fastBox.isSelected()){
					process.setAnalysisFunctionType(AnalysisFunctionType.FAST_PCA);
				}else{
					process.setAnalysisFunctionType(AnalysisFunctionType.PCA);
				}
				process.setInputParameters(getPCAInputParameters());
				boolean processCompleted = ExecuteAnalysisProcessWizard.createExecuteAnalysisProcessWizard(frame, process);
				if(processCompleted){
					PCADataSet pds = new PCADataSet();
					pds.setDataFileIndex(d.getDataFile().getIndex());
					pds.setGroupPath(selectedInputPath + selectedDatasetName + "-PCA_000");
					d.getDataFile().setPCADataSet(pds);
					GetPCASResultsWorker worker = new GetPCASResultsWorker(this, d.getDataFile().getPCADataSet(), frame);
					worker.execute();
				}
			
			}
			
		}else if(ae.getSource()==execKMeansClusteringButton){
			
			if(goodKMeansClusteringData()){
				
				AnalysisProcess process = new AnalysisProcess();
				process.setDataFile(d.getDataFile());
				process.setAnalysisFunctionType(AnalysisFunctionType.KMEANS_CLUSTERING);
				process.setInputParameters(getKMeansClusteringInputParameters());
				boolean processCompleted = ExecuteAnalysisProcessWizard.createExecuteAnalysisProcessWizard(frame, process);
				if(processCompleted){
					KMeansClusteringDataSet kmcds = new KMeansClusteringDataSet();
					kmcds.setDataFileIndex(d.getDataFile().getIndex());
					kmcds.setGroupPath(selectedInputPath + selectedDatasetName + "-Cluster_000");
					d.getDataFile().setKMeansClusteringDataSet(kmcds);
					GetKMeansClusteringResultsWorker worker = new GetKMeansClusteringResultsWorker(this, d.getDataFile().getKMeansClusteringDataSet(), frame);
					worker.execute();
				}
			
			}
			
		}else if(ae.getSource()==viewPCAResultsButton){
			
			PCADataSet pds = new PCADataSet();
			pds.setDataFileIndex(d.getDataFile().getIndex());
			pds.setGroupPath(selectedInputPath + selectedDatasetName + "-PCA_000");
			d.getDataFile().setPCADataSet(pds);
			GetPCASResultsWorker worker = new GetPCASResultsWorker(this, d.getDataFile().getPCADataSet(), frame);
			worker.execute();
			
		}else if(ae.getSource()==viewKMeansClusteringResultsButton){
			
			KMeansClusteringDataSet kcds = new KMeansClusteringDataSet();
			kcds.setDataFileIndex(d.getDataFile().getIndex());
			kcds.setGroupPath(selectedInputPath + selectedDatasetName + "-Cluster_000");
			d.getDataFile().setKMeansClusteringDataSet(kcds);
			GetKMeansClusteringResultsWorker worker = new GetKMeansClusteringResultsWorker(this, d.getDataFile().getKMeansClusteringDataSet(), frame);
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
			
		}else if(ae.getSource()==compBoxKMeans){
			
			if(compBoxKMeans.isSelected()){
				
				compFieldKMeans.setEnabled(true);
				compLabelKMeans.setEnabled(true);
				compLabelKMeans2.setEnabled(true);
				compLabelKMeans2.setVisible(true);
				
			}else{
				
				compFieldKMeans.setEnabled(false);
				compLabelKMeans.setEnabled(false);
				compLabelKMeans2.setEnabled(false);
				compLabelKMeans2.setVisible(true);
				
			}
			
		}
		
	}

	private String getKMeansClusteringInputParameters(){
		
		String string = "";
		
		string += "INPUT_DATASET_PATH=";
		string += selectedInputPath + selectedDatasetName + "\n";
		
		string += "NUM_CLUSTERS=";
		string += clusterField.getText() + "\n";
		
		if(compBoxKMeans.isSelected()){
			
			string += "NUM_COMPS=";
			string += compFieldKMeans.getText() + "\n";
		
		}else{

			string += "NUM_COMPS=";
			string += compMaxKMeans + "\n";
			
		}
		
		return string;
	}
	
	private String getPCAInputParameters(){
		
		String string = "";
		
		string += "INPUT_DATASET_PATH=";
		string += selectedInputPath + selectedDatasetName + "\n";
		
		string += "DIMS=";
		string += selectedDims + "\n";
		
		if(pcaPanelMode==PCAPanelMode.COMPLEX){
			
			string += "IS_COMPLEX=";
			string += "TRUE\n";
			
			string += "DATA_TYPE=";
			string += ((PCAValueType) typeBox.getSelectedItem()).name() + "\n";
			
		}else if(pcaPanelMode==PCAPanelMode.REAL){
			
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
		string += selectedInputPath + selectedDatasetName + "-PCA_000\n";
		
		return string;
	}

	public void stateChanged(ChangeEvent ce) {
		
		if(ce.getSource()==pane){
			
			switch(pane.getSelectedIndex()){
			
				case 0:
					mode = MultivariateAnalyzerMode.PCA_ANALYSIS_MODE;
					break;
					
				case 1:
					mode = MultivariateAnalyzerMode.KMEANS_CLUSTERING_ANALYSIS_MODE;
					break;
					
				case 2:
					mode = MultivariateAnalyzerMode.ICA_ANALYSIS_MODE;
					break;
					
				case 3:
					mode = MultivariateAnalyzerMode.BAYESIAN_ANALYSIS_MODE;
					break;
					
			}
			
			maml.multivariateAnalyzerModeChanged(mode);
			
			dataFileTreeNodeSelected(node);
			
		}
		
	}

	public void updateAfterGetPCASResults() {
		if(d.getDataFile().getPCADataSet().getPCADataList().size()<=20){
			d.getDataFile().getPCADataSet().setMaxComponentIndex(d.getDataFile().getPCADataSet().getPCADataList().size()-1);
		}else{
			d.getDataFile().getPCADataSet().setMaxComponentIndex(19);
		}
		gpsrl.updateAfterGetPCASResults();
		GetPCAUVResultsWorker worker = new GetPCAUVResultsWorker(this, d.getDataFile().getPCADataSet(), frame);
		worker.execute();
	}

	public void updateAfterGetPCAUVResults() {
		filePanel.reloadTreeModelAfterPCA();
		gpuvrl.updateAfterGetPCAUVResults();
	}

	public void updateAfterGetKMeansClusteringResults() {
		filePanel.reloadTreeModelAfterKMeansClustering();
		gkmcrl.updateAfterGetKMeansClusteringResults();
	}
	
}
