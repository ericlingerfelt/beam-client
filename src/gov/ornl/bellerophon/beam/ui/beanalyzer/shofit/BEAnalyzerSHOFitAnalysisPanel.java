package gov.ornl.bellerophon.beam.ui.beanalyzer.shofit;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.feature.BEAnalyzerData;
import gov.ornl.bellerophon.beam.data.util.*;
import gov.ornl.bellerophon.beam.enums.ChartScaleType;
import gov.ornl.bellerophon.beam.enums.ColorMapType;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;
import gov.ornl.bellerophon.beam.enums.SHOFitDatasetType;
import gov.ornl.bellerophon.beam.enums.SHOFitLoopPlotType;
import gov.ornl.bellerophon.beam.enums.SHOFitPlotType;
import gov.ornl.bellerophon.beam.enums.SHOFitDataType;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.file.CustomFileFilter;
import gov.ornl.bellerophon.beam.file.FileType;
import gov.ornl.bellerophon.beam.ui.chart.BinnedColorScalePanel;
import gov.ornl.bellerophon.beam.ui.chart.GridPointCellPanelMouseListener;
import gov.ornl.bellerophon.beam.ui.chart.GridPointCellPanelSelectionListener;
import gov.ornl.bellerophon.beam.ui.chart.GridPointChartPanel;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import gov.ornl.bellerophon.beam.ui.dialog.CautionDialog;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ExcelWriter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.export.TextSaver;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.format.Colors;
import gov.ornl.bellerophon.beam.ui.util.BoundsPopupMenuListener;
import gov.ornl.bellerophon.beam.ui.util.PlainFileChooserFactory;
import gov.ornl.bellerophon.beam.ui.worker.GetRawDataWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetRawDataListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Color;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BEAnalyzerSHOFitAnalysisPanel extends JPanel implements ChangeListener
																		, ActionListener
																		, ImageExporter
																		, GetRawDataListener
																		, ComponentListener
																		, DataExporter
																		, ExcelWriter{

	//Data Structures
	private BEAnalyzerData d;
	private SHOFitDataSet sfds;
	private ColorMap chartColorMap, plotColorMap;
	private GridPoint selectedChartGridPoint, mouseOverChartGridPoint, mouseOverPlotGridPoint;
	private String selectedGroup;
	private SHOFitDatasetType selectedDatasetType;
	
	private TreeMap<SHOFitDataType, Double> globalMaxMap = new TreeMap<SHOFitDataType, Double>();
	private TreeMap<SHOFitDataType, Double> globalMinMap = new TreeMap<SHOFitDataType, Double>();
	
	private int dcOffsetIndex;
	private SHOFitDataType shoFitDataTypeChart, shoFitDataTypePlotX, shoFitDataTypePlotY;
	private int[][] countArray; 
	
	//New DS
	private boolean[][] gridPointMaskArray;
	private TreeMap<SHOFitDataType, BinnedScale> binMap = new TreeMap<SHOFitDataType, BinnedScale>();
	
	//Panels
	private GridPointChartPanel chartPanel, plotPanel;
	private BEAnalyzerSHOFitAnalysisFitPlotPanel fitPlotPanel;
	private BEAnalyzerSHOFitAnalysisLoopPlotPanel loopPlotPanel;
	private BinnedColorScalePanel colorScalePanelChart, colorScalePanelPlot;
	private JPanel buttonPanelChartValues, buttonPanelPlotValues, graphicsPanelChart
					, graphicsPanelPlot, buttonPanelChartOptions, buttonPanelChartPlotView
					, buttonPanelChartOutput;
	
	//Constants
	private int numBinsPlot = 100;
	private int numBinsCountsDefault = 100;
	private int numBinsCounts = 100;
	private int numStdDevs = 3;
	private int chartSquareSize;
	private int plotSquareSize;
	private DecimalFormat countsFormat  = new DecimalFormat("##########");
	private DecimalFormat dcOffsetFormat  = new DecimalFormat("#########0.00");
	
	//Chart UI components
	private JScrollPane spChart;
	private JComboBox<SHOFitDataType> dataTypeBoxChart;
	private JTextField gridPointXField, gridPointYField
						, aField, wField, qField, pField
						, dcOffsetField, dcStepField;
	private JLabel dataTypeLabel, gridPointXLabel, gridPointYLabel
						, aLabel, wLabel, qLabel, pLabel
						, dcOffsetLabel, dcStepLabel
						, chartSizeLabel, chartColorMapLabel, groupLabel
						, datasetLabel, plotTypeLabel, loopPlotTypeLabel, fitPlotTypeLabel;
	private JSlider dcOffsetSlider;
	private JCheckBox showChartCrossHairsBox, applyLimitsBoxChart, applyMaskBoxChart;
	private JSpinner chartSizeSpinner;
	private SpinnerNumberModel chartSizeSpinnerModel;
	private JComboBox<ColorMapType> chartColorMapBox;
	private JButton mainDataButton;
	private JComboBox<ComplexValueType> fitPlotTypeBox;
	private JComboBox<SHOFitDatasetType> datasetBox;
	private JComboBox<String> groupBox;
	private JComboBox<SHOFitPlotType> plotTypeBox;
	private JComboBox<SHOFitLoopPlotType> loopPlotTypeBox;
	private boolean chartInitialized = false;
	
	//Plot UI components
	private JScrollPane spPlot;
	private JTextField xValueField, yValueField, countsValueField;
	private JComboBox<SHOFitDataType> dataTypeBoxPlotX, dataTypeBoxPlotY;
	private JCheckBox showPlotCrossHairsBox, applyLimitsBoxPlot;
	private JLabel dataTypeLabelX, dataTypeLabelY, scaleLabelX, scaleLabelY, 
					minLabelX, maxLabelX, minLabelY, maxLabelY, xValueLabel, yValueLabel, countsValueLabel, 
					plotColorMapLabel, plotSizeLabel;
	private JSpinner minXSpinner, maxXSpinner, 
							minYSpinner, maxYSpinner, plotSizeSpinner;
	private SpinnerListModel minXSpinnerModel, maxXSpinnerModel, 
							minYSpinnerModel, maxYSpinnerModel;
	private SpinnerNumberModel plotSizeSpinnerModel;
	private JComboBox<ChartScaleType> scaleBoxPlotX, scaleBoxPlotY;
	private ChartScaleType scalePlotX, scalePlotY;
	private JComboBox<ColorMapType> plotColorMapBox;
	private boolean plotInitialized = false;
	
	private JTabbedPane pane;
	private Frame owner;
	private JPanel chartLayoutPanel;
	
	//Excel objects
	private Workbook wb;
	private CellStyle dateCellStyle, wrapCellStyle, headerCellStyle
							, rowHeaderCellStyle, rowHeaderWrapCellStyle, defaultCellStyle
							, boldCellStyle;
	
	public BEAnalyzerSHOFitAnalysisPanel(Frame owner, BEAnalyzerData d){
		
		this.owner = owner;
		this.d = d;
		
		chartColorMap = new ColorMap();
		chartColorMap.setColorMapType(ColorMapType.RAINBOW);
		
		chartColorMapBox = new JComboBox<ColorMapType>();
		for(ColorMapType type: ColorMapType.values()){
			chartColorMapBox.addItem(type);
		}
		chartColorMapBox.setSelectedItem(ColorMapType.RAINBOW);
		chartColorMapBox.addActionListener(this);
		
		fitPlotTypeBox = new JComboBox<ComplexValueType>();
		for(ComplexValueType type: ComplexValueType.values()){
			fitPlotTypeBox.addItem(type);
		}
		fitPlotTypeBox.addActionListener(this);
		
		plotTypeBox = new JComboBox<SHOFitPlotType>();
		for(SHOFitPlotType type: SHOFitPlotType.values()){
			plotTypeBox.addItem(type);
		}
		plotTypeBox.setSelectedItem(SHOFitPlotType.FIT_PLOT);
		plotTypeBox.addActionListener(this);
		
		loopPlotTypeBox = new JComboBox<SHOFitLoopPlotType>();
		for(SHOFitLoopPlotType type: SHOFitLoopPlotType.values()){
			loopPlotTypeBox.addItem(type);
		}
		loopPlotTypeBox.setSelectedItem(SHOFitLoopPlotType.A);
		loopPlotTypeBox.addActionListener(this);
		
		fitPlotTypeLabel = new JLabel("Quantity:");
		
		chartSizeSpinnerModel = new SpinnerNumberModel();
		chartSizeSpinnerModel.setMinimum(1);
		chartSizeSpinnerModel.setMaximum(100);
		chartSizeSpinnerModel.setStepSize(1);
		chartSizeSpinner = new JSpinner(chartSizeSpinnerModel);
		((JSpinner.DefaultEditor) chartSizeSpinner.getEditor()).getTextField().setEditable(false);
		chartSizeSpinner.addChangeListener(this);
		
		plotTypeLabel = new JLabel("Plot Type:");
		loopPlotTypeLabel = new JLabel("Quantity:");
		
		chartColorMapLabel = new JLabel("Colormap:");
		chartSizeLabel = new JLabel("Zoom:");
		dataTypeLabel = new JLabel("Param:");
		dcOffsetLabel = new JLabel("DC Offset (V):");
		dcStepLabel = new JLabel("DC Step:");
		gridPointXLabel = new JLabel("X:");
		gridPointYLabel = new JLabel("Y:");
		aLabel = new JLabel(SHOFitDataType.A.toString() + ":");
		wLabel = new JLabel(SHOFitDataType.W.toString() + ":");
		qLabel = new JLabel(SHOFitDataType.Q.toString() + ":");
		pLabel = new JLabel(SHOFitDataType.P.toString() + ":");
		
		dcOffsetField = new JTextField();
		dcOffsetField.setEditable(false);
		dcStepField = new JTextField();
		dcStepField.setEditable(false);
		gridPointXField = new JTextField();
		gridPointXField.setEditable(false);
		gridPointYField = new JTextField();
		gridPointYField.setEditable(false);
		aField = new JTextField();
		aField.setEditable(false);
		wField = new JTextField();
		wField.setEditable(false);
		qField = new JTextField();
		qField.setEditable(false);
		pField = new JTextField();
		pField.setEditable(false);
		
		datasetLabel = new JLabel("Dataset:");
		
		datasetBox = new JComboBox<SHOFitDatasetType>();
		
		dataTypeBoxChart = new JComboBox<SHOFitDataType>();
		for(SHOFitDataType type: SHOFitDataType.values()){
			dataTypeBoxChart.addItem(type);
		}
		dataTypeBoxChart.setSelectedItem(SHOFitDataType.A);
		dataTypeBoxChart.addActionListener(this);
		
		groupBox = new JComboBox<String>();
		groupLabel = new JLabel("Group:");
		
		dcOffsetSlider = new JSlider();
		
		fitPlotPanel = new BEAnalyzerSHOFitAnalysisFitPlotPanel();
		loopPlotPanel = new BEAnalyzerSHOFitAnalysisLoopPlotPanel();
		
		chartPanel = new GridPointChartPanel(GridPointChartPanel.GridPointChartType.GRID_CELL_MAP);
		chartPanel.setGridPointCellMouseListener(new ChartGridPointCellPanelMouseListener(this));
		chartPanel.setGridPointCellSelectionListener(new ChartGridPointCellPanelSelectionListener(this));
		chartPanel.setMarginLeft(80);
		chartPanel.setMarginRight(20);
		chartPanel.setMarginTop(20);
		chartPanel.setMarginBottom(20);
		
		colorScalePanelChart = new BinnedColorScalePanel();
		
		showChartCrossHairsBox = new JCheckBox("Show Crosshairs?");
		showChartCrossHairsBox.setSelected(true);
		showChartCrossHairsBox.addActionListener(this);
		
		applyLimitsBoxChart = new JCheckBox("Apply Data Limits?");
		applyLimitsBoxChart.setSelected(true);
		applyLimitsBoxChart.addActionListener(this);
		
		applyMaskBoxChart = new JCheckBox("Apply Param Mask?");
		applyMaskBoxChart.setSelected(false);
		applyMaskBoxChart.addActionListener(this);
		
		mainDataButton = new JButton("View Raw Data");
		mainDataButton.addActionListener(this);
		mainDataButton.setEnabled(false);
		
		buttonPanelChartOptions = new JPanel();
		buttonPanelChartOptions.setBorder(Borders.getBorder("Chart Options"));
		
		buttonPanelChartValues = new JPanel();
		buttonPanelChartValues.setBorder(Borders.getBorder("Selected Values"));
		double[] columnButtonChartValues = {5, TableLayoutConstants.PREFERRED,
												7, TableLayoutConstants.FILL, 5};
		double[] rowButtonChartValues = {5, TableLayoutConstants.PREFERRED
												, 7, TableLayoutConstants.PREFERRED
												, 7, TableLayoutConstants.PREFERRED
												, 7, TableLayoutConstants.PREFERRED
												, 7, TableLayoutConstants.PREFERRED
												, 7, TableLayoutConstants.PREFERRED
												, 7, TableLayoutConstants.PREFERRED, 5};
		buttonPanelChartValues.setLayout(new TableLayout(columnButtonChartValues, rowButtonChartValues));
		buttonPanelChartValues.add(gridPointXLabel, 		"1, 1, r, c");
		buttonPanelChartValues.add(gridPointXField, 		"3, 1, f, c");
		buttonPanelChartValues.add(gridPointYLabel, 		"1, 3, r, c");
		buttonPanelChartValues.add(gridPointYField, 		"3, 3, f, c");
		buttonPanelChartValues.add(aLabel, 					"1, 5, r, c");
		buttonPanelChartValues.add(aField, 					"3, 5, f, c");
		buttonPanelChartValues.add(wLabel, 					"1, 7, r, c");
		buttonPanelChartValues.add(wField, 					"3, 7, f, c");
		buttonPanelChartValues.add(qLabel, 					"1, 9, r, c");
		buttonPanelChartValues.add(qField, 					"3, 9, f, c");
		buttonPanelChartValues.add(pLabel, 					"1, 11, r, c");
		buttonPanelChartValues.add(pField, 					"3, 11, f, c");
		buttonPanelChartValues.add(showChartCrossHairsBox, 	"1, 13, 3, 13, c, c");
		
		JPanel buttonPanelChartView = new JPanel();
		buttonPanelChartView.setBorder(Borders.getBorder("Chart View Options"));
		double[] columnButtonChartView = {5, TableLayoutConstants.PREFERRED,
											7, TableLayoutConstants.FILL, 5};
		double[] rowButtonChartView = {5, TableLayoutConstants.PREFERRED, 
											9, TableLayoutConstants.PREFERRED,
											9, TableLayoutConstants.PREFERRED,
											9, TableLayoutConstants.PREFERRED, 5};
		buttonPanelChartView.setLayout(new TableLayout(columnButtonChartView, rowButtonChartView));
		buttonPanelChartView.add(chartSizeLabel, 		"1, 1, r, c");
		buttonPanelChartView.add(chartSizeSpinner, 		"3, 1, f, c");
		buttonPanelChartView.add(chartColorMapLabel, 	"1, 3, r, c");
		buttonPanelChartView.add(chartColorMapBox, 		"3, 3, f, c");
		buttonPanelChartView.add(applyLimitsBoxChart, 	"1, 5, 3, 5, c, c");
		buttonPanelChartView.add(applyMaskBoxChart, 	"1, 7, 3, 7, c, c");
		
		buttonPanelChartPlotView = new JPanel();
		buttonPanelChartPlotView.setBorder(Borders.getBorder("Plot View Options"));

		buttonPanelChartOutput = new JPanel();
		double[] columnButtonChartOutput = {TableLayoutConstants.FILL};
		double[] rowButtonChartOutput = {TableLayoutConstants.PREFERRED, 
											10, TableLayoutConstants.PREFERRED,
											10, TableLayoutConstants.PREFERRED};
		buttonPanelChartOutput.setLayout(new TableLayout(columnButtonChartOutput, rowButtonChartOutput));
		buttonPanelChartOutput.add(buttonPanelChartValues, 		"0, 0, f, c");
		buttonPanelChartOutput.add(buttonPanelChartView, 		"0, 2, f, c");
		buttonPanelChartOutput.add(buttonPanelChartPlotView, 	"0, 4, f, c");
		
		graphicsPanelChart = new JPanel();
		graphicsPanelChart.setOpaque(false);
		double[] colGraphicsChart = {5, TableLayoutConstants.PREFERRED, 
										10, TableLayoutConstants.PREFERRED,
										10, TableLayoutConstants.PREFERRED, 5};
		double[] rowGraphicsChart = {5, TableLayoutConstants.PREFERRED, 5};
		graphicsPanelChart.setLayout(new TableLayout(colGraphicsChart, rowGraphicsChart));
		
		spChart = new JScrollPane(graphicsPanelChart);
		spChart.addComponentListener(this);
		
		chartLayoutPanel = new JPanel();
		double[] colChartLayout = {5, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.PREFERRED, 5};
		double[] rowChartLayout = {5, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.PREFERRED, 5};
		chartLayoutPanel.setLayout(new TableLayout(colChartLayout, rowChartLayout));
		chartLayoutPanel.add(spChart, 					"1, 1, f, f");	
		chartLayoutPanel.add(buttonPanelChartOptions,	"1, 3, 3, 3, f, c");	
		chartLayoutPanel.add(buttonPanelChartOutput,	"3, 1, f, t");	
		
		//////////PLOT COMPONENTS///////////////////////////////////////////
		plotPanel = new GridPointChartPanel(GridPointChartPanel.GridPointChartType.HISTROGRAM);
		plotPanel.setGridPointCellMouseListener(new PlotGridPointCellPanelMouseListener(this));
		plotPanel.showMouseOverGridHighlight(true);
		plotPanel.showSelectedGridHighlight(false);
		plotPanel.setMarginLeft(80);
		plotPanel.setMarginRight(20);
		plotPanel.setMarginTop(20);
		plotPanel.setMarginBottom(60);

		plotColorMap = new ColorMap();
		plotColorMap.setColorMapType(ColorMapType.RAINBOW);
		
		plotColorMapBox = new JComboBox<ColorMapType>();
		for(ColorMapType type: ColorMapType.values()){
			plotColorMapBox.addItem(type);
		}
		plotColorMapBox.setSelectedItem(ColorMapType.RAINBOW);
		plotColorMapBox.addActionListener(this);
		
		plotSizeSpinnerModel = new SpinnerNumberModel();
		plotSizeSpinnerModel.setMinimum(1);
		plotSizeSpinnerModel.setMaximum(40);
		plotSizeSpinnerModel.setStepSize(1);
		plotSizeSpinner = new JSpinner(plotSizeSpinnerModel);
		((JSpinner.DefaultEditor) plotSizeSpinner.getEditor()).getTextField().setEditable(false);
		plotSizeSpinner.addChangeListener(this);
		
		colorScalePanelPlot = new BinnedColorScalePanel();

		showPlotCrossHairsBox = new JCheckBox("Show Crosshairs?");
		showPlotCrossHairsBox.setSelected(true);
		showPlotCrossHairsBox.addActionListener(this);
		
		plotColorMapLabel = new JLabel("Colormap:");
		plotSizeLabel = new JLabel("Zoom:");
		dataTypeLabelX = new JLabel("Param:");
		dataTypeLabelY = new JLabel("Param:");
		scaleLabelX = new JLabel("Scale:");
		scaleLabelY = new JLabel("Scale:");
		minLabelX = new JLabel("Min:");
		maxLabelX = new JLabel("Max:");
		minLabelY = new JLabel("Min:");
		maxLabelY = new JLabel("Max:");
		xValueLabel = new JLabel("X Bin:");
		yValueLabel = new JLabel("Y Bin:");
		countsValueLabel = new JLabel("Counts:");
		
		xValueField = new JTextField();
		xValueField.setEditable(false);
		yValueField = new JTextField();
		yValueField.setEditable(false);
		countsValueField = new JTextField();
		countsValueField.setEditable(false);
		
		minXSpinnerModel = new SpinnerListModel();
		maxXSpinnerModel = new SpinnerListModel();
		minYSpinnerModel = new SpinnerListModel();
		maxYSpinnerModel = new SpinnerListModel();
		
		minXSpinner = new JSpinner(minXSpinnerModel);
		((JSpinner.DefaultEditor) minXSpinner.getEditor()).getTextField().setEditable(false);
		minXSpinner.addChangeListener(this);
		
		maxXSpinner = new JSpinner(maxXSpinnerModel);
		((JSpinner.DefaultEditor) maxXSpinner.getEditor()).getTextField().setEditable(false);
		maxXSpinner.addChangeListener(this);
		
		minYSpinner = new JSpinner(minYSpinnerModel);
		((JSpinner.DefaultEditor) minYSpinner.getEditor()).getTextField().setEditable(false);
		minYSpinner.addChangeListener(this);
		
		maxYSpinner = new JSpinner(maxYSpinnerModel);
		((JSpinner.DefaultEditor) maxYSpinner.getEditor()).getTextField().setEditable(false);
		maxYSpinner.addChangeListener(this);
		
		dataTypeBoxPlotX = new JComboBox<SHOFitDataType>();
		for(SHOFitDataType type: SHOFitDataType.values()){
			dataTypeBoxPlotX.addItem(type);
		}
		dataTypeBoxPlotX.setSelectedItem(SHOFitDataType.Q);
		dataTypeBoxPlotX.addActionListener(this);
		
		dataTypeBoxPlotY = new JComboBox<SHOFitDataType>();
		for(SHOFitDataType type: SHOFitDataType.values()){
			dataTypeBoxPlotY.addItem(type);
		}
		dataTypeBoxPlotY.setSelectedItem(SHOFitDataType.A);
		dataTypeBoxPlotY.addActionListener(this);
		
		scaleBoxPlotX = new JComboBox<ChartScaleType>();
		for(ChartScaleType type: ChartScaleType.values()){
			scaleBoxPlotX.addItem(type);
		}
		scaleBoxPlotX.addActionListener(this);
		
		scaleBoxPlotY = new JComboBox<ChartScaleType>();
		for(ChartScaleType type: ChartScaleType.values()){
			scaleBoxPlotY.addItem(type);
		}
		scaleBoxPlotY.addActionListener(this);
		
		JPanel buttonPanelPlotX = new JPanel();
		buttonPanelPlotX.setBorder(Borders.getBorder("X Axis Options"));
		double[] columnButtonPlotX = {5, TableLayoutConstants.PREFERRED,
									7, TableLayoutConstants.FILL,
									10, TableLayoutConstants.PREFERRED,
									7, TableLayoutConstants.FILL,
									10, TableLayoutConstants.PREFERRED,
									7, TableLayoutConstants.FILL,
									10, TableLayoutConstants.PREFERRED,
									7, TableLayoutConstants.FILL, 5};
		double[] rowButtonPlotX = {5, TableLayoutConstants.PREFERRED, 5};
		buttonPanelPlotX.setLayout(new TableLayout(columnButtonPlotX, rowButtonPlotX));
		buttonPanelPlotX.add(dataTypeLabelX, 	"1, 1, r, c");
		buttonPanelPlotX.add(dataTypeBoxPlotX, 	"3, 1, f, c");
		buttonPanelPlotX.add(scaleLabelX, 		"5, 1, r, c");
		buttonPanelPlotX.add(scaleBoxPlotX, 	"7, 1, f, c");
		buttonPanelPlotX.add(minLabelX, 		"9, 1, r, c");
		buttonPanelPlotX.add(minXSpinner, 		"11, 1, f, c");
		buttonPanelPlotX.add(maxLabelX, 		"13, 1, r, c");
		buttonPanelPlotX.add(maxXSpinner, 		"15, 1, f, c");
		
		JPanel buttonPanelPlotY = new JPanel();
		buttonPanelPlotY.setBorder(Borders.getBorder("Y Axis Options"));
		double[] columnButtonPlotY = {5, TableLayoutConstants.PREFERRED,
									7, TableLayoutConstants.FILL,
									10, TableLayoutConstants.PREFERRED,
									7, TableLayoutConstants.FILL,
									10, TableLayoutConstants.PREFERRED,
									7, TableLayoutConstants.FILL,
									10, TableLayoutConstants.PREFERRED,
									7, TableLayoutConstants.FILL, 5};
		double[] rowButtonPlotY = {5, TableLayoutConstants.PREFERRED, 5};
		buttonPanelPlotY.setLayout(new TableLayout(columnButtonPlotY, rowButtonPlotY));
		buttonPanelPlotY.add(dataTypeLabelY, 	"1, 1, r, c");
		buttonPanelPlotY.add(dataTypeBoxPlotY, 	"3, 1, f, c");
		buttonPanelPlotY.add(scaleLabelY, 		"5, 1, r, c");
		buttonPanelPlotY.add(scaleBoxPlotY, 	"7, 1, f, c");
		buttonPanelPlotY.add(minLabelY, 		"9, 1, r, c");
		buttonPanelPlotY.add(minYSpinner, 		"11, 1, f, c");
		buttonPanelPlotY.add(maxLabelY, 		"13, 1, r, c");
		buttonPanelPlotY.add(maxYSpinner, 		"15, 1, f, c");
		
		buttonPanelPlotValues = new JPanel();
		buttonPanelPlotValues.setBorder(Borders.getBorder("Selected Values"));
		double[] columnButtonPlotValues = {5, TableLayoutConstants.PREFERRED,
											7, TableLayoutConstants.FILL, 5};
		double[] rowButtonPlotValues = {5, TableLayoutConstants.PREFERRED, 
											7, TableLayoutConstants.PREFERRED,
											7, TableLayoutConstants.PREFERRED,
											7, TableLayoutConstants.PREFERRED, 5};
		buttonPanelPlotValues.setLayout(new TableLayout(columnButtonPlotValues, rowButtonPlotValues));
		buttonPanelPlotValues.add(xValueLabel, 				"1, 1, r, c");
		buttonPanelPlotValues.add(xValueField, 				"3, 1, f, c");
		buttonPanelPlotValues.add(yValueLabel, 				"1, 3, r, c");
		buttonPanelPlotValues.add(yValueField, 				"3, 3, f, c");
		buttonPanelPlotValues.add(countsValueLabel, 		"1, 5, r, c");
		buttonPanelPlotValues.add(countsValueField, 		"3, 5, f, c");
		buttonPanelPlotValues.add(showPlotCrossHairsBox, 	"1, 7, 3, 7, c, c");
		
		applyLimitsBoxPlot = new JCheckBox("Apply Data Limits?");
		applyLimitsBoxPlot.setSelected(true);
		applyLimitsBoxPlot.addActionListener(this);
		
		JPanel buttonPanelPlotView = new JPanel();
		buttonPanelPlotView.setBorder(Borders.getBorder("View Options"));
		double[] columnButtonPlotView = {5, TableLayoutConstants.PREFERRED,
											7, TableLayoutConstants.FILL, 5};
		double[] rowButtonPlotView = {5, TableLayoutConstants.PREFERRED, 
											7, TableLayoutConstants.PREFERRED,
											7, TableLayoutConstants.PREFERRED, 5};
		buttonPanelPlotView.setLayout(new TableLayout(columnButtonPlotView, rowButtonPlotView));
		buttonPanelPlotView.add(plotSizeLabel, 		"1, 1, r, c");
		buttonPanelPlotView.add(plotSizeSpinner, 	"3, 1, f, c");
		buttonPanelPlotView.add(plotColorMapLabel, 	"1, 3, r, c");
		buttonPanelPlotView.add(plotColorMapBox, 	"3, 3, f, c");
		buttonPanelPlotView.add(applyLimitsBoxPlot, "1, 5, 3, 5, c, c");
		
		JPanel buttonPanelPlotAxis = new JPanel();
		double[] columnButtonPlotAxis = {TableLayoutConstants.FILL};
		double[] rowButtonPlotAxis = {TableLayoutConstants.PREFERRED, 10, TableLayoutConstants.PREFERRED};
		buttonPanelPlotAxis.setLayout(new TableLayout(columnButtonPlotAxis, rowButtonPlotAxis));
		buttonPanelPlotAxis.add(buttonPanelPlotX,	 	"0, 0, f, c");
		buttonPanelPlotAxis.add(buttonPanelPlotY, 		"0, 2, f, c");
		
		JPanel buttonPanelPlotOutput = new JPanel();
		double[] columnButtonPlotOutput = {TableLayoutConstants.FILL};
		double[] rowButtonPlotOutput = {TableLayoutConstants.PREFERRED, 10, TableLayoutConstants.PREFERRED};
		buttonPanelPlotOutput.setLayout(new TableLayout(columnButtonPlotOutput, rowButtonPlotOutput));
		buttonPanelPlotOutput.add(buttonPanelPlotValues, 	"0, 0, f, c");
		buttonPanelPlotOutput.add(buttonPanelPlotView, 		"0, 2, f, c");
		
		graphicsPanelPlot = new JPanel();
		graphicsPanelPlot.setOpaque(false);
		double[] colGraphicsPlot = {5, TableLayoutConstants.PREFERRED, 10, TableLayoutConstants.PREFERRED, 5};
		double[] rowGraphicsPlot = {5, TableLayoutConstants.PREFERRED, 5};
		graphicsPanelPlot.setLayout(new TableLayout(colGraphicsPlot, rowGraphicsPlot));
		graphicsPanelPlot.add(plotPanel, 	 	   "1, 1, l, t");	
		graphicsPanelPlot.add(colorScalePanelPlot, "3, 1, l, t");	
		
		spPlot = new JScrollPane(graphicsPanelPlot);
		spPlot.addComponentListener(this);
		
		JPanel plotLayoutPanel = new JPanel();
		double[] colPlotLayout = {5, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.PREFERRED, 5};
		double[] rowPlotLayout = {5, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.PREFERRED, 5};
		plotLayoutPanel.setLayout(new TableLayout(colPlotLayout, rowPlotLayout));
		plotLayoutPanel.add(spPlot, 				"1, 1, f, f");	
		plotLayoutPanel.add(buttonPanelPlotAxis,	"1, 3, 3, 3, f, c");	
		plotLayoutPanel.add(buttonPanelPlotOutput,	"3, 1, f, t");	
		
		pane = new JTabbedPane();
		pane.add("SHO Fit Parameter Map", chartLayoutPanel);
		pane.add("SHO Fit Histrogram Filter", plotLayoutPanel);
		
		double[] col = {TableLayoutConstants.FILL};
		double[] row = {TableLayoutConstants.FILL};
		setLayout(new TableLayout(col, row));
			
	}
	
	public void setCurrentState(){

		this.sfds = d.getDataFile().getSHOFitDataSet();
		
		setSelectedChartGridPoint(null);
		chartPanel.setSelectedGridPoint(null);
		
		remove(pane);
		datasetBox.removeActionListener(this);
		datasetBox.removeAllItems();
		if(!sfds.containsSHOFitResults()){
			AttentionDialog.createDialog(owner, "The selected dataset does not contain any SHO Fit results. You will only have access to the SHO Fit Guess values.");
			datasetBox.addItem(SHOFitDatasetType.SHO_FIT_GUESS);
			datasetBox.setSelectedItem(SHOFitDatasetType.SHO_FIT_GUESS);
		}else{
			datasetBox.addItem(SHOFitDatasetType.SHO_FIT_GUESS);
			datasetBox.addItem(SHOFitDatasetType.SHO_FIT_RESULTS);
			datasetBox.setSelectedItem(SHOFitDatasetType.SHO_FIT_RESULTS);
		}
		datasetBox.addActionListener(this);
		add(pane, "0, 0, f, f");
		
		groupBox.removeActionListener(this);
		groupBox.removeAllItems();
		for(String s: sfds.getPlotGroupNames()){
			groupBox.addItem(s);
		}
		groupBox.setSelectedIndex(0);
		groupBox.addActionListener(this);

		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
		groupBox.addPopupMenuListener(listener);
		groupBox.setPrototypeDisplayValue("1234567890");
		
		selectedGroup = groupBox.getSelectedItem().toString();
		selectedDatasetType = (SHOFitDatasetType) datasetBox.getSelectedItem();
		
		setDCOffsetUI(sfds.getDCList(selectedGroup).size()>1);
		setChartPlotViewUI(sfds.getDCList(selectedGroup).size()>1, (SHOFitPlotType) plotTypeBox.getSelectedItem());
		
		this.dcOffsetIndex = 0;
		this.shoFitDataTypeChart = (SHOFitDataType) dataTypeBoxChart.getSelectedItem();
		this.shoFitDataTypePlotX = (SHOFitDataType) dataTypeBoxPlotX.getSelectedItem();
		this.shoFitDataTypePlotY = (SHOFitDataType) dataTypeBoxPlotY.getSelectedItem();
		this.scalePlotX = (ChartScaleType) scaleBoxPlotX.getSelectedItem();
		this.scalePlotY = (ChartScaleType) scaleBoxPlotY.getSelectedItem(); 

		setGlobalLimitsMaps();
		
		binMap.clear();
		for(SHOFitDataType shoFitDataType: SHOFitDataType.values()){
			BinnedScale bs = new BinnedScale(globalMinMap.get(shoFitDataType), globalMaxMap.get(shoFitDataType), numBinsPlot);
			binMap.put(shoFitDataType, bs);
		}
		
		SHOFitDataCell[][] array = sfds.getCellArray(selectedGroup, selectedDatasetType);
		chartPanel.setGridWidth(array.length);
		chartPanel.setGridHeight(array[0].length);

		gridPointMaskArray = new boolean[array.length][array[0].length];
		
		plotPanel.setGridWidth(numBinsPlot);
		plotPanel.setGridHeight(numBinsPlot);
		
		ComplexValueType fitPlotType = (ComplexValueType) fitPlotTypeBox.getSelectedItem();
		fitPlotPanel.setCurrentData(null, null, null, fitPlotType);
		
		SHOFitLoopPlotType loopPlotType = (SHOFitLoopPlotType) loopPlotTypeBox.getSelectedItem();
		loopPlotPanel.setCurrentData(null, null, loopPlotType);
		
		setXLimitsState();
		setYLimitsState();
		setPlotState();
		setChartState();
		setDCOffsetState();
		setChartPanelValuesState();
		
		if(chartInitialized){
			setChartSquareSize();
		}
		
		if(plotInitialized){
			setPlotSquareSize();
		}
		
		validate();
		repaint();
	}
	
	public void exportCurrentImage(){
		
		String suffix = "_sho_fit_results";
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
			if(pane.getSelectedIndex()==0){
				exportChartImage(filename);
			}else if(pane.getSelectedIndex()==1){
				exportPlotImage(filename);
			}
		}catch(IOException ioe){
			CaughtExceptionHandler.handleException(ioe, owner);
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
				int value = CautionDialog.createCautionDialog(owner, msg, "Attention!");
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
				BufferedImage bi = new BufferedImage(graphicsPanelChart.getSize().width, graphicsPanelChart.getSize().height, BufferedImage.TYPE_INT_ARGB); 
				Graphics g = bi.createGraphics();
				graphicsPanelChart.paint(g);
				g.dispose();
				ImageIO.write(bi, "png", file);
				break;
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
				int value = CautionDialog.createCautionDialog(owner, msg, "Attention!");
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
				BufferedImage bi = new BufferedImage(graphicsPanelPlot.getSize().width, graphicsPanelPlot.getSize().height, BufferedImage.TYPE_INT_ARGB); 
				Graphics g = bi.createGraphics();
				graphicsPanelPlot.paint(g);
				g.dispose();
				ImageIO.write(bi, "png", file);
				break;
		}
	}
	
	private void setChartPlotViewUI(boolean showLoopPlotOptions, SHOFitPlotType fitPlotType){
		
		buttonPanelChartPlotView.removeAll();
		graphicsPanelChart.removeAll();

		if(showLoopPlotOptions){
		
			if(fitPlotType==SHOFitPlotType.FIT_PLOT){
				
				double[] columnButtonChartPlotView = {5, TableLayoutConstants.PREFERRED,
															7, TableLayoutConstants.FILL, 5};
				double[] rowButtonChartPlotView = {5, TableLayoutConstants.PREFERRED, 
															7, TableLayoutConstants.PREFERRED,
															7, TableLayoutConstants.PREFERRED, 5};
				buttonPanelChartPlotView.setLayout(new TableLayout(columnButtonChartPlotView, rowButtonChartPlotView));
				buttonPanelChartPlotView.add(plotTypeLabel, 	"1, 1, r, c");
				buttonPanelChartPlotView.add(plotTypeBox, 		"3, 1, f, c");
				buttonPanelChartPlotView.add(fitPlotTypeLabel, 	"1, 3, r, c");
				buttonPanelChartPlotView.add(fitPlotTypeBox, 	"3, 3, f, c");
				buttonPanelChartPlotView.add(mainDataButton, 	"1, 5, 3, 5, f, c");
				
				graphicsPanelChart.add(chartPanel, 		 		"1, 1, l, t");	
				graphicsPanelChart.add(colorScalePanelChart, 	"3, 1, l, t");	
				graphicsPanelChart.add(fitPlotPanel, 			"5, 1, l, t");
				
			}else if(fitPlotType==SHOFitPlotType.LOOP_PLOT){
				
				double[] columnButtonChartPlotView = {5, TableLayoutConstants.PREFERRED,
															7, TableLayoutConstants.FILL, 5};
				double[] rowButtonChartPlotView = {5, TableLayoutConstants.PREFERRED, 
															7, TableLayoutConstants.PREFERRED, 5};
				buttonPanelChartPlotView.setLayout(new TableLayout(columnButtonChartPlotView, rowButtonChartPlotView));
				buttonPanelChartPlotView.add(plotTypeLabel, 	"1, 1, r, c");
				buttonPanelChartPlotView.add(plotTypeBox, 		"3, 1, f, c");
				buttonPanelChartPlotView.add(loopPlotTypeLabel, "1, 3, r, c");
				buttonPanelChartPlotView.add(loopPlotTypeBox, 	"3, 3, f, c");
				
				graphicsPanelChart.add(chartPanel, 		 		"1, 1, l, t");	
				graphicsPanelChart.add(colorScalePanelChart, 	"3, 1, l, t");	
				graphicsPanelChart.add(loopPlotPanel, 			"5, 1, l, t");
				
			}
		
		}else{
			
			double[] columnButtonChartPlotView = {5, TableLayoutConstants.PREFERRED,
														7, TableLayoutConstants.FILL, 5};
			double[] rowButtonChartPlotView = {5, TableLayoutConstants.PREFERRED, 
														7, TableLayoutConstants.PREFERRED, 5};
			buttonPanelChartPlotView.setLayout(new TableLayout(columnButtonChartPlotView, rowButtonChartPlotView));
			buttonPanelChartPlotView.add(fitPlotTypeLabel, 	"1, 1, r, c");
			buttonPanelChartPlotView.add(fitPlotTypeBox, 	"3, 1, f, c");
			buttonPanelChartPlotView.add(mainDataButton, 	"1, 3, 3, 3, f, c");
			
			graphicsPanelChart.add(chartPanel, 		 		"1, 1, l, t");	
			graphicsPanelChart.add(colorScalePanelChart, 	"3, 1, l, t");	
			graphicsPanelChart.add(fitPlotPanel, 			"5, 1, l, t");
		}
		
		graphicsPanelChart.validate();
		graphicsPanelChart.repaint();
		
		buttonPanelChartPlotView.validate();
		buttonPanelChartPlotView.repaint();
		
		buttonPanelChartOutput.validate();
		buttonPanelChartOutput.repaint();
		
		validate();
		repaint();
		
	}
	
	private void setDCOffsetUI(boolean showDCOffsetComponents){
		
		buttonPanelChartOptions.removeAll();
		
		if(showDCOffsetComponents){
		
			double[] columnButtonChartOptions = {5, TableLayoutConstants.PREFERRED,
														7, TableLayoutConstants.FILL,
														10, TableLayoutConstants.PREFERRED,
														7, TableLayoutConstants.FILL, 
														10, TableLayoutConstants.PREFERRED,
														7, TableLayoutConstants.FILL, 
														10, TableLayoutConstants.PREFERRED,
														7, TableLayoutConstants.FILL,
														10, TableLayoutConstants.PREFERRED,
														7, TableLayoutConstants.FILL, 5};
			double[] rowButtonChartOptions = {5, TableLayoutConstants.PREFERRED
														, 10, TableLayoutConstants.PREFERRED, 5};
			buttonPanelChartOptions.setLayout(new TableLayout(columnButtonChartOptions, rowButtonChartOptions));
			buttonPanelChartOptions.add(datasetLabel, 		"1, 1, r, c");
			buttonPanelChartOptions.add(datasetBox, 		"3, 1, f, c");
			buttonPanelChartOptions.add(groupLabel, 		"5, 1, r, c");
			buttonPanelChartOptions.add(groupBox, 			"7, 1, f, c");
			buttonPanelChartOptions.add(dataTypeLabel, 		"9, 1, r, c");
			buttonPanelChartOptions.add(dataTypeBoxChart, 	"11, 1, f, c");
			buttonPanelChartOptions.add(dcStepLabel, 		"13, 1, r, c");
			buttonPanelChartOptions.add(dcStepField, 		"15, 1, f, c");
			buttonPanelChartOptions.add(dcOffsetLabel, 		"17, 1, r, c");
			buttonPanelChartOptions.add(dcOffsetField, 		"19, 1, f, c");
			buttonPanelChartOptions.add(dcOffsetSlider, 	"1, 3, 19, 3, f, c");
		
		}else{
			
			double[] columnButtonChartOptions = {5, TableLayoutConstants.PREFERRED,
														7, TableLayoutConstants.FILL, 
														10, TableLayoutConstants.PREFERRED,
														7, TableLayoutConstants.FILL, 
														10, TableLayoutConstants.PREFERRED,
														7, TableLayoutConstants.FILL, 5};
			double[] rowButtonChartOptions = {5, TableLayoutConstants.PREFERRED, 5};
			buttonPanelChartOptions.setLayout(new TableLayout(columnButtonChartOptions, rowButtonChartOptions));
			buttonPanelChartOptions.add(datasetLabel, 		"1, 1, r, c");
			buttonPanelChartOptions.add(datasetBox, 		"3, 1, f, c");
			buttonPanelChartOptions.add(groupLabel, 		"5, 1, r, c");
			buttonPanelChartOptions.add(groupBox, 			"7, 1, f, c");
			buttonPanelChartOptions.add(dataTypeLabel, 		"9, 1, r, c");
			buttonPanelChartOptions.add(dataTypeBoxChart, 	"11, 1, f, c");
		}
		
		buttonPanelChartOptions.validate();
		buttonPanelChartOptions.repaint();
		
	}
	
	private void setXLimitsState(){
		BinnedScale bs = binMap.get(shoFitDataTypePlotX);
		scalePlotX = bs.getChartScaleType();
		scaleBoxPlotX.removeActionListener(this);
		scaleBoxPlotX.setSelectedItem(scalePlotX);
		scaleBoxPlotX.addActionListener(this);
		ArrayList<Double> binList = bs.getBinList();
		ArrayList<String> binListFormatted = new ArrayList<String>();
		for(Double d: binList){
			String value = "";
			if(scalePlotX==ChartScaleType.LIN){
				value = shoFitDataTypePlotX.getDecimalFormatLin().format(d);
			}else if(scalePlotX==ChartScaleType.LOG){
				value = shoFitDataTypePlotX.getDecimalFormatLog().format(d);
			}
			binListFormatted.add(value);
		}
		minXSpinner.removeChangeListener(this);
		maxXSpinner.removeChangeListener(this);
		minXSpinnerModel.setList(binListFormatted);
		maxXSpinnerModel.setList(binListFormatted);
		minXSpinner.setValue(binListFormatted.get(bs.getBinMin()));
		maxXSpinner.setValue(binListFormatted.get(bs.getBinMax()));
		minXSpinner.addChangeListener(this);
		maxXSpinner.addChangeListener(this);
	}
	
	private void setYLimitsState(){
		BinnedScale bs = binMap.get(shoFitDataTypePlotY);
		scalePlotY = bs.getChartScaleType();
		scaleBoxPlotY.removeActionListener(this);
		scaleBoxPlotY.setSelectedItem(scalePlotY);
		scaleBoxPlotY.addActionListener(this);
		ArrayList<Double> binList = bs.getBinList();
		ArrayList<String> binListFormatted = new ArrayList<String>();
		for(Double d: binList){
			String value = "";
			if(scalePlotY==ChartScaleType.LIN){
				value = shoFitDataTypePlotY.getDecimalFormatLin().format(d);
			}else if(scalePlotY==ChartScaleType.LOG){
				value = shoFitDataTypePlotY.getDecimalFormatLog().format(d);
			}
			binListFormatted.add(value);
		}
		minYSpinner.removeChangeListener(this);
		maxYSpinner.removeChangeListener(this);
		minYSpinnerModel.setList(binListFormatted);
		maxYSpinnerModel.setList(binListFormatted);
		minYSpinner.setValue(binListFormatted.get(bs.getBinMin()));
		maxYSpinner.setValue(binListFormatted.get(bs.getBinMax()));
		minYSpinner.addChangeListener(this);
		maxYSpinner.addChangeListener(this);
	}
	
	private void setDCOffsetState(){
		dcOffsetSlider.removeChangeListener(this);
		dcOffsetSlider.setMinimum(0);
		dcOffsetSlider.setMaximum(sfds.getDCList(selectedGroup).size()-1);
		dcOffsetSlider.setValue(0);
		dcOffsetSlider.addChangeListener(this);
		dcOffsetField.setText(String.valueOf(sfds.getDCList(selectedGroup).get(0)));
		dcStepField.setText(String.valueOf(0));
	}
	
	private void setDCOffsetIndex(int dcOffsetIndex){
		this.dcOffsetIndex = dcOffsetIndex;
		setChartState();
	}
	
	private void setSHOFitDataTypeChart(SHOFitDataType shoFitDataTypeChart){
		this.shoFitDataTypeChart = shoFitDataTypeChart;
		setChartFieldValues();
		setChartState();
	}
	
	private void setSHOFitDataTypePlotX(SHOFitDataType shoFitDataTypePlotX){
		this.shoFitDataTypePlotX = shoFitDataTypePlotX;
		setXLimitsState();
		setPlotState();
	}
	
	private void setSHOFitDataTypePlotY(SHOFitDataType shoFitDataTypePlotY){
		this.shoFitDataTypePlotY = shoFitDataTypePlotY;
		setYLimitsState();
		setPlotState();
	}
	
	private void setScalePlotX(ChartScaleType scalePlotX){
		this.scalePlotX = scalePlotX;
		binMap.get(shoFitDataTypePlotX).setChartScaleType(scalePlotX);
		setXLimitsState();
		setPlotState();
		setChartState();
	}
	
	private void setScalePlotY(ChartScaleType scalePlotY){
		this.scalePlotY = scalePlotY;
		binMap.get(shoFitDataTypePlotY).setChartScaleType(scalePlotY);
		setYLimitsState();
		setPlotState();
		setChartState();
	}
	
	private void setMinPlotX(String minPlotX){
		int minPlotXIndex = minXSpinnerModel.getList().indexOf(minPlotX);
		binMap.get(shoFitDataTypePlotX).setBinMin(minPlotXIndex);
		setPlotState();
		setChartState();
	}
	
	private void setMaxPlotX(String maxPlotX){
		int maxPlotXIndex = maxXSpinnerModel.getList().indexOf(maxPlotX);
		binMap.get(shoFitDataTypePlotX).setBinMax(maxPlotXIndex);
		setPlotState();
		setChartState();
	}
	
	private void setMinPlotY(String minPlotY){
		int minPlotYIndex = minYSpinnerModel.getList().indexOf(minPlotY);
		binMap.get(shoFitDataTypePlotY).setBinMin(minPlotYIndex);
		setPlotState();
		setChartState();
	}
	
	private void setMaxPlotY(String maxPlotY){
		int maxPlotYIndex = maxYSpinnerModel.getList().indexOf(maxPlotY);
		binMap.get(shoFitDataTypePlotY).setBinMax(maxPlotYIndex);
		setPlotState();
		setChartState();
	}
	
	public void setSelectedChartGridPoint(GridPoint selectedChartGridPoint){
		this.selectedChartGridPoint = selectedChartGridPoint;
		ComplexValueType fitPlotType = (ComplexValueType) fitPlotTypeBox.getSelectedItem();
		SHOFitLoopPlotType loopPlotType = (SHOFitLoopPlotType) loopPlotTypeBox.getSelectedItem();
		if(selectedChartGridPoint!=null){
			mainDataButton.setEnabled(true);
			SHOFitDataCell sfdc = sfds.getCellArray(selectedGroup, selectedDatasetType)[selectedChartGridPoint.getX()-1][selectedChartGridPoint.getY()-1];
			sfds.setSelectedGridPoint(sfdc.getGridPoint());
			setFitPlotState();
			setLoopPlotState();
		}else{
			mainDataButton.setEnabled(false);
			fitPlotPanel.setCurrentData(null, null, null, fitPlotType);
			loopPlotPanel.setCurrentData(null, null, loopPlotType);
			sfds.setSelectedGridPoint(null);
		}
		setChartFieldValues();
	}
	
	public void setMouseOverChartGridPoint(GridPoint mouseOverChartGridPoint){
		this.mouseOverChartGridPoint = mouseOverChartGridPoint;
		setChartFieldValues();
	}
	
	public void setMouseOverPlotGridPoint(GridPoint mouseOverPlotGridPoint){
		this.mouseOverPlotGridPoint = mouseOverPlotGridPoint;
		setPlotFieldValues();
	}
	
	public void stateChanged(ChangeEvent ce){
		if(ce.getSource()==dcOffsetSlider){
			int sliderValue = dcOffsetSlider.getValue();
			dcOffsetField.setText(dcOffsetFormat.format(sfds.getDCList(selectedGroup).get(sliderValue)));
			dcStepField.setText(String.valueOf((sliderValue)));
			setDCOffsetIndex(sliderValue);
			setFitPlotState();
			setChartFieldValues();
		}else if(ce.getSource()==minXSpinner){
			setMinPlotX(minXSpinner.getValue().toString());
		}else if(ce.getSource()==maxXSpinner){
			setMaxPlotX(maxXSpinner.getValue().toString());
		}else if(ce.getSource()==minYSpinner){
			setMinPlotY(minYSpinner.getValue().toString());
		}else if(ce.getSource()==maxYSpinner){
			setMaxPlotY(maxYSpinner.getValue().toString());
		}else if(ce.getSource()==chartSizeSpinner){
			int value = (int) chartSizeSpinner.getValue();
			chartPanel.setSquareSize(value);
			fitPlotPanel.setPreferredSize((int)chartPanel.getPreferredSize().getHeight());
			loopPlotPanel.setPreferredSize((int)chartPanel.getPreferredSize().getHeight());
			setChartState();
			graphicsPanelChart.revalidate();
		}else if(ce.getSource()==plotSizeSpinner){
			int value = (int) plotSizeSpinner.getValue();
			plotPanel.setSquareSize(value);
			setPlotState();
			graphicsPanelPlot.revalidate();
		}
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==dataTypeBoxChart){
			setSHOFitDataTypeChart((SHOFitDataType) dataTypeBoxChart.getSelectedItem());
			setChartPanelValuesState();
		}else if(ae.getSource()==dataTypeBoxPlotX){
			setSHOFitDataTypePlotX((SHOFitDataType) dataTypeBoxPlotX.getSelectedItem());
		}else if(ae.getSource()==dataTypeBoxPlotY){
			setSHOFitDataTypePlotY((SHOFitDataType) dataTypeBoxPlotY.getSelectedItem());
		}else if(ae.getSource()==scaleBoxPlotX){
			setScalePlotX((ChartScaleType) scaleBoxPlotX.getSelectedItem());
		}else if(ae.getSource()==scaleBoxPlotY){
			setScalePlotY((ChartScaleType) scaleBoxPlotY.getSelectedItem());	
		}else if(ae.getSource()==showPlotCrossHairsBox){
			plotPanel.showCrossHairs(showPlotCrossHairsBox.isSelected());
		}else if(ae.getSource()==showChartCrossHairsBox){
			chartPanel.showCrossHairs(showChartCrossHairsBox.isSelected());
		}else if(ae.getSource()==chartColorMapBox){
			chartColorMap.setColorMapType((ColorMapType) chartColorMapBox.getSelectedItem());
			setChartState();
		}else if(ae.getSource()==plotColorMapBox){
			plotColorMap.setColorMapType((ColorMapType) plotColorMapBox.getSelectedItem());
			setPlotState();
		}else if(ae.getSource()==applyLimitsBoxChart || ae.getSource()==applyLimitsBoxPlot){
			if(ae.getSource()==applyLimitsBoxChart){
				applyLimitsBoxPlot.removeActionListener(this);
				applyLimitsBoxPlot.setSelected(applyLimitsBoxChart.isSelected());
				applyLimitsBoxPlot.addActionListener(this);
			}else if(ae.getSource()==applyLimitsBoxPlot){
				applyLimitsBoxChart.removeActionListener(this);
				applyLimitsBoxChart.setSelected(applyLimitsBoxPlot.isSelected());
				applyLimitsBoxChart.addActionListener(this);
			}
			setGlobalLimitsMaps();
			binMap.clear();
			for(SHOFitDataType shoFitDataType: SHOFitDataType.values()){
				BinnedScale bs = new BinnedScale(globalMinMap.get(shoFitDataType), globalMaxMap.get(shoFitDataType), numBinsPlot);
				binMap.put(shoFitDataType, bs);
			}
			setXLimitsState();
			setYLimitsState();
			setPlotState();
			setChartState();
			setDCOffsetState();
			setChartPanelValuesState();
			setChartFieldValues();
		}else if(ae.getSource()==applyMaskBoxChart){
			setChartState();
		}else if(ae.getSource()==mainDataButton){
			GetRawDataWorker worker = new GetRawDataWorker(this, d.getDataFile().getSHOFitDataSet(), owner);
			worker.execute();
		}else if(ae.getSource()==groupBox){
			selectedGroup = groupBox.getSelectedItem().toString();
			setGlobalLimitsMaps();
			binMap.clear();
			for(SHOFitDataType shoFitDataType: SHOFitDataType.values()){
				BinnedScale bs = new BinnedScale(globalMinMap.get(shoFitDataType), globalMaxMap.get(shoFitDataType), numBinsPlot);
				binMap.put(shoFitDataType, bs);
			}
			setXLimitsState();
			setYLimitsState();
			setPlotState();
			setChartState();
			setDCOffsetState();
			setChartPanelValuesState();
			setFitPlotState();
		}else if(ae.getSource()==plotTypeBox){
			setChartPlotViewUI(sfds.getDCList(selectedGroup).size()>1, (SHOFitPlotType) plotTypeBox.getSelectedItem());
			setFitPlotState();
			setLoopPlotState();
		}else if(ae.getSource()==fitPlotTypeBox){
			setFitPlotState();
		}else if(ae.getSource()==loopPlotTypeBox){
			setLoopPlotState();
		}else if(ae.getSource()==datasetBox){
			selectedDatasetType = (SHOFitDatasetType) datasetBox.getSelectedItem();
			setGlobalLimitsMaps();
			binMap.clear();
			for(SHOFitDataType shoFitDataType: SHOFitDataType.values()){
				BinnedScale bs = new BinnedScale(globalMinMap.get(shoFitDataType), globalMaxMap.get(shoFitDataType), numBinsPlot);
				binMap.put(shoFitDataType, bs);
			}
			setXLimitsState();
			setYLimitsState();
			setPlotState();
			setChartState();
			setDCOffsetState();
			setChartPanelValuesState();
			setFitPlotState();
			setLoopPlotState();
		}
	}
	
	private void setChartPanelValuesState(){

		aLabel.setForeground(Color.black);
		wLabel.setForeground(Color.black);
		qLabel.setForeground(Color.black);
		pLabel.setForeground(Color.black);
		
		buttonPanelChartValues.remove(aLabel);
		buttonPanelChartValues.remove(aField);
		buttonPanelChartValues.remove(wLabel);
		buttonPanelChartValues.remove(wField);
		buttonPanelChartValues.remove(qLabel);
		buttonPanelChartValues.remove(qField);
		buttonPanelChartValues.remove(pLabel);
		buttonPanelChartValues.remove(pField);
		
		switch(shoFitDataTypeChart){
			case A:
				aLabel.setForeground(Colors.RED);
				buttonPanelChartValues.add(aLabel, 				"1, 5, r, c");
				buttonPanelChartValues.add(aField, 				"3, 5, f, c");
				buttonPanelChartValues.add(wLabel, 				"1, 7, r, c");
				buttonPanelChartValues.add(wField, 				"3, 7, f, c");
				buttonPanelChartValues.add(qLabel, 				"1, 9, r, c");
				buttonPanelChartValues.add(qField, 				"3, 9, f, c");
				buttonPanelChartValues.add(pLabel, 				"1, 11, r, c");
				buttonPanelChartValues.add(pField, 				"3, 11, f, c");
				break;
			case W:
				wLabel.setForeground(Colors.RED);
				buttonPanelChartValues.add(wLabel, 				"1, 5, r, c");
				buttonPanelChartValues.add(wField, 				"3, 5, f, c");
				buttonPanelChartValues.add(aLabel, 				"1, 7, r, c");
				buttonPanelChartValues.add(aField, 				"3, 7, f, c");
				buttonPanelChartValues.add(qLabel, 				"1, 9, r, c");
				buttonPanelChartValues.add(qField, 				"3, 9, f, c");
				buttonPanelChartValues.add(pLabel, 				"1, 11, r, c");
				buttonPanelChartValues.add(pField, 				"3, 11, f, c");
				break;
			case Q:
				qLabel.setForeground(Colors.RED);
				buttonPanelChartValues.add(qLabel, 				"1, 5, r, c");
				buttonPanelChartValues.add(qField, 				"3, 5, f, c");
				buttonPanelChartValues.add(aLabel, 				"1, 7, r, c");
				buttonPanelChartValues.add(aField, 				"3, 7, f, c");
				buttonPanelChartValues.add(wLabel, 				"1, 9, r, c");
				buttonPanelChartValues.add(wField, 				"3, 9, f, c");
				buttonPanelChartValues.add(pLabel, 				"1, 11, r, c");
				buttonPanelChartValues.add(pField, 				"3, 11, f, c");
				break;
			case P:
				pLabel.setForeground(Colors.RED);
				buttonPanelChartValues.add(pLabel, 				"1, 5, r, c");
				buttonPanelChartValues.add(pField, 				"3, 5, f, c");
				buttonPanelChartValues.add(aLabel, 				"1, 7, r, c");
				buttonPanelChartValues.add(aField, 				"3, 7, f, c");
				buttonPanelChartValues.add(wLabel, 				"1, 9, r, c");
				buttonPanelChartValues.add(wField, 				"3, 9, f, c");
				buttonPanelChartValues.add(qLabel, 				"1, 11, r, c");
				buttonPanelChartValues.add(qField, 				"3, 11, f, c");
				break;
		}
		validate();
	}
	
	private void setPlotFieldValues(){
		if(mouseOverPlotGridPoint!=null){
			ArrayList<Double> binListX = binMap.get(shoFitDataTypePlotX).getBinList();
			ArrayList<Double> binListY = binMap.get(shoFitDataTypePlotY).getBinList();
			double xValue0 = binListX.get(mouseOverPlotGridPoint.getX()-1);
			double yValue0 = binListY.get(mouseOverPlotGridPoint.getY()-1);
			if(mouseOverPlotGridPoint.getX()<binListX.size() && mouseOverPlotGridPoint.getY()<binListY.size()){
				double xValue1 = binListX.get(mouseOverPlotGridPoint.getX());
				double yValue1 = binListY.get(mouseOverPlotGridPoint.getY());
				double countsValue = countArray[mouseOverPlotGridPoint.getX()-1][mouseOverPlotGridPoint.getY()-1];
				if(scalePlotX==ChartScaleType.LIN){
					xValueField.setText(shoFitDataTypePlotX.getDecimalFormatLin().format(xValue0) 
											+ ", " 
											+ shoFitDataTypePlotX.getDecimalFormatLin().format(xValue1));
				}else if(scalePlotX==ChartScaleType.LOG){
					xValueField.setText(shoFitDataTypePlotX.getDecimalFormatLog().format(xValue0) 
											+ ", " 
											+ shoFitDataTypePlotX.getDecimalFormatLog().format(xValue1));
				}
				
				if(scalePlotY==ChartScaleType.LIN){
					yValueField.setText(shoFitDataTypePlotY.getDecimalFormatLin().format(yValue0)
											+ ", " 
											+ shoFitDataTypePlotY.getDecimalFormatLin().format(yValue1));
				}else if(scalePlotY==ChartScaleType.LOG){
					yValueField.setText(shoFitDataTypePlotY.getDecimalFormatLog().format(yValue0)
											+ ", " 
											+ shoFitDataTypePlotY.getDecimalFormatLog().format(yValue1));
				}
				countsValueField.setText(countsFormat.format(countsValue));
			}else{
				xValueField.setText("");
				yValueField.setText("");
				countsValueField.setText("");
			}
		}else{
			xValueField.setText("");
			yValueField.setText("");
			countsValueField.setText("");
		}
	}
	
	private void setChartFieldValues(){
		if(selectedChartGridPoint!=null){
			SHOFitData sfd = sfds.getCellArray(selectedGroup, selectedDatasetType)[selectedChartGridPoint.getX()-1][selectedChartGridPoint.getY()-1].getSHOFitDataList().get(dcOffsetSlider.getValue());
			aField.setText(SHOFitDataType.A.getDecimalFormatLin().format(sfd.getA()));
			if(sfd.getA()<0 && applyLimitsBoxChart.isSelected()){
				aField.setText(SHOFitDataType.A.getDecimalFormatLin().format(Math.abs(sfd.getA())));
			}
			wField.setText(SHOFitDataType.W.getDecimalFormatLin().format(sfd.getW()/1000.0));
			qField.setText(SHOFitDataType.Q.getDecimalFormatLin().format(sfd.getQ()));
			if(sfd.getQ()<0 && applyLimitsBoxChart.isSelected()){
				qField.setText(SHOFitDataType.Q.getDecimalFormatLin().format(Math.abs(sfd.getQ())));
			}
			pField.setText(SHOFitDataType.P.getDecimalFormatLin().format(sfd.getP()));
			gridPointXField.setText(String.valueOf(selectedChartGridPoint.getX()));
			gridPointYField.setText(String.valueOf(selectedChartGridPoint.getY()));
		}else if(mouseOverChartGridPoint!=null){
			SHOFitData sfd = sfds.getCellArray(selectedGroup, selectedDatasetType)[mouseOverChartGridPoint.getX()-1][mouseOverChartGridPoint.getY()-1].getSHOFitDataList().get(dcOffsetSlider.getValue());
			aField.setText(SHOFitDataType.A.getDecimalFormatLin().format(sfd.getA()));
			if(sfd.getA()<0 && applyLimitsBoxChart.isSelected()){
				aField.setText(SHOFitDataType.A.getDecimalFormatLin().format(Math.abs(sfd.getA())));
			}
			wField.setText(SHOFitDataType.W.getDecimalFormatLin().format(sfd.getW()/1000.0));
			qField.setText(SHOFitDataType.Q.getDecimalFormatLin().format(sfd.getQ()));
			if(sfd.getQ()<0 && applyLimitsBoxChart.isSelected()){
				qField.setText(SHOFitDataType.Q.getDecimalFormatLin().format(Math.abs(sfd.getQ())));
			}
			pField.setText(SHOFitDataType.P.getDecimalFormatLin().format(sfd.getP()));
			gridPointXField.setText(String.valueOf(mouseOverChartGridPoint.getX()));
			gridPointYField.setText(String.valueOf(mouseOverChartGridPoint.getY()));
		}else{
			aField.setText("");
			wField.setText("");
			qField.setText("");
			pField.setText("");
			gridPointXField.setText("");
			gridPointYField.setText("");
		}
	}
	
	private void setFitPlotState(){
		ComplexValueType type = (ComplexValueType) fitPlotTypeBox.getSelectedItem();
		if(selectedChartGridPoint!=null){
			SHOFitDataCell sfdc = sfds.getCellArray(selectedGroup, selectedDatasetType)[selectedChartGridPoint.getX()-1][selectedChartGridPoint.getY()-1];
			sfdc.populateFitDataList();
			fitPlotPanel.setCurrentData(sfdc.getFitDataArray(dcOffsetIndex, type)
											, sfdc.getMainDataArray(dcOffsetIndex, type)
											, sfdc.getWListInKHz()
											, type);
		}
	}
	
	private void setLoopPlotState(){
		SHOFitLoopPlotType type = (SHOFitLoopPlotType) loopPlotTypeBox.getSelectedItem();
		if(selectedChartGridPoint!=null){
			SHOFitDataCell sfdc = sfds.getCellArray(selectedGroup, selectedDatasetType)[selectedChartGridPoint.getX()-1][selectedChartGridPoint.getY()-1];
			loopPlotPanel.setCurrentData(sfdc.getSHOFitDataList(), sfds.getDCList(selectedGroup), type);
		}
	}
	
	private void setPlotState(){
		
		BinnedScale bsX = binMap.get(shoFitDataTypePlotX);
		BinnedScale bsY = binMap.get(shoFitDataTypePlotY);
		
		ArrayList<Double> binListX = bsX.getBinList();
		ArrayList<Double> binListY = bsY.getBinList();
		
		countArray = new int[binListX.size()][binListY.size()];
		for(int i=0; i<binListX.size(); i++){
			for(int j=0; j<binListY.size(); j++){
				countArray[i][j] = 0;
			}
		}

		int dcOffsetListLength = sfds.getDCList(selectedGroup).size();
		SHOFitDataCell[][] cellArray = sfds.getCellArray(selectedGroup, selectedDatasetType);
		
		for(int i=0; i<cellArray.length; i++){
			for(int j=0; j<cellArray[0].length; j++){
				
				SHOFitDataCell cell = cellArray[i][j];
				
				for(int k=0; k<dcOffsetListLength; k++){
					
					double valueX = cell.getSHOFitDataList().get(k).getValue(shoFitDataTypePlotX);
					double valueY = cell.getSHOFitDataList().get(k).getValue(shoFitDataTypePlotY);
					
					if((shoFitDataTypePlotX==SHOFitDataType.A || shoFitDataTypePlotX==SHOFitDataType.Q)
							&& applyLimitsBoxPlot.isSelected() && valueX<0){
						valueX = Math.abs(valueX);
					}
					
					if((shoFitDataTypePlotY==SHOFitDataType.A || shoFitDataTypePlotY==SHOFitDataType.Q)
							&& applyLimitsBoxPlot.isSelected() && valueY<0){
						valueY = Math.abs(valueY);
					}
					
					int binIndexX = bsX.getBinIndexFromValue(valueX);
					int binIndexY = bsY.getBinIndexFromValue(valueY);
					
					if(binIndexX!=-1 && binIndexY!=-1){
						countArray[binIndexX][binIndexY]++;
					}
				}
				
			}
		}

		int min = Integer.MAX_VALUE;
		int max = 0;
		
		for(int i=0; i<countArray.length; i++){
			for(int j=0; j<countArray[0].length; j++){
				int count = countArray[i][j];
				if(count>0){
					min = Math.min(count, min);
					max = Math.max(count, max);
				}
			}
		}
		
		numBinsCounts = numBinsCountsDefault;
		
		ArrayList<Color> colorBinList = new ArrayList<Color>();
		BinnedScale countsBinnedScale = new BinnedScale(min, max, numBinsCounts);
		ArrayList<Double> countsBinList = countsBinnedScale.getBinList();
		for(Double value: countsBinList){
			Color color = Color.WHITE;
			if(value>=1){
				color = plotColorMap.getRGB((value-min)/(double)(max-min));
			}
			colorBinList.add(color);
		}

		Color[][] colorArray = new Color[countArray.length][countArray[0].length];
		for(int i=0; i<countArray.length; i++){
			for(int j=0; j<countArray[0].length; j++){
				int counts = countArray[i][j];
				Color color = getColorFromBin(counts, countsBinList, colorBinList, countsBinnedScale.getChartScaleType());
				colorArray[i][j] = color;
			}
		}
		plotPanel.setColorArray(colorArray);
		
		if(scalePlotX==ChartScaleType.LIN){
			plotPanel.setXTitle(shoFitDataTypePlotX.toString());
		}else{
			plotPanel.setXTitle("Log10(" + shoFitDataTypePlotX.toString() + ")");
		}
		if(scalePlotY==ChartScaleType.LIN){
			plotPanel.setYTitle(shoFitDataTypePlotY.toString());
		}else{
			plotPanel.setYTitle("Log10(" + shoFitDataTypePlotY.toString() + ")");
		}
		
		ArrayList<String> xTickLabelList = new ArrayList<String>();
		ArrayList<String> yTickLabelList = new ArrayList<String>();
		
		double minX = globalMinMap.get(shoFitDataTypePlotX);
		double maxX = globalMaxMap.get(shoFitDataTypePlotX);
		
		double minY = globalMinMap.get(shoFitDataTypePlotY);
		double maxY = globalMaxMap.get(shoFitDataTypePlotY);
		
		ArrayList<Double> tickValueListX = BinnedScale.generateBinList(minX, maxX, 5, scalePlotX);
		ArrayList<Double> tickValueListY = BinnedScale.generateBinList(minY, maxY, 5, scalePlotY);
		
		for(Double xTickValue: tickValueListX){
			if(scalePlotX==ChartScaleType.LIN){
				xTickLabelList.add(shoFitDataTypePlotX.getDecimalFormatLin().format(xTickValue));
			}else{
				xTickLabelList.add(shoFitDataTypePlotX.getDecimalFormatLog().format(xTickValue));
			}
		}

		for(Double yTickValue: tickValueListY){
			if(scalePlotY==ChartScaleType.LIN){
				yTickLabelList.add(shoFitDataTypePlotY.getDecimalFormatLin().format(yTickValue));
			}else{
				yTickLabelList.add(shoFitDataTypePlotY.getDecimalFormatLog().format(yTickValue));
			}
		}
		
		plotPanel.setXTickLabelList(xTickLabelList);
		plotPanel.setYTickLabelList(yTickLabelList);
		
		colorScalePanelPlot.setCurrentState(countsBinList, 
												colorBinList, 
												countsFormat, 
												20, 
												(int) plotPanel.getPreferredSize().getHeight(), 
												plotPanel.getMarginTop(), 
												plotPanel.getMarginBottom());
		
	}
	
	private void setChartState(){
		
		if(applyMaskBoxChart.isSelected()){
			setGridPointMaskArray();
		}
		
		BinnedScale bs = binMap.get(shoFitDataTypeChart);
		ChartScaleType binListScaleType = ChartScaleType.LIN;
		ArrayList<Double> binList = bs.getLimitedBinList(binListScaleType);
		if(!applyLimitsBoxChart.isSelected()){
			binList = bs.getBinList(binListScaleType);
		}
		
		ArrayList<Color> colorBinList = new ArrayList<Color>();
		double min = binList.get(0);
		double max = binList.get(binList.size()-1);
		for(int i=1; i<binList.size(); i++){
			Color color = chartColorMap.getRGB((binList.get(i)-min)/(max-min));
			colorBinList.add(color);
		}
		
		SHOFitDataCell[][] cellArray = sfds.getCellArray(selectedGroup, selectedDatasetType);
		Color[][] colorArray = new Color[cellArray.length][cellArray[0].length];
		for(int i=0; i<cellArray.length; i++){
			for(int j=0; j<cellArray[0].length; j++){
				if(applyMaskBoxChart.isSelected() && gridPointMaskArray[i][j]){
					colorArray[i][j] = Color.WHITE;
					continue;
				}
				SHOFitDataCell cell = cellArray[i][j];
				double value = cell.getSHOFitDataList().get(dcOffsetIndex).getValue(shoFitDataTypeChart);
				if((shoFitDataTypeChart==SHOFitDataType.A || shoFitDataTypeChart==SHOFitDataType.Q) && value<0 && applyLimitsBoxChart.isSelected()){
					value = Math.abs(value);
				}
				Color color = getColorFromBin(value, binList, colorBinList, binListScaleType);
				colorArray[i][j] = color;
			}
		}
		
		chartPanel.setColorArray(colorArray);
		
		colorScalePanelChart.setCurrentState(binList, 
												colorBinList, 
												shoFitDataTypeChart.getDecimalFormatLin(), 
												20, 
												(int) chartPanel.getPreferredSize().getHeight(), 
												chartPanel.getMarginTop(), 
												chartPanel.getMarginBottom());
	}
	
	private void setGridPointMaskArray(){
		for(int i=0; i<gridPointMaskArray.length; i++){
			for(int j=0; j<gridPointMaskArray.length; j++){
				gridPointMaskArray[i][j] = false;
			}
		}
		SHOFitDataCell[][] cellArray = sfds.getCellArray(selectedGroup, selectedDatasetType);
		for(int i=0; i<cellArray.length; i++){
			for(int j=0; j<cellArray[0].length; j++){
				for(SHOFitDataType type: SHOFitDataType.values()){
					if(gridPointMaskArray[i][j]){
						break;
					}
					SHOFitDataCell cell = cellArray[i][j];
					double value = cell.getSHOFitDataList().get(dcOffsetIndex).getValue(type);
					BinnedScale bs = binMap.get(type);
					
					if((type==SHOFitDataType.A || type==SHOFitDataType.Q) && value<0 && applyLimitsBoxChart.isSelected()){
						value = Math.abs(value);
					}
					
					if(bs.getBinIndexFromValue(value)==-1){
						gridPointMaskArray[i][j] = true;
						break;
					}
				}
			}
		}
	}
	
	private Color getColorFromBin(double value
									, ArrayList<Double> binValueList
									, ArrayList<Color> binColorList
									, ChartScaleType chartScaleType){
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
				if(value>lowValue && value<highValue){
					return binColorList.get(i);
				}
			}
		}else if(chartScaleType==ChartScaleType.LOG){
			for(int i=0; i<binValueList.size()-1; i++){
				double lowValue = binValueList.get(i);
				double highValue = binValueList.get(i+1);
				if(Math.log10(value)>(0.999999*lowValue) && Math.log10(value)<(1.000001*highValue)){
					return binColorList.get(i);
				}
			}
		}
		return Color.WHITE;
	}
	
	private void setGlobalLimitsMaps(){
		
		SHOFitDataCell[][] cellArray = sfds.getCellArray(selectedGroup, selectedDatasetType);
		int dcOffsetListLength = sfds.getDCList(selectedGroup).size();
		
		for(SHOFitDataType type: SHOFitDataType.values()){
			
			double min = Double.MAX_VALUE;
			double max = 0;
			
			if(applyLimitsBoxChart.isSelected()){
			
				if(type==SHOFitDataType.P){
					min = -3.1415926536;
					max = 3.1415926536;
					globalMinMap.put(type, min);
					globalMaxMap.put(type, max);
					continue;
				}
				
				if(type==SHOFitDataType.W){
					min = sfds.getWList(selectedGroup).get(0);
					max = sfds.getWList(selectedGroup).get(sfds.getWList(selectedGroup).size()-1);
					globalMinMap.put(type, min);
					globalMaxMap.put(type, max);
					continue;
				}
				
				double mean = getGlobalMean(type);
				double stdDev = getGlobalStandardDeviation(mean, type);
				double stdDevLowLimit = mean-(numStdDevs*stdDev);
				double stdDevHighLimit = mean+(numStdDevs*stdDev);
				
				for(int i=0; i<cellArray.length; i++){
					for(int j=0; j<cellArray[0].length; j++){
						SHOFitDataCell cell = cellArray[i][j];
						for(int k=0; k<dcOffsetListLength; k++){
							double value = cell.getSHOFitDataList().get(k).getValue(type);
							if(type==SHOFitDataType.A){
								if(value<0){
									value = Math.abs(value);
								}
								if(value>0.01){
									continue;
								}
							}
							if(type==SHOFitDataType.Q){
								if(value<0){
									value = Math.abs(value);
								}
								if(value>1000){
									continue;
								}
							}
							if(value < stdDevLowLimit || value > stdDevHighLimit){
								continue;
							}
							min = Math.min(min, value);
							max = Math.max(max, value);
						}
					}
				}
				
			}else{
				
				for(int i=0; i<cellArray.length; i++){
					for(int j=0; j<cellArray[0].length; j++){
						SHOFitDataCell cell = cellArray[i][j];
						for(int k=0; k<dcOffsetListLength; k++){
							double value = cell.getSHOFitDataList().get(k).getValue(type);
							min = Math.min(min, value);
							max = Math.max(max, value);
						}
					}
				}
				
			}
			
			globalMinMap.put(type, min);
			globalMaxMap.put(type, max);
			
		}
	}
	
	private double getGlobalMean(SHOFitDataType type){
		
		SHOFitDataCell[][] cellArray = sfds.getCellArray(selectedGroup, selectedDatasetType);
		int dcListLength = sfds.getDCList(selectedGroup).size();
		int totalNumberOfValues = 0;
		double totalValue = 0;
		
		for(int i=0; i<cellArray.length; i++){
			for(int j=0; j<cellArray[0].length; j++){
				SHOFitDataCell cell = cellArray[i][j];
				for(int k=0; k<dcListLength; k++){
					double value = cell.getSHOFitDataList().get(k).getValue(type);
					if(type==SHOFitDataType.A){
						if(value<0){
							value = Math.abs(value);
						}
						if(value>0.01){
							continue;
						}
					}
					if(type==SHOFitDataType.Q){
						if(value<0){
							value = Math.abs(value);
						}
						if(value>1000){
							continue;
						}
					}
					totalValue += value;
					totalNumberOfValues++;
				}
			}
		}
		return totalValue/(double)totalNumberOfValues;
	}
	
	private double getGlobalStandardDeviation(double mean, SHOFitDataType type){
		
		SHOFitDataCell[][] cellArray = sfds.getCellArray(selectedGroup, selectedDatasetType);
		int dcOffsetListLength = sfds.getDCList(selectedGroup).size();
		int totalNumberOfValues = 0;
		double sumOfSquares = 0;
		
		for(int i=0; i<cellArray.length; i++){
			for(int j=0; j<cellArray[0].length; j++){
				SHOFitDataCell cell = cellArray[i][j];
				for(int k=0; k<dcOffsetListLength; k++){
					double value = cell.getSHOFitDataList().get(k).getValue(type);
					if(type==SHOFitDataType.A){
						if(value<0){
							value = Math.abs(value);
						}
						if(value>0.01){
							continue;
						}
					}
					if(type==SHOFitDataType.Q){
						if(value<0){
							value = Math.abs(value);
						}
						if(value>1000){
							continue;
						}
					}
					sumOfSquares += Math.pow(value-mean, 2);
					totalNumberOfValues++;
				}
			}
		}
		return Math.sqrt(sumOfSquares/(double)totalNumberOfValues);
	}

	public void updateAfterGetRawData(){
		setFitPlotState();
	}

	private void setChartSquareSize(){
		int height = spChart.getSize().height - chartPanel.getMarginBottom() - chartPanel.getMarginTop();
		int gridHeight = chartPanel.getGridHeight();
		chartSquareSize = (int) ((double)height/(double)gridHeight) - 1;
		if(chartSquareSize<1){
			chartSquareSize = 1;
		}else if(chartSquareSize>100){
			chartSquareSize = 100;
		}
		chartPanel.setSquareSize(chartSquareSize);
		fitPlotPanel.setPreferredSize((int)chartPanel.getPreferredSize().getHeight());
		loopPlotPanel.setPreferredSize((int)chartPanel.getPreferredSize().getHeight());
		setChartState();
		chartSizeSpinner.removeChangeListener(this);
		chartSizeSpinner.setValue(chartSquareSize);	
		chartSizeSpinner.addChangeListener(this);
		graphicsPanelChart.revalidate();
	}
	
	private void setPlotSquareSize(){
		int height = spPlot.getSize().height - plotPanel.getMarginBottom() - plotPanel.getMarginTop();
		int gridHeight = plotPanel.getGridHeight();
		plotSquareSize = (int) ((double)height/(double)gridHeight);
		if(plotSquareSize<1){
			plotSquareSize = 1;
		}else if(plotSquareSize>100){
			plotSquareSize = 100;
		}
		plotPanel.setSquareSize(plotSquareSize);
		setPlotState();
		plotSizeSpinner.removeChangeListener(this);
		plotSizeSpinner.setValue(plotSquareSize);
		plotSizeSpinner.addChangeListener(this);
		graphicsPanelPlot.revalidate();
	}
	
	public void componentResized(ComponentEvent ce){
		if(ce.getSource()==spChart && !chartInitialized){
			chartInitialized = true;
			setChartSquareSize();	
		}
		if(ce.getSource()==spPlot && !plotInitialized){
			plotInitialized = true;
			setPlotSquareSize();
		}
	}
	public void componentMoved(ComponentEvent ce){}
	public void componentShown(ComponentEvent ce){}
	public void componentHidden(ComponentEvent ce){}

	public void exportCurrentData(){
		
		String filename = d.getDataFile().getName() + "_sho_fit_plot_data";
		ArrayList<FileType> list = new ArrayList<FileType>();
		list.add(FileType.XLSX);
		HashMap<FileType, String> map = new HashMap<FileType, String>();
		try{
			TextSaver.saveText(owner, list, map, filename, this);
		}catch (Exception e){
			CaughtExceptionHandler.handleException(e, owner);
		}
		
	}

	private void writeExcelTablePlot(Sheet sheet, int rowCounter){
		
		Row row = sheet.createRow(rowCounter);
		
		String string = "Data Quantity";
		Cell cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = fitPlotTypeBox.getSelectedItem().toString();
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "DC Step";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = dcStepField.getText();
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	   
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "DC Offset (V)";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = dcOffsetField.getText(); 
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
		
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "X Quantity";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = dataTypeBoxPlotX.getSelectedItem().toString(); 
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
		
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "X Scale";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = scalePlotX.toString(); 
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
		
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "Y Quantity";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = dataTypeBoxPlotY.getSelectedItem().toString(); 
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
		
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "Y Scale";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = scalePlotY.toString(); 
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue("X Bin");
		
	    cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue("Y Bin");
	    
	    cell = row.createCell(2);
		row.getCell(2).setCellStyle(defaultCellStyle);
	    cell.setCellValue("Counts");
	    
		ArrayList<Double> binListX = binMap.get(shoFitDataTypePlotX).getBinList();
		ArrayList<Double> binListY = binMap.get(shoFitDataTypePlotY).getBinList();
		
		for(int i=0; i<countArray.length; i++){
			for(int j=0; j<countArray[0].length; j++){

				double xValue0 = binListX.get(i);
				double yValue0 = binListY.get(j);
				double xValue1 = binListX.get(i+1);
				double yValue1 = binListY.get(j+1);
				double countsValue = countArray[i][j];
				
				String xValue = "";
				String yValue = "";
				
				if(scalePlotX==ChartScaleType.LIN){
					xValue = shoFitDataTypePlotX.getDecimalFormatLin().format(xValue0) 
											+ ", " 
											+ shoFitDataTypePlotX.getDecimalFormatLin().format(xValue1);
				}else if(scalePlotX==ChartScaleType.LOG){
					xValue = shoFitDataTypePlotX.getDecimalFormatLog().format(xValue0) 
											+ ", " 
											+ shoFitDataTypePlotX.getDecimalFormatLog().format(xValue1);
				}
				
				if(scalePlotY==ChartScaleType.LIN){
					yValue = shoFitDataTypePlotY.getDecimalFormatLin().format(yValue0) 
											+ ", " 
											+ shoFitDataTypePlotY.getDecimalFormatLin().format(yValue1);
				}else if(scalePlotY==ChartScaleType.LOG){
					yValue = shoFitDataTypePlotY.getDecimalFormatLog().format(yValue0) 
											+ ", " 
											+ shoFitDataTypePlotY.getDecimalFormatLog().format(yValue1);
				}
				
				rowCounter++;
				row = sheet.createRow(rowCounter);
				
				cell = row.createCell(0);
				row.getCell(0).setCellStyle(defaultCellStyle);
			    cell.setCellValue(xValue);
				
				cell = row.createCell(1);
				row.getCell(1).setCellStyle(defaultCellStyle);
			    cell.setCellValue(yValue);
			    
			    cell = row.createCell(2);
				row.getCell(2).setCellStyle(defaultCellStyle);
			    cell.setCellValue(countsValue);
				
			}
		}
	    
		resizeAllColumns(sheet, 8500, 4);	
	}
	
	private void writeExcelTableChart(Sheet sheet, int rowCounter){

		Row row = sheet.createRow(rowCounter);
		
		String string = "Data Quantity";
		Cell cell = row.createCell(0);
		
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = fitPlotTypeBox.getSelectedItem().toString();
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "DC Step";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = dcStepField.getText();
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "DC Offset (V)";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = dcOffsetField.getText(); 
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
		
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    string = "X";
		cell = row.createCell(0);
		row.getCell(0).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    string = "Y"; 
		cell = row.createCell(1);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
		
	    string = "Value"; 
		cell = row.createCell(3);
		row.getCell(1).setCellStyle(defaultCellStyle);
	    cell.setCellValue(string);
	    
	    int dataInitRowCounter = rowCounter;
	    
	    rowCounter++;
	    row = sheet.createRow(rowCounter);
	    
	    SHOFitDataCell[][] cellArray = sfds.getCellArray(selectedGroup, selectedDatasetType);
	    for(int i=0; i<cellArray.length; i++){
			for(int j=0; j<cellArray[0].length; j++){
				cell = row.createCell(0);
				row.getCell(0).setCellStyle(defaultCellStyle);
			    cell.setCellValue(i+1);
				
				cell = row.createCell(1);
				row.getCell(1).setCellStyle(defaultCellStyle);
			    cell.setCellValue(j+1);
			    
			    SHOFitDataCell sfdc = cellArray[i][j];
				double value = sfdc.getSHOFitDataList().get(dcOffsetIndex).getValue(shoFitDataTypeChart);
				if((shoFitDataTypeChart==SHOFitDataType.A || shoFitDataTypeChart==SHOFitDataType.Q) && value<0 && applyLimitsBoxChart.isSelected()){
					value = Math.abs(value);
				}
				
				cell = row.createCell(2);
				row.getCell(2).setCellStyle(defaultCellStyle);
			    cell.setCellValue(value);
			    
			    rowCounter++;
			    row = sheet.createRow(rowCounter);
			}
			
		}
	    
		if(selectedChartGridPoint!=null){

			rowCounter = dataInitRowCounter;
			row = sheet.getRow(dataInitRowCounter);
			
			cell = row.createCell(4);
			row.getCell(4).setCellStyle(defaultCellStyle);
		    cell.setCellValue(fitPlotPanel.getXTitle());
			
		    cell = row.createCell(5);
			row.getCell(5).setCellStyle(defaultCellStyle);
		    cell.setCellValue(fitPlotPanel.getYTitle() + " (Fit)");
			
		    if(fitPlotPanel.getYValueArray().length==2){
				
			    cell = row.createCell(6);
				row.getCell(6).setCellStyle(defaultCellStyle);
			    cell.setCellValue(fitPlotPanel.getYTitle() + " (Raw)");
		    	
		    }
		    
			rowCounter++;
			
			for(int i=0; i<fitPlotPanel.getXValueArray()[0].length; i++){
				
				row = sheet.getRow(rowCounter);
				
				cell = row.createCell(4);
				row.getCell(4).setCellStyle(defaultCellStyle);
			    cell.setCellValue(fitPlotPanel.getXValueArray()[0][i]);
				
				cell = row.createCell(5);
				row.getCell(5).setCellStyle(defaultCellStyle);
			    cell.setCellValue(fitPlotPanel.getYValueArray()[0][i]);
			    
			    if(fitPlotPanel.getYValueArray().length==2){
			    	
			    	cell = row.createCell(6);
					row.getCell(6).setCellStyle(defaultCellStyle);
				    cell.setCellValue(fitPlotPanel.getYValueArray()[1][i]);
			    	
			    }

				rowCounter++;
				
			}
			
		}
		
		resizeAllColumns(sheet, 8500, 7);
	}
	
	/**
	 * Write excel report to the spreadsheet.
	 */
	private void writeExcelReport(){
		
		String string = "";
		if(pane.getSelectedIndex()==0){
			string = "SHO Fit Parameter Map";
		}else if(pane.getSelectedIndex()==1){
			string = "SHO Fit Histogram Filter";
		}
		
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
	    
	    string = datasetBox.getSelectedItem().toString();
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
	    if(pane.getSelectedIndex()==0){
	    	writeExcelTableChart(sheet, rowCounter);
		}else if(pane.getSelectedIndex()==1){
			writeExcelTablePlot(sheet, rowCounter);
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

class ChartGridPointCellPanelSelectionListener implements GridPointCellPanelSelectionListener{

	private BEAnalyzerSHOFitAnalysisPanel parent;
	
	public ChartGridPointCellPanelSelectionListener(BEAnalyzerSHOFitAnalysisPanel parent){
		this.parent = parent;
	}
	
	public void gridPointCellPanelSelected(GridPoint gridPoint) {
		parent.setSelectedChartGridPoint(gridPoint);
	}
	
}

class ChartGridPointCellPanelMouseListener implements GridPointCellPanelMouseListener{

	private BEAnalyzerSHOFitAnalysisPanel parent;
	
	public ChartGridPointCellPanelMouseListener(BEAnalyzerSHOFitAnalysisPanel parent){
		this.parent = parent;
	}

	public void gridPointCellPanelMouseOvered(GridPoint gridPoint) {
		parent.setMouseOverChartGridPoint(gridPoint);
	}
	
}

class PlotGridPointCellPanelMouseListener implements GridPointCellPanelMouseListener{

	private BEAnalyzerSHOFitAnalysisPanel parent;
	
	public PlotGridPointCellPanelMouseListener(BEAnalyzerSHOFitAnalysisPanel parent){
		this.parent = parent;
	}
	
	public void gridPointCellPanelMouseOvered(GridPoint gridPoint) {
		parent.setMouseOverPlotGridPoint(gridPoint);
	}
	
}
