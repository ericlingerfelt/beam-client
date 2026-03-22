/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: DataManagerPanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.datamanager;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.io.File;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.CustomFile;
import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.file.CustomFileFilter;
import gov.ornl.bellerophon.beam.file.FileType;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import gov.ornl.bellerophon.beam.ui.dialog.CautionDialog;
import gov.ornl.bellerophon.beam.ui.dialog.CreateDirDialog;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorDialog;
import gov.ornl.bellerophon.beam.ui.dialog.MoveDataFileDialog;
import gov.ornl.bellerophon.beam.ui.dialog.MoveDirDialog;
import gov.ornl.bellerophon.beam.ui.dialog.RenameDataFileDialog;
import gov.ornl.bellerophon.beam.ui.dialog.RenameDirDialog;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.format.Buttons;
import gov.ornl.bellerophon.beam.ui.format.Colors;
import gov.ornl.bellerophon.beam.ui.util.PlainFileChooserFactory;
import gov.ornl.bellerophon.beam.ui.util.RemoteDirTree;
import gov.ornl.bellerophon.beam.ui.util.RemoteDirTreeListener;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import gov.ornl.bellerophon.beam.ui.wizard.upload.UploadDataFileWizard;
import gov.ornl.bellerophon.beam.ui.worker.CreateDirWorker;
import gov.ornl.bellerophon.beam.ui.worker.DeleteDataFileWorker;
import gov.ornl.bellerophon.beam.ui.worker.DeleteDirWorker;
import gov.ornl.bellerophon.beam.ui.worker.DownloadDataFileWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetDataFileInfoWorker;
import gov.ornl.bellerophon.beam.ui.worker.MoveDataFileWorker;
import gov.ornl.bellerophon.beam.ui.worker.MoveDirWorker;
import gov.ornl.bellerophon.beam.ui.worker.RenameDataFileWorker;
import gov.ornl.bellerophon.beam.ui.worker.RenameDirWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.CreateDirListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.DeleteDataFileListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.DeleteDirListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.DownloadDataFileListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetDataFileInfoListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.MoveDataFileListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.MoveDirListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.RenameDataFileListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.RenameDirListener;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class DataManagerPanel extends JPanel implements ActionListener
															, RemoteDirTreeListener
															, DownloadDataFileListener
															, CreateDirListener
															, DeleteDirListener
															, GetDataFileInfoListener
															, DeleteDataFileListener
															, RenameDataFileListener
															, RenameDirListener
															, MoveDataFileListener
															, MoveDirListener{

	private Frame frame;
	private JTextArea infoArea;
	private JScrollPane sp;
	private CustomFile selectedCustomFile;
	private RemoteDirTree tree;
	private JPanel buttonPanel, buttonContainerPanel, infoPanel, treePanel;
	private JButton uploadFileButton, uploadFileButton2, downloadFileButton, deleteFileButton, moveFileButton, renameFileButton, editFileButton,
						moveDirButton, deleteDirButton, createDirButton, renameDirButton, editDirButton, searchDirButton, searchFileButton; 
	private CustomFile rootDirFile;
	private enum ButtonPanelState{DIR, FILE, NOTHING}
	private boolean initialized = false;
	
	public DataManagerPanel(Frame frame){
		
		this.frame = frame;
		
		downloadFileButton = Buttons.getIconButton("Download Data File"
														, "icons/document-save.png"
														, Buttons.IconPosition.RIGHT
														, Colors.GREEN
														, this
														, new Dimension(225, 50)
														, -5);
		
		moveFileButton = Buttons.getIconButton("Move Data File"
														, "icons/move-file.png"
														, Buttons.IconPosition.RIGHT
														, Colors.GREEN
														, this
														, new Dimension(225, 50)
														, 23);

		renameFileButton = Buttons.getIconButton("Rename Data File"
														, "icons/rename-file.png"
														, Buttons.IconPosition.RIGHT
														, Colors.BLUE
														, this
														, new Dimension(225, 50)
														, 7);
		
		searchFileButton = Buttons.getIconButton("Search by Metadata"
														, "icons/system-search.png"
														, Buttons.IconPosition.RIGHT
														, Colors.BLUE
														, this
														, new Dimension(225, 50)
														, 1);
		
		editFileButton = Buttons.getIconButton("Modify Metadata"
														, "icons/edit-file.png"
														, Buttons.IconPosition.RIGHT
														, Colors.BLUE
														, this
														, new Dimension(225, 50)
														, 13);
		
		deleteFileButton = Buttons.getIconButton("Delete Data File"
														, "icons/edit-delete.png"
														, Buttons.IconPosition.RIGHT
														, Colors.RED
														, this
														, new Dimension(225, 50)
														, 18);
		
		uploadFileButton = Buttons.getIconButton("Upload Data File"
														, "icons/go-up.png"
														, Buttons.IconPosition.RIGHT
														, Colors.GREEN
														, this
														, new Dimension(225, 50));
		
		uploadFileButton2 = Buttons.getIconButton("Upload Data File"
														, "icons/go-up.png"
														, Buttons.IconPosition.RIGHT
														, Colors.GREEN
														, this
														, new Dimension(225, 50)
														, 12);
		
		moveDirButton = Buttons.getIconButton("Move Directory"
														, "icons/move-folder.png"
														, Buttons.IconPosition.RIGHT
														, Colors.GREEN
														, this
														, new Dimension(225, 50)
														, 8);
		
		createDirButton = Buttons.getIconButton("Create Directory"
														, "icons/folder-new.png"
														, Buttons.IconPosition.RIGHT
														, Colors.BLUE
														, this
														, new Dimension(225, 50)
														, 6);
		
		renameDirButton = Buttons.getIconButton("Rename Directory"
														, "icons/rename-folder.png"
														, Buttons.IconPosition.RIGHT
														, Colors.BLUE
														, this
														, new Dimension(225, 50)
														, -1);
		
		
		editDirButton = Buttons.getIconButton("Modify Metadata"
														, "icons/edit-folder.png"
														, Buttons.IconPosition.RIGHT
														, Colors.BLUE
														, this
														, new Dimension(225, 50)
														, 8);
		
		searchDirButton = Buttons.getIconButton("Search by Metadata"
														, "icons/system-search.png"
														, Buttons.IconPosition.RIGHT
														, Colors.BLUE
														, this
														, new Dimension(225, 50)
														, -5);
		
		deleteDirButton = Buttons.getIconButton("Delete Directory"
														, "icons/edit-delete.png"
														, Buttons.IconPosition.RIGHT
														, Colors.RED
														, this
														, new Dimension(225, 50)
														, 8);
		
		buttonPanel = new JPanel();
		
		buttonContainerPanel = new JPanel();
		buttonContainerPanel.setBorder(Borders.getBorder("Data Management Functions"));
		buttonContainerPanel.setPreferredSize(new Dimension(350, 9000));
		double[] colButton = {5, TableLayoutConstants.FILL, 5};
		double[] rowButton = {5, TableLayoutConstants.FILL, 5};
		buttonContainerPanel.setLayout(new TableLayout(colButton, rowButton));
		buttonContainerPanel.add(buttonPanel, "1, 1, c, c");
		
		infoArea = new JTextArea();
		infoArea.setWrapStyleWord(true);
		infoArea.setLineWrap(true);
		infoArea.setEditable(false);
		sp = new JScrollPane(infoArea);
		
		infoPanel = new JPanel();
		infoPanel.setBorder(Borders.getBorder("Data Info"));
		infoPanel.setPreferredSize(new Dimension(350, 9000));
		
		double[] colInfo = {5, TableLayoutConstants.FILL, 5};
		double[] rowInfo = {5, TableLayoutConstants.FILL, 5};
		infoPanel.setLayout(new TableLayout(colInfo, rowInfo));
		infoPanel.add(sp, "1, 1, f, f");
		
		tree = new RemoteDirTree(this);
		JScrollPane treePane = new JScrollPane(tree);
		
		treePanel = new JPanel();
		treePanel.setBorder(Borders.getBorder("BEAM Data Storage Area"));
		double[] colTree = {5, TableLayoutConstants.FILL, 5};
		double[] rowTree = {5, TableLayoutConstants.FILL, 5};
		treePanel.setLayout(new TableLayout(colTree, rowTree));
		treePanel.add(treePane, "1, 1, f, f");
		
		double[] col = {10, TableLayoutConstants.FILL, 10, TableLayoutConstants.PREFERRED, 10};
		double[] row = {10, 0.4, 10, 0.6, 10};
		setLayout(new TableLayout(col, row));
		add(treePanel, 				"1, 1, 1, 3, f, f");
		add(infoPanel, 				"3, 1, f, f");
		add(buttonContainerPanel, 	"3, 3, f, f");
		
	}
	
	public void setCurrentState(){
		if(!initialized){
			rootDirFile = (CustomFile) ((DefaultMutableTreeNode) MainData.getDirTreeModel().getRoot()).getUserObject();
			infoArea.setText("");
			setButtonPanelState(ButtonPanelState.NOTHING);
			initialized = true;
		}
	}
	
	public void customFileSelected(CustomFile selectedCustomFile) {
		this.selectedCustomFile = selectedCustomFile;
		if(selectedCustomFile!=null){
			if(selectedCustomFile instanceof DataFile){
				setButtonPanelState(ButtonPanelState.FILE);
				GetDataFileInfoWorker worker = new GetDataFileInfoWorker(this,  (DataFile) selectedCustomFile, frame);
				worker.execute();
			}else{
				setButtonPanelState(ButtonPanelState.DIR);
				infoArea.setText(selectedCustomFile.toStringInfo());	
			}
		}else{
			setButtonPanelState(ButtonPanelState.NOTHING);
			infoArea.setText("");
		}
	}
	
	private void setButtonPanelState(ButtonPanelState state){
		switch(state){
			case DIR:
				setDirButtonUI();
				break;
			case FILE:
				setFileButtonUI();
				break;
			case NOTHING:
				setNothingButtonUI();
				break;
		}
	}
	
	private void setFileButtonUI(){
		
		buttonContainerPanel.setBorder(Borders.getBorder("Data File Management Functions"));
		infoPanel.setBorder(Borders.getBorder("Selected Data File Information"));
		
		buttonPanel.removeAll();
		
		double[] columnButton = {10, TableLayoutConstants.PREFERRED, 10};
		double[] rowButton = {10, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED, 10};
		buttonPanel.setLayout(new TableLayout(columnButton, rowButton));
		buttonPanel.add(uploadFileButton2,  "1, 1, c, c");
		buttonPanel.add(downloadFileButton, "1, 3, c, c");
		buttonPanel.add(moveFileButton, 	"1, 5, c, c");
		buttonPanel.add(renameFileButton, 	"1, 7, c, c");
		buttonPanel.add(editFileButton, 	"1, 9, c, c");
		buttonPanel.add(searchFileButton, 	"1, 11, c, c");
		buttonPanel.add(deleteFileButton, 	"1, 13, c, c");
		
		validate();
		repaint();
	}
	
	private void setDirButtonUI(){
		
		buttonContainerPanel.setBorder(Borders.getBorder("Directory Management Functions"));
		infoPanel.setBorder(Borders.getBorder("Directory Information"));
		
		buttonPanel.removeAll();
		
		double[] columnButton = {10, TableLayoutConstants.PREFERRED, 10};
		double[] rowButton = {10, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED, 10};
		buttonPanel.setLayout(new TableLayout(columnButton, rowButton));
		buttonPanel.add(uploadFileButton, 	"1, 1, c, c");
		buttonPanel.add(moveDirButton, 		"1, 3, c, c");
		buttonPanel.add(createDirButton, 	"1, 5, c, c");
		buttonPanel.add(renameDirButton, 	"1, 7, c, c");
		buttonPanel.add(editDirButton, 		"1, 9, c, c");
		buttonPanel.add(searchDirButton, 	"1, 11, c, c");
		buttonPanel.add(deleteDirButton, 	"1, 13, c, c");
		
		validate();
		repaint();
	}
	
	private void setNothingButtonUI(){
		
		buttonContainerPanel.setBorder(Borders.getBorder("Data Management Functions"));
		
		buttonPanel.removeAll();
		
		WordWrapLabel label = new WordWrapLabel();
		label.setText("Please select a directory or data file from your BEAM data storage area using the tree on the left.");
		
		double[] columnButton = {20, TableLayoutConstants.FILL, 20};
		double[] rowButton = {20, TableLayoutConstants.FILL, 20};
		buttonPanel.setLayout(new TableLayout(columnButton, rowButton));
		buttonPanel.add(label, 	"1, 1, c, c");
		
		validate();
		repaint();
	}
	
	public void actionPerformed(ActionEvent ae) {
	
		if(ae.getSource()==uploadFileButton){
			uploadFile();
		}else if(ae.getSource()==uploadFileButton2){
			uploadFile2();
		}else if(ae.getSource()==downloadFileButton){
			downloadFile();
		}else if(ae.getSource()==deleteFileButton){
			deleteFile();
		}else if(ae.getSource()==moveFileButton){
			moveFile();
		}else if(ae.getSource()==renameFileButton){
			renameFile();
		}else if(ae.getSource()==createDirButton){
			createDir();
		}else if(ae.getSource()==deleteDirButton){
			deleteDir();
		}else if(ae.getSource()==moveDirButton){
			moveDir();
		}else if(ae.getSource()==renameDirButton){
			renameDir();
		}else if(ae.getSource()==searchDirButton 
					|| ae.getSource()==searchFileButton
					|| ae.getSource()==editDirButton
					|| ae.getSource()==editFileButton){
			String error = "This software tool is still under development.";
			AttentionDialog.createDialog(frame, error);
		}
		
	}

	private void uploadFile2(){
		if(selectedCustomFile!=null){
			CustomFile parentFile = selectedCustomFile.getParent();
			DataFile df = UploadDataFileWizard.createUploadDataFileWizard(frame, parentFile);
			if(df!=null){
				if(parentFile.containsFile(df)){
					MainData.getDirTreeModel().removeNodeFromParent(parentFile.getFileMap().get(df.getName()).getTreeNode());
				}
				df.getParent().addFile(df);
				parentFile.addFile(df);
				MainData.getDirTreeModel().insertNodeInto(df.getTreeNode(), parentFile.getTreeNode(), parentFile.getTreeNode().getChildCount());
				tree.sortTreeNodeChildren(parentFile.getTreeNode());
				tree.setSelectionPath(new TreePath(MainData.getDirTreeModel().getPathToRoot(df.getTreeNode())));
				selectedCustomFile = df;
			}
		}
	}
	
	private void uploadFile(){
		if(selectedCustomFile!=null && selectedCustomFile.isDir()){
			DataFile df = UploadDataFileWizard.createUploadDataFileWizard(frame, selectedCustomFile);
			if(df!=null){
				if(selectedCustomFile.containsFile(df)){
					MainData.getDirTreeModel().removeNodeFromParent(selectedCustomFile.getFileMap().get(df.getName()).getTreeNode());
				}
				df.getParent().addFile(df);
				selectedCustomFile.addFile(df);
				MainData.getDirTreeModel().insertNodeInto(df.getTreeNode(), selectedCustomFile.getTreeNode(), selectedCustomFile.getTreeNode().getChildCount());
				tree.sortTreeNodeChildren(selectedCustomFile.getTreeNode());
				tree.setSelectionPath(new TreePath(MainData.getDirTreeModel().getPathToRoot(df.getTreeNode())));
				selectedCustomFile = df;
			}
		}else{
			String error = "Please select a directory from your BEAM data storage area for file upload.";
			AttentionDialog.createDialog(frame, error);
		}
	}
	
	private void downloadFile(){
		if(selectedCustomFile!=null && selectedCustomFile instanceof DataFile){
			File file = getDataSaveFile(selectedCustomFile.getName());
			if(file!=null){
				((DataFile) selectedCustomFile).setDownloadFile(file);
				DownloadDataFileWorker worker = new DownloadDataFileWorker(this, (DataFile) selectedCustomFile, frame);
				worker.execute();
			}
		}else{
			String error = "Please select a data file from your BEAM data storage area to download.";
			AttentionDialog.createDialog(frame, error);
		}
	}
	
	private File getDataSaveFile(String filename){
		JFileChooser fileDialog = PlainFileChooserFactory.createPlainFileChooser();
		fileDialog.setAcceptAllFileFilterUsed(false);
		fileDialog.addChoosableFileFilter(new CustomFileFilter(FileType.H5));
		fileDialog.setSelectedFile(new File(filename));
		int returnVal = fileDialog.showSaveDialog(this); 
		MainData.setAbsolutePath(fileDialog.getCurrentDirectory());
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File file = fileDialog.getSelectedFile();
			String filepath = file.getAbsolutePath();
			if(new File(filepath).exists()){
				String msg = "The file " + file.getName() + " exists. Do you want to replace it?";
				int value = CautionDialog.createCautionDialog(frame, msg, "Attention!");
				if(value==CautionDialog.NO){
					getDataSaveFile(file.getName());
				}else{
					return file;
				}
			}else{
				return file;
			}
		}
		return null;
	}
	
	private void deleteFile(){
		if(selectedCustomFile!=null && selectedCustomFile instanceof DataFile){
			String string = "Are you sure you want to delete the selected data file?";
			int returnValue = CautionDialog.createCautionDialog(frame, string, "Attention!");
			if(returnValue==CautionDialog.YES){
				DeleteDataFileWorker worker = new DeleteDataFileWorker(this, (DataFile) selectedCustomFile, frame);
				worker.execute();
			}
		}else{
			String error = "Please select a data file from your BEAM data storage area to delete.";
			AttentionDialog.createDialog(frame, error);
		}
	}
	
	private void moveFile(){
		if(selectedCustomFile!=null && selectedCustomFile instanceof DataFile){
			CustomFile moveDataFileCustomFile = MoveDataFileDialog.createMoveDataFileDialog(frame, selectedCustomFile, rootDirFile);
			if(moveDataFileCustomFile!=null){
				if(moveDataFileCustomFile.containsFile(selectedCustomFile)){
					String error = "The directory you selected already contains a directory or file with the same name. Please enter a different parent directory.";
					AttentionDialog.createDialog(frame, error);
					moveFile();
				}else{
					MoveDataFileWorker worker = new MoveDataFileWorker(this, selectedCustomFile, moveDataFileCustomFile, frame);
					worker.execute();
				}
			}
		}else{
			String error = "Please select a data file from your BEAM data storage area to move.";
			AttentionDialog.createDialog(frame, error);
		}
	}
	
	private void renameFile(){
		if(selectedCustomFile!=null && selectedCustomFile instanceof DataFile){
			int returnValue = RenameDataFileDialog.createRenameDataFileDialog(frame, (DataFile) selectedCustomFile);
			if(returnValue==RenameDataFileDialog.SUBMIT){
				if(selectedCustomFile.getParent().containsFile(selectedCustomFile.getNewName())){
					String error = "The data file name you entered already exists at the path specified. Please enter a different name.";
					AttentionDialog.createDialog(frame, error);
					renameFile();
				}else{
					RenameDataFileWorker worker = new RenameDataFileWorker(this, (DataFile) selectedCustomFile, frame);
					worker.execute();
				}
			}
		}else{
			String error = "Please select a data file from your BEAM data storage area to rename.";
			AttentionDialog.createDialog(frame, error);
		}
	}
	
	private void createDir(){
		if(selectedCustomFile!=null && selectedCustomFile.isDir()){
			CustomFile newCustomFile = CreateDirDialog.createCreateDirDialog(frame, selectedCustomFile, selectedCustomFile.equals(rootDirFile));
			if(newCustomFile!=null){
				if(!selectedCustomFile.containsFile(newCustomFile.getName())){
					CreateDirWorker worker = new CreateDirWorker(this, newCustomFile, frame);
					worker.execute();
				}else{
					String error = "The directory name you entered already exists.";
					AttentionDialog.createDialog(frame, error);
					createDir();
				}
			}
		}else{
			String error = "Please select a directory from your BEAM data storage area.";
			AttentionDialog.createDialog(frame, error);
		}
	}
	
	private void deleteDir(){
		if(selectedCustomFile.equals(rootDirFile)){
			String error = "You can not delete your root user directory.";
			AttentionDialog.createDialog(frame, error);
		}else if(selectedCustomFile!=null && selectedCustomFile.isDir()){
			if(!selectedCustomFile.isPop()){
				String string = "Are you sure you wish to delete this directory?";
				int returnValue = CautionDialog.createCautionDialog(frame, string, "Attention!");
				if(returnValue==CautionDialog.YES){
					DeleteDirWorker worker = new DeleteDirWorker(this, selectedCustomFile, frame);
					worker.execute();
				}
			}else{
				String string = "The directory you have selected contains data files and/or other directories. "
								+ "	Are you sure you wish to delete this directory and its children?";
				int returnValue = CautionDialog.createCautionDialog(frame, string, "Attention!");
				if(returnValue==CautionDialog.YES){
					DeleteDirWorker worker = new DeleteDirWorker(this, selectedCustomFile, frame);
					worker.execute();
				}
			}
		}else{
			String error = "Please select a director from your BEAM data storage area to delete.";
			AttentionDialog.createDialog(frame, error);
		}
	}
	
	private void moveDir(){
		if(selectedCustomFile!=null && selectedCustomFile.isDir()){
			if(!selectedCustomFile.equals(rootDirFile)){
				CustomFile moveDirCustomFile = MoveDirDialog.createMoveDirDialog(frame, selectedCustomFile, rootDirFile);
				if(moveDirCustomFile!=null){
					if(moveDirCustomFile.equals(selectedCustomFile)){
						String error = "You can not move a directory into itself.";
						AttentionDialog.createDialog(frame, error);
						moveDir();
					}else if(moveDirCustomFile.containsFile(selectedCustomFile)){
						String error = "The directory you selected already contains a directory or file with the same name. Please enter a different parent directory.";
						AttentionDialog.createDialog(frame, error);
						moveDir();
					}else{
						MoveDirWorker worker = new MoveDirWorker(this, selectedCustomFile, moveDirCustomFile, frame);
						worker.execute();
					}
				}
			}else{
				String error = "You can not move your root directory.";
				ErrorDialog.createDialog(frame, error);
			}
		}else{
			String error = "Please select a directory from your BEAM data storage area to move.";
			AttentionDialog.createDialog(frame, error);
		}
	}
	
	private void renameDir(){
		if(selectedCustomFile!=null && selectedCustomFile.isDir()){
			if(!selectedCustomFile.equals(rootDirFile)){
				int returnValue = RenameDirDialog.createRenameDirDialog(frame, selectedCustomFile);
				if(returnValue==RenameDirDialog.SUBMIT){
					if(selectedCustomFile.getParent().containsFile(selectedCustomFile.getNewName())){
						String error = "The directory name you entered already exists at the path specified. Please enter a different name.";
						AttentionDialog.createDialog(frame, error);
						renameDir();
					}else{
						RenameDirWorker worker = new RenameDirWorker(this, selectedCustomFile, frame);
						worker.execute();
					}
				}
			}else{
				String error = "You can not rename your root user directory.";
				AttentionDialog.createDialog(frame, error);
			}
		}else{
			String error = "Please select a directory from your BEAM data storage area to rename.";
			AttentionDialog.createDialog(frame, error);
		}
	}

	public void updateAfterDownloadDataFile() {}

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

	public void updateAfterGetDataFileInfo() {
		if(selectedCustomFile!=null && selectedCustomFile instanceof DataFile){
			infoArea.setText(((DataFile) selectedCustomFile).toStringInfo());
		}
	}

	public void updateAfterMoveDataFile(CustomFile customFile, CustomFile newParentCustomFile) {
		customFile.getParent().removeFile(customFile);
		MainData.getDirTreeModel().removeNodeFromParent(customFile.getTreeNode());
		customFile.setParent(newParentCustomFile);
		customFile.setPath(newParentCustomFile.getFullPath());
		newParentCustomFile.addFile(customFile);
		MainData.getDirTreeModel().insertNodeInto(customFile.getTreeNode(), newParentCustomFile.getTreeNode(), newParentCustomFile.getTreeNode().getChildCount());
		tree.sortTreeNodeChildren(newParentCustomFile.getTreeNode());
		tree.setSelectionPath(new TreePath(MainData.getDirTreeModel().getPathToRoot(customFile.getTreeNode())));
	}
	
	public void updateAfterMoveDir(CustomFile customFile, CustomFile newParentCustomFile) {
		customFile.getParent().removeFile(customFile);
		MainData.getDirTreeModel().removeNodeFromParent(customFile.getTreeNode());
		customFile.setParent(newParentCustomFile);
		customFile.setPath(newParentCustomFile.getFullPath());
		newParentCustomFile.addFile(customFile);
		MainData.getDirTreeModel().insertNodeInto(customFile.getTreeNode(), newParentCustomFile.getTreeNode(), newParentCustomFile.getTreeNode().getChildCount());
		tree.sortTreeNodeChildren(newParentCustomFile.getTreeNode());
		tree.setSelectionPath(new TreePath(MainData.getDirTreeModel().getPathToRoot(customFile.getTreeNode())));
	}

	public void updateAfterRenameDir(CustomFile customFile) {
		customFile.getParent().removeFile(customFile);
		customFile.setName(customFile.getNewName());
		customFile.setNewName("");
		customFile.getParent().addFile(customFile);
		tree.sortTreeNodeChildren(customFile.getParent().getTreeNode());
		tree.setSelectionPath(new TreePath(MainData.getDirTreeModel().getPathToRoot(customFile.getTreeNode())));
	}

	public void updateAfterRenameDataFile(CustomFile customFile) {
		customFile.getParent().removeFile(customFile);
		customFile.setName(customFile.getNewName());
		customFile.setNewName("");
		customFile.getParent().addFile(customFile);
		tree.sortTreeNodeChildren(customFile.getParent().getTreeNode());
		tree.setSelectionPath(new TreePath(MainData.getDirTreeModel().getPathToRoot(customFile.getTreeNode())));
	}

	public void updateAfterDeleteDataFile(CustomFile customFile) {
		customFile.getParent().removeFile(customFile);
		MainData.getDirTreeModel().removeNodeFromParent(customFile.getTreeNode());
		tree.setSelectionPath(new TreePath(MainData.getDirTreeModel().getPathToRoot(customFile.getParent().getTreeNode())));
	}
	
}
