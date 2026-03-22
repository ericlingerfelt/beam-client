package gov.ornl.bellerophon.beam.ui.util;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;

public class DataFileTree extends JTree implements TreeSelectionListener{

	private DataFileTreeNodeSelectionListener dftnsl;
	
	public DataFileTree(DataFileTreeNodeSelectionListener dftnsl){
		this.dftnsl = dftnsl;
		setRootVisible(false);
		setShowsRootHandles(true);
		addTreeSelectionListener(this);
		setExpandsSelectedPaths(true);
		DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
		setSelectionModel(selectionModel);
	}
	
	public void setDataFileTreeRenderer(DefaultTreeCellRenderer dtcr){
		setCellRenderer(dtcr);
	}

	public void valueChanged(TreeSelectionEvent tse){
		if(tse.getSource()==this){
			if(getSelectionPath()!=null){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)getSelectionPath().getLastPathComponent();
				dftnsl.dataFileTreeNodeSelected(node);
			}else{
				dftnsl.dataFileTreeNodeSelected(null);
			}
		}
	}

}