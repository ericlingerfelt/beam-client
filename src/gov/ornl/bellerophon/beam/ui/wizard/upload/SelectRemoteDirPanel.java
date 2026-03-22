/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: SelectRemoteDirPanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.wizard.upload;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.CustomFile;
import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import gov.ornl.bellerophon.beam.ui.dialog.CautionDialog;
import gov.ornl.bellerophon.beam.ui.dialog.CreateDirDialog;
import gov.ornl.bellerophon.beam.ui.util.RemoteDirTree;
import gov.ornl.bellerophon.beam.ui.util.RemoteDirTreeListener;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import gov.ornl.bellerophon.beam.ui.worker.CreateDirWorker;
import gov.ornl.bellerophon.beam.ui.worker.DeleteDirWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.CreateDirListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.DeleteDirListener;
import info.clearthought.layout.*;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class SelectRemoteDirPanel extends JPanel implements ActionListener, RemoteDirTreeListener, CreateDirListener, DeleteDirListener{

	private RemoteDirTree tree;
	private JButton createDirButton, deleteDirButton;
	private JLabel dirLabel;
	private WordWrapLabel topLabel;
	private JTextArea dirArea;
	private DataFile selectedDataFile;
	private CustomFile selectedCustomFile;
	private UploadDataFileWizard owner;
	private JScrollPane dirAreaSP;
	private JPanel treePanel;
	private CustomFile rootDirFile;
	
	public SelectRemoteDirPanel(UploadDataFileWizard owner) {
		
		this.owner = owner;
		
		topLabel = new WordWrapLabel(true);
		topLabel.setText("Please select a directory from your BEAM data storage area for data file upload and click <i>Continue</i>.");

		dirLabel = new JLabel("Selected Directory:");
		
		dirArea = new JTextArea();
		dirArea.setEditable(false);
		dirArea.setLineWrap(true);
		dirAreaSP = new JScrollPane(dirArea);
		
		createDirButton = new JButton("Create New Directory");
		createDirButton.addActionListener(this);
		
		deleteDirButton = new JButton("Delete Selected Directory");
		deleteDirButton.addActionListener(this);
		
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
		valuePanel.add(dirLabel, 			"0, 0, l, c");
		valuePanel.add(dirAreaSP, 			"0, 2, f, f");
		valuePanel.add(createDirButton, 	"0, 4, f, c");
		valuePanel.add(deleteDirButton, 	"0, 6, f, c");
		
		double[] column = {20, TableLayoutConstants.FILL
							, 20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
							, 30, TableLayoutConstants.FILL, 20};

		setLayout(new TableLayout(column, row));
		add(topLabel, 	"1, 1, 3, 1, c, c");
		add(treePanel,	"1, 3, f, f");
		add(valuePanel,	"3, 3, f, f");
		
	}

	public void setCurrentState(DataFile selectedDataFile){
		this.selectedDataFile = selectedDataFile;
		rootDirFile = (CustomFile) ((DefaultMutableTreeNode) MainData.getDirTreeModel().getRoot()).getUserObject();
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==createDirButton){
			if(selectedCustomFile!=null && selectedCustomFile.isDir()){
				CustomFile newCustomFile = CreateDirDialog.createCreateDirDialog(owner, selectedCustomFile, selectedCustomFile.equals(rootDirFile));
				if(newCustomFile!=null){
					if(!selectedCustomFile.containsFile(newCustomFile)){
						CreateDirWorker worker = new CreateDirWorker(this, newCustomFile, owner);
						worker.execute();
					}else{
						String error = "The directory you entered already exists.";
						AttentionDialog.createDialog(owner, error);
					}
				}
			}else{
				String error = "Please select a directory from your BEAM data storage area.";
				AttentionDialog.createDialog(owner, error);
			}
		}else if(ae.getSource()==deleteDirButton){
			if(selectedCustomFile.equals(rootDirFile)){
				String error = "You can not delete your root user directory.";
				AttentionDialog.createDialog(owner, error);
			}else if(selectedCustomFile!=null && selectedCustomFile.isDir()){
				if(!selectedCustomFile.isPop()){
					String string = "Are you sure you wish to delete this directory?";
					int returnValue = CautionDialog.createCautionDialog(owner, string, "Attention!");
					if(returnValue==CautionDialog.YES){
						DeleteDirWorker worker = new DeleteDirWorker(this, selectedCustomFile, owner);
						worker.execute();
					}
				}else{
					String string = "The directory you have selected contains data files and/or other directories. "
									+ "	Are you sure you wish to delete this directory and its children?";
					int returnValue = CautionDialog.createCautionDialog(owner, string, "Attention!");
					if(returnValue==CautionDialog.YES){
						DeleteDirWorker worker = new DeleteDirWorker(this, selectedCustomFile, owner);
						worker.execute();
					}
				}
			}else{
				String error = "Please select a directory from your BEAM data storage area.";
				AttentionDialog.createDialog(owner, error);
			}
		}
	}
	
	public boolean goodData(){
		return selectedCustomFile!=null && selectedCustomFile.isDir();
	}
	
	public void getCurrentState(){
		selectedDataFile.setPath(dirArea.getText());
		selectedDataFile.setParent(selectedCustomFile);
	}

	public void customFileSelected(CustomFile selectedCustomFile) {
		this.selectedCustomFile = selectedCustomFile;
		if(selectedCustomFile!=null && selectedCustomFile.isDir()){
			dirArea.setText(selectedCustomFile.getFullPath());
		}else{
			dirArea.setText("");
		}
	}

	public void updateAfterCreateDir(CustomFile newCustomFile) {
		selectedCustomFile.addFile(newCustomFile);
		MainData.getDirTreeModel().insertNodeInto(newCustomFile.getTreeNode(), selectedCustomFile.getTreeNode(), selectedCustomFile.getTreeNode().getChildCount());
		tree.sortTreeNodeChildren(selectedCustomFile.getTreeNode());
		tree.setSelectionPath(new TreePath(MainData.getDirTreeModel().getPathToRoot(newCustomFile.getTreeNode())));
		selectedCustomFile = newCustomFile;
	}

	public void updateAfterDeleteDir(CustomFile customFile) {
		customFile.getParent().removeFile(customFile);
		MainData.getDirTreeModel().removeNodeFromParent(customFile.getTreeNode());
		tree.setSelectionPath(new TreePath(MainData.getDirTreeModel().getPathToRoot(customFile.getParent().getTreeNode())));
		selectedCustomFile = customFile.getParent();
	}
}
