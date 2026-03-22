/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: BEAnalyzerData.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.feature;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.data.util.DataFile;

/**
 * The Class AtomFinderData is the main data structure for the Atom Finder tool.
 *
 * @author Eric J. Lingerfelt
 */
public class BEAnalyzerData implements Data{
	
	private DataFile dataFile;
	
	/**
	 * The Constructor.
	 */
	public BEAnalyzerData(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public BEAnalyzerData clone(){
		BEAnalyzerData bead = new BEAnalyzerData();
		bead.dataFile = dataFile;
		return bead;
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.data.Data#initialize()
	 */
	public void initialize(){
		dataFile = null;
	}
	
	public DataFile getDataFile(){return dataFile;}
	public void setDataFile(DataFile dataFile){this.dataFile = dataFile;}
}
