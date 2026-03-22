/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: UncaughtExceptionHandler.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.exception;

import java.awt.Frame;
import java.io.PrintWriter;
import java.io.StringWriter;

import gov.ornl.bellerophon.beam.data.util.UncaughtException;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.io.WebServiceCom;
import gov.ornl.bellerophon.beam.ui.dialog.MessageDialog;


/**
 * The Class UncaughtExceptionHandler.
 *
 * @author Eric J. Lingerfelt
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{
	
	/* (non-Javadoc)
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	public void uncaughtException(Thread t, Throwable e){
		if(e instanceof java.lang.ThreadDeath
				|| e instanceof java.lang.IllegalThreadStateException
				|| e instanceof java.util.NoSuchElementException){
			return;
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		UncaughtException ueds = new UncaughtException();
		ueds.setStackTrace(sw.toString());
		WebServiceCom.getInstance().doWebServiceComCall(ueds, Action.LOG_JAVA_EXCEPTION);
		MessageDialog.createMessageDialog(new Frame()
				, "An error has occurred completing your request. "
				+ "The appropriate staff have been notified."
				, "Error!");
	}

}
