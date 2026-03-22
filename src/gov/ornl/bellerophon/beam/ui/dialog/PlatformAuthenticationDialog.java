/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: PlatformAuthenticationDialog.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import info.clearthought.layout.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;

/**
 * The Class LogInDialog creates a dialog for entering the username and password of a user.
 *
 * @author Eric J. Lingerfelt
 */
public class PlatformAuthenticationDialog extends JDialog implements ActionListener, KeyListener{

	private JTextField usernameField;
	private JButton submitButton, cancelButton;
	private JPasswordField passwordField;
	private int value;
	private String passwordString;
	private AnalysisProcess process;
	public static final int SUBMIT = 1;
	public static final int CANCEL = 0;

	/**
	 * The Constructor.
	 *
	 * @param owner the owner
	 */
	public PlatformAuthenticationDialog(Window owner, AnalysisProcess process, String passwordString){
		
		super(owner, "Please enter your credentials for the " 
							+ process.getAnalysisFunction().getAnalysisPlatform().getName() 
							+ " below"
							, Dialog.ModalityType.APPLICATION_MODAL);
		
		this.process = process;
		this.passwordString = passwordString;
		
		setSize(550, 180);
		setLocationRelativeTo(owner);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			} 
		});
		
		double gap = 10;
		double[] col = {gap, TableLayoutConstants.FILL, gap};
		double[] row = {20, TableLayoutConstants.FILL, 5, TableLayoutConstants.PREFERRED, 20};
		
		Container c = getContentPane();
		c.setLayout(new TableLayout(col, row));
		
		usernameField = new JTextField();
		usernameField.addKeyListener(this);
		passwordField = new JPasswordField();
		passwordField.addKeyListener(this);
		
		JLabel usernameLabel = new JLabel("Username");
		JLabel passwordLabel = new JLabel(passwordString);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		
		double[] colData = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.FILL};
		double[] rowData = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		JPanel dataPanel = new JPanel();
		dataPanel.setLayout(new TableLayout(colData, rowData));
		dataPanel.add(usernameLabel, "0, 0, r, c");
		dataPanel.add(usernameField, "2, 0, f, c");
		dataPanel.add(passwordLabel, "0, 2, r, c");
		dataPanel.add(passwordField, "2, 2, f, c");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(submitButton);
		buttonPanel.add(cancelButton);

		c.add(dataPanel, "1, 1, f, f");
		c.add(buttonPanel, "1, 3, c, c");
		
		initialize();
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent ke){
		if(ke.getKeyCode()==KeyEvent.VK_ENTER){
			submitValues();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent ke){}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent ke){}
	
	/**
	 * Initializes this dialog.
	 */
	private void initialize(){
		usernameField.setText("");
		passwordField.setText("");
	}
	
	/**
	 * Submits the username and password values for validation.
	 */
	private void submitValues(){
		
		if(usernameField.getText().trim().equals("")){
			ErrorDialog.createDialog(this, "Please enter a value for Username.");
			return;
		}
		if(String.valueOf(passwordField.getPassword()).trim().equals("")){
			ErrorDialog.createDialog(this, "Please enter a value for " + passwordString + ".");
			return;
		}
		
		String username = usernameField.getText().trim();
		String password = String.valueOf(passwordField.getPassword()).trim();
		
		process.setUsername(username);
		process.setPassword(password);
		
		value = SUBMIT;
		
		setVisible(false);
			
	}
	

	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==submitButton){
			submitValues();
		}else if(ae.getSource()==cancelButton){
			value = CANCEL;
			process.setUsername("");
			process.setPassword("");
			setVisible(false);
		}
	}	
	
	public static int createPlatformAuthenticationDialog(Window owner, AnalysisProcess process, String passwordString){
		PlatformAuthenticationDialog dialog = new PlatformAuthenticationDialog(owner, process, passwordString);
		dialog.setVisible(true);
		return dialog.value;
	}
}