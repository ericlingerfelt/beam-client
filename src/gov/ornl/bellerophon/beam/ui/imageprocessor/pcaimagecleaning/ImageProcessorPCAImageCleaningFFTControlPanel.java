package gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning;

import gov.ornl.bellerophon.beam.data.util.ColorMap;
import gov.ornl.bellerophon.beam.enums.ChartScaleType;
import gov.ornl.bellerophon.beam.enums.ColorMapType;
import gov.ornl.bellerophon.beam.enums.PCAImageType;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageProcessorPCAImageCleaningFFTControlPanel extends JPanel implements ActionListener, ChangeListener{

	private JSpinner zoomSpinner, sigmaSpinner;
	private SpinnerNumberModel zoomModel, sigmaModel;
	private JCheckBox viewColorBarsBox, viewAxesBox, applySigmaBox, showCrossHairsBox, mapBox;
	private JTextField xField, yField, valueField;
	private JComboBox<ColorMapType> colorMapBox;
	private JComboBox<ChartScaleType> chartScaleBox;
	private ImageProcessorPCAImageCleaningFFTControlPanelListener listener;
	private PCAImageType type;
	
	public ImageProcessorPCAImageCleaningFFTControlPanel(ImageProcessorPCAImageCleaningFFTControlPanelListener listener, PCAImageType type){
		
		this.listener = listener;
		this.type = type;
		
		JLabel xLabel = new JLabel("X:");
		JLabel yLabel = new JLabel("Y:");
		JLabel valueLabel = new JLabel("Value:");
		JLabel zoomLabel = new JLabel("Zoom:");
		JLabel colorMapLabel = new JLabel("Colormap:");
		JLabel sigmaLabel = new JLabel("Sigma Range:");
		JLabel chartScaleLabel = new JLabel("Plot Scale:");
		
		zoomModel = new SpinnerNumberModel(); 
		zoomModel.setMinimum(1);
		zoomModel.setMaximum(100);
		zoomModel.setStepSize(1);
		
		zoomSpinner = new JSpinner(zoomModel);
		zoomSpinner.setValue(1);
		zoomSpinner.addChangeListener(this);
		((JSpinner.DefaultEditor) zoomSpinner.getEditor()).getTextField().setEditable(false);
		
		sigmaModel = new SpinnerNumberModel(2.0, 0.1, 5.0, 0.1); 
		sigmaSpinner = new JSpinner(sigmaModel);
		sigmaSpinner.addChangeListener(this);
		((JSpinner.DefaultEditor) sigmaSpinner.getEditor()).getTextField().setEditable(false);
		
		viewColorBarsBox = new JCheckBox("View Colorbars?", true);
		viewColorBarsBox.addActionListener(this);
		
		viewAxesBox = new JCheckBox("View Axes?", false);
		viewAxesBox.addActionListener(this);
		
		applySigmaBox = new JCheckBox("Apply Sigma Range?", true);
		applySigmaBox.addActionListener(this);
		
		showCrossHairsBox = new JCheckBox("Show Crosshairs?", true);
		showCrossHairsBox.setSelected(true);
		showCrossHairsBox.addActionListener(this);
		
		mapBox = new JCheckBox("Map Outliers to Colormap?", true);
		mapBox.addActionListener(this);
		
		chartScaleBox = new JComboBox<ChartScaleType>();
		for(ChartScaleType t: ChartScaleType.values()){
			chartScaleBox.addItem(t);
		}
		chartScaleBox.setSelectedIndex(0);
		chartScaleBox.addActionListener(this);
		
		colorMapBox = new JComboBox<ColorMapType>();
		for(ColorMapType t: ColorMapType.values()){
			colorMapBox.addItem(t);
		}
		colorMapBox.setSelectedIndex(0);
		colorMapBox.addActionListener(this);
		
		xField = new JTextField();
		xField.setEditable(false);
		
		yField = new JTextField();
		yField.setEditable(false);
		
		valueField = new JTextField();
		valueField.setEditable(false);
		
		JPanel buttonPanelValues = new JPanel();
		buttonPanelValues.setBorder(Borders.getBorder("Selected Values"));
		double[] columnButtonChartValues = {5, TableLayoutConstants.PREFERRED,
												7, TableLayoutConstants.FILL, 5};
		double[] rowButtonChartValues = {5, TableLayoutConstants.PREFERRED
												, 7, TableLayoutConstants.PREFERRED
												, 7, TableLayoutConstants.PREFERRED
												, 7, TableLayoutConstants.PREFERRED, 5};
		buttonPanelValues.setLayout(new TableLayout(columnButtonChartValues, rowButtonChartValues));
		buttonPanelValues.add(xLabel, 			"1, 1, r, c");
		buttonPanelValues.add(xField, 			"3, 1, f, c");
		buttonPanelValues.add(yLabel, 			"1, 3, r, c");
		buttonPanelValues.add(yField, 			"3, 3, f, c");
		buttonPanelValues.add(valueLabel, 		"1, 5, r, c");
		buttonPanelValues.add(valueField, 		"3, 5, f, c");
		buttonPanelValues.add(showCrossHairsBox,"1, 7, 3, 7, c, c");
		
		JPanel boxPanel = new JPanel();
		double[] columnBox = {5, TableLayoutConstants.PREFERRED,
								7, TableLayoutConstants.FILL, 5};
		double[] rowBox = {5, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED
							, 7, TableLayoutConstants.PREFERRED, 5};
		boxPanel.setLayout(new TableLayout(columnBox, rowBox));
		boxPanel.add(viewColorBarsBox, 			"1, 1, 3, 1, l, c");
		boxPanel.add(viewAxesBox, 				"1, 3, 3, 3, l, c");
		boxPanel.add(applySigmaBox, 			"1, 5, 3, 5, l, c");
		boxPanel.add(mapBox, 					"1, 7, 3, 7, l, c");
		boxPanel.add(chartScaleLabel, 			"1, 9, r, c");
		boxPanel.add(chartScaleBox, 			"3, 9, f, c");
		
		JPanel buttonPanelView = new JPanel();
		buttonPanelView.setBorder(Borders.getBorder("View Options"));
		double[] columnButtonChartView = {5, TableLayoutConstants.PREFERRED,
											7, TableLayoutConstants.FILL, 5};
		double[] rowButtonChartView = {5, TableLayoutConstants.PREFERRED, 
												7, TableLayoutConstants.PREFERRED,
												7, TableLayoutConstants.PREFERRED,
												7, TableLayoutConstants.PREFERRED,
												7, TableLayoutConstants.PREFERRED, 5};
		buttonPanelView.setLayout(new TableLayout(columnButtonChartView, rowButtonChartView));
		buttonPanelView.add(zoomLabel, 					"1, 1, r, c");
		buttonPanelView.add(zoomSpinner, 				"3, 1, f, c");
		buttonPanelView.add(colorMapLabel, 				"1, 3, r, c");
		buttonPanelView.add(colorMapBox, 				"3, 3, f, c");
		buttonPanelView.add(boxPanel, 					"1, 5, 3, 5, c, c");
		buttonPanelView.add(sigmaLabel, 				"1, 7, r, c");
		buttonPanelView.add(sigmaSpinner, 				"3, 7, f, c");
		
		double[] columnButtonChartOutput = {TableLayoutConstants.FILL};
		double[] rowButtonChartOutput = {TableLayoutConstants.PREFERRED, 
											10, TableLayoutConstants.PREFERRED};
		setLayout(new TableLayout(columnButtonChartOutput, rowButtonChartOutput));
		add(buttonPanelValues, 	"0, 0, f, c");
		add(buttonPanelView, 	"0, 2, f, c");
	}

	public void setValues(String x, String y, String value){
		xField.setText(x);
		yField.setText(y);
		valueField.setText(value);
	}
	
	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource()==zoomSpinner){
			listener.fftControlPanelZoomSizeChanged(type);
		}else if(ce.getSource()==sigmaSpinner){
			listener.fftControlPanelSigmaValueChanged(type);
		}
	}
	
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource()==viewColorBarsBox){
			listener.fftControlPanelShowColorBarsChanged(type);
		}else if(ae.getSource()==viewAxesBox){
			listener.fftControlPanelShowAxisChanged(type);
		}else if(ae.getSource()==showCrossHairsBox){
			listener.fftControlPanelShowCrossHairsChanged(type);
		}else if(ae.getSource()==colorMapBox){
			listener.fftControlPanelColorMapChanged(type);
		}else if(ae.getSource()==applySigmaBox){
			if(!applySigmaBox.isSelected()){
				mapBox.setSelected(false);
				mapBox.setEnabled(false);
			}else{
				mapBox.setEnabled(true);
			}
			listener.fftControlPanelApplySigmaChanged(type);
		}else if(ae.getSource()==mapBox){
			listener.fftControlPanelMapOutliersChanged(type);
		}else if(ae.getSource()==chartScaleBox){
			listener.fftControlPanelChartScaleChanged(type);
		}
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

	public boolean applySigma() {
		return applySigmaBox.isSelected();
	}

	public boolean mapOutliers() {
		return mapBox.isSelected();
	}
	
	public int getZoomSize(){
		return (int) zoomSpinner.getValue();
	}
	
	public double getSigmaValue(){
		return (double) sigmaSpinner.getValue();
	}	
	
	public ColorMap getColorMap() {
		ColorMap cm = new ColorMap();
		cm.setColorMapType((ColorMapType) colorMapBox.getSelectedItem());
		return cm;
	}
	
	public ChartScaleType getChartScaleType() {
		return (ChartScaleType) chartScaleBox.getSelectedItem();
	}

	public void setZoomSize(int zoomSize){
		zoomSpinner.removeChangeListener(this);
		zoomSpinner.setValue(zoomSize);
		zoomSpinner.addChangeListener(this);
	}
	
}
