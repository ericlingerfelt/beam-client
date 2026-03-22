package gov.ornl.bellerophon.beam.ui.export;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.file.CustomFileFilter;
import gov.ornl.bellerophon.beam.file.FileType;
import gov.ornl.bellerophon.beam.io.IOUtilities;
import gov.ornl.bellerophon.beam.ui.dialog.CautionDialog;
import gov.ornl.bellerophon.beam.ui.util.PlainFileChooserFactory;

import java.awt.*;

import javax.swing.*;

import java.io.File;
import java.util.*;

/**
 * The Class TextSaver contains several static methods used to save plain text 
 * and HTML formatted text to a file.
 *
 * @author Eric J. Lingerfelt
 */
public class TextSaver{

	/**
	 * Saves plain text to a file.
	 *
	 * @param string the file's contents
	 * @param owner the owner
	 * @throws Exception the exception
	 */
	public static void savePlainText(String string, Frame owner) throws Exception{
		ArrayList<FileType> list = new ArrayList<FileType>();
		list.add(FileType.TXT);
		HashMap<FileType, String> map = new HashMap<FileType, String>();
		map.put(FileType.TXT, string);
		saveText(owner, list, map);
	}

	/**
	 * Saves HTML formatted text to a file.
	 *
	 * @param string the file's contents
	 * @param owner the owner
	 * @throws Exception the exception
	 */
	public static void saveHTMLText(String string, Frame owner) throws Exception{
		ArrayList<FileType> list = new ArrayList<FileType>();
		list.add(FileType.HTML);
		HashMap<FileType, String> map = new HashMap<FileType, String>();
		map.put(FileType.HTML, string);
		saveText(owner, list, map);
	}
	
	/**
	 * Saves text to a file.
	 *
	 * @param owner the owner
	 * @param list the list
	 * @param map the map
	 * @throws Exception the exception
	 */
	public static void saveText(Frame owner
			, ArrayList<FileType> list
			, HashMap<FileType, String> map) throws Exception{
		saveText(owner, list, map, "", null);
	}
	
	/**
	 * Saves text to a file.
	 *
	 * @param owner the owner
	 * @param list the list
	 * @param map the map
	 * @param excelWriter the excel writer
	 * @throws Exception the exception
	 */
	public static void saveText(Frame owner
			, ArrayList<FileType> list
			, HashMap<FileType, String> map
			, ExcelWriter excelWriter) throws Exception{
		saveText(owner, list, map, "", null);
	}
	
	/**
	 * Saves text to a file.
	 *
	 * @param owner the owner
	 * @param list the list
	 * @param map the map
	 * @param filename the filename
	 * @param excelWriter the excel writer
	 * @throws Exception the exception
	 */
	public static void saveText(Frame owner
								, ArrayList<FileType> list
								, HashMap<FileType, String> map
								, String filename
								, ExcelWriter excelWriter) throws Exception{
		JFileChooser fileDialog = PlainFileChooserFactory.createPlainFileChooser();
		for(FileType type: list){
			fileDialog.addChoosableFileFilter(new CustomFileFilter(type));
		}
		fileDialog.setAcceptAllFileFilterUsed(false);
		fileDialog.setSelectedFile(new File(filename));
		fileDialog.setFileFilter(new CustomFileFilter(list.get(0)));
		int returnVal = fileDialog.showSaveDialog(owner); 
		CustomFileFilter selectedFilter = (CustomFileFilter)fileDialog.getFileFilter();
		MainData.setAbsolutePath(fileDialog.getCurrentDirectory());
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File file = fileDialog.getSelectedFile();
			String filepath = file.getAbsolutePath();
        	if(selectedFilter.getFileExtension(file)==null){
        		filepath += "." + selectedFilter.getExtension();
        	}else if(!selectedFilter.getFileExtension(file).equals(selectedFilter.getExtension())){
        		filepath += "." + selectedFilter.getExtension();
        	}
        	
			if(new File(filepath).exists()){
				String msg = "The file " + file.getName() + " exists.<br><br>Do you want to replace it?";
				int value = CautionDialog.createCautionDialog(owner, msg, "Attention!");
				if(value==CautionDialog.NO){
					saveText(owner, list, map, filename, excelWriter);
				}
			}
        	
        	if(selectedFilter.getFileType()==FileType.XLSX){
        		if(excelWriter!=null){
        			excelWriter.writeExcel(filepath);
        		}
        	}else{
        		IOUtilities.writeFile(filepath, map.get(selectedFilter.getFileType()).getBytes());
        	}
		}
	}
}