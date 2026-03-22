package gov.ornl.bellerophon.beam.ui.qensoptimizer;

import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.WorkflowUpdate;
import gov.ornl.bellerophon.beam.enums.WorkflowUpdateType;
import gov.ornl.bellerophon.beam.ui.format.Calendars;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import gov.ornl.bellerophon.beam.ui.worker.AbortWorkflowWorker;
import gov.ornl.bellerophon.beam.ui.worker.ExecuteQENSOptimizationWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetWorkflowUpdatesWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.AbortWorkflowListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.ExecuteQENSOptimizationListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetWorkflowUpdatesListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.*;

public class QENSOptimizerStatusDialog extends JDialog implements ActionListener, 
															ExecuteQENSOptimizationListener, 
															AbortWorkflowListener, 
															GetWorkflowUpdatesListener{

	private static Dimension SIZE = new Dimension(850, 625);
	private AnalysisProcess process;
	private Frame owner;
	private ExecuteQENSOptimizationWorker executeSNSSimulationWorker;
	private Timer timer = new Timer(0, this);
	private int lastWorkflowStatusIndex;
	private JButton abortButton, resultsButton;
	private JTextArea area;
	private WordWrapLabel topLabel;
	private boolean processCompleted = false;
	
	public QENSOptimizerStatusDialog(Frame owner, AnalysisProcess process){
		
		super(owner, "Execute QENS Optimization");
		this.owner = owner;
		this.process = process;
		
		setSize(SIZE);
		setLocationRelativeTo(owner);
		
		topLabel = new WordWrapLabel(true);
		
		abortButton = new JButton("Abort QENS Optimization Process");
		abortButton.addActionListener(this);
		
		resultsButton = new JButton("View QENS Optimization Results");
		resultsButton.addActionListener(this);
		
		area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(true);
		JScrollPane sp = new JScrollPane(area);
		
		JLabel statusLabel = new JLabel("<html><b>QENS Optimization Status</b></html>");
		
		double[] column = {20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
						, 30, TableLayoutConstants.PREFERRED
						, 10, TableLayoutConstants.FILL
						, 20, TableLayoutConstants.PREFERRED, 20};
		setLayout(new TableLayout(column, row));
		add(topLabel, 		"1, 1, c, c");
		add(statusLabel, 	"1, 3, l, c");
		add(sp, 			"1, 5, f, f");
		add(abortButton, 	"1, 7, c, c");
		
		ExecuteQENSOptimizationWorker worker  = new ExecuteQENSOptimizationWorker(this, process, owner);
		worker.execute();
	}
	
	public static boolean createSNSSimulatorStatusDialog(Frame owner, AnalysisProcess process){
		QENSOptimizerStatusDialog dialog = new QENSOptimizerStatusDialog(owner, process);
		dialog.setVisible(true);
		return dialog.processCompleted;
	}

	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==abortButton){
			abortExecuteSNSSimulationWorker();
			AbortWorkflowWorker worker = new AbortWorkflowWorker(this, process, owner);
			worker.execute();
		}else if(ae.getSource()==resultsButton){
			owner.setVisible(false);
		}else if(ae.getSource()==timer){
			GetWorkflowUpdatesWorker worker = new GetWorkflowUpdatesWorker(this, process, owner);
			worker.execute();
		}
	}

	public void updateAfterExecuteQENSOptimization(){
		process.setExecuting(true);
		process.clearStatusMap();
		startWorkflowStatusUpdating();
		lastWorkflowStatusIndex = -1;
		area.setText("");
		topLabel.setText("The QENS optimization workflow is now being executed.");
		remove(resultsButton);
		abortButton.setEnabled(true);
		add(abortButton, 	"1, 7, c, c");
		validate();
	}
	
	public void abortExecuteSNSSimulationWorker(){
		executeSNSSimulationWorker.cancel(true);
	}

	public void processCompleted(){
		processCompleted = true;
		process.setExecuting(false);
		timer.stop();
		DecimalFormat df = new DecimalFormat("0.#########");
		Calendar startDate = process.getStatusMap().firstEntry().getValue().getCreateDate();
		Calendar endDate = process.getStatusMap().lastEntry().getValue().getCreateDate();
		double time = (endDate.getTimeInMillis() - startDate.getTimeInMillis()) / 1000;
		process.clearStatusMap();
		topLabel.setText("The QENS optimization workflow has been completed.");
		area.append("The QENS optimization workflow was completed in "+ df.format(time) + " seconds!\n");
		remove(abortButton);
		add(resultsButton, 	"1, 7, c, c");
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
				}else if(ws.getType()==WorkflowUpdateType.MESSAGE){
					area.append(Calendars.getFormattedOutputDateString(ws.getCreateDate()) + " | " + ws.getValue() + "\n");
				}
			}
		}
	}
	
	public void updateAfterAbortWorkflow(){
		process.setExecuting(false);
		timer.stop();
		topLabel.setText("The QENS optimization workflow has been aborted.");
		area.append("\nQENS optimization workflow has been aborted!\n");
		abortButton.setEnabled(false);
	}
	
}
