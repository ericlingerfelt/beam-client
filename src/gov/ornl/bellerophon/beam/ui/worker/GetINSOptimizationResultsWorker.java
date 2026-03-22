package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.SNSData;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetINSOptimizationResultsListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetINSOptimizationResultsWorker extends SwingWorker<ErrorResult, Void>{

	private GetINSOptimizationResultsListener gisrl;
	private Window owner;
	private SNSData sd;
	private DelayDialog dialog;
	
	public GetINSOptimizationResultsWorker(GetINSOptimizationResultsListener gisrl
										, SNSData sd
										, Window owner){
		this.gisrl = gisrl;
		this.owner = owner;
		this.sd = sd;
		String string = "Please wait while the INS Optimization results are loaded.";
		dialog = new DelayDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(sd, Action.GET_INS_OPTIMIZATION_RESULTS);
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
					gisrl.updateAfterGetINSOptimizationResults(sd);
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
}