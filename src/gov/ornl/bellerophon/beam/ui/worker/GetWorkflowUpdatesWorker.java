package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetWorkflowUpdatesListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetWorkflowUpdatesWorker extends SwingWorker<ErrorResult, Void>{
	
	private GetWorkflowUpdatesListener gwul;
	private Window owner;
	private AnalysisProcess process;
	
	/**
	 * Instantiates a new gets the dir listing worker.
	 *
	 * @param parent the parent
	 * @param frame the frame
	 * @param tree the tree
	 * @param node the node
	 */
	public GetWorkflowUpdatesWorker(GetWorkflowUpdatesListener gwul
												, AnalysisProcess process
												, Window owner){
		this.gwul = gwul;
		this.process = process;
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		return WebServiceCom.getInstance().doWebServiceComCall(process, Action.GET_WORKFLOW_UPDATES);
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				gwul.updateAfterGetWorkflowUpdates();
			}else{
				ErrorResultDialog.createErrorResultDialog(owner, result);
			}
		}catch(Exception e){
			CaughtExceptionHandler.handleException(e, owner);
		}
	}
	
}
	