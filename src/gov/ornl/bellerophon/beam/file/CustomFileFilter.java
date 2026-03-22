/*******************************************************************************
 * This file is part of the Bellerophon client side application.
 * 
 * Filename: CustomFileFilter.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2009 - 2013, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.file;

import java.io.File;

/**
 * The Class CustomFileFilter.
 *
 * @author Eric J. Lingerfelt
 */
public class CustomFileFilter extends javax.swing.filechooser.FileFilter{
	
	private String extension, description;
	private FileType fileType;
	
	/**
	 * Instantiates a new custom file filter.
	 *
	 * @param fileType the file type
	 */
	public CustomFileFilter(FileType fileType){
		this.fileType = fileType;
		this.extension = fileType.name().toLowerCase();
		this.description = fileType.toString();
	}
	
	/**
	 * Gets the file type.
	 *
	 * @return the file type
	 */
	public FileType getFileType(){
		return fileType;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * Gets the extension.
	 *
	 * @return the extension
	 */
	public String getExtension(){
		return extension;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File file){
		if(file.isDirectory()){return true;}
		String fileExtension = getFileExtension(file);
		if(fileExtension!=null){
			return fileExtension.equals(extension);
		}
		return false;
	}
	
	/**
	 * Gets the file extension.
	 *
	 * @param file the file
	 * @return the file extension
	 */
	public String getFileExtension(File file){
		String ext = null;
		String s = file.getName();
		int i = s.lastIndexOf('.');
		if(i>0 && i<(s.length() - 1)){
			ext = s.substring(i+1).toLowerCase();
		}
		return ext;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(!(o instanceof CustomFileFilter)){
			return false;
		}
		CustomFileFilter cff = (CustomFileFilter)o;
		return cff.fileType==fileType;
	}
	
}

