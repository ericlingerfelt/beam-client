/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: Feature.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.enums;

/**
 * The Enum Feature contains values for each Bellerophon feature.
 *
 * @author Eric J. Lingerfelt
 */
public enum Feature {
	
	BE_ANALYZER("BE Analyzer", "<html>BE Analyzer</html>"), 
	ATOM_FINDER("Atom Finder", "<html>Atom Finder</html>"),
	IMAGE_PROCESSOR("Image Processor", "<html>Image Processor</html>"),
	MULTIVARIATE_ANALYZER("Multivariate Analyzer", "<html>Multivariate Analyzer</html>"),
	DATA_MANAGER("Data Manager", "<html>Data Manager</html>"),
	ACCOUNT_MANAGER("My Account", "<html>My Account</html>"),
	LOGOUT("Log Out and Exit", "<html>Log Out and Exit</html>"), 
	QENS_OPTIMIZER("QENS Optimizer", "<html>QENS Optimizer</html>"),
	GINS_SIMULATOR("GINS Simulator", "<html>GINS Simulator</html>"),
	INS_OPTIMIZER("INS Optimizer", "<html>INS Optimizer</html>");
	
	private String string, htmlString;
	
	/**
	 * Instantiates a new feature.
	 *
	 * @param string the string
	 * @param htmlString the html string
	 */
	Feature(String string, String htmlString){
		this.string = string;
		this.htmlString = htmlString;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}	
	
	/**
	 * Gets the hTML string.
	 *
	 * @return the hTML string
	 */
	public String getHTMLString(){return htmlString;}
}
