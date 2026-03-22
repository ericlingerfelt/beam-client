/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: ErrorResultDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import java.awt.*;

import javax.swing.*;

import gov.ornl.bellerophon.beam.data.util.*;


/**
 * The Class ErrorResultDialog.
 *
 * @author Eric J. Lingerfelt
 */
public class ErrorResultDialog extends JDialog{
	
	/**
	 * Creates the dialog.
	 *
	 * @param owner the owner
	 * @param result the result
	 */
	public static void createErrorResultDialog(Window window, ErrorResult result){
		MessageDialog dialog = new MessageDialog(window, result.getString(), "Error!");
    	dialog.setVisible(true);
	}
	
}


