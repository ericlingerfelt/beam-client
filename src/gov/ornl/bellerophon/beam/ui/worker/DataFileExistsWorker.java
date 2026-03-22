/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: DataFileExistsWorker.java
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
import gov.ornl.bellerophon.beam.ui.worker.listener.DataFileExistsListener;

import java.awt.Cursor;
import java.awt.Window;
import javax.swing.SwingWorker;

public class DataFileExistsWorker extends SwingWorker<ErrorResult, Void>{

	private DataFileExistsListener dfel;
	private DataFile datafile;
	private Window owner;

	public DataFileExistsWorker(DataFileExistsListener dfel, DataFile datafile, Window owner){
		this.owner = owner;
		this.datafile = datafile;
		this.dfel = dfel;
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		return WebServiceCom.getInstance().doWebServiceComCall(datafile, Action.DATA_FILE_EXISTS);
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				dfel.updateAfterDataFileExists();
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
