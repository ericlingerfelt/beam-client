package gov.ornl.bellerophon.beam.ui.util;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;

import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class PlainFileChooserFactory{
	public static JFileChooser createPlainFileChooser(){
		LookAndFeel previousLF = UIManager.getLookAndFeel();
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			JFileChooser fileDialog = new JFileChooser(MainData.getAbsolutePath()); 
			UIManager.setLookAndFeel(previousLF);
			return fileDialog;
		} catch (ClassNotFoundException 
				| InstantiationException
				| IllegalAccessException  
				| UnsupportedLookAndFeelException e){
			e.printStackTrace();
			CaughtExceptionHandler.handleException(e, null);
		}
		return null;
	}
}
