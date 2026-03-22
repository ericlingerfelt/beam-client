package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.pca;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import gov.ornl.bellerophon.beam.data.feature.MultivariateAnalyzerData;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MultivariateAnalyzerPCASetViewPanel extends JPanel implements ActionListener, ChangeListener{

	private JSpinner rowSpinner, colSpinner;
	private SpinnerNumberModel rowModel, colModel;
	private JTextField setField;
	private JSlider setSlider;
	private ArrayList<MultivariateAnalyzerPCASetViewPanelListener> listenerList;
	private int listenerIndex;
	private MultivariateAnalyzerData d;
	
	public MultivariateAnalyzerPCASetViewPanel(MultivariateAnalyzerData d){
		
		this.d = d;
		
		rowModel = new SpinnerNumberModel(2, 1, 5, 1);
		colModel = new SpinnerNumberModel(2, 1, 5, 1);

		rowSpinner = new JSpinner(rowModel);
		rowSpinner.addChangeListener(this);
		((JSpinner.DefaultEditor) rowSpinner.getEditor()).getTextField().setEditable(false);
		
		colSpinner = new JSpinner(colModel);
		colSpinner.addChangeListener(this);
		((JSpinner.DefaultEditor) colSpinner.getEditor()).getTextField().setEditable(false);

		setField = new JTextField();
		setField.addActionListener(this);
		
		setSlider = new JSlider();	
		setSlider.setMinimum(0);
		setSlider.setLabelTable(setSlider.createStandardLabels(1));
		setSlider.setPaintLabels(true);
		setSlider.setPaintTicks(true);
		setSlider.setSnapToTicks(true);

		JLabel rowLabel = new JLabel("Num Rows:");
		JLabel colLabel = new JLabel("Num Cols:");
		JLabel setLabel = new JLabel("PC Set:");
		
		setBorder(Borders.getBorder("PC Set Options"));
		double[] columnButtonChartOptions = {5, TableLayoutConstants.PREFERRED,
						7, TableLayoutConstants.FILL,
						12, TableLayoutConstants.PREFERRED,
						7, TableLayoutConstants.FILL, 
						12, TableLayoutConstants.PREFERRED,
						7, TableLayoutConstants.FILL, 5};
		double[] rowButtonChartOptions = {5, TableLayoutConstants.PREFERRED
						, 10, TableLayoutConstants.PREFERRED, 5};
		setLayout(new TableLayout(columnButtonChartOptions, rowButtonChartOptions));
		add(rowLabel, 		"1, 1, r, c");
		add(rowSpinner, 	"3, 1, f, c");
		add(colLabel, 		"5, 1, r, c");
		add(colSpinner, 	"7, 1, f, c");
		add(setLabel, 		"9, 1, r, c");
		add(setField, 		"11, 1, f, c");
		add(setSlider, 		"1, 3, 11, 3, f, c");
	}

	public void setMultivariateAnalyzerPCASetViewPanelListenerList(ArrayList<MultivariateAnalyzerPCASetViewPanelListener> listenerList){
		this.listenerList = listenerList;
	}
	
	public void setListenerIndex(int listenerIndex){
		this.listenerIndex = listenerIndex;
	}
	
	public void setCurrentState(){
		
		int numPC = d.getDataFile().getPCADataSet().getMaxComponentIndex()+1;
		int numPCPerSet = getNumCols()*getNumRows();
		int numPCSets = (int) Math.ceil((double)numPC/(double)numPCPerSet);
		
		setSlider.removeChangeListener(this);
		setSlider.setMinimum(1);
		setSlider.setMaximum(numPCSets);
		setSlider.setValue(1);
		setSlider.addChangeListener(this);
		setField.removeActionListener(this);
		setField.setText(String.valueOf(setSlider.getValue()));
		setField.addActionListener(this);

		if(setSlider.getMaximum()<=20){
			setSlider.setLabelTable(setSlider.createStandardLabels(1, 1));
		}else if(setSlider.getMaximum()<=200){
			setSlider.setLabelTable(setSlider.createStandardLabels(10, 10));
		}
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource()==setSlider && !setSlider.getValueIsAdjusting()){
			setField.removeActionListener(this);
			setField.setText(String.valueOf(setSlider.getValue()));
			setField.addActionListener(this);
			listenerList.get(listenerIndex).setViewPanelStateChanged();
		}else if(ce.getSource()==rowSpinner || ce.getSource()==colSpinner){
			setCurrentState();
			listenerList.get(listenerIndex).setViewPanelDimensionsChanged();
		}
	}

	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource()==setField){
			setSlider.removeChangeListener(this);
			setSlider.setValue(Integer.valueOf(setField.getText()));
			setSlider.addChangeListener(this);
			listenerList.get(listenerIndex).setViewPanelStateChanged();
		}
	}
	
	public int getSetIndex(){
		return setSlider.getValue()-1;
	}
	
	public int getNumCols(){
		return (int) colSpinner.getValue();
	}
	
	public int getNumRows(){
		return (int) rowSpinner.getValue();
	}
	
}
