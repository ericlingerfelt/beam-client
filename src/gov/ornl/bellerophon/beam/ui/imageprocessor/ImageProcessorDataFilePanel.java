/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: BEAnalyzerDataFilePanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.imageprocessor;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.feature.ImageProcessorData;
import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.file.CustomFileFilter;
import gov.ornl.bellerophon.beam.file.FileType;
import gov.ornl.bellerophon.beam.ui.dialog.CautionDialog;
import gov.ornl.bellerophon.beam.ui.dialog.SelectRemoteDataFileDialog;
import gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning.ImageProcessorPCAImageCleaningDataFileTreeCellRenderer;
import gov.ornl.bellerophon.beam.ui.util.CustomFileChangeListener;
import gov.ornl.bellerophon.beam.ui.util.DataFileSelectionListener;
import gov.ornl.bellerophon.beam.ui.util.DataFileTree;
import gov.ornl.bellerophon.beam.ui.util.DataFileTreeNodeSelectionListener;
import gov.ornl.bellerophon.beam.ui.util.PlainFileChooserFactory;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import gov.ornl.bellerophon.beam.ui.wizard.upload.UploadDataFileWizard;
import gov.ornl.bellerophon.beam.ui.worker.DownloadDataFileWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetDataFileTreeWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.DownloadDataFileListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetDataFileTreeListener;
import info.clearthought.layout.*;

import java.awt.Frame;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class ImageProcessorDataFilePanel extends JPanel implements ActionListener, 
																	DownloadDataFileListener, 
																	CustomFileChangeListener,
																	GetDataFileTreeListener{

	private Frame frame;
	private ImageProcessorData d;
	private JButton selectDataFileButton, uploadDataFileButton, downloadDataFileButton;
	private JTextArea infoArea;
	private JScrollPane spTree, spInfo;
	private DataFile dataFile;
	private JPanel buttonPanel;
	private WordWrapLabel selectLabel;
	private JTabbedPane pane;
	private DataFileSelectionListener dfsl;
	private DataFileTree tree;
	
	public ImageProcessorDataFilePanel(Frame frame, ImageProcessorData d, DataFileSelectionListener dfsl, DataFileTreeNodeSelectionListener dftnsl){
		
		this.frame = frame;
		this.d = d;
		this.dfsl = dfsl;
		
		selectLabel = new WordWrapLabel();
		selectLabel.setText("Please select a data file from your BEAM data storage area or upload a new data file to your"
				+ " BEAM data storage area for image processing.");
		
		selectDataFileButton = new JButton("Select");
		selectDataFileButton.addActionListener(this);
		
		uploadDataFileButton = new JButton("Upload");
		uploadDataFileButton.addActionListener(this);
		
		downloadDataFileButton = new JButton("Download");
		downloadDataFileButton.addActionListener(this);
		
		tree = new DataFileTree(dftnsl);
		tree.setDataFileTreeRenderer(new ImageProcessorPCAImageCleaningDataFileTreeCellRenderer());
		spTree = new JScrollPane(tree);
		
		infoArea = new JTextArea();
		infoArea.setWrapStyleWord(true);
		infoArea.setLineWrap(true);
		infoArea.setEditable(false);
		spInfo = new JScrollPane(infoArea);
		
		pane = new JTabbedPane();
		pane.add("File Info", spInfo);
		pane.add("Data Tree", spTree);
		
		buttonPanel = new JPanel();
		double[] colButton = {TableLayoutConstants.FILL
								, 7, TableLayoutConstants.FILL
								, 7, TableLayoutConstants.FILL};
		double[] rowButton = {TableLayoutConstants.PREFERRED};
		buttonPanel.setLayout(new TableLayout(colButton, rowButton));
		buttonPanel.add(selectDataFileButton,  	"0, 0, f, c");
		buttonPanel.add(uploadDataFileButton,  	"2, 0, f, c");
		buttonPanel.add(downloadDataFileButton, "4, 0, f, c");
		
	}

	public void setCurrentState(){
		
		this.dataFile = d.getDataFile();
		
		removeAll();
		
		if(dataFile==null){
			
			infoArea.setText("");
			
			double[] col = {10, TableLayoutConstants.FILL, 10};
			double[] row = {10, TableLayoutConstants.FILL
							, 10, TableLayoutConstants.PREFERRED, 10};
			setLayout(new TableLayout(col, row));
			add(selectLabel, "1, 1, f, c");
			add(buttonPanel, "1, 3, c, c");
			
			downloadDataFileButton.setEnabled(false);
			
		}else{
			
			infoArea.setText(dataFile.toStringInfo());
			infoArea.setCaretPosition(0);
			
			tree.setModel(dataFile.getTreeModel());
			
			if(d.getDataFile().getPCAImageCleaningDataSet()!=null && !d.getDataFile().getPCAImageCleaningDataSet().getGroupPath().equals("")){
				selectPCAImageCleaningGroupTreeNode();
			}
			
			double[] col = {10, TableLayoutConstants.FILL, 10};
			double[] row = {10, TableLayoutConstants.FILL
							, 10, TableLayoutConstants.PREFERRED, 10};
			setLayout(new TableLayout(col, row));
			add(pane, "1, 1, f, f");
			add(buttonPanel, "1, 3, c, c");
			
			downloadDataFileButton.setEnabled(true);

		}
		
		validate();
		repaint();
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==uploadDataFileButton){
			DataFile df = UploadDataFileWizard.createUploadDataFileWizard(frame);
			if(df!=null){
				if(d.getDataFile()!=null){
					d.getDataFile().removeCustomFileChangeListener(this);
				}
				d.setDataFile(df);
				d.getDataFile().addCustomFileChangeListener(this);
				df.getParent().addFile(df);
				MainData.getDirTreeModel().insertNodeInto(df.getTreeNode(), df.getParent().getTreeNode(), df.getParent().getTreeNode().getChildCount());
				MainData.getDirTreeModel().reload();
				GetDataFileTreeWorker worker = new GetDataFileTreeWorker(this, df, frame);
				worker.execute();
			}
		}else if(ae.getSource()==selectDataFileButton){
			DataFile df = SelectRemoteDataFileDialog.createSelectRemoteDataFileDialog(frame);
			if(df!=null){
				if(d.getDataFile()!=null){
					d.getDataFile().removeCustomFileChangeListener(this);
				}
				d.setDataFile(df);
				d.getDataFile().addCustomFileChangeListener(this);
				GetDataFileTreeWorker worker = new GetDataFileTreeWorker(this, df, frame);
				worker.execute();
			}
		}else if(ae.getSource()==downloadDataFileButton){
			File file = getDataSaveFile(d.getDataFile().getName());
			if(file!=null){
				d.getDataFile().setDownloadFile(file);
				DownloadDataFileWorker worker = new DownloadDataFileWorker(this, d.getDataFile(), frame);
				worker.execute();
			}
		}
	}
	
	private File getDataSaveFile(String filename){
		JFileChooser fileDialog = PlainFileChooserFactory.createPlainFileChooser();
		fileDialog.setAcceptAllFileFilterUsed(false);
		fileDialog.addChoosableFileFilter(new CustomFileFilter(FileType.H5));
		fileDialog.setSelectedFile(new File(filename));
		int returnVal = fileDialog.showSaveDialog(frame); 
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

	private void selectPCAImageCleaningGroupTreeNode(){
		String groupPath = d.getDataFile().getPCAImageCleaningDataSet().getGroupPath();
		DefaultMutableTreeNode groupNode = d.getDataFile().getDefaultMutableTreeNode(groupPath);
		tree.setSelectionPath(new TreePath(d.getDataFile().getTreeModel().getPathToRoot(groupNode)));
	}
	
	public void updateAfterDownloadDataFile(){

	}

	public void customFileChanged(){
		infoArea.setText(dataFile.toStringInfo());
		infoArea.setCaretPosition(0);	
	}

	public void updateAfterGetDataFileTree(){
		setCurrentState();
		if(dataFile!=null){
			dfsl.dataFileSelected();
		}
	}
	
	public void reloadTreeModelAfterPCAImageCleaning() {
		GetDataFileTreeWorker worker = new GetDataFileTreeWorker(this, d.getDataFile(), frame);
		worker.execute();
	}

}