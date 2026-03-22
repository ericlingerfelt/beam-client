package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.pca;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import hdf.object.Datatype;
import hdf.object.HObject;
import hdf.object.h5.H5CompoundDS;
import hdf.object.h5.H5Group;
import hdf.object.h5.H5ScalarDS;

public class MultivariateAnalyzerPCADataFileTreeCellRenderer extends DefaultTreeCellRenderer {

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
				renderer.setEnabled(false);
				
				if(hObject instanceof H5ScalarDS){
					
					H5ScalarDS sDS =  (H5ScalarDS) hObject;
					
					if(sDS.getDatatype().getDatatypeClass() == Datatype.CLASS_FLOAT){
						if(sDS.getDims().length>1){
							long x = sDS.getDims()[0];
							long y = sDS.getDims()[1];
							if(x>1 && y>1){
								renderer.setEnabled(true);
							}
						}
					}
					
				}else if(hObject instanceof H5CompoundDS){
					
					H5CompoundDS cDS =  (H5CompoundDS) hObject;
					if(cDS.getDatatype().getDatatypeClass() == Datatype.CLASS_COMPOUND){
						if(cDS.getMemberNames()[0].equals("r") && cDS.getMemberNames()[1].equals("i")){
							if(cDS.getDims().length>1){
								long x = cDS.getDims()[0];
								long y = cDS.getDims()[1];
								if(x>1 && y>1){
									renderer.setEnabled(true);
								}
							}
						}
					}
					
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
