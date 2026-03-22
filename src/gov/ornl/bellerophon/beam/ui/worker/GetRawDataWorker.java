package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.SHOFitDataSet;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDownloadDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetRawDataListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetRawDataWorker extends SwingWorker<ErrorResult, Void>{

	private GetRawDataListener grdl;
	private Window owner;
	private SHOFitDataSet sfds;
	private DelayDownloadDialog dialog;
	
	public GetRawDataWorker(GetRawDataListener grdl
								, SHOFitDataSet sfds
								, Window owner){
		this.grdl = grdl;
		this.owner = owner;
		this.sfds = sfds;
		String string = "Please wait while raw data results are retrieved, downloaded, and processed locally for the selected cell.";
		dialog = new DelayDownloadDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(sfds, Action.GET_RAW_DATA, dialog, owner);
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
					grdl.updateAfterGetRawData();
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