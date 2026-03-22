package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.PCAImageCleaningDataSet;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCAImageCleaningDimsListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetPCAImageCleaningDimsWorker extends SwingWorker<ErrorResult, Void>{

	private GetPCAImageCleaningDimsListener gpicdl;
	private Window owner;
	private PCAImageCleaningDataSet picds;
	private DelayDialog dialog;
	
	public GetPCAImageCleaningDimsWorker(GetPCAImageCleaningDimsListener gpicdl
													, PCAImageCleaningDataSet picds
													, Window owner){
		this.gpicdl = gpicdl;
		this.picds = picds;
		this.owner = owner;
		String string = "Please wait while information about this dataset is retrieved, downloaded, and processed locally.";
		dialog = new DelayDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(picds, Action.GET_PCA_IMAGE_CLEANING_DIMS);
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
					gpicdl.updateAfterGetPCAImageCleaningDims();
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