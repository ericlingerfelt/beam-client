/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: Strings.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.format;


/**
 * The Class Strings.
 *
 * @author Eric J. Lingerfelt
 */
public class Strings {

	/**
	 * Gets the plain text.
	 *
	 * @param string the string
	 * @return the plain text
	 */
	public static String getPlainText(String string){
		string = convertDecToHex(string);
		string = string.replace("&nbsp;", "");
		string = string.replace("</td><td>", "\t");
		string = string.replace("</tr>", "\n");
		string = string.replace("<br><br>", "\n");
		string = string.replaceAll("\\<.*?>","");
		string = string.trim();
		return string;
	}
	
	/**
	 * Clean value.
	 *
	 * @param string the string
	 * @return the string
	 */
	public static String cleanValue(String string){
		string = convertDecToHex(string);
		string = string.replace("<br>", "\n");
		string = string.replaceAll("\\<.*?>","");
		string = string.trim();
		return string;
	}
	
	/**
	 * Convert dec to hex.
	 *
	 * @param input the input
	 * @return the string
	 */
	private static String convertDecToHex(String input){
		String string = "";
		if(input.indexOf("&#")==-1){
			return input;
		}
		String[] array = input.split("&#");
		string += array[0];
		for(int i=1; i<array.length; i++){
			int dec = Integer.parseInt(array[i].substring(0, array[i].indexOf(";"))); 
			array[i] = array[i].substring(array[i].indexOf(";")+1);
			array[i] = (char)dec + array[i];
			string += array[i];
		}
		return string;
	}
	
}
