/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: ErrorDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import java.awt.*;
import javax.swing.*;


/**
 * The Class ErrorDialog.
 *
 * @author Eric J. Lingerfelt
 */
public class ErrorDialog extends JDialog{

	/**
	 * Creates the dialog.
	 *
	 * @param owner the owner
	 * @param error the error
	 */
	public static void createDialog(Window owner, String error){
		MessageDialog dialog = new MessageDialog(owner, error, "Error!");
    	dialog.setVisible(true);
	}
	
}