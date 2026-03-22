/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: RemoteDirTree.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.util;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.CustomFile;

import java.awt.Component;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class RemoteDirTree extends JTree implements TreeSelectionListener{

	private RemoteDirTreeListener rdtl;
	
	public RemoteDirTree(RemoteDirTreeListener rdtl){
		this.rdtl = rdtl;
		setRootVisible(true);
		setShowsRootHandles(true);
		addTreeSelectionListener(this);
		setExpandsSelectedPaths(true);
		DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
		setSelectionModel(selectionModel);
		setCellRenderer(new CustomFileTreeCellRenderer());
		setModel(MainData.getDirTreeModel());
		validate();
		repaint();
	}
	
	public void sortTreeNodeChildren(DefaultMutableTreeNode treeNode){
		ArrayList<DefaultMutableTreeNode> childList = new ArrayList<DefaultMutableTreeNode>();
		Enumeration<?> e = treeNode.children();
		while(e.hasMoreElements()){
			childList.add((DefaultMutableTreeNode) e.nextElement());
		}
		Collections.sort(childList, new DefaultMutableTreeNodeComparator());
		treeNode.removeAllChildren();
		for(DefaultMutableTreeNode child: childList){
			MainData.getDirTreeModel().insertNodeInto(child, treeNode, treeNode.getChildCount());
		}
		MainData.getDirTreeModel().reload();
	}

	public void valueChanged(TreeSelectionEvent tse){
		if(tse.getSource()==this){
			if(getSelectionPath()!=null){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)getSelectionPath().getLastPathComponent();
				rdtl.customFileSelected((CustomFile)node.getUserObject());
			}else{
				rdtl.customFileSelected(null);
			}
		}
	}
	
}

class DefaultMutableTreeNodeComparator implements Comparator<DefaultMutableTreeNode>{

	public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
		return o1.toString().compareToIgnoreCase(o2.toString());
	}
	
}

class CustomFileTreeCellRenderer extends DefaultTreeCellRenderer{

	public Component getTreeCellRendererComponent(JTree tree
													, Object value
													, boolean selected
													, boolean expanded
													, boolean isLeaf
													, int row
													, boolean hasFocus){
		
		JLabel renderer = (JLabel)super.getTreeCellRendererComponent(tree
																	, value
																	, selected
																	, expanded
																	, isLeaf
																	, row
																	, hasFocus);
		
		DefaultMutableTreeNode node  = (DefaultMutableTreeNode)value;
		
		if(node.getUserObject() instanceof CustomFile){
			CustomFile file = (CustomFile)node.getUserObject();
			if(file.isDir()){
				if(expanded){
					renderer.setIcon(openIcon);
				}else{
					renderer.setIcon(closedIcon);
				}
			}else{
				renderer.setIcon(leafIcon);
			}
		}
		return renderer;
	}
}
