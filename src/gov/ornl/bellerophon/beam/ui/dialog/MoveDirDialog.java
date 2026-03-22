package gov.ornl.bellerophon.beam.ui.dialog;

import gov.ornl.bellerophon.beam.data.util.CustomFile;
import gov.ornl.bellerophon.beam.ui.util.RemoteDirTree;
import gov.ornl.bellerophon.beam.ui.util.RemoteDirTreeListener;
import gov.ornl.bellerophon.beam.ui.util.WordWrapLabel;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MoveDirDialog extends JDialog implements ActionListener, RemoteDirTreeListener{

	private CustomFile customFile, selectedCustomFile;
	private JButton submitButton, cancelButton;
	private RemoteDirTree tree;
	private JTextArea dirArea;
	
	public MoveDirDialog(Window owner, CustomFile customFile){
		this(owner, customFile, null);
	}
	
	public MoveDirDialog(Window owner, CustomFile customFile, CustomFile rootDirFile){
		
		super(owner, "Move Directory", Dialog.ModalityType.APPLICATION_MODAL);
		
		this.customFile = customFile;
		
		setSize(645, 415);
		setLocationRelativeTo(owner);
		
		WordWrapLabel topLabel = new WordWrapLabel(true);
		topLabel.setText("Please select a new parent directory for the " + customFile.getFullPath() + " data directory and click <i>Submit</i>.");

		JLabel dirLabel = new JLabel("Selected Remote Directory:");
		
		dirArea = new JTextArea();
		dirArea.setEditable(false);
		dirArea.setLineWrap(true);
		JScrollPane dirAreaSP = new JScrollPane(dirArea);
		
		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		tree = new RemoteDirTree(this);
		JScrollPane treePane = new JScrollPane(tree);
		JPanel treePanel = new JPanel();
		double[] colTree = {5, TableLayoutConstants.FILL, 5};
		double[] rowTree = {5, TableLayoutConstants.FILL, 5};
		treePanel.setLayout(new TableLayout(colTree, rowTree));
		treePanel.add(treePane, "1, 1, f, f");
		
		JPanel valuePanel = new JPanel();
		double[] columnValue = {TableLayoutConstants.FILL};
		double[] rowValue = {TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.FILL
								, 20, TableLayoutConstants.PREFERRED
								, 10, TableLayoutConstants.PREFERRED};
		valuePanel.setLayout(new TableLayout(columnValue, rowValue));
		valuePanel.add(dirLabel, 			"0, 0, l, c");
		valuePanel.add(dirAreaSP, 			"0, 2, f, f");
		valuePanel.add(submitButton, 		"0, 4, f, c");
		valuePanel.add(cancelButton, 		"0, 6, f, c");
		
		double[] column = {20, TableLayoutConstants.FILL
							, 20, TableLayoutConstants.FILL, 20};
		double[] row = {20, TableLayoutConstants.PREFERRED
							, 30, TableLayoutConstants.FILL, 20};

		setLayout(new TableLayout(column, row));
		add(topLabel, 	"1, 1, 3, 1, c, c");
		add(treePanel,	"1, 3, f, f");
		add(valuePanel,	"3, 3, f, f");
		
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==submitButton){
			if(selectedCustomFile==null){
				AttentionDialog.createDialog(this, "Please select a directory from your BEAM data storage area using the tree on the left.");
				return;
			}
			customFile.setNewPath(selectedCustomFile.getFullPath());
			setVisible(false);
		}else if(ae.getSource()==cancelButton){
			selectedCustomFile = null;
			setVisible(false);
		}
	}
	
	public void customFileSelected(CustomFile selectedCustomFile) {
		if(selectedCustomFile!=null && selectedCustomFile.isDir()){
			this.selectedCustomFile = selectedCustomFile;
			dirArea.setText(selectedCustomFile.getFullPath());
		}else{
			this.selectedCustomFile = null;
			dirArea.setText("");
		}
	}
	
	public static CustomFile createMoveDirDialog(Frame owner, CustomFile customFile, CustomFile rootDirFile){
		MoveDirDialog dialog = new MoveDirDialog(owner, customFile, rootDirFile);
		dialog.setVisible(true);
		return dialog.selectedCustomFile;
	}
	
	public static CustomFile createMoveDirDialog(Frame owner, CustomFile customFile){
		MoveDirDialog dialog = new MoveDirDialog(owner, customFile);
		dialog.setVisible(true);
		return dialog.selectedCustomFile;
	}

}