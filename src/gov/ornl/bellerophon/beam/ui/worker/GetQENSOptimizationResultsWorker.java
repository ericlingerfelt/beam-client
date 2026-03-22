package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.SNSData;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetQENSOptimizationResultsListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetQENSOptimizationResultsWorker extends SwingWorker<ErrorResult, Void>{

	private GetQENSOptimizationResultsListener gssrl;
	private Window owner;
	private SNSData sd;
	private DelayDialog dialog;
	
	public GetQENSOptimizationResultsWorker(GetQENSOptimizationResultsListener gssrl
										, SNSData sd
										, Window owner){
		this.gssrl = gssrl;
		this.owner = owner;
		this.sd = sd;
		String string = "Please wait while the SNS simulation results are loaded.";
		dialog = new DelayDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(sd, Action.GET_QENS_OPTIMIZATION_RESULTS);
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
					gssrl.updateAfterGetQENSOptimizationResults(sd);
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