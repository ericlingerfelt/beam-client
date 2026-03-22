/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: StepPanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.wizard;

import gov.ornl.bellerophon.beam.ui.format.Fonts;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The StepPanel class is a JPanel that displays the the current step number 
 * and maximum step number in the upper right hand corner of a Wizard panel.
 * 
 * @author Eric J. Lingerfelt
 */
public class StepPanel extends JPanel{
	
	private JLabel label;
	private int numberOfSteps, currentStep;
	
	/**
	 * The Constructor.
	 *
	 * @param currentStep the current step number
	 * @param numberOfSteps the maximum number of steps
	 */
	public StepPanel(int currentStep, int numberOfSteps){
		this.numberOfSteps = numberOfSteps;
		setLayout(new FlowLayout());
		label = new JLabel("Step " + String.valueOf(currentStep) + " of " + String.valueOf(numberOfSteps));
		label.setFont(Fonts.titleFont);
		add(label);
	}
	
	/**
	 * Sets the current step number of this class.
	 * 
	 * @param currentStep the current step number to display
	 */
	public void setCurrentStep(int currentStep){
		this.currentStep = currentStep;
		label.setText("Step " + currentStep + " of " + numberOfSteps);
	}
	
	/**
	 * Sets the maximum number of steps.
	 * 
	 * @param numberOfSteps the maximum number of steps
	 */
	public void setNumberOfSteps(int numberOfSteps){
		this.numberOfSteps = numberOfSteps;
		label.setText("Step " + currentStep + " of " + numberOfSteps);
	}
	
}
