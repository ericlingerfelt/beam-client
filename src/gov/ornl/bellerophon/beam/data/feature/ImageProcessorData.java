/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: ImageProcessorData.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.feature;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.data.util.DataFile;

public class ImageProcessorData implements Data{
	
	private DataFile dataFile;
	
	/**
	 * The Constructor.
	 */
	public ImageProcessorData(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public ImageProcessorData clone(){
		ImageProcessorData bead = new ImageProcessorData();
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
