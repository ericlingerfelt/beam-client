package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.PCAImageCleaningDataSet;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDownloadDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCAImageCleaningResultsListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetPCAImageCleaningResultsWorker extends SwingWorker<ErrorResult, Void>{

	private GetPCAImageCleaningResultsListener gpicrl;
	private Window owner;
	private PCAImageCleaningDataSet picds;
	private DelayDownloadDialog dialog;
	
	public GetPCAImageCleaningResultsWorker(GetPCAImageCleaningResultsListener gpicrl
												, PCAImageCleaningDataSet picds
												, Window owner){
		this.gpicrl = gpicrl;
		this.picds = picds;
		this.owner = owner;
		String string = "Please wait while PCA Image Cleaning results are retrieved, downloaded, and processed locally.";
		dialog = new DelayDownloadDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(picds, Action.GET_PCA_IMAGE_CLEANING_RESULTS, dialog, owner);
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
					gpicrl.updateAfterGetPCAImageCleaningResults();
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