/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: GetRootDirListingWorker.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.worker;

import java.awt.Window;
import java.util.Iterator;

import gov.ornl.bellerophon.beam.data.util.*;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.DelayDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetCompleteDirListingListener;

import javax.swing.SwingWorker;

public class GetCompleteDirListingWorker extends SwingWorker<ErrorResult, Void>{

	private GetCompleteDirListingListener gcdll;
	private CustomFile rootDirFile;
	private Window owner;
	private DelayDialog dialog;

	public GetCompleteDirListingWorker(GetCompleteDirListingListener gcdll, CustomFile rootDirFile, Window owner){
		this.gcdll = gcdll;
		this.rootDirFile = rootDirFile;
		this.owner = owner;
		String string = "Please wait while BEAM loads your account and data file information.";
		dialog = new DelayDialog(owner, string, "Please wait...");
	}

	protected ErrorResult doInBackground(){
		dialog.open();
		ErrorResult result = getCompleteDirListing(rootDirFile);
		if(result.isError()){
			return result;
		}
		return result;
	}
	
	private ErrorResult getCompleteDirListing(CustomFile customFile){
		ErrorResult result = WebServiceCom.getInstance().doWebServiceComCall(customFile, Action.GET_DIR_LISTING);
		if(result.isError()){
			return result;
		}
		if(customFile.isPop()){
			Iterator<CustomFile> itr = customFile.getFileIterator();
			while(itr.hasNext()){
				result = getCompleteDirListing(itr.next());
				if(result.isError()){
					return result;
				}
			}
		}
		return result;
	}
	
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				dialog.close();
				gcdll.updateAfterGetCompleteDirListing(rootDirFile);
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
