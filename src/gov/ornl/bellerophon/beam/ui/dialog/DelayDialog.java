/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: DelayDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import info.clearthought.layout.*;

import java.awt.*;

import javax.swing.*;

import gov.ornl.bellerophon.beam.ui.util.*;

/**
 * The Class DelayDialog.
 *
 * @author Eric J. Lingerfelt
 */
public class DelayDialog extends JDialog{
	
	private Window owner;
	
	/**
	 * Instantiates a new delay dialog.
	 *
	 * @param owner the owner
	 * @param string the string
	 * @param title the title
	 */
	public DelayDialog(Window owner, String string, String title){
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		this.owner = owner;
		setSize(325, 200);

		double gap = 10;
		double[] col = {gap, TableLayoutConstants.FILL, gap};
		double[] row = {gap, TableLayoutConstants.FILL, 5, TableLayoutConstants.PREFERRED, gap};
		
		Container c = getContentPane();
		c.setLayout(new TableLayout(col, row));
		
		WordWrapLabel textArea = new WordWrapLabel();
		textArea.setText(string);
		
		JScrollPane sp = new JScrollPane(textArea
								, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
								, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		c.add(sp, "1, 1, f, f");
	}
	
	/**
	 * Open.
	 */
	public void open(){
		setLocationRelativeTo(owner);
		OpenDialogWorker task = new OpenDialogWorker(this);
		task.execute();
	}
	
	/**
	 * Close.
	 */
	public void close(){
		setVisible(false);
		dispose();
	}
}