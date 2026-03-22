/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: WizardDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.wizard;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import gov.ornl.bellerophon.beam.ui.format.*;
import info.clearthought.layout.*;

/**
 * The Class WizardFrame is the parent class to all wizards.
 *
 * @author Eric J. Lingerfelt
 */
public abstract class WizardDialog extends JDialog{

	protected JPanel introPanel;
	protected Container c;
	protected JPanel buttonPanel;
	protected JButton backButton, endButton, continueButton;
	protected int panelIndex;
	protected String panelString;
	private TitlePanel titlePanel;
	private StepPanel stepPanel;
	private int numberOfSteps;
	private JPanel currentPanel;
	private Dimension size;
	
	private static final String TITLE = "1, 1, 3, 1, l, t";
	private static final String STEP = "5, 1, r, t";
	protected static final String CENTER = "1, 3, 5, 3, c, c";
	protected static final String FULL = "1, 3, 5, 3, f, f";
	private static final String BUTTON = "1, 5, 5, 5, c, b";
	protected static final String FULL_WIDTH = "1, 3, 5, 3, f, c";
	
	/**
	 * The Constructor.
	 *
	 * @param title the feature's title
	 * @param continueOnTitle the title of the feature's Continue On button
	 * @param size the size
	 * @param numberOfSteps the number of steps
	 */
	public WizardDialog(Frame owner
						, final String title
						, Dimension size
						, int numberOfSteps){
		
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		setSize(size);
		setLocationRelativeTo(owner);
		
		this.size = size;
		this.numberOfSteps = numberOfSteps;

		titlePanel = new TitlePanel(title, panelString);
		stepPanel = new StepPanel(panelIndex, numberOfSteps);
		
		double border = 5;
		double gap = 5;
		double[] col = {border, TableLayoutConstants.FILL, gap, TableLayoutConstants.PREFERRED, gap, TableLayoutConstants.PREFERRED, border};
		double[] row = {border, TableLayoutConstants.PREFERRED, gap, TableLayoutConstants.FILL, gap, TableLayoutConstants.PREFERRED, border};
		
		c = getContentPane();
		c.setLayout(new TableLayout(col, row));
		
		backButton = new JButton("< Back");
		backButton.setFont(Fonts.buttonFont);
		
		continueButton = new JButton("Continue >");
		continueButton.setFont(Fonts.buttonFont);
		
		endButton = new JButton("Close " + title);
		endButton.setFont(Fonts.buttonFont);
		/*endButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				WizardDialog.this.setVisible(false);
				WizardDialog.this.dispose();
			}
		});*/
		
		panelIndex = 0;
		buttonPanel = new JPanel();
		
		c.add(titlePanel, TITLE);
		c.add(stepPanel, STEP);
		c.add(buttonPanel, BUTTON);
		
		addIntroButtons();

	}
	
	/**
	 * Sets the intro panel.
	 *
	 * @param introPanel the new intro panel
	 */
	protected void setIntroPanel(JPanel introPanel){this.introPanel = introPanel;}
	
	/**
	 * Sets the content panel.
	 *
	 * @param currentPanel the current panel
	 * @param newPanel the new panel
	 * @param panelIndex the panel index
	 * @param numberOfSteps the number of steps
	 * @param panelString the panel string
	 * @param constraints the constraints
	 */
	protected void setContentPanel(JPanel currentPanel
									, JPanel newPanel
									, int panelIndex
									, int numberOfSteps
									, String panelString
									, String constraints){
		
		this.panelIndex = panelIndex;
		this.numberOfSteps = numberOfSteps;
		this.currentPanel = currentPanel;
		stepPanel.setNumberOfSteps(numberOfSteps);
		stepPanel.setCurrentStep(panelIndex);
		titlePanel.setPanelString(panelString);
		stepPanel.setVisible(panelIndex!=0);
		titlePanel.setVisible(panelIndex!=0);
		if(currentPanel!=null){
			c.remove(currentPanel);
		}
		if(newPanel!=null){
			c.add(newPanel, constraints);
		}
		this.currentPanel = newPanel;
		c.repaint();
		validate();
	}
	
	/**
	 * Sets the panel full.
	 */
	public void setPanelFull(){
		c.add(currentPanel, FULL);
		validate();
		repaint();
	}
	
	/**
	 * Sets the panel centered.
	 */
	public void setPanelCenter(){
		c.add(currentPanel, CENTER);
		validate();
		repaint();
	}
	
	/**
	 * Sets the content panel.
	 *
	 * @param oldPanel the old panel
	 * @param newPanel the new panel
	 * @param panelIndex the panel index
	 * @param panelString the panel string
	 * @param constraints the constraints
	 */
	protected void setContentPanel(JPanel oldPanel, JPanel newPanel, int panelIndex, String panelString, String constraints){
		setContentPanel(oldPanel, newPanel, panelIndex, numberOfSteps, panelString, constraints);
	}
	
	/**
	 * Sets the content panel.
	 *
	 * @param newPanel the new panel
	 * @param panelIndex the panel index
	 * @param panelString the panel string
	 * @param constraints the constraints
	 */
	protected void setContentPanel(JPanel newPanel, int panelIndex, String panelString, String constraints){
		setContentPanel(currentPanel, newPanel, panelIndex, numberOfSteps, panelString, constraints);
	}
	
	/**
	 * Adds the intro buttons.
	 */
	protected void addIntroButtons(){
		buttonPanel.removeAll();
		buttonPanel.add(continueButton);
	}
	
	/**
	 * Adds the full buttons.
	 */
	protected void addFullButtons(){
		buttonPanel.removeAll();
		buttonPanel.add(backButton);
		buttonPanel.add(continueButton);
	}
	
	/**
	 * Adds the end buttons.
	 */
	protected void addEndButtons(){
		buttonPanel.removeAll();
		buttonPanel.add(backButton);
		buttonPanel.add(endButton);
	}
	
	/**
	 * Adds the end buttons.
	 */
	protected void addBackButton(){
		buttonPanel.removeAll();
		buttonPanel.add(backButton);
	}
	
	public void addEndButtonsWithoutBackButton(){
		buttonPanel.removeAll();
		buttonPanel.add(endButton);
	}
	
	/**
	 * Sets the navigation action listener.
	 *
	 * @param al the new navigation action listener
	 */
	protected void setNavActionListeners(ActionListener al){
		backButton.addActionListener(al);
		continueButton.addActionListener(al);
		endButton.addActionListener(al);
	}
	
	/**
	 * Initializes the feature.
	 */
	public void initialize(){
		setSize(size);
		c.removeAll();
		c.add(titlePanel, TITLE);
		c.add(stepPanel, STEP);
		c.add(buttonPanel, BUTTON);
		addIntroButtons();
		setContentPanel(introPanel, 0, "", CENTER);
	}
	
}