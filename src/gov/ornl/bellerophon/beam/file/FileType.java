/*******************************************************************************
 * This file is part of the Bellerophon client side application.
 * 
 * Filename: FileType.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2009 - 2013, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.file;

/**
 * The Enum FileType.
 *
 * @author Eric J. Lingerfelt
 */
public enum FileType {

	HTML("HTML Document (*.html)"), 
	TXT("Text (Tab Delimited) (*.txt)"), 
	DAT("Grace Data (*.dat)"), 
	SILO("Silo Data (*.silo)"), 
	RTF("Rich Text Format (*.rtf)"),
	PDF("Portable Document Format (*.pdf)"),
	EPS("Encapsulated PostScript (*.eps)"),
	PS("PostScript (*.ps)"),
	DOC("Microsoft Word Document (*.doc)"),
	PPT("Microsoft PowerPoint Presentation (*.ppt)"),
	XLS("Microsoft Excel 97/2000/2003 Workbook (*.xls)"),
	XLSX("Microsoft Excel 2007/2010 Workbook (*.xlsx)"),
	PNG("Portable Network Graphics (*.png)"),
	GIF("Graphics Interchange Format (*.gif)"),
	JPG("Joint Photographic Experts Group (*.jpg)"),
	WMV("Windows Media Video (*.wmv)"),
	MOV("QuickTime Movie (*.mov)"),
	MPG("Moving Pictures Expert Group (*.mpg)"),
	AVI("Audio Video Interleave (*.avi)"),
	ZIP("Zip File (*.zip)"),
	TGZ("GZip Compressed Tar File (*.tgz)"),
	MP4("MPEG-4 Movie (*.mp4)"), 
	H5("HDF5 (*.h5)"), 
	PY("Python (*.py)"),
	SVG("Scalable Vector Graphics (*.svg)");
	
	private String string;
	
	/**
	 * Instantiates a new file type.
	 *
	 * @param string the string
	 */
	FileType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}
}
