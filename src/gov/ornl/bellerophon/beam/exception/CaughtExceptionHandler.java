/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: CaughtExceptionHandler.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import gov.ornl.bellerophon.beam.data.util.UncaughtException;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.MessageDialog;

import java.awt.*;

/**
 * The Class CaughtExceptionHandler.
 *
 * @author Eric J. Lingerfelt
 */
public class CaughtExceptionHandler {

	/**
	 * Handle exception.
	 *
	 * @param e the e
	 * @param owner the owner
	 */
	public static void handleException(Exception e, Window owner){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		UncaughtException ueds = new UncaughtException();
		ueds.setStackTrace(sw.toString());
		WebServiceCom.getInstance().doWebServiceComCall(ueds, Action.LOG_JAVA_EXCEPTION);
		if(owner==null){
			owner = new Frame();
		}
		MessageDialog.createMessageDialog(owner
				, "An error has occurred completing your request. "
				+ "The appropriate staff have been notified."
				, "Error!");
	}
	
}
