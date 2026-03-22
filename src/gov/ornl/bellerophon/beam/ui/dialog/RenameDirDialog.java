/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: RenameDirDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import gov.ornl.bellerophon.beam.data.util.CustomFile;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class RenameDirDialog extends JDialog implements ActionListener{

	private CustomFile customFile;
	private JTextField filenameField;
	private JButton submitButton, cancelButton;
	public int selectedValue;
	public static final int SUBMIT = 1;
	public static final int CANCEL = 0;
	
	public RenameDirDialog(Window owner, CustomFile customFile){
		
		super(owner, "Rename Directory", Dialog.ModalityType.APPLICATION_MODAL);
		
		this.customFile = customFile;
		
		setSize(615, 165);
		setLocationRelativeTo(owner);
		
		WordWrapLabel topLabel = new WordWrapLabel(true);
		topLabel.setText("Please enter a new name for the selected data directory below and click <i>Submit</i>.");
		
		JLabel pathLabel = new JLabel("<html><b>" + customFile.getParent().getFullPath() + "/</b></html>");
		
		filenameField = new JTextField();
		filenameField.setText(customFile.getName());
		
		JPanel pathPanel = new JPanel();
		double[] columnPath = {TableLayoutConstants.PREFERRED, 10, TableLayoutConstants.FILL};
		double[] rowPath = {TableLayoutConstants.PREFERRED};
		pathPanel.setLayout(new TableLayout(columnPath, rowPath));
		pathPanel.add(pathLabel, 		"0, 0, r, c");
		pathPanel.add(filenameField, 	"2, 0, f, f");
		
		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(submitButton);
		buttonPanel.add(cancelButton);
			
		double gap = 20;
		double[] col = {gap, TableLayoutConstants.FILL, gap};
		double[] row = {gap, TableLayoutConstants.PREFERRED
							, 10, TableLayoutConstants.PREFERRED
							, 10, TableLayoutConstants.PREFERRED, gap};
		
		Container c = getContentPane();
		c.setLayout(new TableLayout(col, row));
		
		c.add(topLabel, 	"1, 1, c, c");
		c.add(pathPanel, 	"1, 3, f, f");
		c.add(buttonPanel, 	"1, 5, c, c");
		
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==submitButton){
			if(!filenameField.getText().trim().equals("")){
				if(goodFilename()){
					customFile.setNewName(filenameField.getText().trim());
					selectedValue = SUBMIT;
					setVisible(false);
				}else{
					String error = "Data directory names may only contain letters, numbers, and underscores. " +
									"Also, the first character of a filename can not be a period.";
					AttentionDialog.createDialog(this, error);
				}
			}else{
				String error = "Please enter a new name for the selected directory.";
				AttentionDialog.createDialog(this, error);
			}
		}else if(ae.getSource()==cancelButton){
			selectedValue = CANCEL;
			setVisible(false);
		}
	}
	
	public boolean goodFilename(){
		boolean goodFilename = true;
		if(filenameField.getText().trim().startsWith(".") || !filenameField.getText().trim().matches("[\\-._a-zA-Z0-9]+")){
			goodFilename = false;
		}
		return goodFilename;
	}
	
	public static int createRenameDirDialog(Frame owner, CustomFile customFile){
		RenameDirDialog dialog = new RenameDirDialog(owner, customFile);
		dialog.setVisible(true);
		return dialog.selectedValue;
	}
	
}