/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: RenameDirWorker.java
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
import gov.ornl.bellerophon.beam.ui.worker.listener.RenameDataFileListener;

import java.awt.Cursor;
import java.awt.Window;

import javax.swing.SwingWorker;

public class RenameDataFileWorker extends SwingWorker<ErrorResult, Void>{

	private RenameDataFileListener rdfl;
	private Window owner;
	private DataFile dataFile;

	/**
	 * Instantiates a new gets the dir listing worker.
	 *
	 * @param parent the parent
	 * @param frame the frame
	 * @param tree the tree
	 * @param node the node
	 */
	public RenameDataFileWorker(RenameDataFileListener rdfl
									, DataFile dataFile
									, Window owner){
		this.rdfl = rdfl;
		this.owner = owner;
		this.dataFile = dataFile;
	}

	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#doInBackground()
	 */
	protected ErrorResult doInBackground(){
		owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		return WebServiceCom.getInstance().doWebServiceComCall(dataFile, Action.RENAME_DATA_FILE);
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.gui.util.swingworker.SwingWorker#done()
	 */
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				rdfl.updateAfterRenameDataFile(dataFile);
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