/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: DirectoryChooser.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.dialog;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;

/**
 * The Class DirectoryChooser.
 *
 * @author Eric J. Lingerfelt
 */
public class DirectoryChooser extends JTree
			      implements TreeSelectionListener, MouseListener {

    private static FileSystemView fsv = FileSystemView.getFileSystemView();
    private Dialog parent;
    
    /**
     * Instantiates a new directory chooser.
     *
     * @param parent the parent
     */
    public DirectoryChooser(Dialog parent) {
		this(null, parent);
    }

    /**
     * Instantiates a new directory chooser.
     *
     * @param dir the dir
     * @param parent the parent
     */
    public DirectoryChooser(File dir, Dialog parent) {
		super(new DirNode(fsv.getRoots()[0]));
		this.parent = parent;
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setSelectedDirectory(dir);
		addTreeSelectionListener(this);
		addMouseListener(this);
    }

    /**
     * Creates the folder.
     *
     * @param fileName the file name
     */
    public void createFolder(String fileName){
    	if(getSelectedDirectory()!=null){
    		try{
    			File dir = new File(getSelectedDirectory(), fileName);
    			dir.mkdir();
    			setSelectedDirectory(dir);
    		}catch(SecurityException se){
    			String string = "You are not permitted to create a new folder in the path " 
    									+ getSelectedDirectory().getName()
    									+ ". Please contact your System Administrator to obtain permission.";
    			MessageDialog.createMessageDialog(parent, string, "Attention!");
    		}
    	}
    }
    
    /**
     * Sets the selected directory.
     *
     * @param dir the new selected directory
     */
    public void setSelectedDirectory(File dir) {
    	if (dir == null) {
			dir = fsv.getDefaultDirectory();
		}
		setSelectionPath(mkPath(dir));
    }

    /**
     * Gets the selected directory.
     *
     * @return the selected directory
     */
    public File getSelectedDirectory() {
		DirNode node = (DirNode)getLastSelectedPathComponent();
		if (node != null) {
		    File dir = node.getDir();
		    if (fsv.isFileSystem(dir)) {
		    	return dir;
		    }
		}
		return null;
    }

    /**
     * Adds the action listener.
     *
     * @param l the l
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
 
    /**
     * Removes the action listener.
     *
     * @param l the l
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }
 
    /**
     * Gets the action listeners.
     *
     * @return the action listeners
     */
    public ActionListener[] getActionListeners() {
        return listenerList.getListeners(ActionListener.class);
    }

    /*--- End Public API -----*/




    /*--- TreeSelectionListener Interface -----*/

    /* (non-Javadoc)
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent ev) {
		File oldDir = null;
		TreePath oldPath = ev.getOldLeadSelectionPath();
		if (oldPath != null) {
		    oldDir = ((DirNode)oldPath.getLastPathComponent()).getDir();
		    if (!fsv.isFileSystem(oldDir)) {
		    	oldDir = null;
		    }
		}
		File newDir = getSelectedDirectory();
		firePropertyChange("selectedDirectory", oldDir, newDir);
    }

    /*--- MouseListener Interface -----*/

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
		if (e.getClickCount() == 2) {
		    TreePath path = getPathForLocation(e.getX(), e.getY());
		    if (path != null && path.equals(getSelectionPath()) && getSelectedDirectory() != null) {
		    	fireActionPerformed("dirSelected", e);
		    }
		}
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {}
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {}
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {}
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {}


    /*--- Private Section ------*/

    /**
     * Mk path.
     *
     * @param dir the dir
     * @return the tree path
     */
    private TreePath mkPath(File dir) {
		DirNode root = (DirNode)getModel().getRoot();
		if (root.getDir().equals(dir)) {
		    return new TreePath(root);
		}
	
		TreePath parentPath = mkPath(fsv.getParentDirectory(dir));
		DirNode parentNode = (DirNode)parentPath.getLastPathComponent();
		Enumeration enumeration = parentNode.children();
		while (enumeration.hasMoreElements()) {
		    DirNode child = (DirNode)enumeration.nextElement();
		    if (child.getDir().equals(dir)) {
		    	return parentPath.pathByAddingChild(child);		
		    }
		}
		return null;
    }


    /**
     * Fire action performed.
     *
     * @param command the command
     * @param evt the evt
     */
    private void fireActionPerformed(String command, InputEvent evt) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
			    command, evt.getWhen(), evt.getModifiers());
        ActionListener[] listeners = getActionListeners();
        for (int i = listeners.length - 1; i >= 0; i--) {
        	listeners[i].actionPerformed(e);
        }
    }


    /**
     * The Class DirNode.
     *
     * @author Eric J. Lingerfelt
     */
    private static class DirNode extends DefaultMutableTreeNode {
		
		/**
		 * Instantiates a new dir node.
		 *
		 * @param dir the dir
		 */
		DirNode(File dir) {
		    super(dir);
		}
	
		/**
		 * Gets the dir.
		 *
		 * @return the dir
		 */
		public File getDir() {
		    return (File)userObject;
		}
	
		/* (non-Javadoc)
		 * @see javax.swing.tree.DefaultMutableTreeNode#getChildCount()
		 */
		public int getChildCount() {
		    populateChildren();
		    return super.getChildCount();
		}
	
		/* (non-Javadoc)
		 * @see javax.swing.tree.DefaultMutableTreeNode#children()
		 */
		public Enumeration children() {
		    populateChildren();
		    return super.children();
		}
	
		/* (non-Javadoc)
		 * @see javax.swing.tree.DefaultMutableTreeNode#isLeaf()
		 */
		public boolean isLeaf() {
		    return false;
		}
	
		/**
		 * Populate children.
		 */
		private void populateChildren() {
		    if (children == null) {
				File[] files = fsv.getFiles(getDir(), true);
				Arrays.sort(files);
				for (int i = 0; i < files.length; i++) {
				    File f = files[i];
				    if(fsv.getSystemTypeDescription(f)==null){
					    if (fsv.isTraversable(f).booleanValue()) {
						insert(new DirNode(f),
						       (children == null) ? 0: children.size());
					    }
				    }else{
				    	if (fsv.isTraversable(f).booleanValue()
					    		&& !fsv.getSystemTypeDescription(f).equals("Shortcut")) {
						insert(new DirNode(f),
						       (children == null) ? 0: children.size());
					    }
				    }
				}
		    }
		}
	
		/* (non-Javadoc)
		 * @see javax.swing.tree.DefaultMutableTreeNode#toString()
		 */
		public String toString() {
		    return fsv.getSystemDisplayName(getDir());
		}
	
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object o) {
		    return (o instanceof DirNode &&
			    userObject.equals(((DirNode)o).userObject));
		}
    }
}
