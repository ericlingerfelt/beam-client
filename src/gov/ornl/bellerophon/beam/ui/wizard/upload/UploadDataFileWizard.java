/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: UploadDataFileWizard.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.wizard.upload;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.CustomFile;
import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import gov.ornl.bellerophon.beam.ui.wizard.WizardDialog;
import gov.ornl.bellerophon.beam.ui.worker.DataFileExistsWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.DataFileExistsListener;

public class UploadDataFileWizard extends WizardDialog implements ActionListener, DataFileExistsListener{

	private static Dimension SIZE = new Dimension(750, 550);
	private DataFile selectedDataFile = new DataFile();
	private CustomFile rootDirFile, selectedCustomFile;
	private File selectedLocalFile = null;
	public boolean fileUploaded = false;
	private Frame owner;
	
	private SelectLocalFilePanel selectPanel = new SelectLocalFilePanel(this);
	private ReviewDataFilePanel reviewPanel = new ReviewDataFilePanel();
	private EnterUserDataPanel enterPanel = new EnterUserDataPanel(this);
	private SelectRemoteDirPanel dirPanel = new SelectRemoteDirPanel(this);
	private UploadDataFilePanel uploadPanel = new UploadDataFilePanel(this);
	
	public static DataFile createUploadDataFileWizard(Frame owner, CustomFile selectedCustomFile){
		int numSteps = 0;
		if(selectedCustomFile!=null){
			numSteps = 4;
		}else{
			numSteps = 5;
		}
		UploadDataFileWizard wizard = new UploadDataFileWizard(owner, numSteps, selectedCustomFile);
		wizard.setVisible(true);
		if(wizard.fileUploaded){
			return wizard.selectedDataFile;
		}
		return null;
	}
	
	public static DataFile createUploadDataFileWizard(Frame owner){
		return createUploadDataFileWizard(owner, null);
	}
	
	public UploadDataFileWizard(Frame owner, int numSteps, CustomFile selectedCustomFile){
		super(owner, "Upload Data File", SIZE, numSteps);
		this.owner = owner;
		this.selectedCustomFile = selectedCustomFile;
		selectedDataFile = new DataFile();
		setNavActionListeners(this);
		initialize();
	}
	
	public void initialize(){
		
		if(selectedCustomFile!=null){
			selectedDataFile.setParent(selectedCustomFile);
			selectedDataFile.setPath(selectedCustomFile.getFullPath());
		}else{
			rootDirFile = new CustomFile();
			rootDirFile.setName(MainData.getUser().getUsername());
			rootDirFile.setPath("");
			rootDirFile.setDir(true);
		}

		addIntroButtons();
		selectPanel.setCurrentState(selectedLocalFile, selectedDataFile);
		setContentPanel(selectPanel, 1, "Select Data File to Upload", FULL_WIDTH);
	}

	@Override
	public void actionPerformed(ActionEvent ae){
		
		if(selectedCustomFile!=null){
			
			if(ae.getSource()==continueButton){
				switch(panelIndex){
					case 1:
						if(selectPanel.goodData()){
							if(selectPanel.goodFilename()){
								selectPanel.getCurrentState();
								reviewPanel.setCurrentState(selectedDataFile);
								addFullButtons();
								setContentPanel(selectPanel, reviewPanel, 2, 4, "Review Data File Information", CENTER);
							}else{
								String error = "Folder names may only contain letters, numbers, underscores, hyphens, and periods. " +
												"Also, the first character of a filename can not be a period.";
								AttentionDialog.createDialog(this, error);
							}
						}else{
							String error = "Please select an HDF5 data file from your computer.";
							AttentionDialog.createDialog(this, error);
						}
						break;
					case 2:
						enterPanel.setCurrentState(selectedDataFile);
						setContentPanel(reviewPanel, enterPanel, 3, 4, "Enter Data File Information", FULL);
						break;
					case 3:
						if(enterPanel.goodData()){
							enterPanel.getCurrentState();
							DataFileExistsWorker worker = new DataFileExistsWorker(this, selectedDataFile, owner);
							worker.execute();
						}else{
							String error = "Please ...";
							AttentionDialog.createDialog(this, error);
						}
						break;
				}
				
			}else if(ae.getSource()==backButton){
				switch(panelIndex){
					case 2:
						addIntroButtons();
						setContentPanel(reviewPanel, selectPanel, 1, 4, "Select Local File to Upload", FULL_WIDTH);
						break;
					case 3:
						setContentPanel(enterPanel, reviewPanel, 2, 4, "Review Data File Information", CENTER);
						break;
					case 4:
						addFullButtons();
						setContentPanel(uploadPanel, enterPanel, 3, 4, "Enter Data File Information", FULL);
						break;
				}
			}else if(ae.getSource()==endButton){
				setVisible(false);
			}
			
		}else{
		
			if(ae.getSource()==continueButton){
				switch(panelIndex){
					case 1:
						if(selectPanel.goodData()){
							if(selectPanel.goodFilename()){
								if(selectPanel.goodDataSize()){
									selectPanel.getCurrentState();
									reviewPanel.setCurrentState(selectedDataFile);
									addFullButtons();
									setContentPanel(selectPanel, reviewPanel, 2, 5, "Review Data File Information", CENTER);
								}else{
									DecimalFormat df  = new DecimalFormat("########0.0");
									String error = "BEAM Users are allotted 50GB of storage space. You are currently using "
													+ df.format(MainData.getTotalDataSize()/(long)1E9) + "GB of storage. The selected file can "
													+ "not be uploaded due to this constraint";
									AttentionDialog.createDialog(this, error);
								}
							}else{
								String error = "Folder names may only contain letters, numbers, underscores, hyphens, and periods. " +
												"Also, the first character of a filename can not be a period.";
								AttentionDialog.createDialog(this, error);
							}
						}else{
							String error = "Please select an HDF5 data file from your computer.";
							AttentionDialog.createDialog(this, error);
						}
						break;
					case 2:
						enterPanel.setCurrentState(selectedDataFile);
						setContentPanel(reviewPanel, enterPanel, 3, 5, "Enter Data File Information", FULL);
						break;
					case 3:
						if(enterPanel.goodData()){
							enterPanel.getCurrentState();
							dirPanel.setCurrentState(selectedDataFile);
							setContentPanel(enterPanel, dirPanel, 4, 5, "Select Directory", FULL);
						}else{
							String error = "Please ...";
							AttentionDialog.createDialog(this, error);
						}
						break;
					case 4:
						if(dirPanel.goodData()){
							dirPanel.getCurrentState();
							DataFileExistsWorker worker = new DataFileExistsWorker(this, selectedDataFile, owner);
							worker.execute();
						}else{
							String error = "Please select a directory from your BEAM data storage area.";
							AttentionDialog.createDialog(this, error);
						}
						break;
				}
				
			}else if(ae.getSource()==backButton){
				switch(panelIndex){
					case 2:
						addIntroButtons();
						setContentPanel(reviewPanel, selectPanel, 1, 5, "Select Local File to Upload", FULL_WIDTH);
						break;
					case 3:
						setContentPanel(enterPanel, reviewPanel, 2, 5, "Review Data File Information", CENTER);
						break;
					case 4:
						setContentPanel(dirPanel, enterPanel, 3, 5, "Enter Data File Information", FULL);
						break;
					case 5:
						addFullButtons();
						setContentPanel(uploadPanel, dirPanel, 4, 5, "Select Directory", FULL);
						break;
				}
			}else if(ae.getSource()==endButton){
				setVisible(false);
			}
		}
		
		validate();
	}

	public void updateAfterDataFileExists() {
		uploadPanel.setCurrentState(selectedDataFile);
		if(selectedCustomFile!=null){
			setContentPanel(enterPanel, uploadPanel, 4, 4, "Upload Data File to BEAM Data Storage Area", CENTER);
		}else{
			setContentPanel(dirPanel, uploadPanel, 5, 5, "Upload Data File to BEAM Data Storage Area", CENTER);
		}
		addBackButton();
		validate();
	}

}
