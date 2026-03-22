/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: ErrorResult.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;

/**
 * The Class ErrorResult is the main data structure for reporting web service errors.
 *
 * @author Eric J. Lingerfelt
 */
public class ErrorResult implements Data{
	
	private boolean error;
	private String string;
	
	/**
	 * The Constructor.
	 */
	public ErrorResult(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.data.Data#initialize()
	 */
	public void initialize(){
		error = false;
		string = "";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public ErrorResult clone(){
		ErrorResult r = new ErrorResult();
		r.error = error;
		r.string = string;
		return r;
	}
	
	/**
	 * Checks if is error.
	 *
	 * @return true, if is error
	 */
	public boolean isError(){return error;}
	
	/**
	 * Sets the error.
	 *
	 * @param error the new error
	 */
	public void setError(boolean error){this.error = error;}
	
	/**
	 * Gets the string.
	 *
	 * @return the string
	 */
	public String getString(){return string;}
	
	/**
	 * Sets the string.
	 *
	 * @param string the new string
	 */
	public void setString(String string){this.string = string;}
	
}
