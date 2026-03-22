/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: GetDataFileInfoWorker.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.worker;

import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetDataFileInfoListener;

import java.awt.Cursor;
import java.awt.Window;

import javax.swing.SwingWorker;

public class GetDataFileInfoWorker extends SwingWorker<ErrorResult, Void>{

	private GetDataFileInfoListener gdfil;
	private DataFile dataFile;
	private Window owner;

	public GetDataFileInfoWorker(GetDataFileInfoListener gdfil, DataFile dataFile, Window owner){
		this.gdfil = gdfil;
		this.dataFile = dataFile;
		this.owner = owner;
	}

	protected ErrorResult doInBackground(){
		owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		ErrorResult result = WebServiceCom.getInstance().doWebServiceComCall(dataFile, Action.GET_DATA_FILE_INFO);
		if(result.isError()){
			return result;
		}
		return result;
	}
	
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				gdfil.updateAfterGetDataFileInfo();
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
