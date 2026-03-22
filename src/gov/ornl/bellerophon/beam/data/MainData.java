/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: MainData.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import gov.ornl.bellerophon.beam.data.util.*;
import gov.ornl.bellerophon.beam.enums.*;
import gov.ornl.bellerophon.beam.ui.BeamFrame;

import javax.swing.filechooser.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * The Class MainData is the main data structure for the application. MainData's methods are static 
 * so that these fields can be accessed without instantiation. 
 *
 * @author Eric J. Lingerfelt
 */
public class MainData{

	private static URLType urlType;
	private static SystemType systemType;
	private static ResourceType resourceType;
	private static boolean debug, warningShown, downloading;
	private static String id;
	private static java.io.File absolutePath;
	private static User user;
	private static DefaultTreeModel dirTreeModel;
	private static CustomFile rootDirFile;
	private static long totalDataSize;
	private static TreeMap<Integer, BeamFrame> windowMap;
	private static TreeMap<String, AnalysisPlatform> platformMap;
	private static TreeMap<Integer, AnalysisFunction> functionMap;
	private static int windowIndexCounter = 0;
	
	public static String BEAM_URL;
	public static String PHP_URL;
	public static String DATA_URL;
	public static String SCP_PRIVATE_KEY = "";
	public static final String SERVER_URL = "https://nucastrodata2.ornl.gov";
	public static final String SERVER_DOMAIN_NAME = "nucastrodata2.ornl.gov";
	public static final String VERSION = "1.0.0";
	public static final long MAX_DATA_SIZE = (long) 50.0E9;
	public static final String SERVER_HOST_KEY = "nucastrodata2.ornl.gov,160.91.134.61 ecdsa-sha2-nistp256 "
												+ "AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBKZR6"
												+ "Ch/IbQ3B3D6RV8yU2yEOHJzAvNQMM0VSfCSnecpYFDOhT0fB+j8pCngnKt"
												+ "PT6f0geFzEHqDP8KkzscgzhI=";
	
	public static final boolean doSNSDemo = false;
	
	/**
	 * Initializes the data structure.
	 */
	public static void initialize(){
		windowMap = new TreeMap<Integer, BeamFrame>();
		platformMap = new TreeMap<String, AnalysisPlatform>();
		functionMap = new TreeMap<Integer, AnalysisFunction>();
		downloading = false;
		user = null;
		id = "";
		warningShown = false;
		absolutePath = new File(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/Desktop");
		dirTreeModel = null;
		rootDirFile = null;
	}
	
	public static void addAnalysisFunction(AnalysisFunction analysisFunction){
		functionMap.put(analysisFunction.getIndex(), analysisFunction);
	}
	public static ArrayList<AnalysisFunction> getAnalysisFunctions(AnalysisFunctionType analysisFunctionType){
		ArrayList<AnalysisFunction> list = new ArrayList<AnalysisFunction>();
		Iterator<AnalysisFunction> itr = functionMap.values().iterator();
		while(itr.hasNext()){
			AnalysisFunction af = itr.next();
			if(af.getAnalysisFunctionType()==analysisFunctionType){
				list.add(af);
			}
		}
		return list;
	}
	
	public static void addAnalysisPlatform(AnalysisPlatform analysisPlatform){
		platformMap.put(analysisPlatform.getName(), analysisPlatform);
	}
	public static AnalysisPlatform getAnalysisPlatform(String analysisPlatformName){return platformMap.get(analysisPlatformName);}
	
	public static void addBeamWindow(BeamFrame frame){
		windowIndexCounter++;
		frame.setWindowIndex(windowIndexCounter);
		windowMap.put(windowIndexCounter, frame);
	}
	
	public static void removeBeamWindow(BeamFrame frame){
		windowMap.remove(frame.getWindowIndex());
	}
	
	public static int getNumberOfBeamWindows(){
		return windowMap.size();
	}
	
	public static CustomFile getRootDirFile(){return rootDirFile;} 
	public static void setRootDirFile(CustomFile file){rootDirFile = file;}
	
	public static DefaultTreeModel getDirTreeModel(){return dirTreeModel;} 
	public static void setDirTreeModel(DefaultTreeModel model){dirTreeModel = model;}
	
	/**
	 * Gets the absolute path.
	 *
	 * @return the absolute path
	 */
	public static File getAbsolutePath(){return absolutePath;} 
	
	/**
	 * Sets the absolute path.
	 *
	 * @param file the new absolute path
	 */
	public static void setAbsolutePath(File file){absolutePath = file;}
	
	/**
	 * Gets the system type.
	 *
	 * @return the system type
	 */
	public static SystemType getSystemType(){return systemType;}
	
	/**
	 * Sets the system type.
	 *
	 * @param type the new system type
	 */
	public static void setSystemType(SystemType type){systemType = type;}
	
	/**
	 * Gets the resource type.
	 *
	 * @return the resource type
	 */
	public static ResourceType getResourceType(){return resourceType;}
	
	/**
	 * Sets the resource type.
	 *
	 * @param type the new resource type
	 */
	public static void setResourceType(ResourceType type){resourceType = type;}
	
	/**
	 * Gets the uRL type.
	 *
	 * @return the uRL type
	 */
	public static URLType getURLType(){return urlType;}
	
	/**
	 * Sets the uRL type.
	 *
	 * @param type the new uRL type
	 */
	public static void setURLType(URLType type){
		urlType = type;
		BEAM_URL = SERVER_URL + "/beam" + urlType.toString();
		PHP_URL = BEAM_URL + "/php/beam.php";
		DATA_URL = BEAM_URL + "/data";
	}
	
	/**
	 * Checks if is debug.
	 *
	 * @return true, if is debug
	 */
	public static boolean isDebug(){return debug;} 
	
	/**
	 * Sets the debug.
	 *
	 * @param flag the new debug
	 */
	public static void setDebug(boolean flag){debug = flag;}
	
	/**
	 * Checks if is downloading.
	 *
	 * @return true, if is downloading
	 */
	public static boolean isDownloading(){return downloading;} 
	
	/**
	 * Sets the downloading.
	 *
	 * @param flag the new downloading
	 */
	public static void setDownloading(boolean flag){downloading = flag;}
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public static User getUser(){return user;} 
	
	/**
	 * Sets the user.
	 *
	 * @param u the new user
	 */
	public static void setUser(User u){user = u;}
	
	/**
	 * Gets the iD.
	 *
	 * @return the iD
	 */
	public static String getID(){return id;} 
	
	/**
	 * Sets the iD.
	 *
	 * @param string the new iD
	 */
	public static void setID(String string){id = string;}
	
	/**
	 * Checks if is warning shown.
	 *
	 * @return true, if is warning shown
	 */
	public static boolean isWarningShown(){return warningShown;} 
	
	/**
	 * Sets the warning shown.
	 *
	 * @param flag the new warning shown
	 */
	public static void setWarningShown(boolean flag){warningShown = flag;}

	public static long getTotalDataSize(){
		totalDataSize = 0L;
		if(dirTreeModel!=null){
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) dirTreeModel.getRoot();
			CustomFile rootDirFile = (CustomFile) rootNode.getUserObject();
			getTotalDataSizeForCustomFile(rootDirFile);
		}
		return totalDataSize;
	}
	
	private static void getTotalDataSizeForCustomFile(CustomFile customFile){
		if(customFile.isPop()){
			Iterator<CustomFile> itr = customFile.getFileIterator();
			while(itr.hasNext()){
				CustomFile cf = itr.next();
				if(!cf.isDir()){
					totalDataSize += ((DataFile)cf).getSize();
				}
				getTotalDataSizeForCustomFile(cf);
			}
		}
	}
	
}


