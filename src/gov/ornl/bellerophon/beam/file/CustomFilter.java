/*******************************************************************************
 * This file is part of the Bellerophon client side application.
 * 
 * Filename: CustomFilter.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2009 - 2013, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.file;

import java.io.File;
import javax.swing.filechooser.*;

/**
 * The Class CustomFilter.
 *
 * @author Eric J. Lingerfelt
 */
public class CustomFilter extends FileFilter {
	
	private FilterType filterType;
	
	/**
	 * Instantiates a new custom filter.
	 *
	 * @param filterType the filter type
	 */
	public CustomFilter(FilterType filterType){
		this.filterType = filterType;
	}
	
	/**
	 * Gets the filter type.
	 *
	 * @return the filter type
	 */
	public FilterType getFilterType(){
		return this.filterType;
	}
	
    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = FilterUtils.getExtension(f);
        if (extension != null) {
            if (filterType.containsValue(extension)) {
            	return true;
            }
			return false;
        }

        return false;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return filterType.toString();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o){
		if(!(o instanceof CustomFilter)){
			return false;
		}
		CustomFilter cf = (CustomFilter)o;
		return cf.filterType==filterType;
	}
}