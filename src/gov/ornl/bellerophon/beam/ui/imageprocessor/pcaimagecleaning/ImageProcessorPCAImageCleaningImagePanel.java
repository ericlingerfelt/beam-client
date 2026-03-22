package gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.feature.ImageProcessorData;
import gov.ornl.bellerophon.beam.data.util.BinnedScale;
import gov.ornl.bellerophon.beam.data.util.GridPoint;
import gov.ornl.bellerophon.beam.data.util.PCAImageCleaningDataSet;
import gov.ornl.bellerophon.beam.enums.ChartScaleType;
import gov.ornl.bellerophon.beam.enums.PCAImageType;
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
import gov.ornl.bellerophon.beam.ui.worker.listener.GeneratePCAImagesListener;
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
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImageProcessorPCAImageCleaningImagePanel extends JPanel implements ComponentListener, 
																				GridPointCellPanelMouseListener,
																				ImageProcessorPCAImageCleaningImageControlPanelListener,
																				GeneratePCAImagesListener, 
																				ImageExporter,
																				DataExporter,
																				MouseListener, 
																				ExcelWriter{

	private Frame frame;
	private ImageProcessorData d;
	private PCAImageCleaningDataSet picds;
	private JTabbedPane tabbedPane;
	private JScrollPane sp;
	private JPanel graphicsPanel;
	private GridPointChartScalePanel selectedScalePanel;
	
	private TreeMap<PCAImageType, GridPointChartScalePanel> scalePanelMap;
	private TreeMap<PCAImageType, ImageProcessorPCAImageCleaningImageControlPanel> controlPanelMap;
	
	//Excel objects
	private Workbook wb;
	private CellStyle dateCellStyle, wrapCellStyle, headerCellStyle
							, rowHeaderCellStyle, rowHeaderWrapCellStyle, defaultCellStyle
							, boldCellStyle;
	
	public ImageProcessorPCAImageCleaningImagePanel(Frame frame, ImageProcessorData d){
		this.frame = frame;
		this.d = d;

		scalePanelMap = new TreeMap<PCAImageType, GridPointChartScalePanel>();
		scalePanelMap.put(PCAImageType.CLEAN, new GridPointChartScalePanel(PCAImageType.CLEAN.ordinal()));
		scalePanelMap.put(PCAImageType.NOISE, new GridPointChartScalePanel(PCAImageType.NOISE.ordinal()));
		
		controlPanelMap = new TreeMap<PCAImageType, ImageProcessorPCAImageCleaningImageControlPanel>();
		controlPanelMap.put(PCAImageType.CLEAN, new ImageProcessorPCAImageCleaningImageControlPanel(this, PCAImageType.CLEAN));
		controlPanelMap.put(PCAImageType.NOISE, new ImageProcessorPCAImageCleaningImageControlPanel(this, PCAImageType.NOISE));
		
		JPanel panel1 = new JPanel();
		double[] col1 = {5, TableLayoutConstants.FILL, 5};
		double[] row1 = {5, TableLayoutConstants.FILL, 5};
		panel1.setLayout(new TableLayout(col1, row1));
		panel1.add(controlPanelMap.get(PCAImageType.CLEAN), "1, 1, f, f");	
		
		JPanel panel2 = new JPanel();
		double[] col2 = {5, TableLayoutConstants.FILL, 5};
		double[] row2 = {5, TableLayoutConstants.FILL, 5};
		panel2.setLayout(new TableLayout(col2, row2));
		panel2.add(controlPanelMap.get(PCAImageType.NOISE), "1, 1, f, f");	
		
		tabbedPane = new JTabbedPane();
		tabbedPane.add(PCAImageType.CLEAN.toString(), panel1);
		tabbedPane.add(PCAImageType.NOISE.toString(), panel2);
		
		graphicsPanel = new JPanel();
		sp = new JScrollPane(graphicsPanel);
		sp.addComponentListener(this);
		
		double[] colGraphics = {5, TableLayoutConstants.PREFERRED, 5, TableLayoutConstants.PREFERRED, 5};
		double[] rowGraphics = {5, TableLayoutConstants.PREFERRED, 5};
		graphicsPanel.setLayout(new TableLayout(colGraphics, rowGraphics));
		
		double[] col = {5, TableLayoutConstants.FILL
				, 10, TableLayoutConstants.PREFERRED, 5};
		double[] row = {5, TableLayoutConstants.FILL, 5};
		setLayout(new TableLayout(col, row));
		add(sp, 			"1, 1, f, f");		
		add(tabbedPane,		"3, 1, f, f");		
	}

	public void setCurrentState(){
		
		picds = d.getDataFile().getPCAImageCleaningDataSet();
		
		scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().showSelectedGridHighlight(false);
		scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().setGridPointCellMouseListener(this);
		scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().showCrossHairs(controlPanelMap.get(PCAImageType.CLEAN).showCrossHairs());
		scalePanelMap.get(PCAImageType.CLEAN).showColorBars(controlPanelMap.get(PCAImageType.CLEAN).showColorBars());
		scalePanelMap.get(PCAImageType.CLEAN).showAxis(controlPanelMap.get(PCAImageType.CLEAN).showAxis());
		scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().addMouseListener(this);
		
		scalePanelMap.get(PCAImageType.NOISE).getChartPanel().showSelectedGridHighlight(false);
		scalePanelMap.get(PCAImageType.NOISE).getChartPanel().setGridPointCellMouseListener(this);
		scalePanelMap.get(PCAImageType.NOISE).getChartPanel().showCrossHairs(controlPanelMap.get(PCAImageType.NOISE).showCrossHairs());
		scalePanelMap.get(PCAImageType.NOISE).showColorBars(controlPanelMap.get(PCAImageType.NOISE).showColorBars());
		scalePanelMap.get(PCAImageType.NOISE).showAxis(controlPanelMap.get(PCAImageType.NOISE).showAxis());
		scalePanelMap.get(PCAImageType.NOISE).getChartPanel().addMouseListener(this);
		
		graphicsPanel.removeAll();
		graphicsPanel.add(scalePanelMap.get(PCAImageType.CLEAN), "1, 1, c, c");
		graphicsPanel.add(scalePanelMap.get(PCAImageType.NOISE), "3, 1, c, c");
		
		setChartScalePanelState(PCAImageType.CLEAN);
		setChartScalePanelState(PCAImageType.NOISE);
		
		int width = 0;
		if(controlPanelMap.get(PCAImageType.CLEAN).showColorBars()){
			width += (int) (scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().getPreferredSize().getWidth() + scalePanelMap.get(PCAImageType.CLEAN).getScalePanel().getPreferredSize().getWidth());
		}else{
			width += (int) (scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().getPreferredSize().getWidth());
		}
		if(controlPanelMap.get(PCAImageType.NOISE).showColorBars()){
			width += (int) (scalePanelMap.get(PCAImageType.NOISE).getChartPanel().getPreferredSize().getWidth() + scalePanelMap.get(PCAImageType.NOISE).getScalePanel().getPreferredSize().getWidth());
		}else{
			width += (int) (scalePanelMap.get(PCAImageType.NOISE).getChartPanel().getPreferredSize().getWidth());
		}
		int height = (int) (scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().getPreferredSize().getHeight());
			
		graphicsPanel.setPreferredSize(new Dimension(width+150, height));
		graphicsPanel.setOpaque(false);
		graphicsPanel.validate();
		graphicsPanel.repaint();
		sp.revalidate();
		
	}

	public void imageControlPanelShowCrossHairsChanged(PCAImageType type) {
		scalePanelMap.get(type).getChartPanel().showCrossHairs(controlPanelMap.get(type).showCrossHairs());
	}

	public void imageControlPanelShowColorBarsChanged(PCAImageType type) {
		setChartScalePanelState(type);
	}

	public void imageControlPanelShowAxisChanged(PCAImageType type) {
		scalePanelMap.get(type).getChartPanel().setShowAxis(controlPanelMap.get(type).showAxis());
	}

	public void imageControlPanelApplyUniversalRangeChanged(PCAImageType type) {
		setChartScalePanelState(type);
	}

	public void imageControlPanelApplySigmaChanged(PCAImageType type) {
		setChartScalePanelState(type);
	}

	public void imageControlPanelMapOutliersChanged(PCAImageType type) {
		setChartScalePanelState(type);
	}

	public void imageControlPanelZoomSizeChanged(PCAImageType type) {
		if(type==PCAImageType.CLEAN){
			controlPanelMap.get(PCAImageType.NOISE).setZoomSize(controlPanelMap.get(PCAImageType.CLEAN).getZoomSize());
		}else if(type==PCAImageType.NOISE){
			controlPanelMap.get(PCAImageType.CLEAN).setZoomSize(controlPanelMap.get(PCAImageType.NOISE).getZoomSize());
		}
		setChartScalePanelState(PCAImageType.CLEAN);
		setChartScalePanelState(PCAImageType.NOISE);
	}

	public void imageControlPanelSigmaValueChanged(PCAImageType type) {
		setChartScalePanelState(type);
	}

	public void imageControlPanelColorMapChanged(PCAImageType type) {
		setChartScalePanelState(type);
	}
	
	public void updateAfterGeneratePCAImages() {
		setCurrentState();
	}
	
	/*private int getInitialZoomSize(){
		
		GridPoint gp = (GridPoint) pds.getPCADataList().get(0).getUMap().keySet().toArray()[pds.getPCADataList().get(0).getUMap().keySet().size()-1];
		int zoomSize = (int) Math.ceil((double)250/(double)gp.getX());
		return zoomSize;
		
	}*/
	
	private void setChartScalePanelState(PCAImageType type){
		
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		
		double[][] array = picds.getImage(type).getValueArray();
		ImageProcessorPCAImageCleaningImageControlPanel controlPanel = controlPanelMap.get(type);
		GridPointChartScalePanel chartScalePanel = scalePanelMap.get(type);
		Color[][] colorArray = new Color[array.length][array[0].length];
			
		if(controlPanel.applySigma()){
			
			double sigma = controlPanel.getSigmaValue();
			double mean = getMean(type);
			double stdDev = getStandardDeviation(mean, type);
			double stdDevLowLimit = mean-(sigma*stdDev);
			double stdDevHighLimit = mean+(sigma*stdDev);
			
			for(int i=0; i<array.length; i++){
				for(int j=0; j<array[0].length; j++){
					double value = array[i][j];
					if(value < stdDevLowLimit || value > stdDevHighLimit){
						continue;
					}
					min = Math.min(min, value);
					max = Math.max(max, value);
				}
			}
			
		}else{
			
			for(int i=0; i<array.length; i++){
				for(int j=0; j<array[0].length; j++){
					double value = array[i][j];
					min = Math.min(min, value);
					max = Math.max(max, value);
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
		
		for(int i=0; i<array.length; i++){
			for(int j=0; j<array[0].length; j++){
				double value = array[i][j];
				Color color = getColorFromBin(value, binList, colorBinList, binListScaleType, controlPanel.mapOutliers());
				colorArray[i][j] = color;
			}
		}
		
		chartScalePanel.showColorBars(controlPanel.showColorBars());
		chartScalePanel.getChartPanel().setGridWidth(array.length);
		chartScalePanel.getChartPanel().setGridHeight(array[0].length);
		chartScalePanel.getChartPanel().setColorArray(colorArray);
		chartScalePanel.getChartPanel().setSquareSize(controlPanel.getZoomSize());
		chartScalePanel.getScalePanel().setCurrentState(binList, 
												colorBinList, 
												new DecimalFormat("0.000E0"), 
												20, 
												(int) chartScalePanel.getChartPanel().getPreferredSize().getHeight(), 
												chartScalePanel.getChartPanel().getMarginTop(), 
												chartScalePanel.getChartPanel().getMarginBottom());
		int width = 0;
		if(controlPanel.showColorBars()){
			width = (int) (chartScalePanel.getChartPanel().getPreferredSize().getWidth() + chartScalePanel.getScalePanel().getPreferredSize().getWidth() + 80);
		}else{
			width = (int) (chartScalePanel.getChartPanel().getPreferredSize().getWidth());
		}
		int height = (int) (chartScalePanel.getChartPanel().getPreferredSize().getHeight());
		chartScalePanel.setPreferredSize(new Dimension(width, height));
		chartScalePanel.getScalePanel().revalidate();
		chartScalePanel.getScalePanel().repaint();
		chartScalePanel.validate();
		chartScalePanel.repaint();
		
		width = 0;
		if(controlPanelMap.get(PCAImageType.CLEAN).showColorBars()){
			width += (int) (scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().getPreferredSize().getWidth() + scalePanelMap.get(PCAImageType.CLEAN).getScalePanel().getPreferredSize().getWidth());
		}else{
			width += (int) (scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().getPreferredSize().getWidth());
		}
		if(controlPanelMap.get(PCAImageType.NOISE).showColorBars()){
			width += (int) (scalePanelMap.get(PCAImageType.NOISE).getChartPanel().getPreferredSize().getWidth() + scalePanelMap.get(PCAImageType.NOISE).getScalePanel().getPreferredSize().getWidth());
		}else{
			width += (int) (scalePanelMap.get(PCAImageType.NOISE).getChartPanel().getPreferredSize().getWidth());
		}
		height = (int) (scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().getPreferredSize().getHeight());
			
		graphicsPanel.setPreferredSize(new Dimension(width+150, height));
		graphicsPanel.setOpaque(false);
		graphicsPanel.validate();
		graphicsPanel.repaint();
		sp.revalidate();
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
	
	private double getMean(PCAImageType type){
		int totalNumberOfValues = 0;
		double totalValue = 0;
		double[][] array = picds.getImage(type).getValueArray();
		for(int i=0; i<array.length; i++){
			for(int j=0; j<array[0].length; j++){
				double value = array[i][j];
				totalValue += value;
				totalNumberOfValues++;
			}
		}
		return totalValue/(double)totalNumberOfValues;
	}
	
	private double getStandardDeviation(double mean, PCAImageType type){
		int totalNumberOfValues = 0;
		double sumOfSquares = 0;
		double[][] array = picds.getImage(type).getValueArray();
		for(int i=0; i<array.length; i++){
			for(int j=0; j<array[0].length; j++){
				double value = array[i][j];
				sumOfSquares += Math.pow(value-mean, 2);
				totalNumberOfValues++;
			}
		}
		return Math.sqrt(sumOfSquares/(double)totalNumberOfValues);
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
		if(gridPoint!=null && selectedScalePanel!=null){
			String x = String.valueOf(gridPoint.getX());
			String y = String.valueOf(gridPoint.getY());
			if(selectedScalePanel.equals(scalePanelMap.get(PCAImageType.CLEAN))){
				String value = new DecimalFormat("0.000E0").format(picds.getImage(PCAImageType.CLEAN).getValueArray()[gridPoint.getX()-1][gridPoint.getY()-1]);
				controlPanelMap.get(PCAImageType.CLEAN).setValues(x, y, value);
			}else if(selectedScalePanel.equals(scalePanelMap.get(PCAImageType.NOISE))){
				String value = new DecimalFormat("0.000E0").format(picds.getImage(PCAImageType.NOISE).getValueArray()[gridPoint.getX()-1][gridPoint.getY()-1]);
				controlPanelMap.get(PCAImageType.NOISE).setValues(x, y, value);
			}
		}else{
			controlPanelMap.get(PCAImageType.CLEAN).setValues("", "", "");
			controlPanelMap.get(PCAImageType.NOISE).setValues("", "", "");
		}
	}
	
	public void mouseClicked(MouseEvent me){}
	public void mousePressed(MouseEvent me){}
	public void mouseReleased(MouseEvent me){}

	public void mouseEntered(MouseEvent me){
		if(me.getSource() instanceof GridPointChartPanel){
			if(scalePanelMap.get(PCAImageType.CLEAN).getChartPanel().equals((GridPointChartPanel) me.getSource())){
				selectedScalePanel = scalePanelMap.get(PCAImageType.CLEAN);
				tabbedPane.setSelectedIndex(0);
			}else if(scalePanelMap.get(PCAImageType.NOISE).getChartPanel().equals((GridPointChartPanel) me.getSource())){
				selectedScalePanel = scalePanelMap.get(PCAImageType.NOISE);
				tabbedPane.setSelectedIndex(1);
			}
		}
	}

	public void mouseExited(MouseEvent me){
		if(me.getSource() instanceof GridPointChartPanel){
			selectedScalePanel = null;
		}
	}
	
	public void exportCurrentData() {
		
		String suffix = "_image_data";
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
		
		for(PCAImageType type: PCAImageType.values()){
			
			Row row;
			Cell cell;
			int rowCounter = 0;
			int cellCounter = 0;
			int rowOffset = 0;
			int cellOffset = 0;
			
			String xlsSheetName = "Image Type " + type.toString();
			Sheet sheet = wb.createSheet(xlsSheetName);
			
			row = sheet.createRow(rowCounter++);
			cell = row.createCell(cellCounter++);
		    cell.setCellValue("Data File Name");
		    cell = row.createCell(cellCounter++);
		    cell.setCellValue(d.getDataFile().getFullPath());
		    
		    row = sheet.createRow(rowCounter++);
		    cellCounter = 0;
			
			double[][] array = picds.getImage(type).getValueArray();
			for(int i=0; i<array.length; i++){
				for(int j=0; j<array[0].length; j++){
					double value = array[i][j];
					int xpos = i+1;
					int ypos = j+1;
		
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
		
		String suffix = "_image_data";
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
