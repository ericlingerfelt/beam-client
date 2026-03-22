/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: DoSpectrogramAverageWorker.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.MeanSpectrogramDataSet;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDownloadDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetMeanSpectrogramListener;

import java.awt.Window;

import javax.swing.SwingWorker;

public class GetMeanSpectrogramWorker extends SwingWorker<ErrorResult, Void>{

	private GetMeanSpectrogramListener gmsl;
	private Window owner;
	private MeanSpectrogramDataSet msds;
	private DelayDownloadDialog dialog;

	public GetMeanSpectrogramWorker(GetMeanSpectrogramListener gmsl
														, MeanSpectrogramDataSet msds
														, Window owner){
		this.gmsl = gmsl;
		this.owner = owner;
		this.msds = msds;
		
		String string = "Please wait while the mean spectrogram is retrieved, downloaded, and processed locally.";
		dialog = new DelayDownloadDialog(owner, string, "Please wait...");
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		dialog.open();
		return WebServiceCom.getInstance().doWebServiceComCall(msds, Action.GET_MEAN_SPECTROGRAM, dialog, owner);
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				gmsl.updateAfterGetMeanSpectrogram();
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