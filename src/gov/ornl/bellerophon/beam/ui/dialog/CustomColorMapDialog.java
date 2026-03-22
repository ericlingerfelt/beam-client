package gov.ornl.bellerophon.beam.ui.dialog;

import gov.ornl.bellerophon.beam.data.util.ColorMap;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

public class CustomColorMapDialog extends JDialog implements ActionListener{

	private ColorMap colorMap;
	
	public CustomColorMapDialog(Window owner, ColorMap colorMap){
		
	}
	
	public static ColorMap createCustomColorMapDialog(Window owner, ColorMap colorMap){
		CustomColorMapDialog dialog = new CustomColorMapDialog(owner, colorMap);
		dialog.setVisible(true);
		return dialog.colorMap;
	}
	
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
	}

}
