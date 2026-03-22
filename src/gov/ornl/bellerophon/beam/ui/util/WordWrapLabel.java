/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: WordWrapLabel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.util;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

/**
 * The Class WordWrapLabel is a specialized JEditorPane used to create a JLabel-like component that 
 * wraps around the GUI when it is resized.
 *
 * @author Eric J. Lingerfelt
 */
public class WordWrapLabel extends JEditorPane{

	private boolean isBold;
	
	/**
	 * The Constructor.
	 */
	public WordWrapLabel(){
		setEditable(false);
		setBorder(null);
		setEditorKit(new HTMLEditorKit());
		setBackground(null);
	}
	
	/**
	 * The Constructor.
	 *
	 * @param isBold a flag indicating whether to use bolded text.
	 */
	public WordWrapLabel(boolean isBold){
		this();
		this.isBold = isBold;
	}
	
	/**
	 * Sets the text of this label and forces the color of the text to be red.
	 *
	 * @param text the text
	 */
	public void setRedText(String text){
		super.setText(text);
		if(isBold){
			super.setText(getText().replaceAll("<body>", "<body><font face=\"sans-serif\" size=\"3\" color=\"#FF0000\"><b>"));
		}else{
			super.setText(getText().replaceAll("<body>", "<body><font face=\"sans-serif\" size=\"3\" color=\"#FF0000\">"));
		}
		if(text.indexOf("<td>")!=-1){
			super.setText(getText().replaceAll("<td>", "<td><font face=\"sans-serif\" size=\"3\" color=\"#FF0000\">"));
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JEditorPane#setText(java.lang.String)
	 */
	public void setText(String text){
		super.setText(text);
		if(isBold){
			super.setText(getText().replaceAll("<body>", "<body><font face=\"sans-serif\" size=\"3\"><b>"));
		}else{
			super.setText(getText().replaceAll("<body>", "<body><font face=\"sans-serif\" size=\"3\">"));
		}
		if(text.indexOf("<td>")!=-1){
			super.setText(getText().replaceAll("<td>", "<td><font face=\"sans-serif\" size=\"3\">"));
		}
		if(text.indexOf("<td align=\"center\">")!=-1){
			super.setText(getText().replaceAll("<td align=\"center\">", "<td align=\"center\"><font face=\"sans-serif\" size=\"3\">"));
		}
	}
	
}
