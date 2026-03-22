package gov.ornl.bellerophon.beam.ui.wizard.analysis;

import gov.ornl.bellerophon.beam.data.util.AnalysisPlatform;
import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import gov.ornl.bellerophon.beam.ui.dialog.PlatformAuthenticationDialog;
import gov.ornl.bellerophon.beam.ui.wizard.WizardDialog;
import gov.ornl.bellerophon.beam.ui.worker.ApplyAnalysisFunctionWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.ApplyAnalysisFunctionListener;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExecuteAnalysisProcessWizard extends WizardDialog implements ActionListener
																			, ApplyAnalysisFunctionListener{

	private static Dimension SIZE = new Dimension(990, 700);
	private AnalysisProcess process;
	private Frame owner;
	public boolean processCompletedSuccessfully = false;
	private ApplyAnalysisFunctionWorker applyAnalysisFunctionWorker;
	
	private SelectPlatformPanel selectPanel = new SelectPlatformPanel();
	private ExecuteAnalysisProcessPanel executePanel = new ExecuteAnalysisProcessPanel(this);
	
	public static boolean createExecuteAnalysisProcessWizard(Frame owner, AnalysisProcess process){
		ExecuteAnalysisProcessWizard wizard = new ExecuteAnalysisProcessWizard(owner, process);
		wizard.setVisible(true);
		return wizard.processCompletedSuccessfully;
	}

	public ExecuteAnalysisProcessWizard(Frame owner, AnalysisProcess process){
		super(owner, "Execute Analysis Process", SIZE, 2);
		this.owner = owner;
		this.process = process;
		setNavActionListeners(this);
		initialize();
	}

	public void initialize(){
		addIntroButtons();
		selectPanel.setCurrentState(process);
		setContentPanel(selectPanel, 1, "Select and Configure Computing Resource", FULL_WIDTH);
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==continueButton){
			switch(panelIndex){
				case 1:
					selectPanel.getCurrentState();
					
					applyAnalysisFunctionWorker = null;
					int returnValue = -1;
					
					AnalysisPlatform ap = process.getAnalysisFunction().getAnalysisPlatform();
					if(ap.getName().equals("SHPC")){
						applyAnalysisFunctionWorker = new ApplyAnalysisFunctionWorker(this, process, owner);
						applyAnalysisFunctionWorker.execute();
					}else if(ap.getName().equals("Titan") || ap.getName().equals("Rhea") || ap.getName().equals("Eos")){
						returnValue = PlatformAuthenticationDialog.createPlatformAuthenticationDialog(this, process, "Passcode");
						if(returnValue==PlatformAuthenticationDialog.SUBMIT){
							applyAnalysisFunctionWorker = new ApplyAnalysisFunctionWorker(this, process, this);
							applyAnalysisFunctionWorker.execute();
						}	
					}else if(ap.getName().equals("nucastrodata2.ornl.gov")){
						applyAnalysisFunctionWorker = new ApplyAnalysisFunctionWorker(this, process, owner);
						applyAnalysisFunctionWorker.execute();
					}
					break;
			}
			
		}else if(ae.getSource()==backButton){
			switch(panelIndex){
				case 2:
					if(process.isExecuting()){
						String error = "You must abort this analysis process before going back to Step 1.";
						AttentionDialog.createDialog(owner, error);
					}else{
						setContentPanel(executePanel, selectPanel, 1, 2, "Select and Configure Computing Resource", FULL_WIDTH);
						addIntroButtons();
					}
					break;
			}
		}else if(ae.getSource()==endButton){
			if(process.isExecuting()){
				String error = "You must abort this analysis process before closing this window.";
				AttentionDialog.createDialog(owner, error);
			}else{
				setVisible(false);
			}
		}
	
		validate();
	}
	
	public void updateAfterApplyAnalysisFunctionAuthenticationError(){
		
		applyAnalysisFunctionWorker = null;
		int returnValue = -1;
		
		AnalysisPlatform ap = process.getAnalysisFunction().getAnalysisPlatform();
		if(ap.getName().equals("Titan") || ap.getName().equals("Rhea") || ap.getName().equals("Eos")){
			returnValue = PlatformAuthenticationDialog.createPlatformAuthenticationDialog(owner, process, "Passcode");
			if(returnValue==PlatformAuthenticationDialog.SUBMIT){
				applyAnalysisFunctionWorker = new ApplyAnalysisFunctionWorker(this, process, this);
				applyAnalysisFunctionWorker.execute();
			}
		}
		
	}
	
	public void updateAfterApplyAnalysisFunction(){
		process.setExecuting(true);
		process.clearStatusMap();
		executePanel.setCurrentState(process);
		setContentPanel(selectPanel, executePanel, 2, 2, "Monitor Progress", FULL);
		addEndButtons();
		validate();
		executePanel.startWorkflowStatusUpdating();
	}

	public void abortApplyAnalysisFunctionWorker(){
		applyAnalysisFunctionWorker.cancel(true);
	}

}
