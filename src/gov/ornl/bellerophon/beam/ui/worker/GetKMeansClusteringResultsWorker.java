package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.KMeansClusteringDataSet;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDownloadDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetKMeansClusteringResultsListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetKMeansClusteringResultsWorker extends SwingWorker<ErrorResult, Void>{

	private GetKMeansClusteringResultsListener gkmcrl;
	private Window owner;
	private KMeansClusteringDataSet kmcds;
	private DelayDownloadDialog dialog;
	
	public GetKMeansClusteringResultsWorker(GetKMeansClusteringResultsListener gkmcrl
											, KMeansClusteringDataSet kmcds
											, Window owner){
		this.gkmcrl = gkmcrl;
		this.owner = owner;
		this.kmcds = kmcds;
		String string = "Please wait while the K-Means Clustering results are retrieved, downloaded, and processed locally.";
		dialog = new DelayDownloadDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(kmcds, Action.GET_KMEANS_CLUSTERING_RESULTS, dialog, owner);
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
					gkmcrl.updateAfterGetKMeansClusteringResults();
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