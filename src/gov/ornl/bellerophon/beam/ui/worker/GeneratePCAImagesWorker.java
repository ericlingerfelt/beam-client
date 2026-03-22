package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.PCAImageCleaningDataSet;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GeneratePCAImagesListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GeneratePCAImagesWorker extends SwingWorker<ErrorResult, Void>{

	private GeneratePCAImagesListener gpil;
	private Window owner;
	private PCAImageCleaningDataSet picds;
	private DelayDialog dialog;
	
	public GeneratePCAImagesWorker(GeneratePCAImagesListener gpil
									, PCAImageCleaningDataSet picds
									, Window owner){
		this.gpil = gpil;
		this.picds = picds;
		this.owner = owner;
		
		String string = "Please wait while PCA Cleaned Images are regenerated.";
		dialog = new DelayDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		picds.generateImages();
		return new ErrorResult();
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		if(!isCancelled()){
			try{
				ErrorResult result = get();
				if(!result.isError()){
					gpil.updateAfterGeneratePCAImages();
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
}