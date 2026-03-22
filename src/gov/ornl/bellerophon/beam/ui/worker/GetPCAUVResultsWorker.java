package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.PCADataSet;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDownloadDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCAUVResultsListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetPCAUVResultsWorker extends SwingWorker<ErrorResult, Void>{

	private GetPCAUVResultsListener gpuvrl;
	private Window owner;
	private PCADataSet pds;
	private DelayDownloadDialog dialog;
	
	public GetPCAUVResultsWorker(GetPCAUVResultsListener gpuvrl
								, PCADataSet pds
								, Window owner){
		this.gpuvrl = gpuvrl;
		this.pds = pds;
		this.owner = owner;
		String string = "Please wait while PCA UV results are retrieved, downloaded, and processed locally.";
		dialog = new DelayDownloadDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(pds, Action.GET_PCA_UV_RESULTS, dialog, owner);
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
					gpuvrl.updateAfterGetPCAUVResults();
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