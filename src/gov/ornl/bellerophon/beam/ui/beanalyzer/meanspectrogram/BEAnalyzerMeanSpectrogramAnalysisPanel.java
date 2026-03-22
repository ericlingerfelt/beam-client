package gov.ornl.bellerophon.beam.ui.beanalyzer.meanspectrogram;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.feature.BEAnalyzerData;
import gov.ornl.bellerophon.beam.data.util.MeanSpectrogramData;
import gov.ornl.bellerophon.beam.data.util.MeanSpectrogramDataSet;
import gov.ornl.bellerophon.beam.enums.ColorMapType;
import gov.ornl.bellerophon.beam.enums.BEDataType;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.file.CustomFileFilter;
import gov.ornl.bellerophon.beam.file.FileType;
import gov.ornl.bellerophon.beam.ui.dialog.CautionDialog;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ExcelWriter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.export.TextSaver;
import gov.ornl.bellerophon.beam.ui.util.BoundsPopupMenuListener;
import gov.ornl.bellerophon.beam.ui.util.PlainFileChooserFactory;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BEAnalyzerMeanSpectrogramAnalysisPanel extends JPanel implements ActionListener, 
																							ImageExporter,
																							DataExporter,
																							ExcelWriter,
																							ComponentListener{

	private BEAnalyzerMeanSpectrogramChartPanel chartPanel;
	private BEAnalyzerMeanSpectrogramPlotPanel plotPanel;
	private JPanel buttonPanel;
	private BEAnalyzerData d;
	private MeanSpectrogramDataSet msds;
	private MeanSpectrogramData msd;
	
	private JComboBox<String> groupBox;
	private JComboBox<ComplexValueType> typeBox;
	private JComboBox<ColorMapType> colorMapBox;
	private JLabel groupLabel, typeLabel, colorMapLabel;
	private JScrollPane chartPane, plotPane;
	private Frame frame;
	
	//Excel objects
	private Workbook wb;
	private CellStyle dateCellStyle, wrapCellStyle, headerCellStyle
						, rowHeaderCellStyle, rowHeaderWrapCellStyle, defaultCellStyle
						, boldCellStyle;
	
	public BEAnalyzerMeanSpectrogramAnalysisPanel(Frame frame, BEAnalyzerData d){
	
		this.frame = frame;
		this.d = d;
		
		addComponentListener(this);
		
		chartPanel = new BEAnalyzerMeanSpectrogramChartPanel();
		plotPanel = new BEAnalyzerMeanSpectrogramPlotPanel(this);
	
		chartPane = new JScrollPane(chartPanel);
		plotPane = new JScrollPane(plotPanel);
		plotPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		plotPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		
		colorMapBox = new JComboBox<ColorMapType>();
		for(ColorMapType type: ColorMapType.values()){
			colorMapBox.addItem(type);
		}
		colorMapBox.setSelectedIndex(0);
		colorMapBox.addActionListener(this);
		
		typeBox = new JComboBox<ComplexValueType>();
		for(ComplexValueType type: ComplexValueType.values()){
			typeBox.addItem(type);
		}
		typeBox.addActionListener(this);
		
		groupBox = new JComboBox<String>();
		groupBox.addActionListener(this);
		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
		groupBox.addPopupMenuListener(listener);
		groupBox.setPrototypeDisplayValue("1234567890");
		
		groupLabel = new JLabel("Group:");
		typeLabel = new JLabel("Quantity:");
		colorMapLabel = new JLabel("Colormap:");
		
		buttonPanel = new JPanel();
		
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL
						, 10, TableLayoutConstants.PREFERRED, 10};
		setLayout(new TableLayout(col, row));	
		
	}
	
	public BEAnalyzerMeanSpectrogramChartPanel getChartPanel(){
		return chartPanel;
	}
	
	public BEAnalyzerMeanSpectrogramPlotPanel getPlotPanel(){
		return plotPanel;
	}
	
	public void setCurrentState(){
		
		msds = d.getDataFile().getMeanSpectrogramDataSet();
		
		groupBox.removeActionListener(this);
		groupBox.removeAllItems();
		for(String s: msds.getDataMap().keySet()){
			groupBox.addItem(s);
		}
		groupBox.setSelectedIndex(0);
		groupBox.addActionListener(this);
		
		msd = msds.getDataMap().get(groupBox.getSelectedItem());
		if(msd.getType()==BEDataType.BE_LINE){
			plotPanel.setCurrentData(msd, (ComplexValueType) typeBox.getSelectedItem());
		}else if(msd.getType()==BEDataType.BEPS){
			chartPanel.setCurrentData(msd, (ComplexValueType) typeBox.getSelectedItem());
			chartPanel.setColorMap(((ColorMapType) colorMapBox.getSelectedItem()).getColorMap());
		}
		layoutUI();
	}
	
	private void layoutUI(){
		
		removeAll();
		buttonPanel.removeAll();
		
		if(msd.getType()==BEDataType.BE_LINE){
			
			add(plotPane, 		"1, 1, f, f");	
			add(buttonPanel, 	"1, 3, f, c");
			
			double[] columnButton = {5, TableLayoutConstants.PREFERRED, 7
									, TableLayoutConstants.FILL, 10
									, TableLayoutConstants.PREFERRED, 7
									, TableLayoutConstants.FILL, 5};
			double[] rowButton = {5, TableLayoutConstants.PREFERRED, 5};
			buttonPanel.setLayout(new TableLayout(columnButton, rowButton));
			buttonPanel.add(groupLabel, 			"1, 1, r, c");
			buttonPanel.add(groupBox, 				"3, 1, f, c");
			buttonPanel.add(typeLabel, 				"5, 1, r, c");
			buttonPanel.add(typeBox, 				"7, 1, f, c");
			
		}else if(msd.getType()==BEDataType.BEPS){
			
			add(chartPane, 	"1, 1, f, f");	
			add(buttonPanel, 	"1, 3, f, c");
			
			double[] columnButton = {5, TableLayoutConstants.PREFERRED, 7
								, TableLayoutConstants.FILL, 10
								, TableLayoutConstants.PREFERRED, 7
								, TableLayoutConstants.FILL, 10
								, TableLayoutConstants.PREFERRED, 7
								, TableLayoutConstants.FILL, 5};
			double[] rowButton = {5, TableLayoutConstants.PREFERRED, 5};
			buttonPanel.setLayout(new TableLayout(columnButton, rowButton));
			buttonPanel.add(groupLabel, 			"1, 1, r, c");
			buttonPanel.add(groupBox, 				"3, 1, f, c");
			buttonPanel.add(typeLabel, 				"5, 1, r, c");
			buttonPanel.add(typeBox, 				"7, 1, f, c");
			buttonPanel.add(colorMapLabel, 			"9, 1, r, c");
			buttonPanel.add(colorMapBox, 			"11, 1, f, c");
			
		}
		
		validate();

		plotPanel.setVisible(false);
		plotPanel.setVisible(true);
	}
	
	public Dimension getPlotBounds(){
		Dimension d = plotPane.getViewport().getBounds().getSize();
		Dimension newD = new Dimension((int)(d.getWidth() - 20), (int)(d.getHeight() - 20));
		return newD;
	}
	
	public void componentResized(ComponentEvent ce) {
		if(msd!=null && msd.getType()==BEDataType.BE_LINE){
			plotPanel.setPreferredSize(getPlotBounds());
		}else if(msd!=null && msd.getType()==BEDataType.BEPS){
			chartPanel.drawChart();
		}
	}
	public void componentMoved(ComponentEvent ce) {}
	public void componentShown(ComponentEvent ce) {}
	public void componentHidden(ComponentEvent ce) {}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==typeBox || ae.getSource()==groupBox){
			msd = msds.getDataMap().get(groupBox.getSelectedItem());
			if(msd.getType()==BEDataType.BE_LINE){
				plotPanel.setCurrentData(msd, (ComplexValueType) typeBox.getSelectedItem());
			}else if(msd.getType()==BEDataType.BEPS){
				chartPanel.setCurrentData(msd, (ComplexValueType) typeBox.getSelectedItem());
			}
		}else if(ae.getSource()==colorMapBox){
			if(msd.getType()==BEDataType.BEPS){
				chartPanel.setColorMap(((ColorMapType) colorMapBox.getSelectedItem()).getColorMap());
			}
		}
	}

	public void exportCurrentImage(){
		
		String suffix = "_mean_spectrogram";
		String dataFileName = d.getDataFile().getName();
		String filename = "";
		
		String[] parts = dataFileName.split("\\.");
		int nParts = parts.length;		
		for(int i=0; i<nParts-1; i++) { filename += parts[i];}
		if(parts[nParts-1].equals("h5")) {
			filename += suffix;			
		}
		else {
			filename += parts[nParts-1] + suffix;
		}
		
		try{
			if(msd.getType()==BEDataType.BE_LINE){
				exportPlotImage(filename);
			}else if(msd.getType()==BEDataType.BEPS){
				exportChartImage(filename);
			}
		}catch(IOException ioe){
			CaughtExceptionHandler.handleException(ioe, frame);
		}
	}
	
	private void exportPlotImage(String filename) throws IOException{
		JFileChooser fileDialog = PlainFileChooserFactory.createPlainFileChooser();
		fileDialog.setAcceptAllFileFilterUsed(false);
		fileDialog.addChoosableFileFilter(new CustomFileFilter(FileType.PNG));
		fileDialog.setSelectedFile(new File(filename));
		int returnVal = fileDialog.showSaveDialog(this); 
		MainData.setAbsolutePath(fileDialog.getCurrentDirectory());
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File file = fileDialog.getSelectedFile();
			String filepath = file.getAbsolutePath();
			FileType fileType = ((CustomFileFilter) fileDialog.getFileFilter()).getFileType();
			String extension = ((CustomFileFilter) fileDialog.getFileFilter()).getExtension();
			if(!filepath.endsWith("." + extension)){
				file = new File(file.getAbsolutePath() + "." + extension);
			}
			if(file.exists()){
				String msg = "The file " + file.getName() + " exists. Do you want to replace it?";
				int value = CautionDialog.createCautionDialog(frame, msg, "Attention!");
				if(value==CautionDialog.NO){
					exportPlotImage(file.getName());
				}else{
					writePlotImage(file, fileType);
				}
			}else{
				writePlotImage(file, fileType);
			}
		}
	}
	
	private void writePlotImage(File file, FileType fileType) throws IOException{
		switch(fileType){
			case PNG:
				BufferedImage bi = new BufferedImage(plotPanel.getSize().width, plotPanel.getSize().height, BufferedImage.TYPE_INT_ARGB); 
				Graphics g = bi.createGraphics();
				plotPanel.paint(g);
				g.dispose();
				ImageIO.write(bi, "png", file);
				break;
		}
	}
	
	private void exportChartImage(String filename) throws IOException{
		JFileChooser fileDialog = PlainFileChooserFactory.createPlainFileChooser();
		fileDialog.setAcceptAllFileFilterUsed(false);
		fileDialog.addChoosableFileFilter(new CustomFileFilter(FileType.PNG));
		fileDialog.setSelectedFile(new File(filename));
		int returnVal = fileDialog.showSaveDialog(this); 
		MainData.setAbsolutePath(fileDialog.getCurrentDirectory());
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File file = fileDialog.getSelectedFile();
			String filepath = file.getAbsolutePath();
			FileType fileType = ((CustomFileFilter) fileDialog.getFileFilter()).getFileType();
			String extension = ((CustomFileFilter) fileDialog.getFileFilter()).getExtension();
			if(!filepath.endsWith("." + extension)){
				file = new File(file.getAbsolutePath() + "." + extension);
			}
			if(file.exists()){
				String msg = "The file " + file.getName() + " exists. Do you want to replace it?";
				int value = CautionDialog.createCautionDialog(frame, msg, "Attention!");
				if(value==CautionDialog.NO){
					exportChartImage(file.getName());
				}else{
					writeChartImage(file, fileType);
				}
			}else{
				writeChartImage(file, fileType);
			}
		}
	}
	
	private void writeChartImage(File file, FileType fileType) throws IOException{
		switch(fileType){
			case PNG:
				BufferedImage bi = new BufferedImage(chartPanel.getSize().width, chartPanel.getSize().height, BufferedImage.TYPE_INT_ARGB); 
				Graphics g = bi.createGraphics();
				chartPanel.paint(g);
				g.dispose();
				ImageIO.write(bi, "png", file);
				break;
		}
	}

	public void exportCurrentData(){
		
		String filename = d.getDataFile().getName() + "_spectrogram_average_plot_data";
		ArrayList<FileType> list = new ArrayList<FileType>();
		list.add(FileType.XLSX);
		HashMap<FileType, String> map = new HashMap<FileType, String>();
		try{
			TextSaver.saveText(frame, list, map, filename, this);
		}catch (Exception e){
			CaughtExceptionHandler.handleException(e, frame);
		}
		
	}

	private void writeExcelTablePlot(Sheet sheet, int rowCounter){
		
		Row row = sheet.createRow(rowCounter);
		Cell cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(plotPanel.getXTitle());
		
	    cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(plotPanel.getYTitle());
		
		rowCounter++;
		
		for(int i=0; i<plotPanel.getXValueArray().length; i++){
			
			row = sheet.createRow(rowCounter);
			
			cell = row.createCell(0);
			row.getCell(0).setCellStyle(defaultCellStyle);
		    cell.setCellValue(plotPanel.getXValueArray()[i]);
			
			cell = row.createCell(1);
			row.getCell(1).setCellStyle(defaultCellStyle);
		    cell.setCellValue(plotPanel.getYValueArray()[i]);

			rowCounter++;
			
		}
		
		resizeAllColumns(sheet, 8500, 2);	
	}
	
	private void writeExcelTableChart(Sheet sheet, int rowCounter){
		
		Row row = sheet.createRow(rowCounter);
		Cell cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(chartPanel.getXTitle());
		
	    cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(chartPanel.getYTitle());
	    
	    cell = row.createCell(2);
		row.getCell(2).setCellStyle(defaultCellStyle);
	    cell.setCellValue(chartPanel.getZTitle());
		
		rowCounter++;
		
		for(int i=0; i<chartPanel.getXValueArray().length; i++){
			
			for(int j=0; j<chartPanel.getYValueArray().length; j++){
			
				row = sheet.createRow(rowCounter);
				
				cell = row.createCell(0);
				row.getCell(0).setCellStyle(defaultCellStyle);
			    cell.setCellValue(chartPanel.getXValueArray()[i]);
				
				cell = row.createCell(1);
				row.getCell(1).setCellStyle(defaultCellStyle);
			    cell.setCellValue(chartPanel.getYValueArray()[j]);
			    
			    cell = row.createCell(2);
				row.getCell(2).setCellStyle(defaultCellStyle);
			    cell.setCellValue(chartPanel.getZValueArray()[i][j]);
	
				rowCounter++;
			
			}
			
		}
		
		resizeAllColumns(sheet, 8500, 3);
	}
	
	/**
	 * Write excel report to the spreadsheet.
	 */
	public void writeExcelReport(){
		
		String string = "Data File";
		Sheet sheet = wb.createSheet(string);
		int rowCounter = 0;
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = d.getDataFile().getFullPath();
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	   
	    string = "Data Type";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = "Spectrogram Averaged Data";
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "Data Group";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = groupBox.getSelectedItem().toString();
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
		   
	    string = "Data Quantity";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = typeBox.getSelectedItem().toString();
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    rowCounter++;
	    
	    if(msd.getType()==BEDataType.BE_LINE){
	    	writeExcelTablePlot(sheet, rowCounter);
		}else if(msd.getType()==BEDataType.BEPS){
			writeExcelTableChart(sheet, rowCounter);
		}
	    
	}
	
	/* (non-Javadoc)
	 * @see gov.isotopes.omt.gui.util.export.ExcelWriter#writeExcel(java.lang.String)
	 */
	@Override
	public void writeExcel(String filepath) throws Exception {
		wb = new XSSFWorkbook();
		CreationHelper helper = wb.getCreationHelper();
		initializeCellStyles(wb, helper);
		writeExcelReport();
		FileOutputStream fileOut = new FileOutputStream(filepath);
	    wb.write(fileOut);
	    fileOut.close();
	}
	
	private void setCellStyleBorders(CellStyle style){
		style.setBorderBottom(CellStyle.BORDER_THIN);
	    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
	    style.setBorderLeft(CellStyle.BORDER_THIN);
	    style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
	    style.setBorderRight(CellStyle.BORDER_THIN);
	    style.setRightBorderColor(IndexedColors.BLACK.getIndex());
	    style.setBorderTop(CellStyle.BORDER_THIN);
	    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
	}

	private void initializeCellStyles(Workbook wb, CreationHelper helper){
		defaultCellStyle = wb.createCellStyle();
		setCellStyleBorders(defaultCellStyle);

		dateCellStyle = wb.createCellStyle();
		dateCellStyle.setDataFormat(helper.createDataFormat().getFormat("MM/dd/yy HH:mm:ss"));
		setCellStyleBorders(dateCellStyle);
		
		wrapCellStyle = wb.createCellStyle();
		wrapCellStyle.setWrapText(true);
		setCellStyleBorders(wrapCellStyle);
		
		headerCellStyle = wb.createCellStyle();
		headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		Font font = wb.createFont();
		font.setItalic(true);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerCellStyle.setFont(font);
		
		rowHeaderCellStyle = wb.createCellStyle();
		rowHeaderCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		rowHeaderCellStyle.setFont(font);
		setCellStyleBorders(rowHeaderCellStyle);
		
		rowHeaderWrapCellStyle = wb.createCellStyle();
		rowHeaderWrapCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		rowHeaderWrapCellStyle.setFont(font);
		rowHeaderWrapCellStyle.setWrapText(true);
		setCellStyleBorders(rowHeaderWrapCellStyle);
		
		boldCellStyle = wb.createCellStyle();
		font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldCellStyle.setFont(font);
	}
	
	private void resizeAllColumns(Sheet sheet, int size, int colNum){
		for(int i=0; i<colNum; i++){
			sheet.setColumnWidth(i, size);
		}
	}
}
