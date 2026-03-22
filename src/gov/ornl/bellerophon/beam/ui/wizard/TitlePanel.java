/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: TitlePanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.wizard;

import gov.ornl.bellerophon.beam.ui.format.Fonts;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;

import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * The TitlePanel class is a JPanel that displays feature title and
 * the step title in the upper left had corner of a Wizard panel.
 * 
 * @author Eric J. Lingerfelt
 */
public class TitlePanel extends JPanel{

	private WordWrapLabel label;
	private String featureString;
	
	/**
	 * The Constructor.
	 *
	 * @param featureString the feature title
	 * @param panelString the step title
	 */
	public TitlePanel(String featureString, String panelString){
		this.featureString = featureString;
		setLayout(new FlowLayout());
		label = new WordWrapLabel(true);
		label.setText(featureString + " | " + panelString);
		label.setFont(Fonts.titleFont);
		add(label);
	}
	
	/**
	 * Sets the string to display on this step.
	 * 
	 * @param panelString the step title
	 */
	public void setPanelString(String panelString){
		label.setText(featureString + " | " + panelString);
	}
	
}
