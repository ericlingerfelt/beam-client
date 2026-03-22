package gov.ornl.bellerophon.beam.ui.wizard.analysis;

import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.WorkflowUpdate;
import gov.ornl.bellerophon.beam.enums.*;
import gov.ornl.bellerophon.beam.ui.dialog.PlatformAuthenticationDialog;
import gov.ornl.bellerophon.beam.ui.format.Calendars;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import gov.ornl.bellerophon.beam.ui.worker.AbortWorkflowWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetWorkflowUpdatesWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.AbortWorkflowListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetWorkflowUpdatesListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.*;


public class ExecuteAnalysisProcessPanel extends JPanel implements ActionListener
																, GetWorkflowUpdatesListener
																, AbortWorkflowListener{
	
	private AnalysisProcess process;
	private JButton abortButton, resultsButton;
	private JTextArea area;
	private WordWrapLabel topLabel, dataFileValueLabel;
	private JLabel functionValueLabel
					, implementationValueLabel
					, allocationValueLabel
					, facilityValueLabel
					, numNodesValueLabel
					, numCoresUsedValueLabel
					, numCoresValueLabel
					, platformValueLabel;
	private ExecuteAnalysisProcessWizard owner;
	private Timer timer = new Timer(0, this);
	private int lastWorkflowStatusIndex;
	
	public ExecuteAnalysisProcessPanel(ExecuteAnalysisProcessWizard owner){

		this.owner = owner;
		
		topLabel = new WordWrapLabel(true);
		
		abortButton = new JButton("Abort Analysis Process");
		abortButton.addActionListener(this);
		
		resultsButton = new JButton("View Analysis Results");
		resultsButton.addActionListener(this);
		
		area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(true);
		JScrollPane sp = new JScrollPane(area);
		
		JLabel functionLabel = new JLabel("Analysis Function:");
		JLabel implementationLabel = new JLabel("Function Implementation:");
		JLabel platformLabel = new JLabel("Computing Resource:");
		JLabel facilityLabel = new JLabel("User Facility:");
		JLabel allocationLabel = new JLabel("Computing Allocation:");
		JLabel numNodesLabel = new JLabel("Number of Compute Nodes:");
		JLabel numCoresLabel = new JLabel("Number of CPU Cores per Node:");
		JLabel numCoresUsedLabel = new JLabel("Total Number of CPU Cores:");
		WordWrapLabel dataFileLabel = new WordWrapLabel(true);
		dataFileLabel.setText("Selected Data File:");
		
		JLabel statusLabel = new JLabel("<html><b>Execution Status</b></html>");
		
		functionValueLabel = new JLabel();
		dataFileValueLabel = new WordWrapLabel(true);
		platformValueLabel = new JLabel();
		facilityValueLabel = new JLabel();
		numCoresValueLabel = new JLabel();
		numCoresUsedValueLabel = new JLabel();
		implementationValueLabel = new JLabel();
		allocationValueLabel = new JLabel();
		numNodesValueLabel = new JLabel();
		
		JPanel dataFilePanel = new JPanel();
		double[] columnDataFile = {TableLayoutConstants.PREFERRED
									, 7, TableLayoutConstants.PREFERRED};
		double[] rowDataFile = {TableLayoutConstants.PREFERRED};
		dataFilePanel.setLayout(new TableLayout(columnDataFile, rowDataFile));
		dataFilePanel.add(dataFileLabel, 			"0, 0, r, c");
		dataFilePanel.add(dataFileValueLabel, 		"2, 0, l, c");
		
		JPanel valuePanel = new JPanel();
		double[] columnValue = {TableLayoutConstants.PREFERRED
								, 7, TableLayoutConstants.PREFERRED
								, 30, TableLayoutConstants.PREFERRED
								, 7, TableLayoutConstants.PREFERRED};
		double[] rowValue = {TableLayoutConstants.PREFERRED
								, 20, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		valuePanel.setLayout(new TableLayout(columnValue, rowValue));
		valuePanel.add(dataFilePanel, 			"0, 0, 6, 0, c, c");
		valuePanel.add(functionLabel, 			"0, 2, r, c");
		valuePanel.add(functionValueLabel, 		"2, 2, l, c");
		valuePanel.add(implementationLabel, 	"0, 4, r, c");
		valuePanel.add(implementationValueLabel,"2, 4, l, c");
		valuePanel.add(platformLabel, 			"0, 6, r, c");
		valuePanel.add(platformValueLabel, 		"2, 6, l, c");
		valuePanel.add(facilityLabel, 			"0, 8, r, c");
		valuePanel.add(facilityValueLabel, 		"2, 8, l, c");
		valuePanel.add(allocationLabel, 		"4, 2, r, c");
		valuePanel.add(allocationValueLabel, 	"6, 2, l, c");
		valuePanel.add(numNodesLabel, 			"4, 4, r, c");
		valuePanel.add(numNodesValueLabel, 		"6, 4, l, c");
		valuePanel.add(numCoresLabel, 			"4, 6, r, c");
		valuePanel.add(numCoresValueLabel, 		"6, 6, l, c");
		valuePanel.add(numCoresUsedLabel, 		"4, 8, r, c");
		valuePanel.add(numCoresUsedValueLabel,	"6, 8, l, c");
		
		double[] column = {20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
						, 20, TableLayoutConstants.PREFERRED
						, 30, TableLayoutConstants.PREFERRED
						, 10, TableLayoutConstants.FILL
						, 20, TableLayoutConstants.PREFERRED, 20};

		setLayout(new TableLayout(column, row));
		add(topLabel, 		"1, 1, c, c");
		add(valuePanel, 	"1, 3, c, c");
		add(statusLabel, 	"1, 5, l, c");
		add(sp, 			"1, 7, f, f");
		add(abortButton, 	"1, 9, c, c");
		
	}

	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==abortButton){
			owner.abortApplyAnalysisFunctionWorker();
			if(process.getAnalysisFunction().getAnalysisPlatform().getName().equals("Titan")
					|| process.getAnalysisFunction().getAnalysisPlatform().getName().equals("Rhea")
					|| process.getAnalysisFunction().getAnalysisPlatform().getName().equals("Eos")){
				int returnValue = PlatformAuthenticationDialog.createPlatformAuthenticationDialog(owner, process, "Passcode");
				if(returnValue==PlatformAuthenticationDialog.SUBMIT){
					AbortWorkflowWorker worker = new AbortWorkflowWorker(this, process, owner);
					worker.execute();
				}
			}else{
				AbortWorkflowWorker worker = new AbortWorkflowWorker(this, process, owner);
				worker.execute();
			}
		}else if(ae.getSource()==resultsButton){
			owner.setVisible(false);
		}else if(ae.getSource()==timer){
			GetWorkflowUpdatesWorker worker = new GetWorkflowUpdatesWorker(this, process, owner);
			worker.execute();
		}
	}
	
	public void setCurrentState(AnalysisProcess process){
		this.process = process;
		lastWorkflowStatusIndex = -1;
		area.setText("");
		topLabel.setText("The selected analysis process is now being executed.");
		functionValueLabel.setText(process.getAnalysisFunction().toString());
		dataFileValueLabel.setText(process.getDataFile().getFullPath());
		platformValueLabel.setText(process.getAnalysisFunction().getAnalysisPlatform().toString());
		facilityValueLabel.setText(process.getAnalysisFunction().getAnalysisPlatform().getUserFacility().toString());
		implementationValueLabel.setText(process.getAnalysisFunction().getAnalysisFunctionImplementation().toString());
		allocationValueLabel.setText(process.getAllocation().toString());
		numNodesValueLabel.setText(String.valueOf(process.getNumNodes()));
		numCoresValueLabel.setText(String.valueOf(process.getNumCores()));
		numCoresUsedValueLabel.setText(String.valueOf(process.getNumCoresUsed()));
		remove(resultsButton);
		abortButton.setEnabled(true);
		add(abortButton, 	"1, 9, c, c");
		validate();
	}
	
	public void processCompleted(){
		owner.processCompletedSuccessfully = true;
		process.setExecuting(false);
		timer.stop();
		DecimalFormat df = new DecimalFormat("0.#########");
		Calendar startDate = process.getStatusMap().firstEntry().getValue().getCreateDate();
		Calendar endDate = process.getStatusMap().lastEntry().getValue().getCreateDate();
		double time = (endDate.getTimeInMillis() - startDate.getTimeInMillis()) / 1000.0;
		process.clearStatusMap();
		topLabel.setText("The selected analysis process has been completed.");
		area.append(process.getAnalysisFunction() + " analysis process completed in "+ df.format(time) + " seconds!\n");
		remove(abortButton);
		add(resultsButton, 	"1, 9, c, c");
		validate();
		repaint();
	}

	public void processDied(){
		owner.processCompletedSuccessfully = false;
		process.setExecuting(false);
		timer.stop();
		process.clearStatusMap();
		topLabel.setText("The selected analysis process has encountered an error. The BEAM administrator has been notified.");
		remove(abortButton);
		validate();
		repaint();
	}
	
	public void startWorkflowStatusUpdating(){
		timer.setDelay(2000);
		timer.start();
	}

	public void updateAfterGetWorkflowUpdates(){
		Iterator<WorkflowUpdate> itr = process.getStatusMap().values().iterator();
		while(itr.hasNext()){
			WorkflowUpdate ws = itr.next();
			if(ws.getIndex()>lastWorkflowStatusIndex){
				lastWorkflowStatusIndex = ws.getIndex();
				if(ws.getType()==WorkflowUpdateType.STATE && ws.getValue().equals("COMPLETE")){
					processCompleted();
					return;
				}else if(ws.getType()==WorkflowUpdateType.ERROR){
					area.append(Calendars.getFormattedOutputDateString(ws.getCreateDate()) + " | An error has been reported. The BEAM administrator has been notified.\n");
					processDied();
					return;
				}else if(ws.getType()==WorkflowUpdateType.MESSAGE){
					area.append(Calendars.getFormattedOutputDateString(ws.getCreateDate()) + " | " + ws.getValue() + "\n");
				}
			}
		}
	}

	public void updateAfterAbortWorkflow(){
		process.setExecuting(false);
		timer.stop();
		topLabel.setText("The selected analysis process has been aborted.");
		area.append("\n" + process.getAnalysisFunction() + " analysis process has been aborted!\n");
		abortButton.setEnabled(false);
	}
	
}