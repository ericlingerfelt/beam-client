/*******************************************************************************
 * This file is part of the Bellerophon client side application.
 * 
 * Filename: FilterType.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2009 - 2013, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.file;


/**
 * The Enum FilterType.
 *
 * @author Eric J. Lingerfelt
 */
public enum FilterType {
	
	IMAGE("Image File Types", FilterUtils.ImageFileType.values()),
	TEXT("Text File Types", FilterUtils.TextFileType.values()),
	OPEN("Open Office File Types", FilterUtils.OpenOfficeFileType.values()),
	MICROSOFT("Microsoft Office File Types", FilterUtils.MicrosoftOfficeFileType.values()),
	OTHER("Other Document File Types", FilterUtils.OtherDocumentFileType.values()),
	WEB("Web File Types", FilterUtils.WebFileType.values()),
	COMPRESSED("Compressed File Types", FilterUtils.CompressedFileType.values()),
	ALL("All Acceptable File Types", FilterUtils.AllFileTypes.values());
	
	private String string;
	private Object[] values;
	
	/**
	 * Instantiates a new filter type.
	 *
	 * @param string the string
	 * @param values the values
	 */
	FilterType(String string, Object[] values){
		this.string = string;
		this.values = values;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
		String str = string;
		if(!this.name().equals("ALL")){
			str += " (";
			for(int i=0; i<this.values.length; i++){
				str += "*." + this.values[i].toString();
				if(i!=this.values.length-1){
					str += ",";
				}
			}
			str += ")";
		}
		return str;
	}
	
	/**
	 * Contains value.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean containsValue(String value){
		for(int i=0; i<values.length; i++){
			if(values[i].toString().equals(value)){
				return true;
			}
		}
		return false;
	}
}
