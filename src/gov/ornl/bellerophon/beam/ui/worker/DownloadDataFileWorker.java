package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DownloadFileDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.DownloadDataFileListener;

import java.awt.Window;
import javax.swing.SwingWorker;

public class DownloadDataFileWorker extends SwingWorker<ErrorResult, Void>{

	private DownloadDataFileListener ddfl;
	private Window owner;
	private DataFile dataFile;
	private DownloadFileDialog dialog;
	
	public DownloadDataFileWorker(DownloadDataFileListener ddfl, DataFile dataFile, Window owner){
		this.ddfl = ddfl;
		this.owner = owner;
		this.dataFile = dataFile;
		dialog = new DownloadFileDialog(owner, dataFile.getName());
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		ErrorResult result = WebServiceCom.getInstance().doWebServiceComCall(dataFile, Action.GET_DATA_FILE_INFO);
		if(result.isError()){
			return result;
		}
		return WebServiceCom.getInstance().doWebServiceComCall(dataFile, Action.DOWNLOAD_DATA_FILE, dataFile.getDownloadFile(), dialog, owner);
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				dialog.close();
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