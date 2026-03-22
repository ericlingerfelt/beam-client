package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.CustomFile;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.MoveDataFileListener;

import java.awt.Cursor;
import java.awt.Window;
import javax.swing.SwingWorker;

public class MoveDataFileWorker extends SwingWorker<ErrorResult, Void>{
	
	private MoveDataFileListener mdfl;
	private Window owner;
	private CustomFile customFile;
	private CustomFile newParentCustomFile;

	/**
	 * Instantiates a new gets the dir listing worker.
	 *
	 * @param parent the parent
	 * @param frame the frame
	 * @param tree the tree
	 * @param node the node
	 */
	public MoveDataFileWorker(MoveDataFileListener mdfl
									, CustomFile customFile
									, CustomFile newParentCustomFile
									, Window owner){
		this.mdfl = mdfl;
		this.owner = owner;
		this.customFile = customFile;
		this.newParentCustomFile = newParentCustomFile;
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		return WebServiceCom.getInstance().doWebServiceComCall(customFile, Action.MOVE_DATA_FILE);
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				mdfl.updateAfterMoveDataFile(customFile, newParentCustomFile);
				owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}else{
				owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				ErrorResultDialog.createErrorResultDialog(owner, result);
			}
		}catch(Exception e){
			owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			CaughtExceptionHandler.handleException(e, owner);
		}
	}
	
}
