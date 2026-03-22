package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.pca;

import gov.ornl.bellerophon.beam.data.util.ColorMap;
import gov.ornl.bellerophon.beam.data.util.PCAData;
import gov.ornl.bellerophon.beam.data.util.PCADataCell;
import gov.ornl.bellerophon.beam.enums.ColorMapType;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.util.BoundsPopupMenuListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MultivariateAnalyzerPCAEigenControlPanel extends JPanel implements ActionListener, ChangeListener{

	private JSpinner zoomSpinnerCell, zoomSpinnerPlot, sigmaSpinner;
	private SpinnerNumberModel zoomModelCell, zoomModelPlot, sigmaModel;
	private JComboBox<String> groupBox;
	private JPanel boxPanel, buttonPanelView, buttonPanelValues;
	private JCheckBox applyUniversalRangeBox, viewColorBarsBox, viewAxesBox, applySigmaBox, showCrossHairsBox, mapBox;
	private JTextField pcField, xField, yField, xValueField, yValueField, valueField;
	private JLabel pcLabel, xLabel, yLabel, xValueLabel, yValueLabel, valueLabel, zoomLabel, 
					colorMapLabel, sigmaLabel, groupLabel, typeLabel, modeLabel;
	private JComboBox<ColorMapType> colorMapBox;
	private JComboBox<ComplexValueType> typeBox;
	private JComboBox<MultivariateAnalyzerPCAEigenPanelMode> modeBox;
	
	private MultivariateAnalyzerPCAEigenControlPanelListener listener;
	
	public MultivariateAnalyzerPCAEigenControlPanel(MultivariateAnalyzerPCAEigenControlPanelListener listener){
		
		this.listener = listener;
		
		typeBox = new JComboBox<ComplexValueType>();
		groupBox = new JComboBox<String>();
		modeBox = new JComboBox<MultivariateAnalyzerPCAEigenPanelMode>();
		
		groupBox.addActionListener(this);
		BoundsPopupMenuListener groupBoxListener = new BoundsPopupMenuListener(true, false);
		groupBox.addPopupMenuListener(groupBoxListener);
		groupBox.setPrototypeDisplayValue("1234567890");
		
		zoomModelCell = new SpinnerNumberModel(); 
		zoomModelCell.setMinimum(1);
		zoomModelCell.setMaximum(100);
		zoomModelCell.setStepSize(1);
		
		zoomSpinnerCell = new JSpinner(zoomModelCell);
		zoomSpinnerCell.setValue(4);
		zoomSpinnerCell.addChangeListener(this);
		((JSpinner.DefaultEditor) zoomSpinnerCell.getEditor()).getTextField().setEditable(false);
		
		zoomModelPlot = new SpinnerNumberModel(); 
		zoomModelPlot.setMinimum(1);
		zoomModelPlot.setMaximum(100);
		zoomModelPlot.setStepSize(1);
		
		zoomSpinnerPlot = new JSpinner(zoomModelPlot);
		zoomSpinnerPlot.setValue(20);
		zoomSpinnerPlot.addChangeListener(this);
		((JSpinner.DefaultEditor) zoomSpinnerPlot.getEditor()).getTextField().setEditable(false);
		
		sigmaModel = new SpinnerNumberModel(2.0, 0.1, 5.0, 0.1); 
		sigmaSpinner = new JSpinner(sigmaModel);
		sigmaSpinner.addChangeListener(this);
		((JSpinner.DefaultEditor) sigmaSpinner.getEditor()).getTextField().setEditable(false);
		
		colorMapBox = new JComboBox<ColorMapType>();
		for(ColorMapType type: ColorMapType.values()){
			colorMapBox.addItem(type);
		}
		colorMapBox.setSelectedIndex(0);
		colorMapBox.addActionListener(this);
		
		viewColorBarsBox = new JCheckBox("View Colorbars?", true);
		viewColorBarsBox.addActionListener(this);
		
		viewAxesBox = new JCheckBox("View Axes?", false);
		viewAxesBox.addActionListener(this);
		
		applyUniversalRangeBox = new JCheckBox("Apply Universal Range?", false);
		applyUniversalRangeBox.addActionListener(this);
		
		applySigmaBox = new JCheckBox("Apply Sigma Range?", true);
		applySigmaBox.addActionListener(this);
		
		showCrossHairsBox = new JCheckBox("Show Crosshairs?", true);
		showCrossHairsBox.setSelected(true);
		showCrossHairsBox.addActionListener(this);
		
		mapBox = new JCheckBox("Map Outliers to Colormap?", true);
		mapBox.addActionListener(this);
		
		pcField = new JTextField();
		pcField.setEditable(false);
		
		xField = new JTextField();
		xField.setEditable(false);
		
		yField = new JTextField();
		yField.setEditable(false);
		
		xValueField = new JTextField();
		xValueField.setEditable(false);
		
		yValueField = new JTextField();
		yValueField.setEditable(false);
		
		valueField = new JTextField();
		valueField.setEditable(false);
		
		pcLabel = new JLabel("PC:");
		xLabel = new JLabel("X:");
		yLabel = new JLabel("Y:");
		xValueLabel = new JLabel("X Value:");
		yValueLabel = new JLabel("Y Value:");
		valueLabel = new JLabel("Value:");
		colorMapLabel = new JLabel("Colormap:");
		sigmaLabel = new JLabel("Sigma Range:");
		groupLabel = new JLabel("Group:");
		typeLabel = new JLabel("Quantity:");
		modeLabel = new JLabel("View Mode:");
		zoomLabel = new JLabel("Zoom:");
		
		boxPanel = new JPanel();
		double[] columnBox = {TableLayoutConstants.PREFERRED};
		double[] rowBox = {TableLayoutConstants.PREFERRED
						, 7, TableLayoutConstants.PREFERRED
						, 7, TableLayoutConstants.PREFERRED
						, 7, TableLayoutConstants.PREFERRED
						, 7, TableLayoutConstants.PREFERRED};
		boxPanel.setLayout(new TableLayout(columnBox, rowBox));
		boxPanel.add(viewColorBarsBox, 			"0, 0, l, c");
		boxPanel.add(viewAxesBox, 				"0, 2, l, c");
		boxPanel.add(applyUniversalRangeBox, 	"0, 4, l, c");
		boxPanel.add(applySigmaBox, 			"0, 6, l, c");
		boxPanel.add(mapBox, 					"0, 8, l, c");
		
		buttonPanelValues = new JPanel();
		buttonPanelValues.setBorder(Borders.getBorder("Selected Values"));
		
		buttonPanelView = new JPanel();
		buttonPanelView.setBorder(Borders.getBorder("View Options"));
		
		double[] columnButtonChartOutput = {TableLayoutConstants.FILL};
		double[] rowButtonChartOutput = {TableLayoutConstants.PREFERRED, 
											10, TableLayoutConstants.PREFERRED};
		setLayout(new TableLayout(columnButtonChartOutput, rowButtonChartOutput));
		add(buttonPanelValues, 	"0, 0, f, c");
		add(buttonPanelView, 	"0, 2, f, c");
		
	}

	public void setValues(String pc, String x, String y, String xValue, String yValue, String value){
		pcField.setText(pc);
		xField.setText(x);
		yField.setText(y);
		xValueField.setText(xValue);
		yValueField.setText(yValue);
		valueField.setText(value);
	}
	
	private void layoutUI(){
		
		buttonPanelValues.removeAll();
		buttonPanelView.removeAll();

		if(getMode()==MultivariateAnalyzerPCAEigenPanelMode.PLOT_MODE){
			
			double[] columnButtonChartView = {5, TableLayoutConstants.PREFERRED,
					7, TableLayoutConstants.FILL, 5};
			double[] rowButtonChartView = {5, TableLayoutConstants.PREFERRED,
					7, TableLayoutConstants.PREFERRED, 5};
			buttonPanelView.setLayout(new TableLayout(columnButtonChartView, rowButtonChartView));
			buttonPanelView.add(zoomLabel, 		"1, 1, r, c");
			buttonPanelView.add(zoomSpinnerPlot, "3, 1, f, c");
			buttonPanelView.add(typeLabel, 		"1, 3, r, c");
			buttonPanelView.add(typeBox, 		"3, 3, f, c");
	
			if(groupBox.getSelectedItem()==null){
				double[] columnButtonChartValues = {5, TableLayoutConstants.PREFERRED,
						7, TableLayoutConstants.FILL, 5};
				double[] rowButtonChartValues = {5, TableLayoutConstants.PREFERRED, 
						7, TableLayoutConstants.PREFERRED, 5};
				buttonPanelValues.setLayout(new TableLayout(columnButtonChartValues, rowButtonChartValues));
				buttonPanelValues.add(modeLabel, 		"1, 1, r, c");
				buttonPanelValues.add(modeBox, 			"3, 1, f, c");
				buttonPanelValues.add(pcLabel, 			"1, 3, r, c");
				buttonPanelValues.add(pcField, 			"3, 3, f, c");
			}else{
				double[] columnButtonChartValues = {5, TableLayoutConstants.PREFERRED,
						7, TableLayoutConstants.FILL, 5};
				double[] rowButtonChartValues = {5, TableLayoutConstants.PREFERRED, 
						7, TableLayoutConstants.PREFERRED,
						7, TableLayoutConstants.PREFERRED, 5};
				buttonPanelValues.setLayout(new TableLayout(columnButtonChartValues, rowButtonChartValues));
				buttonPanelValues.add(modeLabel, 		"1, 1, r, c");
				buttonPanelValues.add(modeBox, 			"3, 1, f, c");
				buttonPanelValues.add(groupLabel, 		"1, 3, r, c");
				buttonPanelValues.add(groupBox, 		"3, 3, f, c");
				buttonPanelValues.add(pcLabel, 			"1, 5, r, c");
				buttonPanelValues.add(pcField, 			"3, 5, f, c");
			}

		}else if(getMode()==MultivariateAnalyzerPCAEigenPanelMode.CELL_MODE){
		
			double[] columnButtonChartView = {5, TableLayoutConstants.PREFERRED,
					7, TableLayoutConstants.FILL, 5};
			double[] rowButtonChartView = {5, TableLayoutConstants.PREFERRED, 
					7, TableLayoutConstants.PREFERRED,
					7, TableLayoutConstants.PREFERRED,
					7, TableLayoutConstants.PREFERRED,
					7, TableLayoutConstants.PREFERRED,
					7, TableLayoutConstants.PREFERRED, 5};
			buttonPanelView.setLayout(new TableLayout(columnButtonChartView, rowButtonChartView));
			buttonPanelView.add(zoomLabel, 					"1, 1, r, c");
			buttonPanelView.add(zoomSpinnerCell, 				"3, 1, f, c");
			buttonPanelView.add(typeLabel, 					"1, 3, r, c");
			buttonPanelView.add(typeBox, 					"3, 3, f, c");
			buttonPanelView.add(colorMapLabel, 				"1, 5, r, c");
			buttonPanelView.add(colorMapBox, 				"3, 5, f, c");
			buttonPanelView.add(boxPanel, 					"1, 7, 3, 7, c, c");
			buttonPanelView.add(sigmaLabel, 				"1, 9, r, c");
			buttonPanelView.add(sigmaSpinner, 				"3, 9, f, c");
	
			double[] columnButtonChartValues = {5, TableLayoutConstants.PREFERRED,
					7, TableLayoutConstants.FILL, 5};
			double[] rowButtonChartValues = {5, TableLayoutConstants.PREFERRED
					, 7, TableLayoutConstants.PREFERRED
					, 7, TableLayoutConstants.PREFERRED
					, 7, TableLayoutConstants.PREFERRED
					, 7, TableLayoutConstants.PREFERRED
					, 7, TableLayoutConstants.PREFERRED
					, 7, TableLayoutConstants.PREFERRED
					, 7, TableLayoutConstants.PREFERRED
					, 7, TableLayoutConstants.PREFERRED, 5};
			buttonPanelValues.setLayout(new TableLayout(columnButtonChartValues, rowButtonChartValues));
			buttonPanelValues.add(modeLabel, 		"1, 1, r, c");
			buttonPanelValues.add(modeBox, 			"3, 1, f, c");
			buttonPanelValues.add(groupLabel, 		"1, 3, r, c");
			buttonPanelValues.add(groupBox, 		"3, 3, f, c");
			buttonPanelValues.add(pcLabel, 			"1, 5, r, c");
			buttonPanelValues.add(pcField, 			"3, 5, f, c");
			buttonPanelValues.add(xLabel, 			"1, 7, r, c");
			buttonPanelValues.add(xField, 			"3, 7, f, c");
			buttonPanelValues.add(yLabel, 			"1, 9, r, c");
			buttonPanelValues.add(yField, 			"3, 9, f, c");
			buttonPanelValues.add(xValueLabel, 		"1, 11, r, c");
			buttonPanelValues.add(xValueField, 		"3, 11, f, c");
			buttonPanelValues.add(yValueLabel, 		"1, 13, r, c");
			buttonPanelValues.add(yValueField, 		"3, 13, f, c");
			buttonPanelValues.add(valueLabel, 		"1, 15, r, c");
			buttonPanelValues.add(valueField, 		"3, 15, f, c");
			buttonPanelValues.add(showCrossHairsBox,"1, 17, 3, 17, c, c");
		}
		
		revalidate();
		repaint();
	
	}
	
	public void setCurrentState(PCAData pd){
		
		typeBox.removeActionListener(this);
		typeBox.removeAllItems();
		Iterator<ComplexValueType> itr = pd.getVPlotMap().keySet().iterator();
		while(itr.hasNext()){
			ComplexValueType type = itr.next();
			typeBox.addItem(type);
		}
		typeBox.setSelectedIndex(0);
		typeBox.addActionListener(this);

		modeBox.removeActionListener(this);
		modeBox.removeAllItems();
		modeBox.addItem(MultivariateAnalyzerPCAEigenPanelMode.PLOT_MODE);
		modeBox.setSelectedIndex(0);
		if(pd.getVCellMap()!=null && has2DCellData(pd.getVCellMap().firstEntry().getValue())){
			modeBox.addItem(MultivariateAnalyzerPCAEigenPanelMode.CELL_MODE);
			modeBox.setSelectedIndex(1);
		}
		modeBox.addActionListener(this); 

		groupBox.removeActionListener(this);
		groupBox.removeAllItems();
		groupBox.addActionListener(this);
		
		if(pd.getVCellMap()!=null && has2DCellData(pd.getVCellMap().firstEntry().getValue())){
			groupBox.removeActionListener(this);
			for(String s: pd.getVCellMap().keySet()){
				groupBox.addItem(s);
			}
			groupBox.setSelectedIndex(0);
			groupBox.addActionListener(this);
			
			int zoomSize = (int) Math.ceil((double)250/(double)Math.min(pd.getVCellMap().firstEntry().getValue().length
												, pd.getVCellMap().firstEntry().getValue()[0].length));
			zoomSpinnerCell.setValue(zoomSize);
			
		}
		
		layoutUI();
		
	}
	
	private boolean has2DCellData(PCADataCell[][] cellArray){
		if(cellArray==null){
			return false;
		}
		return cellArray.length>1;
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource()==zoomSpinnerPlot 
				|| ce.getSource()==zoomSpinnerCell 
				|| ce.getSource()==sigmaSpinner){
			listener.eigenControlPanelStateChanged();
		}
	}
	
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource()==applyUniversalRangeBox 
				|| ae.getSource()==viewColorBarsBox
				|| ae.getSource()==viewAxesBox
				|| ae.getSource()==showCrossHairsBox
				|| ae.getSource()==colorMapBox
				|| ae.getSource()==showCrossHairsBox
				|| ae.getSource()==typeBox
				|| ae.getSource()==groupBox
				|| ae.getSource()==applySigmaBox
				|| ae.getSource()==mapBox){
			listener.eigenControlPanelStateChanged();
		}else if(ae.getSource()==modeBox){
			layoutUI();
			listener.eigenControlPanelStateChanged();
		}

	}
		
	public String getGroup() {
		return (String) groupBox.getSelectedItem();
	}

	public ComplexValueType getType() {
		return (ComplexValueType) typeBox.getSelectedItem();
	}

	public MultivariateAnalyzerPCAEigenPanelMode getMode() {
		return (MultivariateAnalyzerPCAEigenPanelMode) modeBox.getSelectedItem();
	}
	
	public boolean showCrossHairs() {
		return showCrossHairsBox.isSelected();
	}
	
	public boolean showColorBars() {
		return viewColorBarsBox.isSelected();
	}

	public boolean showAxis() {
		return viewAxesBox.isSelected();
	}

	public boolean applyUniversalRange() {
		return applyUniversalRangeBox.isSelected();
	}
	
	public boolean applySigma() {
		return applySigmaBox.isSelected();
	}
	
	public boolean mapOutliers() {
		return mapBox.isSelected();
	}
	
	public double getSigmaValue(){
		return (double) sigmaSpinner.getValue();
	}
	
	public int getZoomSize(){
		if(getMode()==MultivariateAnalyzerPCAEigenPanelMode.PLOT_MODE){
			return (int) zoomSpinnerPlot.getValue();
		}else if(getMode()==MultivariateAnalyzerPCAEigenPanelMode.CELL_MODE){
			return (int) zoomSpinnerCell.getValue();
		}
		return 0;
	}	

	public ColorMap getColorMap() {
		ColorMap cm = new ColorMap();
		cm.setColorMapType((ColorMapType) colorMapBox.getSelectedItem());
		return cm;
	}
	
}
