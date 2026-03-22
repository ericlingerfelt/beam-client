/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: FileTransferMethod.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.enums;

/**
 * The Enum FileTransferMethod contains values for each type of allowable web service file transfer methods.
 *
 * @author Eric J. Lingerfelt
 */
public enum FileTransferMethod {

	GLOBUS,
	SCP, 
	HTTPS;
	
}
