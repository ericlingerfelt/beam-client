/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: MessageDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import info.clearthought.layout.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import gov.ornl.bellerophon.beam.ui.util.*;

/**
 * The Class MessageDialog.
 *
 * @author Eric J. Lingerfelt
 */
public class MessageDialog extends JDialog{

	/**
	 * Instantiates a new message dialog.
	 *
	 * @param owner the owner
	 * @param string the string
	 * @param title the title
	 * @param size the size
	 */
	public MessageDialog(Window owner, String string, String title, Dimension size){
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		setSize(size);
		setLocationRelativeTo(owner);
		layoutDialog(string);
	}
	
	/**
	 * Instantiates a new message dialog.
	 *
	 * @param owner the owner
	 * @param string the string
	 * @param title the title
	 */
	public MessageDialog(Window owner, String string, String title){
		this(owner, string, title, new Dimension(320, 215));
	}
	
	/**
	 * Layout dialog.
	 *
	 * @param string the string
	 */
	private void layoutDialog(String string){
		
		double gap = 10;
		double[] col = {gap, TableLayoutConstants.FILL, gap};
		double[] row = {gap, TableLayoutConstants.FILL, 5, TableLayoutConstants.PREFERRED, gap};
		
		Container c = getContentPane();
		c.setLayout(new TableLayout(col, row));
		
		WordWrapLabel textArea = new WordWrapLabel();
		textArea.setText(string);
		textArea.setCaretPosition(0);
		textArea.setBackground(null);
		
		JScrollPane sp = new JScrollPane(textArea
								, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
								, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				setVisible(false);
				dispose();
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		
		c.add(sp, "1, 1, f, f");
		c.add(buttonPanel, "1, 3, c, c");
	}

	/**
	 * Creates the message dialog.
	 *
	 * @param owner the owner
	 * @param string the string
	 * @param title the title
	 * @param size the size
	 */
	public static void createMessageDialog(Window owner, String string, String title, Dimension size){
		MessageDialog dialog = new MessageDialog(owner, string, title, size);
		dialog.setVisible(true);
	}
	
	/**
	 * Creates the message dialog.
	 *
	 * @param owner the owner
	 * @param string the string
	 * @param title the title
	 */
	public static void createMessageDialog(Window owner, String string, String title){
		MessageDialog dialog = new MessageDialog(owner, string, title);
		dialog.setVisible(true);
	}
	
}
