/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: AttentionDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import java.awt.*;
import javax.swing.*;


/**
 * The Class AttentionDialog.
 *
 * @author Eric J. Lingerfelt
 */
public class AttentionDialog extends JDialog{

	/**
	 * Creates the dialog.
	 *
	 * @param owner the owner
	 * @param error the error
	 */
	public static void createDialog(Window owner, String error){
		MessageDialog dialog = new MessageDialog(owner, error, "Attention!");
    	dialog.setVisible(true);
	}
	
}
