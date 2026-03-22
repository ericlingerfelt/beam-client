package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.pca;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.feature.MultivariateAnalyzerData;
import gov.ornl.bellerophon.beam.data.util.BinnedScale;
import gov.ornl.bellerophon.beam.data.util.GridPoint;
import gov.ornl.bellerophon.beam.data.util.PCADataCell;
import gov.ornl.bellerophon.beam.data.util.PCADataSet;
import gov.ornl.bellerophon.beam.enums.ChartScaleType;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;
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

public class MultivariateAnalyzerPCAEigenPanel extends JPanel implements ComponentListener, 
																				MouseListener, 
																				GridPointCellPanelMouseListener,
																				MultivariateAnalyzerPCAEigenControlPanelListener, 
																				MultivariateAnalyzerPCASetViewPanelListener,
																				ImageExporter,
																				DataExporter,
																				ExcelWriter{

	private Frame frame;
	private MultivariateAnalyzerData d;
	private PCADataSet pds;
	private MultivariateAnalyzerPCAEigenControlPanel controlPanel;

	private JScrollPane sp;
	private JPanel graphicsPanel;
	
	private int numPCPerSet, numPC, minPCIndex, setIndex, numPCSets;
	private double globalMin, globalMax;
	
	private ArrayList<MultivariateAnalyzerPCAEigenPlotPanel> plotPanelList;
	private ArrayList<GridPointChartScalePanel> chartScalePanelList;
	private GridPointChartScalePanel selectedChartScalePanel;
	private MultivariateAnalyzerPCAEigenPlotPanel selectedPlotPanel;
	private MultivariateAnalyzerPCASetViewPanel setPanel;
	
	//Excel objects
	private Workbook wb;
	private CellStyle dateCellStyle, wrapCellStyle, headerCellStyle
							, rowHeaderCellStyle, rowHeaderWrapCellStyle, defaultCellStyle
							, boldCellStyle;
	
	public MultivariateAnalyzerPCAEigenPanel(Frame frame, MultivariateAnalyzerData d, MultivariateAnalyzerPCASetViewPanel setPanel){
		
		this.frame = frame;
		this.d = d;
		this.setPanel = setPanel;
		
		controlPanel = new MultivariateAnalyzerPCAEigenControlPanel(this);
		
		graphicsPanel = new JPanel();
		
		sp = new JScrollPane(graphicsPanel);
		sp.addComponentListener(this);
		
		double[] col = {5, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.PREFERRED, 5};
		double[] row = {5, TableLayoutConstants.FILL, 5};
		setLayout(new TableLayout(col, row));
		add(sp, 			"1, 1, f, f");	
		add(controlPanel,	"3, 1, f, t");	
	}
	
	public void setCurrentState(){
		pds = d.getDataFile().getPCADataSet();
		plotPanelList = new ArrayList<MultivariateAnalyzerPCAEigenPlotPanel>();
		chartScalePanelList = new ArrayList<GridPointChartScalePanel>();
		controlPanel.setCurrentState(pds.getPCADataList().get(0));
		arrangeSet();
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

		if(controlPanel.getMode()==MultivariateAnalyzerPCAEigenPanelMode.PLOT_MODE){
			plotPanelList.clear();
			for(int i=minPCIndex; i<minPCIndex+numPCThisSet; i++){
				MultivariateAnalyzerPCAEigenPlotPanel plotPanel = new MultivariateAnalyzerPCAEigenPlotPanel(i); 
				plotPanel.setCurrentData(pds.getPCADataList().get(i).getVPlotMap().get(controlPanel.getType()));
				plotPanel.setPreferredSize(new Dimension(controlPanel.getZoomSize()*20, controlPanel.getZoomSize()*20));
				plotPanel.addMouseListener(this);
				plotPanelList.add(plotPanel);
			}
		}else if(controlPanel.getMode()==MultivariateAnalyzerPCAEigenPanelMode.CELL_MODE){
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
			
			if(controlPanel.getMode()==MultivariateAnalyzerPCAEigenPanelMode.PLOT_MODE){
			
				MultivariateAnalyzerPCAEigenPlotPanel plotPanel = plotPanelList.get(i);
				graphicsPanel.add(plotPanel, col + ", " + row);
				width = (int) (plotPanel.getPreferredSize().getWidth());
				height = (int) (plotPanel.getPreferredSize().getHeight());
				
			}else if(controlPanel.getMode()==MultivariateAnalyzerPCAEigenPanelMode.CELL_MODE){
				
				GridPointChartScalePanel chartScalePanel = chartScalePanelList.get(i);
				graphicsPanel.add(chartScalePanel, col + ", " + row);
				setChartScalePanelState(chartScalePanel);
				if(controlPanel.showColorBars()){
					width = (int) (chartScalePanel.getChartPanel().getPreferredSize().getWidth() + chartScalePanel.getScalePanel().getPreferredSize().getWidth());
				}else{
					width = (int) (chartScalePanel.getChartPanel().getPreferredSize().getWidth());
				}
				height = (int) (chartScalePanel.getChartPanel().getPreferredSize().getHeight());
			
			}
			
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
		
		if(controlPanel.getGroup()!=null){
		
			globalMin = Double.MAX_VALUE;
			globalMax = 0.0;
			
			if(controlPanel.applySigma()){
				
				double sigma = controlPanel.getSigmaValue();
				double mean = getGlobalMean();
				double stdDev = getGlobalStandardDeviation(mean);
				double stdDevLowLimit = mean-(sigma*stdDev);
				double stdDevHighLimit = mean+(sigma*stdDev);
				
				for(int i=0; i<=pds.getMaxComponentIndex(); i++){
					PCADataCell[][] array = pds.getPCADataList().get(i).getVCellMap().get(controlPanel.getGroup());
					for(int j=0; j<array.length; j++){
						for(int k=0; k<array[0].length; k++){
							PCADataCell cell = array[j][k];
							double value = cell.getValueMap().get(controlPanel.getType());
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
					PCADataCell[][] array = pds.getPCADataList().get(i).getVCellMap().get(controlPanel.getGroup());
					for(int j=0; j<array.length; j++){
						for(int k=0; k<array[0].length; k++){
							PCADataCell cell = array[j][k];
							double value = cell.getValueMap().get(controlPanel.getType());
							globalMin = Math.min(globalMin, value);
							globalMax = Math.max(globalMax, value);
						}
					}
					
				}
				
			}

		}
		
	}
	
	private double getGlobalMean(){
		int totalNumberOfValues = 0;
		double totalValue = 0;
		for(int i=0; i<=pds.getMaxComponentIndex(); i++){
			PCADataCell[][] array = pds.getPCADataList().get(i).getVCellMap().get(controlPanel.getGroup());
			for(int j=0; j<array.length; j++){
				for(int k=0; k<array[0].length; k++){
					PCADataCell cell = array[j][k];
					double value = cell.getValueMap().get(controlPanel.getType());
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
			PCADataCell[][] array = pds.getPCADataList().get(i).getVCellMap().get(controlPanel.getGroup());
			for(int j=0; j<array.length; j++){
				for(int k=0; k<array[0].length; k++){
					PCADataCell cell = array[j][k];
					double value = cell.getValueMap().get(controlPanel.getType());
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
		PCADataCell[][] array = pds.getPCADataList().get(index).getVCellMap().get(controlPanel.getGroup());
		for(int i=0; i<array.length; i++){
			for(int j=0; j<array[0].length; j++){
				PCADataCell cell = array[i][j];
				double value = cell.getValueMap().get(controlPanel.getType());
				totalValue += value;
				totalNumberOfValues++;
			}
		}
		return totalValue/(double)totalNumberOfValues;
	}
	
	private double getStandardDeviation(double mean, int index){
		int totalNumberOfValues = 0;
		double sumOfSquares = 0;
		PCADataCell[][] array = pds.getPCADataList().get(index).getVCellMap().get(controlPanel.getGroup());
		for(int i=0; i<array.length; i++){
			for(int j=0; j<array[0].length; j++){
				PCADataCell cell = array[i][j];
				double value = cell.getValueMap().get(controlPanel.getType());
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
		PCADataCell[][] array = pds.getPCADataList().get(index).getVCellMap().get(controlPanel.getGroup());
		
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

				for(int i=0; i<array.length; i++){
					for(int j=0; j<array[0].length; j++){
						PCADataCell cell = array[i][j];
						double value = cell.getValueMap().get(controlPanel.getType());
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
						PCADataCell cell = array[i][j];
						double value = cell.getValueMap().get(controlPanel.getType());
						min = Math.min(min, value);
						max = Math.max(max, value);
					}
				}
				
			}
		}
		
		BinnedScale bs = new BinnedScale(min, max, 100);
		
		ChartScaleType binListScaleType = ChartScaleType.LIN;
		ArrayList<Double> binList = bs.getLimitedBinList(binListScaleType);
		ArrayList<Color> colorBinList = new ArrayList<Color>();
		
		min = binList.get(0);
		max = binList.get(binList.size()-1);
		for(int i=1; i<binList.size(); i++){
			Color color = controlPanel.getColorMap().getRGB((binList.get(i)-min)/(max-min));
			colorBinList.add(color);
		}
		
		Color[][] colorArray = new Color[array.length][array[0].length];
		
		for(int i=0; i<array.length; i++){
			for(int j=0; j<array[0].length; j++){
				PCADataCell cell = array[i][j];
				double value = cell.getValueMap().get(controlPanel.getType());
				Color color = getColorFromBin(value, binList, colorBinList, binListScaleType, controlPanel.mapOutliers());
				colorArray[i][j] = color;
			}
		}

		chartScalePanel.getChartPanel().setGridWidth(array.length);
		chartScalePanel.getChartPanel().setGridHeight(array[0].length);
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
			double value = pds.getPCADataList().get(selectedChartScalePanel.getIndex()).getVCellMap().get(controlPanel.getGroup())[gridPoint.getX()-1][gridPoint.getY()-1].getValueMap().get(controlPanel.getType());
			double xValue = pds.getPCADataList().get(selectedChartScalePanel.getIndex()).getVCellMap().get(controlPanel.getGroup())[gridPoint.getX()-1][gridPoint.getY()-1].getXValue();
			double yValue = pds.getPCADataList().get(selectedChartScalePanel.getIndex()).getVCellMap().get(controlPanel.getGroup())[gridPoint.getX()-1][gridPoint.getY()-1].getYValue();
			String pc = String.valueOf(selectedChartScalePanel.getIndex()+1);
			String x = String.valueOf(gridPoint.getX());
			String y = String.valueOf(gridPoint.getY());
			String xValueString = new DecimalFormat("0.000E0").format(xValue);
			String yValueString = new DecimalFormat("0.000E0").format(yValue);
			String valueString = new DecimalFormat("0.000E0").format(value);
			controlPanel.setValues(pc, x, y, xValueString, yValueString, valueString);
		}else{
			controlPanel.setValues("", "", "", "", "", "");
		}
	}
	
	public void mouseClicked(MouseEvent me){}
	public void mousePressed(MouseEvent me){}
	public void mouseReleased(MouseEvent me){}

	public void mouseEntered(MouseEvent me){
		if(me.getSource() instanceof MultivariateAnalyzerPCAEigenPlotPanel){
			for(MultivariateAnalyzerPCAEigenPlotPanel panel: plotPanelList){
				if(panel.equals((MultivariateAnalyzerPCAEigenPlotPanel) me.getSource())){
					selectedPlotPanel = panel;
					controlPanel.setValues(String.valueOf(selectedPlotPanel.getIndex() + 1), "", "", "", "", "");
					break;
				}
			}
		}else if(me.getSource() instanceof GridPointChartPanel){
			for(GridPointChartScalePanel panel: chartScalePanelList){
				if(panel.getChartPanel().equals((GridPointChartPanel) me.getSource())){
					selectedChartScalePanel = panel;
					break;
				}
			}
		}
	}

	public void mouseExited(MouseEvent me){
		if(me.getSource() instanceof MultivariateAnalyzerPCAEigenPlotPanel){
			selectedPlotPanel = null;
			controlPanel.setValues("", "", "", "", "", "");
		}else if(me.getSource() instanceof GridPointChartPanel){
			selectedChartScalePanel = null;
		}
	}

	public void setViewPanelStateChanged(){
		createSet();
	}
	
	public void setViewPanelDimensionsChanged(){
		arrangeSet();
	}

	public void eigenControlPanelStateChanged() {
		createSet();
	}

	public void exportCurrentData() {
		
		String suffix = "_eigenvalue_data";
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
		//list.add(FileType.XLS);
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

		String filename = d.getDataFile().getFullPath();
		MultivariateAnalyzerPCAEigenPanelMode mode = controlPanel.getMode();
    	String group = controlPanel.getGroup();
    	ComplexValueType type = controlPanel.getType();
    	
		for(int i=0; i<numPCThisSet; i++){
			
			Row row;
			Cell cell;
			int rowCounter = 0;
			int cellCounter = 0;
			int rowOffset = 0;
			int cellOffset = 0;
			int index;
	    	
			String xlsSheetName = "PC INDEX " + (minPCIndex + i + 1) + " out of " + numPC;
			Sheet sheet = wb.createSheet(xlsSheetName);
			
			row = sheet.createRow(rowCounter++);
			cell = row.createCell(cellCounter++);
		    cell.setCellValue("Data File Name");
		    cell = row.createCell(cellCounter++);
		    cell.setCellValue(filename);
		    cellCounter = 0;
		    
		    row = sheet.createRow(rowCounter++);

	    	row = sheet.createRow(rowCounter++);
			cell = row.createCell(cellCounter++);
		    cell.setCellValue("Eigenvalue Mode");
		    cell = row.createCell(cellCounter++);
		    cell.setCellValue(mode.toString());
		    cellCounter = 0;

	    	row = sheet.createRow(rowCounter++);
			cell = row.createCell(cellCounter++);
		    cell.setCellValue("Eigenvalue Group");
		    cell = row.createCell(cellCounter++);
		    cell.setCellValue(group);
		    cellCounter = 0;

	    	row = sheet.createRow(rowCounter++);
			cell = row.createCell(cellCounter++);
		    cell.setCellValue("Eigenvalue Type");
		    cell = row.createCell(cellCounter++);
		    cell.setCellValue(type.toString());
		    
		    row = sheet.createRow(rowCounter++);

		    switch (mode) {
		    
		    	case PLOT_MODE:
		    		
		    		MultivariateAnalyzerPCAEigenPlotPanel plotPanel = plotPanelList.get(i);
			    	index = plotPanel.getIndex();
			    	
				    row = sheet.createRow(rowCounter++);
				    cellCounter = 0;
					cell = row.createCell(cellCounter++);
				    cell.setCellValue("X");
				    cell = row.createCell(cellCounter++);
				    cell.setCellValue("Y");
				    
				    row = sheet.createRow(rowCounter++);
				    cellCounter = 0;
					cell = row.createCell(cellCounter++);
				    cell.setCellValue(plotPanel.getXTitle());
				    cell = row.createCell(cellCounter++);
				    cell.setCellValue(plotPanel.getYTitle());
			    	
			    	double[] x = plotPanel.getXValueArray();
				    double[] y = plotPanel.getYValueArray();
				    
				    for (int k=0; k<x.length; k++) {
				    	row = sheet.createRow(rowCounter++);
					    cellCounter = 0;
					    cell = row.createCell(cellCounter++, Cell.CELL_TYPE_NUMERIC);
				    	cell.setCellValue(x[k]);
				    	cell = row.createCell(cellCounter++, Cell.CELL_TYPE_NUMERIC);
				    	cell.setCellValue(y[k]);
				    }
		    	break;
		    		
		    	case CELL_MODE:
		    		
		    		GridPointChartScalePanel chartScalePanel = chartScalePanelList.get(i);
			    	index = chartScalePanel.getIndex();
			    	
			    	PCADataCell[][] valueArray = pds.getPCADataList().get(selectedChartScalePanel.getIndex()).getVCellMap().get(controlPanel.getGroup());
			    	
			    	for(int j=0; j<valueArray.length; j++){
						for(int k=0; k<valueArray[0].length; k++){
							PCADataCell cellValue = valueArray[j][k];
							double value = cellValue.getValueMap().get(controlPanel.getType());
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
		    	break;
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
		
		String suffix = "_eigenvalue";
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
