/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: RemoteDirTreeListener.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.util;

import gov.ornl.bellerophon.beam.data.util.CustomFile;

public interface RemoteDirTreeListener {
	public void customFileSelected(CustomFile selectedCustomFile);
}
