/*******************************************************************************
 * This file is part of the Bellerophon client side application.
 * 
 * Filename: FileUtils.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2009 - 2013, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.file;


/**
 * The Class FileUtils.
 *
 * @author Eric J. Lingerfelt
 */
public class FileUtils {

	/**
	 * Validate filename.
	 *
	 * @param filename the filename
	 * @return the string
	 */
	public static String validateFilename(String filename){
		if(filename.equals("")){
			return "The name for the folder must not be blank.";
		}else if(filename.startsWith(".") || !filename.matches("[._a-zA-Z0-9]+")){
			return "Folder names may only contain letters, numbers, underscore, and period. " +
						"Also, the first character of a filename can not be a period.";
		}else if(filename.length()>100){
			return "Folder names must be less than 100 characters.";
		}
		return "";
	}
	
}
