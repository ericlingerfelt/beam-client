/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: UncaughtException.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;

/**
 * The Class UncaughtException is the data structure for uncaught exceptions.
 *
 * @author Eric J. Lingerfelt
 */
public class UncaughtException implements Data{

	private String stackTrace;
	
	/**
	 * The Constructor.
	 */
	public UncaughtException(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public UncaughtException clone(){
		UncaughtException ue = new UncaughtException();
		ue.stackTrace = stackTrace;
		return ue;
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.data.Data#initialize()
	 */
	public void initialize(){
		stackTrace = "";
	}
	
	/**
	 * Gets the stack trace.
	 *
	 * @return the stack trace
	 */
	public String getStackTrace(){return stackTrace;}
	
	/**
	 * Sets the stack trace.
	 *
	 * @param stackTrace the new stack trace
	 */
	public void setStackTrace(String stackTrace){this.stackTrace = stackTrace;}
	
}
