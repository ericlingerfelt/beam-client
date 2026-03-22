package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetDataFileTreeListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetDataFileTreeWorker extends SwingWorker<ErrorResult, Void>{

	private GetDataFileTreeListener gdftl;
	private DataFile dataFile;
	private Window owner;
	private DelayDialog dialog;

	public GetDataFileTreeWorker(GetDataFileTreeListener gdftl, DataFile dataFile, Window owner){
		this.gdftl = gdftl;
		this.dataFile = dataFile;
		this.owner = owner;
		
		String string = "Please wait while the structure of the selected data file is loaded.";
		dialog = new DelayDialog(owner, string, "Please wait...");
	}

	protected ErrorResult doInBackground(){
		dialog.open();
		ErrorResult result = WebServiceCom.getInstance().doWebServiceComCall(dataFile, Action.GET_DATA_FILE_TREE);
		if(result.isError()){
			return result;
		}
		return result;
	}
	
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				dialog.close();
				gdftl.updateAfterGetDataFileTree();
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
