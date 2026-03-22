package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.AbortWorkflowListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class AbortWorkflowWorker extends SwingWorker<ErrorResult, Void>{

	private AbortWorkflowListener awl;
	private Window owner;
	private DelayDialog dialog;
	private AnalysisProcess process;
	
	public AbortWorkflowWorker(AbortWorkflowListener awl
										, AnalysisProcess process
										, Window owner){
		this.awl = awl;
		this.owner = owner;
		this.process = process;

		String string = "Please wait while this " 
						+ process.getAnalysisFunction() 
						+ " analysis process is aborted.";
		dialog = new DelayDialog(owner, string, "Please wait...");
		
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(process, Action.ABORT_WORKFLOW);
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				dialog.close();
				awl.updateAfterAbortWorkflow();
			}else{
				dialog.close();
				ErrorResultDialog.createErrorResultDialog(owner, result);
			}
		}catch(Exception e){
			dialog.close();
			CaughtExceptionHandler.handleException(e, owner);
		}
	}
	
}