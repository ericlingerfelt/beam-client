package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.PCADataSet;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCASResultsListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetPCASResultsWorker extends SwingWorker<ErrorResult, Void>{

	private GetPCASResultsListener gpsrl;
	private Window owner;
	private PCADataSet pds;
	private DelayDialog dialog;
	
	public GetPCASResultsWorker(GetPCASResultsListener gpsrl
								, PCADataSet pds
								, Window owner){
		this.gpsrl = gpsrl;
		this.pds = pds;
		this.owner = owner;
		String string = "Please wait while PCA S results are retrieved, downloaded, and processed locally.";
		dialog = new DelayDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(pds, Action.GET_PCA_S_RESULTS);
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
					gpsrl.updateAfterGetPCASResults();
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