package gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import hdf.object.HObject;
import hdf.object.h5.H5Group;

public class ImageProcessorPCAImageCleaningDataFileTreeCellRenderer extends DefaultTreeCellRenderer {

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

		if(node.getUserObject() instanceof HObject){
			HObject hObject = (HObject) node.getUserObject();
			if(hObject instanceof H5Group){
				if(expanded){
					renderer.setIcon(openIcon);
				}else{
					renderer.setIcon(closedIcon);
				}
			}else{
				renderer.setIcon(leafIcon);
				if(!hObject.getName().equals("Raw_Data")){
				renderer.setEnabled(false);
				}
			}
		}else if(node.getUserObject() instanceof String){

			String string = node.getUserObject().toString();
			if(string.equals("S") 
					|| string.equals("U") 
					|| string.equals("V")){
				renderer.setIcon(leafIcon);
				renderer.setEnabled(false);
			}

		}
		return renderer;
	}
	
}
