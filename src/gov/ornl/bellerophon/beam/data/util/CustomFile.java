/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: CustomFile.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.util;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.io.IOUtilities;
import gov.ornl.bellerophon.beam.ui.format.Calendars;
import gov.ornl.bellerophon.beam.ui.util.CustomFileChangeListener;

/**
 * The Class CustomFile is a wrapper data structure for a file on disk or in memory.
 *
 * @author Eric J. Lingerfelt
 */
public class CustomFile implements Data{

	protected String path, name, newPath, newName;
	private File file;
	protected boolean dir, pop, img;
	private byte[] contents;
	private TreeMap<String, CustomFile> fileMap;
	protected long size;
	protected Calendar createDate, modDate;
	private CustomFile parent;
	private DefaultMutableTreeNode treeNode; 
	private ArrayList<CustomFileChangeListener> customFileChangeListenerList;
	
	/**
	 * The Constructor.
	 */
	public CustomFile(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.data.Data#initialize()
	 */
	public void initialize(){
		path = "";
		name = "";
		newPath = "";
		newName = "";
		dir = false;
		pop = false;
		img = false;
		fileMap = new TreeMap<String, CustomFile>();
		contents = null;
		file = null;
		size = -1;
		createDate = Calendars.getDefaultCalendar();
		modDate = Calendars.getDefaultCalendar();
		parent = null;
		treeNode = new DefaultMutableTreeNode(this);
		customFileChangeListenerList = new ArrayList<CustomFileChangeListener>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public CustomFile clone(){
		CustomFile cf = new CustomFile();
		cf.path = path;
		cf.img = img;
		cf.contents = contents;
		cf.file = file;
		cf.dir = dir;
		cf.name = name;
		cf.size = size;
		cf.pop = pop;
		cf.createDate = (Calendar)createDate.clone();
		cf.modDate = (Calendar)modDate.clone();
		return cf;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(!(o instanceof CustomFile)){
			return false;
		}
		CustomFile f = (CustomFile)o;
		return f.path.equals(path) && f.name.equals(name);
	}
	
	public String toStringInfo(){
		String s = "";
		s += "Directory path = " + getFullPath() + "\n";
		//s += "Creation Date = " + Calendars.getFormattedOutputDateString(createDate) + "\n";
		//s += "Last Modification Date = " + Calendars.getFormattedOutputDateString(modDate) + "\n";
		return s;
	}
	
	public void addCustomFileChangeListener(CustomFileChangeListener cfcl){customFileChangeListenerList.add(cfcl);}
	public void removeCustomFileChangeListener(CustomFileChangeListener cfcl){customFileChangeListenerList.add(cfcl);}
	protected void fireCustomFileChanged(){
		for(CustomFileChangeListener cfcl: customFileChangeListenerList){
			cfcl.customFileChanged();
		}
	}
	
	public String getFullPath(){
		if(path.equals("")){
			return name;
		}
		return path + "/" + name;
	}
	
	public void addFile(CustomFile cf){
		fileMap.put(cf.getName(), cf);
		pop = true;
	}
	
	public void removeFile(CustomFile cf){
		fileMap.remove(cf.getName());
		if(fileMap.size()==0){
			pop = false;
		}
	}
	
	public boolean containsFile(String name){
		return fileMap.containsKey(name);
	}
	
	public boolean containsFile(CustomFile cf){
		return fileMap.containsKey(cf.getName());
	}
	
	public Iterator<CustomFile> getFileIterator(){
		return fileMap.values().iterator();
	}
	
	public TreeMap<String, CustomFile> getFileMap(){return fileMap;}
	public void setFileMap(TreeMap<String, CustomFile> fileMap){this.fileMap = fileMap;}
	
	public long getSize(){return size;}
	public void setSize(long size){
		this.size = size;
		fireCustomFileChanged();
	}
	
	public String getName(){return name;}
	public void setName(String name){
		this.name = name;
		Iterator<CustomFile> itr = fileMap.values().iterator();
		while(itr.hasNext()){
			itr.next().setPath(getFullPath());
		}
		fireCustomFileChanged();
	}
	
	public String getPath(){return path;}
	public void setPath(String path){
		this.path = path;
		Iterator<CustomFile> itr = fileMap.values().iterator();
		while(itr.hasNext()){
			itr.next().setPath(getFullPath());
		}
		fireCustomFileChanged();
	}
	
	public String getNewPath(){return newPath;}
	public void setNewPath(String newPath){this.newPath = newPath;}
	
	public CustomFile getParent(){return parent;}
	public void setParent(CustomFile parent){this.parent = parent;}

	public String getNewName(){return newName;}
	public void setNewName(String newName){this.newName = newName;}
	
	public File getFile(){return file;}
	public void setFile(File file){this.file = file;}
	
	public boolean isPop(){return pop;}
	public void setPop(boolean pop){this.pop = pop;}
	
	public boolean isImg(){return img;}
	public void setImg(boolean img){this.img = img;}
	
	public boolean isDir(){return dir;}
	public void setDir(boolean dir){this.dir = dir;}

	public void setContents(byte[] array) throws Exception{
		if(img){
			contents = array;
		}else{
			file = File.createTempFile("bellerophon", null);
			file.deleteOnExit();
			IOUtilities.writeFile(file, array);
		}
	}
	
	public byte[] getContents() throws IOException{
		if(img){
			return contents;
		}
		if(file==null){
			return null;
		}
		return IOUtilities.readFile(file);
	}

	public String toString(){return name;}
	
	public Calendar getModDate(){return modDate;}
	public void setModDate(Calendar modDate){this.modDate = modDate;}
	
	public Calendar getCreateDate(){return createDate;}
	public void setCreateDate(Calendar createDate){this.createDate = createDate;}
	
	public DefaultMutableTreeNode getTreeNode(){return treeNode;}
	
}
