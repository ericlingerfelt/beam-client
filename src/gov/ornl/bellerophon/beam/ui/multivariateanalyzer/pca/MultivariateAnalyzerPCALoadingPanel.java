package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.pca;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.feature.MultivariateAnalyzerData;
import gov.ornl.bellerophon.beam.data.util.BinnedScale;
import gov.ornl.bellerophon.beam.data.util.GridPoint;
import gov.ornl.bellerophon.beam.data.util.PCADataSet;
import gov.ornl.bellerophon.beam.enums.ChartScaleType;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.file.CustomFileFilter;
import gov.ornl.bellerophon.beam.file.FileType;
import gov.ornl.bellerophon.beam.ui.chart.GridPointCellPanelMouseListener;
import gov.ornl.bellerophon.beam.ui.chart.GridPointChartPanel;
import gov.ornl.bellerophon.beam.ui.chart.GridPointChartScalePanel;
import gov.ornl.bellerophon.beam.ui.dialog.CautionDialog;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ExcelWriter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.export.TextSaver;
import gov.ornl.bellerophon.beam.ui.util.PlainFileChooserFactory;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MultivariateAnalyzerPCALoadingPanel extends JPanel implements ComponentListener, 
																				GridPointCellPanelMouseListener,
																				MouseListener, 
																				MultivariateAnalyzerPCASetViewPanelListener, 
																				MultivariateAnalyzerPCALoadingControlPanelListener,
																				ImageExporter,
																				DataExporter,
																				ExcelWriter{

	private Frame frame;
	private MultivariateAnalyzerData d;
	private PCADataSet pds;
	private MultivariateAnalyzerPCALoadingControlPanel controlPanel;
	
	private JScrollPane sp;
	private JPanel graphicsPanel;
	
	private int numPCPerSet, numPC, minPCIndex, setIndex, numPCSets;
	private double globalMin, globalMax;
	
	private ArrayList<GridPointChartScalePanel> chartScalePanelList;
	private GridPointChartScalePanel selectedChartScalePanel;
	private MultivariateAnalyzerPCASetViewPanel setPanel;
	
	//Excel objects
	private Workbook wb;
	private CellStyle dateCellStyle, wrapCellStyle, headerCellStyle
							, rowHeaderCellStyle, rowHeaderWrapCellStyle, defaultCellStyle
							, boldCellStyle;
	
	public MultivariateAnalyzerPCALoadingPanel(Frame frame, MultivariateAnalyzerData d, MultivariateAnalyzerPCASetViewPanel setPanel){
		this.frame = frame;
		this.d = d;
		this.setPanel = setPanel;

		controlPanel = new MultivariateAnalyzerPCALoadingControlPanel(this);
		
		graphicsPanel = new JPanel();
		sp = new JScrollPane(graphicsPanel);
		sp.addComponentListener(this);
		
		double[] col = {5, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.PREFERRED, 5};
		double[] row = {5, TableLayoutConstants.FILL, 5};
		setLayout(new TableLayout(col, row));
		add(sp, 		 "1, 1, f, f");	
		add(controlPanel,"3, 1, f, t");	
	}

	public void setCurrentState(){
		pds = d.getDataFile().getPCADataSet();
		chartScalePanelList = new ArrayList<GridPointChartScalePanel>();
		controlPanel.setZoomSize(getInitialZoomSize());
		arrangeSet();
	}
	
	private int getInitialZoomSize(){
		
		int zoomSize = (int) Math.ceil( 250 / (double) pds.getPCADataList().get(0).getUArray()[0].length);
		return zoomSize;
		
	}
	
	public void arrangeSet(){
		
		int numCols = setPanel.getNumCols();
		int numRows = setPanel.getNumRows();
		
		numPC = pds.getMaxComponentIndex()+1;
		numPCPerSet = numCols*numRows;
		numPCSets = (int) Math.ceil((double)numPC/(double)numPCPerSet);
		
		createSet();
	}
	
	private void createSet(){
		
		setGlobalLimits();
		
		setIndex = setPanel.getSetIndex();
		
		minPCIndex = numPCPerSet*setIndex;
		int numPCThisSet = numPCPerSet;
		if(setIndex==(numPCSets-1) && (numPC%numPCPerSet)!=0){
			numPCThisSet = numPC%numPCPerSet;
		}
		
		chartScalePanelList.clear();
		for(int i=minPCIndex; i<minPCIndex+numPCThisSet; i++){
			GridPointChartScalePanel chartScalePanel = new GridPointChartScalePanel(i); 
			chartScalePanel.getChartPanel().showSelectedGridHighlight(false);
			chartScalePanel.getChartPanel().setGridPointCellMouseListener(this);
			chartScalePanel.getChartPanel().showCrossHairs(controlPanel.showCrossHairs());
			chartScalePanel.showColorBars(controlPanel.showColorBars());
			chartScalePanel.showAxis(controlPanel.showAxis());
			chartScalePanel.addMouseListener(this);
			chartScalePanelList.add(chartScalePanel);
		}
		
		double[] colArray = new double[setPanel.getNumCols()];
		double[] rowArray = new double[setPanel.getNumRows()];
		
		for(int i=0; i<colArray.length; i++){
			colArray[i] = TableLayoutConstants.PREFERRED;
		}
		
		for(int i=0; i<rowArray.length; i++){
			rowArray[i] = TableLayoutConstants.PREFERRED;
		}
		
		graphicsPanel.removeAll();
		graphicsPanel.setLayout(new TableLayout(colArray, rowArray));
		int col = 0;
		int row = 0;
		int width = 0;
		int height = 0;
		for(int i=0; i<numPCThisSet; i++){
			GridPointChartScalePanel chartScalePanel = chartScalePanelList.get(i);
			graphicsPanel.add(chartScalePanel, col + ", " + row);
			setChartScalePanelState(chartScalePanel);
			if(controlPanel.showColorBars()){
				width = (int) (chartScalePanel.getChartPanel().getPreferredSize().getWidth() + chartScalePanel.getScalePanel().getPreferredSize().getWidth());
			}else{
				width = (int) (chartScalePanel.getChartPanel().getPreferredSize().getWidth());
			}
			height = (int) (chartScalePanel.getChartPanel().getPreferredSize().getHeight());
			col++;
			if(col==setPanel.getNumCols()){
				col = 0;
				row++;
			}
		}
		graphicsPanel.setPreferredSize(new Dimension(width*setPanel.getNumCols()+50, height*setPanel.getNumRows()));
		graphicsPanel.setOpaque(false);
		graphicsPanel.validate();
		graphicsPanel.repaint();
		sp.revalidate();
	}
	
	private void setGlobalLimits(){
		
		globalMin = Double.MAX_VALUE;
		globalMax = 0;
		
		if(controlPanel.applySigma()){
		
			double sigma = controlPanel.getSigmaValue();
			double mean = getGlobalMean();
			double stdDev = getGlobalStandardDeviation(mean);
			double stdDevLowLimit = mean-(sigma*stdDev);
			double stdDevHighLimit = mean+(sigma*stdDev);
			
			for(int i=0; i<=pds.getMaxComponentIndex(); i++){
				double[][] uArray = pds.getPCADataList().get(i).getUArray();
				for(int j=0; j<uArray.length; j++){
					for(int k=0; k<uArray[0].length; k++){
						double value = uArray[j][k];
						if(value < stdDevLowLimit || value > stdDevHighLimit){
							continue;
						}
						globalMin = Math.min(globalMin, value);
						globalMax = Math.max(globalMax, value);
					}
				}
			}
		
		}else{
			
			for(int i=0; i<=pds.getMaxComponentIndex(); i++){
				double[][] uArray = pds.getPCADataList().get(i).getUArray();
				for(int j=0; j<uArray.length; j++){
					for(int k=0; k<uArray[0].length; k++){
						double value = uArray[j][k];
						globalMin = Math.min(globalMin, value);
						globalMax = Math.max(globalMax, value);
						
					}
				}
			}
			
		}
		
	}
	
	private double getGlobalMean(){
		int totalNumberOfValues = 0;
		double totalValue = 0;
		for(int i=0; i<=pds.getMaxComponentIndex(); i++){
			double[][] uArray = pds.getPCADataList().get(i).getUArray();
			for(int j=0; j<uArray.length; j++){
				for(int k=0; k<uArray[0].length; k++){
					double value = uArray[j][k];
					totalValue += value;
					totalNumberOfValues++;
				}
			}
		}
		return totalValue/(double)totalNumberOfValues;
	}
	
	private double getGlobalStandardDeviation(double mean){
		int totalNumberOfValues = 0;
		double sumOfSquares = 0;
		for(int i=0; i<=pds.getMaxComponentIndex(); i++){
			double[][] uArray = pds.getPCADataList().get(i).getUArray();
			for(int j=0; j<uArray.length; j++){
				for(int k=0; k<uArray[0].length; k++){
					double value = uArray[j][k];
					sumOfSquares += Math.pow(value-mean, 2);
					totalNumberOfValues++;
				}
			}
		}
		return Math.sqrt(sumOfSquares/(double)totalNumberOfValues);
	}
	
	private double getMean(int index){
		int totalNumberOfValues = 0;
		double totalValue = 0;
		double[][] uArray = pds.getPCADataList().get(index).getUArray();
		for(int i=0; i<uArray.length; i++){
			for(int j=0; j<uArray[0].length; j++){
				double value = uArray[i][j];
				totalValue += value;
				totalNumberOfValues++;
			}
		}
		return totalValue/(double)totalNumberOfValues;
	}
	
	private double getStandardDeviation(double mean, int index){
		int totalNumberOfValues = 0;
		double sumOfSquares = 0;
		double[][] uArray = pds.getPCADataList().get(index).getUArray();
		for(int i=0; i<uArray.length; i++){
			for(int j=0; j<uArray[0].length; j++){
				double value = uArray[i][j];
				sumOfSquares += Math.pow(value-mean, 2);
				totalNumberOfValues++;
			}
		}
		return Math.sqrt(sumOfSquares/(double)totalNumberOfValues);
	}
	
	private void setChartScalePanelState(GridPointChartScalePanel chartScalePanel){
		
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		
		int index = chartScalePanel.getIndex();
		
		if(controlPanel.applyUniversalRange()){
			
			min = globalMin;
			max = globalMax;
			
		}else{
			
			if(controlPanel.applySigma()){
				
				double sigma = controlPanel.getSigmaValue();
				double mean = getMean(index);
				double stdDev = getStandardDeviation(mean, index);
				double stdDevLowLimit = mean-(sigma*stdDev);
				double stdDevHighLimit = mean+(sigma*stdDev);
				
				double[][] uArray = pds.getPCADataList().get(index).getUArray();
				for(int i=0; i<uArray.length; i++){
					for(int j=0; j<uArray[0].length; j++){
						double value = uArray[i][j];
						if(value < stdDevLowLimit || value > stdDevHighLimit){
							continue;
						}
						min = Math.min(min, value);
						max = Math.max(max, value);
					}
				}
			}else{
				
				double[][] uArray = pds.getPCADataList().get(index).getUArray();
				for(int i=0; i<uArray.length; i++){
					for(int j=0; j<uArray[0].length; j++){
						double value = uArray[i][j];
						min = Math.min(min, value);
						max = Math.max(max, value);
					}
				}
				
			}
		}

		BinnedScale bs = new BinnedScale(min, max, 100);
		
		ChartScaleType binListScaleType = ChartScaleType.LIN;
		ArrayList<Double> binList = bs.getBinList(binListScaleType);
		ArrayList<Color> colorBinList = new ArrayList<Color>();
		
		min = binList.get(0);
		max = binList.get(binList.size()-1);
		for(int i=1; i<binList.size(); i++){
			Color color = controlPanel.getColorMap().getRGB((binList.get(i)-min)/(max-min));
			colorBinList.add(color);
		}
		
		double[][] uArray = pds.getPCADataList().get(index).getUArray();
		Color[][] colorArray = new Color[uArray.length][uArray[0].length];
		for(int i=0; i<uArray.length; i++){
			for(int j=0; j<uArray[0].length; j++){
				double value = uArray[i][j];
				Color color = getColorFromBin(value, binList, colorBinList, binListScaleType, controlPanel.mapOutliers());
				colorArray[i][j] = color;
			}
		}
		
		chartScalePanel.getChartPanel().setGridWidth(uArray.length);
		chartScalePanel.getChartPanel().setGridHeight(uArray[0].length);
		chartScalePanel.getChartPanel().setColorArray(colorArray);
		chartScalePanel.getChartPanel().setSquareSize(controlPanel.getZoomSize());
		chartScalePanel.getChartPanel().addMouseListener(this);
		chartScalePanel.getScalePanel().setCurrentState(binList, 
												colorBinList, 
												new DecimalFormat("0.000E0"), 
												20, 
												(int) chartScalePanel.getChartPanel().getPreferredSize().getHeight(), 
												chartScalePanel.getChartPanel().getMarginTop(), 
												chartScalePanel.getChartPanel().getMarginBottom());
		int width = 0;
		if(controlPanel.showColorBars()){
			width = (int) (chartScalePanel.getChartPanel().getPreferredSize().getWidth() + chartScalePanel.getScalePanel().getPreferredSize().getWidth() + 10);
		}else{
			width = (int) (chartScalePanel.getChartPanel().getPreferredSize().getWidth());
		}
		int height = (int) (chartScalePanel.getChartPanel().getPreferredSize().getHeight());
		chartScalePanel.setPreferredSize(new Dimension(width, height));
		chartScalePanel.getScalePanel().revalidate();
		chartScalePanel.getScalePanel().repaint();
		chartScalePanel.validate();
		chartScalePanel.repaint();
	}
	
	private Color getColorFromBin(double value
										, ArrayList<Double> binValueList
										, ArrayList<Color> binColorList
										, ChartScaleType chartScaleType
										, boolean mapOutliers){
		
		if(chartScaleType==ChartScaleType.LIN){
			for(int i=0; i<binValueList.size()-1; i++){
				double lowValue = binValueList.get(i);
				double highValue = binValueList.get(i+1);
				if(value>=0){
					lowValue = 0.999*lowValue;
					highValue = 1.001*highValue;
				}else{
					lowValue = 1.001*lowValue;
					highValue = 0.999*highValue;
				}
				if(value<=lowValue && mapOutliers && i==0){
					return binColorList.get(0);
				}else if(value>lowValue && value<highValue){
					return binColorList.get(i);
				}
			}
		}else if(chartScaleType==ChartScaleType.LOG){
			for(int i=0; i<binValueList.size()-1; i++){
				double lowValue = binValueList.get(i);
				double highValue = binValueList.get(i+1);
				if(Math.log10(value)<=(0.999999*lowValue) && mapOutliers && i==0){
					return binColorList.get(0);
				}else if(Math.log10(value)>(0.999999*lowValue) && Math.log10(value)<(1.000001*highValue)){
					return binColorList.get(i);
				}
			}
		}
		if(mapOutliers){
			return binColorList.get(binColorList.size()-1);
		}
		return Color.WHITE;
	}
	
	public void componentResized(ComponentEvent ce){
		/*if(ce.getSource()==spChart && !chartInitialized){
			chartInitialized = true;
			setChartSquareSize();	
		}*/
	}
	public void componentMoved(ComponentEvent ce){}
	public void componentShown(ComponentEvent ce){}
	public void componentHidden(ComponentEvent ce){}

	public void gridPointCellPanelMouseOvered(GridPoint gridPoint) {
		if(gridPoint!=null && selectedChartScalePanel!=null){
			String pc = String.valueOf(selectedChartScalePanel.getIndex()+1);
			String x = String.valueOf(gridPoint.getX());
			String y = String.valueOf(gridPoint.getY());
			String value = new DecimalFormat("0.000E0").format(pds.getPCADataList().get(Integer.valueOf(pc)-1).getUArray()[gridPoint.getX()-1][gridPoint.getY()-1]);
			controlPanel.setValues(pc, x, y, value);
		}else{
			controlPanel.setValues("", "", "", "");
		}
	}

	public void mouseClicked(MouseEvent me){}
	public void mousePressed(MouseEvent me){}
	public void mouseReleased(MouseEvent me){}

	public void mouseEntered(MouseEvent me){
		if(me.getSource() instanceof GridPointChartPanel){
			for(GridPointChartScalePanel panel: chartScalePanelList){
				if(panel.getChartPanel().equals((GridPointChartPanel) me.getSource())){
					selectedChartScalePanel = panel;
					break;
				}
			}
		}
	}

	public void mouseExited(MouseEvent me){
		if(me.getSource() instanceof GridPointChartPanel){
			selectedChartScalePanel = null;
		}
	}

	public void setViewPanelStateChanged() {
		createSet();
	}
	
	public void setViewPanelDimensionsChanged() {
		arrangeSet();
	}

	public void loadingControlPanelStateChanged() {
		createSet();
	}

	public void exportCurrentData() {
		
		String suffix = "_loading_map_data";
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
		
		ArrayList<FileType> list = new ArrayList<FileType>();
		list.add(FileType.XLSX);
		HashMap<FileType, String> map = new HashMap<FileType, String>();
		try{
			TextSaver.saveText(frame, list, map, filename, this);
		}catch (Exception e){
			CaughtExceptionHandler.handleException(e, frame);
		}
	}

	public void writeExcel(String filepath) throws Exception {
		wb = new XSSFWorkbook();
		CreationHelper helper = wb.getCreationHelper();
		initializeCellStyles(wb, helper);
		writeExcelReport();
		FileOutputStream fileOut = new FileOutputStream(filepath);
	    wb.write(fileOut);
	    fileOut.close();
	}
	
	private void writeExcelReport(){
		
		int numPCThisSet = numPCPerSet;
		if(setIndex==(numPCSets-1) && (numPC%numPCPerSet)!=0){
			numPCThisSet = numPC%numPCPerSet;
		}
		
		for(int i=0; i<numPCThisSet; i++){
			
			Row row;
			Cell cell;
			int rowCounter = 0;
			int cellCounter = 0;
			int rowOffset = 0;
			int cellOffset = 0;
			
			String xlsSheetName = "PC INDEX " + (minPCIndex + i + 1) + " out of " + numPC;
			Sheet sheet = wb.createSheet(xlsSheetName);
			
			row = sheet.createRow(rowCounter++);
			cell = row.createCell(cellCounter++);
		    cell.setCellValue("Data File Name");
		    cell = row.createCell(cellCounter++);
		    cell.setCellValue(d.getDataFile().getFullPath());
		    
		    row = sheet.createRow(rowCounter++);
		    cellCounter = 0;
			
			GridPointChartScalePanel chartScalePanel = chartScalePanelList.get(i);
			int index = chartScalePanel.getIndex();
			
			double[][] uArray = pds.getPCADataList().get(index).getUArray();
			for(int j=0; j<uArray.length; j++){
				for(int k=0; k<uArray[0].length; k++){
					double value = uArray[i][j];
					int xpos = j+1;
					int ypos = k+1;
					
					row = sheet.getRow(rowCounter);
					if(row==null) { 
						row = sheet.createRow(rowCounter);
					}
					cell = row.getCell(cellOffset + xpos);
					if (cell == null) { 
						cell = row.createCell(cellOffset + xpos, Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(xpos);
					}
					
					row = sheet.getRow(rowCounter + rowOffset + ypos);
					if(row==null) { 
						row = sheet.createRow(rowCounter + rowOffset + ypos);
					}
					cell = row.getCell(cellOffset);
					if (cell == null) { 
						cell = row.createCell(cellOffset, Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(ypos);
					}
					
					row = sheet.getRow(rowCounter + rowOffset + ypos);
					if(row==null) {
						row = sheet.createRow(rowCounter + rowOffset + ypos);
					}
					cell = row.getCell(cellOffset + xpos);
					if (cell == null) {
					    cell = row.createCell(cellOffset + xpos, Cell.CELL_TYPE_NUMERIC);
					}
					cell.setCellValue(value);
				}
			}
		}
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
	
	public void exportCurrentImage() {
		
		String suffix = "_loading_maps";
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
			exportPlotImage(filename);
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
				BufferedImage bi = new BufferedImage(graphicsPanel.getSize().width, graphicsPanel.getSize().height, BufferedImage.TYPE_INT_ARGB); 
				Graphics g = bi.createGraphics();
				graphicsPanel.paint(g);
				g.dispose();
				ImageIO.write(bi, "png", file);
				break;
		}
	}

}
