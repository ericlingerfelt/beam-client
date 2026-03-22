/*******************************************************************************
 * This file is part of the Bellerophon client side application.
 * 
 * Filename: FilterUtils.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2009 - 2013, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.file;

import java.io.*;


/**
 * The Class FilterUtils.
 *
 * @author Eric J. Lingerfelt
 */
public class FilterUtils {

	/**
	 * The Enum ImageFileType.
	 *
	 * @author Eric J. Lingerfelt
	 */
	public enum ImageFileType{jpeg, jpg, gif, tiff, tif, png, bmp};

	/**
     * The Enum TextFileType.
     *
     * @author Eric J. Lingerfelt
     */
    public enum TextFileType{dat, rtf, txt}
    
	/**
     * The Enum OpenOfficeFileType.
     *
     * @author Eric J. Lingerfelt
     */
    public enum OpenOfficeFileType{sxc, sxd, sxm, sxw}
    
	/**
     * The Enum MicrosoftOfficeFileType.
     *
     * @author Eric J. Lingerfelt
     */
    public enum MicrosoftOfficeFileType{xls, ppt, doc, xlsx, pptx, docx}
    
	/**
     * The Enum OtherDocumentFileType.
     *
     * @author Eric J. Lingerfelt
     */
    public enum OtherDocumentFileType{ps, pdf, eps, wpd, tex}
    
	/**
     * The Enum WebFileType.
     *
     * @author Eric J. Lingerfelt
     */
    public enum WebFileType{htm, html, xml}
    
	/**
     * The Enum CompressedFileType.
     *
     * @author Eric J. Lingerfelt
     */
    public enum CompressedFileType{tar, z, gz, zip, tgz}
	
	/**
	 * The Enum AllFileType.
	 *
	 * @author Eric J. Lingerfelt
	 */
	public enum AllFileTypes{jpeg, jpg, gif, tiff, tif, png, bmp, 
								dat, rtf, txt, 
								sxc, sxd, sxm, sxw, 
								xls, ppt, doc, xlsx, pptx, docx, 
								ps, pdf, eps, wpd, tex, 
								htm, html, xml, 
								tar, z, gz, zip, tgz}
    
    /**
     * Gets the extension.
     *
     * @param f the f
     * @return the extension
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	
}
