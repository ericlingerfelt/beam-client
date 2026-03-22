/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: CreateDirDialog.java
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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class CreateDirDialog extends JDialog implements ActionListener{

	private CustomFile newCustomFile, customFile;
	private JTextField filenameField;
	private JButton submitButton, cancelButton;
	private boolean isRootFile;
	
	/**
	 * Instantiates a new caution dialog.
	 *
	 * @param owner the owner
	 * @param string the string
	 * @param title the title
	 */
	public CreateDirDialog(Window owner, CustomFile customFile, boolean isRootFile){
		
		super(owner, "Create Directory in BEAM Data Storage Area ", Dialog.ModalityType.APPLICATION_MODAL);
		
		this.customFile = customFile;
		this.isRootFile = isRootFile;
		
		setSize(615, 165);
		setLocationRelativeTo(owner);
		
		WordWrapLabel topLabel = new WordWrapLabel(true);
		topLabel.setText("Please enter a new directory name below and click <i>Submit</i>.");
		
		JLabel pathLabel = new JLabel("<html><b>" + customFile.getFullPath() + "/</b></html>");
		
		filenameField = new JTextField();
		
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
					newCustomFile = new CustomFile();
					newCustomFile.setDir(true);
					newCustomFile.setPop(false);
					newCustomFile.setName(filenameField.getText().trim());
					newCustomFile.setParent(customFile);
					if(isRootFile){
						newCustomFile.setPath(customFile.getName());
					}else{
						newCustomFile.setPath(customFile.getPath() + "/" + customFile.getName());
					}
					setVisible(false);
				}else{
					String error = "Folder names may only contain letters, numbers, and underscores. " +
									"Also, the first character of a filename can not be a period.";
					AttentionDialog.createDialog(this, error);
				}
			}else{
				String error = "Please enter a name for the new directory in your BEAM data storage area.";
				AttentionDialog.createDialog(this, error);
			}
		}else if(ae.getSource()==cancelButton){
			newCustomFile = null;
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
	
	public static CustomFile createCreateDirDialog(Window owner, CustomFile customFile, boolean isRootFile){
		CreateDirDialog dialog = new CreateDirDialog(owner, customFile, isRootFile);
		dialog.setVisible(true);
		return dialog.newCustomFile;
	}

}
