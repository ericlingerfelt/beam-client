/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: Beam.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam;

import java.awt.*;
import java.util.Iterator;

import javax.swing.tree.DefaultTreeModel;

import com.alee.laf.WebLookAndFeel;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.CustomFile;
import gov.ornl.bellerophon.beam.enums.*;
import gov.ornl.bellerophon.beam.exception.UncaughtExceptionHandler;
import gov.ornl.bellerophon.beam.ui.*;
import gov.ornl.bellerophon.beam.ui.dialog.*;
import gov.ornl.bellerophon.beam.ui.worker.GetAnalysisFunctionsWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetAnalysisPlatformsWorker;
import gov.ornl.bellerophon.beam.ui.worker.GetCompleteDirListingWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetAnalysisFunctionsListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetAnalysisPlatformsListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetCompleteDirListingListener;

/**
 * The Class Beam is the top level class for the client side application. 
 *
 * @author Eric J. Lingerfelt
 */
public class Beam implements GetCompleteDirListingListener, GetAnalysisPlatformsListener, GetAnalysisFunctionsListener{
	
	private BeamFrame frame;
	
	/**
	 * The Constructor. This constructor creates the "Log In" dialog and the "Notice to Users" dialog 
	 * if the log in was successful. Following that the constructor instantiates a new BeamFrame object.
	 */
	public Beam(){
		
		MainData.initialize();
		
		new LogInDialog(new Frame());
		
		String string = "<html><b>Notice to Users</b>"
			+ " Use of this system constitutes consent to security monitoring. "
			+ "Improper use could lead to appropriate disciplinary or legal action.<br><br>"
			+ "<b>Disclaimer to Users</b> This software suite is in development. "
			+ "Please contact Eric Lingerfelt at eric@pandiasoftware.com to report bugs or problems. Thank you.</html>";
		//MessageDialog.createMessageDialog(new Frame(), string, "Notice", new Dimension(400, 300));
		
		CustomFile rootDirFile = new CustomFile();
		rootDirFile.setName(MainData.getUser().getUsername());
		rootDirFile.setPath("");
		rootDirFile.setDir(true);
		rootDirFile.setPop(true);
		
		DefaultTreeModel model = new DefaultTreeModel(rootDirFile.getTreeNode());
		MainData.setDirTreeModel(model);
		
		frame = new BeamFrame();
		MainData.addBeamWindow(frame);
		frame.setVisible(true);
		
		GetCompleteDirListingWorker worker = new GetCompleteDirListingWorker(this, rootDirFile, frame);
		worker.execute();
		
	}
	
	public void updateAfterGetCompleteDirListing(CustomFile rootDirFile){
		createNodesForCustomFile(rootDirFile, MainData.getDirTreeModel());
		GetAnalysisPlatformsWorker worker = new GetAnalysisPlatformsWorker(this, frame);
		worker.execute();
	}
	
	public void updateAfterGetAnalysisPlatforms(){
		GetAnalysisFunctionsWorker worker = new GetAnalysisFunctionsWorker(this, frame);
		worker.execute();
	}
	
	public void updateAfterGetAnalysisFunctions(){}
	
	private void createNodesForCustomFile(CustomFile customFile, DefaultTreeModel model){
		if(customFile.isPop()){
			Iterator<CustomFile> itr = customFile.getFileIterator();
			while(itr.hasNext()){
				CustomFile cf = itr.next();
				model.insertNodeInto(cf.getTreeNode(), customFile.getTreeNode(), customFile.getTreeNode().getChildCount());
				createNodesForCustomFile(cf, model);
			}
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		try{
			if(args.length!=3){
				
				System.err.println("Usage - java Beam PROD|DEV|DEV_2 RELEASE|DEBUG LOCAL|REMOTE");
				System.exit(1);
				
			}else if((!args[0].equalsIgnoreCase("PROD") 
					&& !args[0].equalsIgnoreCase("DEV")
					&& !args[0].equalsIgnoreCase("DEV_2"))
					|| (!args[1].equalsIgnoreCase("RELEASE") && !args[1].equalsIgnoreCase("DEBUG"))
					|| (!args[2].equalsIgnoreCase("LOCAL") && !args[2].equalsIgnoreCase("REMOTE"))){
				
				System.err.println("Usage - java Beam PROD|DEV|DEV_2 RELEASE|DEBUG LOCAL|REMOTE");
				System.exit(1);
				
			}else{
				
				if(args[0].equalsIgnoreCase("PROD")){
					MainData.setURLType(URLType.PROD);
				}else if(args[0].equalsIgnoreCase("DEV")){
					MainData.setURLType(URLType.DEV);
				}else if(args[0].equalsIgnoreCase("DEV_2")){
					MainData.setURLType(URLType.DEV_2);
				}
				
				if(args[1].equalsIgnoreCase("DEBUG")){
					MainData.setDebug(true);
				}else if(args[1].equalsIgnoreCase("RELEASE")){
					MainData.setDebug(false);
				}
				
				if(args[2].equalsIgnoreCase("LOCAL")){
					MainData.setResourceType(ResourceType.LOCAL);
				}else if(args[2].equalsIgnoreCase("REMOTE")){
					MainData.setResourceType(ResourceType.REMOTE);
				}
				
				String osName = System.getProperty("os.name");
				if(osName.startsWith("Windows")){
					MainData.setSystemType(SystemType.WINDOWS);
		    	}else if(osName.startsWith("Mac OS")){
		    		MainData.setSystemType(SystemType.MAC);
		    	}else{
		    		MainData.setSystemType(SystemType.LINUX);
		    	}
				
				Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
				
				java.awt.EventQueue.invokeLater(new Runnable(){
		            public void run(){
		            	WebLookAndFeel.install();
		            	new Beam();	
		            }
				});
			}
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}
