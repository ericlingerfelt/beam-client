/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: IntroPanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui;

import java.util.Calendar;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.*;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.User;
import gov.ornl.bellerophon.beam.io.FileImport;
import gov.ornl.bellerophon.beam.ui.format.Calendars;
import gov.ornl.bellerophon.beam.ui.format.Fonts;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;

/**
 * The Class IntroPanel.
 *
 * @author Eric J. Lingerfelt
 */
public class IntroPanel extends JPanel{

	/**
	 * Instantiates a new intro panel.
	 */
	public IntroPanel(){
		
		User u = MainData.getUser();
		
		if(u==null){
			return;
		}
		
		String ip = u.getLastLoginIP();
		String host = u.getLastLoginHost();
		Calendar c = u.getLastLogInDate();
		String lastLoginDate = Calendars.getFormattedOutputDateString(c);
		
		JLabel label = new JLabel();
		try{
			label.setIcon(new ImageIcon(FileImport.getFileByte("images/beam_trans_logo_small.png")));
		}catch(Exception e){
			
		}
		
		JLabel label1 = new JLabel();
		try{
			label1.setIcon(new ImageIcon(FileImport.getFileByte("images/beam_trans_label_small.png")));
		}catch(Exception e){
			
		}
		
		JLabel label2 = new JLabel();
		try{
			label2.setIcon(new ImageIcon(FileImport.getFileByte("images/ORNL_logo.png")));
		}catch(Exception e){
			
		}
		
		JLabel label3 = new JLabel();
		try{
			if(!MainData.doSNSDemo){
				label3.setIcon(new ImageIcon(FileImport.getFileByte("images/IFIM_logo.png")));
			}else{
				label3.setIcon(new ImageIcon(FileImport.getFileByte("images/CAMM_logo.png")));
			}
		}catch(Exception e){
			
		}
		
		JLabel label4 = new JLabel();
		try{
			label4.setIcon(new ImageIcon(FileImport.getFileByte("images/PANDIA_logo.png")));
		}catch(Exception e){
			
		}
		
		JLabel descLabel = new JLabel("Select a tool from the top row to begin.");
		descLabel.setFont(Fonts.medTitleFont);
		
		WordWrapLabel usernameLabel = new WordWrapLabel(true);
		usernameLabel.setText("XCAMS Username: ");
		
		WordWrapLabel dateLabel = new WordWrapLabel(true);
		dateLabel.setText("Last Login Date: ");
		
		WordWrapLabel hostLabel = new WordWrapLabel(true);
		hostLabel.setText("Last Login Host: ");
		
		WordWrapLabel ipLabel = new WordWrapLabel(true);
		ipLabel.setText("Last Login IP: ");
		
		WordWrapLabel usernameLabel2 = new WordWrapLabel(true);
		usernameLabel2.setText(MainData.getUser().getUsername());
		
		WordWrapLabel dateLabel2 = new WordWrapLabel(true);
		dateLabel2.setText(lastLoginDate);
		
		WordWrapLabel ipLabel2 = new WordWrapLabel(true);
		ipLabel2.setText(ip);
		
		WordWrapLabel hostLabel2 = new WordWrapLabel(true);
		hostLabel2.setText(host);
		
		JPanel logoPanel = new JPanel();
		double[] colLogo = {TableLayoutConstants.FILL};
		double[] rowLogo = {TableLayoutConstants.PREFERRED
							, 20, TableLayoutConstants.PREFERRED};
		logoPanel.setLayout(new TableLayout(colLogo, rowLogo));
		logoPanel.add(label,       "0, 0, c, f");
		logoPanel.add(label1,      "0, 2, c, c");
		
		JPanel loginPanel = new JPanel();
		double[] colLogin = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		double[] rowLogin = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		loginPanel.setLayout(new TableLayout(colLogin, rowLogin));
		loginPanel.add(usernameLabel,   "0, 0, r, c");
		loginPanel.add(usernameLabel2,  "2, 0, l, c");
		loginPanel.add(dateLabel,       "0, 2, r, c");
		loginPanel.add(dateLabel2,      "2, 2, l, c");
		loginPanel.add(hostLabel,       "0, 4, r, c");
		loginPanel.add(hostLabel2,      "2, 4, l, c");
		loginPanel.add(ipLabel,       	"0, 6, r, c");
		loginPanel.add(ipLabel2,       	"2, 6, l, c");
	
		JPanel logoPanel2 = new JPanel();
		double[] colLogo2 = {TableLayoutConstants.FILL
								, 70, TableLayoutConstants.PREFERRED
								, 70, TableLayoutConstants.FILL};
		double[] rowLogo2 = {TableLayoutConstants.PREFERRED};
		logoPanel2.setLayout(new TableLayout(colLogo2, rowLogo2));
		logoPanel2.add(label2,	"0, 0, c, f");
		logoPanel2.add(label3,	"2, 0, c, f");
		logoPanel2.add(label4,	"4, 0, c, f");
		
		double[] col = {TableLayoutConstants.FILL};
		double[] row = {TableLayoutConstants.PREFERRED
						, 40, TableLayoutConstants.FILL
						, 40, TableLayoutConstants.FILL};
		
		setLayout(new TableLayout(col, row));
		add(logoPanel, 		"0, 0, c, c");
		add(loginPanel, 	"0, 2, c, c");
		add(logoPanel2, 	"0, 4, c, b");
		
	}
	
}
