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

import java.awt.Cursor;
import java.awt.Window;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.data.util.*;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetAnalysisPlatformsListener;

import javax.swing.SwingWorker;

public class GetAnalysisPlatformsWorker extends SwingWorker<ErrorResult, Void>{

	private GetAnalysisPlatformsListener gapl;
	private Window owner;

	public GetAnalysisPlatformsWorker(GetAnalysisPlatformsListener gapl, Window owner){
		this.gapl = gapl;
		this.owner = owner;
	}

	class DummyData implements Data{

		public void initialize() {}
		public DummyData clone(){return null;}
		
	}	
	
	protected ErrorResult doInBackground(){
		return WebServiceCom.getInstance().doWebServiceComCall(new DummyData(), Action.GET_ANALYSIS_PLATFORMS);
	}
	
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				gapl.updateAfterGetAnalysisPlatforms();
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