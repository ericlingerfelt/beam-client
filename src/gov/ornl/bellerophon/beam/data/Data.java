/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: Data.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data;

/**
 * The Interface Data is the top level interface for all Bellerophon data structures.
 *
 * @author Eric J. Lingerfelt
 */
public interface Data {
	
	/**
	 * Initializes the data structure!
	 */
	public void initialize();
	
	/**
	 * Clones this data structure.
	 *
	 * @return the data
	 */
	public Data clone();
}
