/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: SelectRemoteDataFileDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import gov.ornl.bellerophon.beam.data.util.CustomFile;
import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.ui.util.RemoteDirTree;
import gov.ornl.bellerophon.beam.ui.util.RemoteDirTreeListener;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import gov.ornl.bellerophon.beam.ui.worker.GetDataFileInfoWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetDataFileInfoListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.*;

import javax.swing.*;

public class SelectRemoteDataFileDialog extends JDialog implements ActionListener
															, RemoteDirTreeListener
															, GetDataFileInfoListener{

	private DataFile dataFile;
	private JButton submitButton, cancelButton;
	private RemoteDirTree tree;
	private WordWrapLabel topLabel;
	private JLabel fileLabel;
	private JTextArea fileArea;
	private JScrollPane fileAreaSP;
	private JPanel treePanel;
	private Window owner;
	
	public SelectRemoteDataFileDialog(Window owner){
		
		super(owner, "Select Data File from BEAM Data Storage Area", Dialog.ModalityType.APPLICATION_MODAL);
		this.owner = owner;
		setSize(700, 450);
		setLocationRelativeTo(owner);
		
		submitButton = new JButton("Submit Selected Data File");
		submitButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel Selection");
		cancelButton.addActionListener(this);
		
		topLabel = new WordWrapLabel(true);
		topLabel.setText("Please select a data file from your BEAM data storage area and click <i>Submit</i>.");

		fileLabel = new JLabel("Selected Data File Information:");
		
		fileArea = new JTextArea();
		fileArea.setEditable(false);
		fileArea.setLineWrap(true);
		fileAreaSP = new JScrollPane(fileArea);
		
		tree = new RemoteDirTree(this);
		JScrollPane treePane = new JScrollPane(tree);
		treePanel = new JPanel();
		double[] colTree = {5, TableLayoutConstants.FILL, 5};
		double[] rowTree = {5, TableLayoutConstants.FILL, 5};
		treePanel.setLayout(new TableLayout(colTree, rowTree));
		treePanel.add(treePane, "1, 1, f, f");
		
		JPanel valuePanel = new JPanel();
		double[] columnValue = {TableLayoutConstants.FILL};
		double[] rowValue = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.FILL
								, 20, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		valuePanel.setLayout(new TableLayout(columnValue, rowValue));
		valuePanel.add(fileLabel, 		"0, 0, l, c");
		valuePanel.add(fileAreaSP, 		"0, 2, f, f");
		valuePanel.add(submitButton, 	"0, 4, f, c");
		valuePanel.add(cancelButton, 	"0, 6, f, c");
		
		double[] column = {20, TableLayoutConstants.FILL
							, 20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
							, 30, TableLayoutConstants.FILL, 20};

		setLayout(new TableLayout(column, row));
		add(topLabel, 	"1, 1, 3, 1, c, c");
		add(treePanel,	"1, 3, f, f");
		add(valuePanel,	"3, 3, f, f");

	}
	
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource()==submitButton){
			setVisible(false);
		}else if(ae.getSource()==cancelButton){
			dataFile = null;
			setVisible(false);
		}
	}
	
	public static DataFile createSelectRemoteDataFileDialog(Frame frame){
		SelectRemoteDataFileDialog dialog = new SelectRemoteDataFileDialog(frame);
		dialog.setVisible(true);
		return dialog.dataFile;
	}

	public void customFileSelected(CustomFile selectedCustomFile){
		if(selectedCustomFile!=null && selectedCustomFile instanceof DataFile){
			dataFile = (DataFile) selectedCustomFile;
			GetDataFileInfoWorker worker = new GetDataFileInfoWorker(this, (DataFile) selectedCustomFile, owner);
			worker.execute();
		}else{
			dataFile = null;
			fileArea.setText("");
		}
	}

	public void updateAfterGetDataFileInfo() {
		fileArea.setText(dataFile.toStringInfo());
	}

}
