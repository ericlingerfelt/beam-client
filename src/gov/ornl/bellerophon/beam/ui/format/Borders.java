/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: Borders.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.format;

import javax.swing.BorderFactory;
import javax.swing.border.*;
import java.awt.*;

/**
 * The Class Borders.
 *
 * @author Eric J. Lingerfelt
 */
public class Borders {
	
	/**
	 * Gets the border font.
	 *
	 * @return the border font
	 */
	
	public static Font getBorderFont(){
		Border blackline = BorderFactory.createLineBorder(Color.black);
		TitledBorder title = BorderFactory.createTitledBorder(blackline, "TEST");
		title.setTitleJustification(TitledBorder.CENTER);
		title.setTitleColor(Color.black);
		return title.getTitleFont();
	}
	
	/**
	 * Gets the border.
	 *
	 * @param string the string
	 * @return the border
	 */
	public static Border getBorder(String string){
		Border blackline = BorderFactory.createLineBorder(Color.black);
		TitledBorder title = BorderFactory.createTitledBorder(blackline, string);
		title.setTitleJustification(TitledBorder.CENTER);
		title.setTitleColor(Color.black);
		return title;
	}
	
}
