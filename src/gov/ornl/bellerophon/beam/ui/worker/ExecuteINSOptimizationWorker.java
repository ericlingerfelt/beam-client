package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.ExecuteINSOptimizationListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class ExecuteINSOptimizationWorker extends SwingWorker<ErrorResult, Void>{

	private ExecuteINSOptimizationListener eiol;
	private Window owner;
	private AnalysisProcess process;
	private DelayDialog dialog;
	
	public ExecuteINSOptimizationWorker(ExecuteINSOptimizationListener eiol
											, AnalysisProcess process
											, Window owner){
		this.eiol = eiol;
		this.owner = owner;
		this.process = process;
		String string = "Please wait while the INS Optimization workflow is prepared and executed.";
		dialog = new DelayDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(process, Action.EXECUTE_INS_OPTIMIZATION);
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
					eiol.updateAfterExecuteINSOptimization();
				}else{
					dialog.close();
					process.setExecuting(false);
					ErrorResultDialog.createErrorResultDialog(owner, result);
				}
			}catch(Exception e){
				dialog.close();
				process.setExecuting(false);
				CaughtExceptionHandler.handleException(e, owner);
			}
		}
	}
	
}
