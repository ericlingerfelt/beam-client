package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.ApplyAnalysisFunctionListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class ApplyAnalysisFunctionWorker extends SwingWorker<ErrorResult, Void>{

	private ApplyAnalysisFunctionListener aafl;
	private Window owner;
	private AnalysisProcess process;
	private DelayDialog dialog;
	
	public ApplyAnalysisFunctionWorker(ApplyAnalysisFunctionListener aafl
								, AnalysisProcess process
								, Window owner){
		this.aafl = aafl;
		this.owner = owner;
		this.process = process;
		String string = "Please wait while the workflow for this " 
							+ process.getAnalysisFunction() 
							+ " analysis process is dynamically generated and submitted to the "
							+ process.getAnalysisFunction().getAnalysisPlatform() + ".";
		dialog = new DelayDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(process, Action.APPLY_ANALYSIS_FUNCTION);
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		if(!isCancelled()){
			try{
				ErrorResult result = get();
				if(!result.isError()){
					dialog.close();
					process.setExecuting(true);
					aafl.updateAfterApplyAnalysisFunction();
				}else{
					dialog.close();
					process.setExecuting(false);
					ErrorResultDialog.createErrorResultDialog(owner, result);
					if(result.getString().indexOf("Authentication failed using the provided credentials. "
													+ "Please wait until your token passcode has changed before trying again.")!=-1){
						aafl.updateAfterApplyAnalysisFunctionAuthenticationError();
					}
				}
			}catch(Exception e){
				dialog.close();
				process.setExecuting(false);
				CaughtExceptionHandler.handleException(e, owner);
			}
		}
	}
	
}