/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: CreateDataFileWorker.java
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
import gov.ornl.bellerophon.beam.io.BytesWrittenListener;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.CreateDataFileListener;

import java.awt.Cursor;
import java.awt.Window;

import javax.swing.SwingWorker;


public class CreateDataFileWorker extends SwingWorker<ErrorResult, Void> {

	private CreateDataFileListener cdfl;
	private BytesWrittenListener bwl;
	private DataFile datafile;
	private Window owner;
	
	/**
	 * Instantiates a new gets the dir listing worker.
	 *
	 * @param parent the parent
	 * @param frame the frame
	 * @param tree the tree
	 * @param node the node
	 */
	public CreateDataFileWorker(CreateDataFileListener cdfl, BytesWrittenListener bwl, DataFile datafile, Window owner){
		this.cdfl = cdfl;
		this.bwl = bwl;
		this.datafile = datafile;
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		ErrorResult result = new ErrorResult();

		datafile.populateHDF5File(datafile.getFile());
		datafile.setSize(datafile.getFile().length());
		result = WebServiceCom.getInstance().doWebServiceComCall(datafile, Action.CREATE_DATA_FILE, datafile.getFile(), bwl, owner);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				cdfl.updateAfterCreateDataFile();
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
