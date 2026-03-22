/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: LogInDialog.java
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

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.User;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.WebServiceCom;

import java.awt.Cursor;

/**
 * The Class LogInDialog creates a dialog for entering the username and password of a user.
 *
 * @author Eric J. Lingerfelt
 */
public class LogInDialog extends JDialog implements ActionListener, KeyListener{

	private JTextField usernameField;
	private JButton submitButton, cancelButton;
	private JPasswordField passwordField;
	
	/**
	 * The Constructor.
	 *
	 * @param owner the owner
	 */
	public LogInDialog(Window owner){
		
		super(owner, "Please log in with your XCAMS credentials below", Dialog.ModalityType.APPLICATION_MODAL);
		
		setSize(420, 160);
		setLocationRelativeTo(owner);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			} 
		});
		
		double gap = 10;
		double[] col = {gap, TableLayoutConstants.FILL, gap};
		double[] row = {gap, TableLayoutConstants.FILL, 5, TableLayoutConstants.PREFERRED, gap};
		
		Container c = getContentPane();
		c.setLayout(new TableLayout(col, row));
		
		usernameField = new JTextField();
		usernameField.addKeyListener(this);
		passwordField = new JPasswordField();
		passwordField.addKeyListener(this);
		
		JLabel usernameLabel = new JLabel("Username");
		JLabel passwordLabel = new JLabel("Password");
		
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
		setVisible(true);
		
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
		MainData.setUser(null);
		usernameField.setText("");
		passwordField.setText("");
	}
	
	/**
	 * Reinitializes this dialog when a log in attempt fails.
	 */
	public void logInFailed(){
		initialize();
	}
	
	/**
	 * Sets the current user when a log in attempt succeeds.
	 *
	 * @param u the user
	 */
	public void logInSucceeded(User u){
		MainData.setUser(u);
		u.setPassword("");
		setVisible(false);
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
			ErrorDialog.createDialog(this, "Please enter a value for Password.");
			return;
		}
		String username = usernameField.getText().trim();
		String password = String.valueOf(passwordField.getPassword()).trim();
		User u = new User();
		u.setUsername(username);
		u.setPassword(password);
		LogInWorker worker = new LogInWorker(this, u);
		worker.execute();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==submitButton){
			submitValues();
		}else if(ae.getSource()==cancelButton){
			System.exit(0);
		}
	}
	
}

/**
 * The LogInWorker class is a SwingWorker used to log in a User and get the User's 
 * meta information.
 * 
 * @author Eric J. Lingerfelt
 */
class LogInWorker extends SwingWorker<ErrorResult, Void>{

	private LogInDialog dialog;
	private User u;
	
	/**
	 * The Constructor.
	 * 
	 * @param dialog a LogInDialog object
	 * @param u a User
	 */
	public LogInWorker (LogInDialog dialog, User u){
		this.dialog = dialog;
		this.u = u;
	}

	protected ErrorResult doInBackground(){
		dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		ErrorResult result = WebServiceCom.getInstance().doWebServiceComCall(u, Action.GET_ID);
		if(result.isError()){
			return result;
		}
		result = WebServiceCom.getInstance().doWebServiceComCall(u, Action.GET_USER_DATA);
		return result;
	}
	
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				dialog.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				dialog.logInSucceeded(u);
			}else{
				dialog.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				ErrorResultDialog.createErrorResultDialog(dialog, result);
				dialog.logInFailed();
			}
		}catch(Exception e){
			dialog.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			dialog.logInFailed();
			CaughtExceptionHandler.handleException(e, dialog);
		}
	}
}