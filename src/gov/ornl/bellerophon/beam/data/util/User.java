/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: User.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.util;

import java.util.Calendar;
import java.util.TreeMap;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.ui.format.Calendars;

/**
 * The Class User is the data structure for a Bellerophon user.
 *
 * @author Eric J. Lingerfelt
 */
public class User implements Data, Comparable<User>{

	private String username, password, firstName, lastName, email, 
					lastLoginIP, lastLoginHost, scpPrivateKey;
	private Calendar lastLogInDate;
	private TreeMap<Integer, Allocation> allocationMap;
	
	/**
	 * The Constructor.
	 */
	public User(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(!(o instanceof User)){
			return false;
		}
		User u = (User)o;
		return u.username.equals(username);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public User clone(){
		User u = new User();
		u.username = username;
		u.password = password;
		u.firstName = firstName;
		u.lastName = lastName;
		u.email = email;
		u.lastLoginIP = lastLoginIP;
		u.lastLoginHost = lastLoginHost;
		u.lastLogInDate = lastLogInDate;
		u.allocationMap = allocationMap;
		return u;
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.data.Data#initialize()
	 */
	public void initialize(){
		username = "";
		password = "";
		firstName = "";
		lastName = "";
		email = "";
		lastLoginIP = "";
		lastLoginHost = "";
		lastLogInDate = Calendars.getDefaultCalendar();
		allocationMap = null;
		scpPrivateKey = "";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		if(firstName.equals("")){
			return lastName;
		}
		return lastName + ", " + firstName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(User u){
		if(lastName.equals(u.lastName)){
			return firstName.compareToIgnoreCase(u.firstName);
		}
		return lastName.compareToIgnoreCase(u.lastName);
	}
	
	public String getScpPrivateKey(){return scpPrivateKey;} 
	public void setScpPrivateKey(String scpPrivateKey){this.scpPrivateKey = scpPrivateKey;}
	
	public TreeMap<Integer, Allocation> getAllocationMap(){return allocationMap;}
	public void setAllocationMap(TreeMap<Integer, Allocation> allocationMap){this.allocationMap = allocationMap;}
	
	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername(){return username;}
	
	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username){this.username = username;}
	
	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword(){return password;}
	
	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password){this.password = password;}
	
	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail(){return email;}
	
	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email){this.email = email;}
	
	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName(){return firstName;}
	
	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName){this.firstName = firstName;}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName(){return lastName;}
	
	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName){this.lastName = lastName;}
	
	/**
	 * Gets the last login ip.
	 *
	 * @return the last login ip
	 */
	public String getLastLoginIP(){return lastLoginIP;}
	
	/**
	 * Sets the last login ip.
	 *
	 * @param lastName the new last login ip
	 */
	public void setLastLoginIP(String lastLoginIP){this.lastLoginIP = lastLoginIP;}
	
	/**
	 * Gets the last login host.
	 *
	 * @return the last login host
	 */
	public String getLastLoginHost(){return lastLoginHost;}
	
	/**
	 * Sets the last login host.
	 *
	 * @param lastName the new last login host
	 */
	public void setLastLoginHost(String lastLoginHost){this.lastLoginHost = lastLoginHost;}
	
	/**
	 * Gets the last login date.
	 *
	 * @return the last login date
	 */
	public Calendar getLastLogInDate(){return lastLogInDate;}
	
	/**
	 * Sets the last login date.
	 *
	 * @param creationDate the new last login date
	 */
	public void setLastLogInDate(Calendar lastLogInDate){this.lastLogInDate = lastLogInDate;}
	
}
