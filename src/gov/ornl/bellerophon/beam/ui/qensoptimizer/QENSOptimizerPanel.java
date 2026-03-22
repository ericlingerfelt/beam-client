package gov.ornl.bellerophon.beam.ui.qensoptimizer;

import gov.ornl.bellerophon.beam.data.feature.QENSOptimizerData;
import gov.ornl.bellerophon.beam.data.util.AnalysisProcess;
import gov.ornl.bellerophon.beam.data.util.ErrorResult;
import gov.ornl.bellerophon.beam.data.util.SNSData;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.io.IOUtilities;
import gov.ornl.bellerophon.beam.ui.dialog.ErrorResultDialog;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.format.Buttons;
import gov.ornl.bellerophon.beam.ui.format.Colors;
import gov.ornl.bellerophon.beam.ui.worker.GetQENSOptimizationResultsWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetQENSOptimizationResultsListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class QENSOptimizerPanel extends JPanel implements ActionListener, ChangeListener, GetQENSOptimizationResultsListener{
	
	private QENSOptimizerData d = new QENSOptimizerData();
	private QENSOptimizerChartPanel chartPanel;
	private QENSOptimizerPlotPanel plotPanel;
	private Frame frame;
	private JButton button, stepButton;
	private JTextField ffParamField;
	private JLabel ffParamLabel, ffParamBoxLabel;
	private JPanel mainPanel;
	private JSpinner ffParamSpinner; 
	private SpinnerListModel ffParamSpinnerModel;
	private JCheckBox fitBox, expBox;
	
	ArrayList<String> valueList = new ArrayList<String>();
	ArrayList<String> spinnerList = new ArrayList<String>();
	private int currentValueIndex;
	
	public QENSOptimizerPanel(Frame frame){

		this.frame = frame;
		
		valueList.add("0.01");
		valueList.add("0.12");
		valueList.add("0.02");
		valueList.add("0.11");
		valueList.add("0.03");
		valueList.add("0.10");
		valueList.add("0.035");
		valueList.add("0.09");
		valueList.add("0.04");
		valueList.add("0.085");
		valueList.add("0.045");
		valueList.add("0.08");
		valueList.add("0.05");
		valueList.add("0.075");
		valueList.add("0.055");
		valueList.add("0.07");
		valueList.add("0.0575");
		valueList.add("0.065");
		valueList.add("0.06");
		valueList.add("0.0625");
		
		currentValueIndex = 0;
		
		button = Buttons.getIconButton("Execute QENS Optimization Workflow"
										, "icons/system-run.png"
										, Buttons.IconPosition.RIGHT
										, Colors.BLUE
										, this
										, new Dimension(300, 50)
										, 12);
		
		ffParamField = new JTextField(20);
		ffParamLabel = new JLabel("Enter Force Field Parameter [0.01 - 0.12]:");
		
		ffParamBoxLabel = new JLabel("Select Force Field Parameter:");
		ffParamSpinnerModel = new SpinnerListModel();
		ffParamSpinner = new JSpinner(ffParamSpinnerModel);
		((JSpinner.DefaultEditor) ffParamSpinner.getEditor()).getTextField().setEditable(false);
		
		fitBox = new JCheckBox("Display Fitted Data?", true);
		fitBox.addActionListener(this);
		expBox = new JCheckBox("Display Experimental Data?", true);
		expBox.addActionListener(this);
		
		stepButton = Buttons.getIconButton("Download Next Result"
												, "icons/go-next.png"
												, Buttons.IconPosition.RIGHT
												, Colors.GREEN
												, this
												, new Dimension(250, 50)
												, 12);
		
		JPanel buttonPanel1 = new JPanel();
		double[] columnButton1 = {10, TableLayoutConstants.PREFERRED, 30, 
									TableLayoutConstants.PREFERRED, 10};
		double[] rowButton1 = {10, TableLayoutConstants.PREFERRED, 10
									, TableLayoutConstants.PREFERRED, 10};
		buttonPanel1.setLayout(new TableLayout(columnButton1, rowButton1));
		buttonPanel1.add(button, 		"1, 1, 1, 3, c, c");
		buttonPanel1.add(ffParamLabel, 	"3, 1, l, c");
		buttonPanel1.add(ffParamField, 	"3, 3, f, c");
		
		JPanel buttonPanel2 = new JPanel();
		double[] columnButton2 = {10, TableLayoutConstants.PREFERRED, 30, 
									TableLayoutConstants.PREFERRED, 10,
									TableLayoutConstants.PREFERRED, 10};
		double[] rowButton2 = {10, TableLayoutConstants.PREFERRED, 10
									, TableLayoutConstants.PREFERRED, 10};
		buttonPanel2.setLayout(new TableLayout(columnButton2, rowButton2));
		buttonPanel2.add(ffParamBoxLabel,"1, 1, l, c");
		buttonPanel2.add(ffParamSpinner, "1, 3, f, c");
		buttonPanel2.add(fitBox,		 "3, 1, l, c");
		buttonPanel2.add(expBox, 		 "3, 3, l, c");
		buttonPanel2.add(stepButton, 	 "5, 1, 5, 3, c, c");
		
		chartPanel = new QENSOptimizerChartPanel();
		JScrollPane spChart = new JScrollPane(chartPanel);

		plotPanel = new QENSOptimizerPlotPanel();
		JScrollPane spPlot = new JScrollPane(plotPanel);
		
		mainPanel = new JPanel();
		mainPanel.setBorder(Borders.getBorder("QENS Optimization Data Viewer"));
		
		double[] colMain = {10, TableLayoutConstants.FILL, 10
								, TableLayoutConstants.FILL, 10};
		double[] rowMain = {10,	TableLayoutConstants.FILL, 5, 
								TableLayoutConstants.PREFERRED, 10};
		mainPanel.setLayout(new TableLayout(colMain, rowMain));
		mainPanel.add(spPlot, 		"1, 1, f, f");
		mainPanel.add(spChart, 		"3, 1, f, f");
		mainPanel.add(buttonPanel1, "1, 3, c, c");
		mainPanel.add(buttonPanel2, "3, 3, c, c");
		
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		add(mainPanel, "1, 1, f, f");
		
	}
	
	public void setCurrentState(){
		
		String currentValue = valueList.get(currentValueIndex);
		spinnerList.add(currentValue);
		
		ffParamSpinner.removeChangeListener(this);
		ffParamSpinnerModel.setList(spinnerList);
		try {
			byte[] bytes = IOUtilities.readURL("https://nucastrodata2.ornl.gov/snsdata/" + currentValue + ".masked");
			File file = File.createTempFile("beam", null);
			file.deleteOnExit();
			IOUtilities.writeFile(file, bytes);
			SNSData sd = new SNSData();
			sd.setFFParam(Double.valueOf(currentValue));
			d.getSNSDataSet().getDataMap().put(Double.valueOf(currentValue), sd);
			sd.populateFromResultsZipFile(file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		chartPanel.setSNSData(d.getSNSDataSet().getDataMap().get(Double.valueOf(currentValue)));
		plotPanel.setSNSDataSet(d.getSNSDataSet());
		ffParamSpinner.addChangeListener(this);
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==button){
			AnalysisProcess process = new AnalysisProcess();
			//process.setPlatform(PlatformType.PILEUS);
			//process.setAnalysisFunction(AnalysisFunctionType.SNS_SIMULATOR);
			process.setInputParameters(String.format("%16.8E", Double.parseDouble(ffParamField.getText())).replaceAll("\\+", "%2b"));
			process.setNumCores(4);
			boolean processCompleted = QENSOptimizerStatusDialog.createSNSSimulatorStatusDialog(frame, process);
			if(processCompleted){
				SNSData sd = new SNSData();
				sd.setFFParam(Double.parseDouble(ffParamField.getText()));
				d.getSNSDataSet().getDataMap().put(sd.getFFParam(), sd);
				GetQENSOptimizationResultsWorker worker = new GetQENSOptimizationResultsWorker(this, sd, frame);
				worker.execute();
			}
		}else if(ae.getSource()==fitBox){
			chartPanel.displayFit(fitBox.isSelected());
		}else if(ae.getSource()==expBox){
			chartPanel.displayExp(expBox.isSelected());
		}else if(ae.getSource()==stepButton){
			gotoNextValue();
		}
	}

	private void gotoNextValue(){
		currentValueIndex++;
		if(valueList.size()>currentValueIndex){
			String currentValue = valueList.get(currentValueIndex);
			LoadValueWorker worker = new LoadValueWorker(this, currentValue, d, frame);
			worker.execute();
		}
	}
	
	public void updateAfterLoadValue(String currentValue){
		chartPanel.setSNSData(d.getSNSDataSet().getDataMap().get(Double.valueOf(currentValue)));
		plotPanel.setSNSDataSet(d.getSNSDataSet());
		
		spinnerList.add(currentValue);
		Collections.sort(spinnerList);
		spinnerList = (ArrayList<String>) spinnerList.clone();
		ffParamSpinnerModel.setList(spinnerList);
		ffParamSpinner.setValue(currentValue);
	}
	
	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource()==ffParamSpinner){
			chartPanel.setSNSData(d.getSNSDataSet().getDataMap().get(Double.valueOf(ffParamSpinner.getValue().toString())));
		}
	}
	
	public void updateAfterGetQENSOptimizationResults(SNSData sd){
		d.getSNSDataSet().getDataMap().put(sd.getFFParam(), sd);
		chartPanel.setSNSData(sd);
		plotPanel.setSNSDataSet(d.getSNSDataSet());
	}
}

class LoadValueWorker extends SwingWorker<ErrorResult, Void>{
	
	private QENSOptimizerPanel parent;
	private Window owner;
	private String currentValue;
	private QENSOptimizerData d;
	
	public LoadValueWorker(QENSOptimizerPanel parent, String currentValue, QENSOptimizerData d, Window owner){
		this.parent = parent;
		this.owner = owner;
		this.currentValue = currentValue;
		this.d = d;
	}
	
	protected ErrorResult doInBackground(){
		owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try{
			byte[] bytes = IOUtilities.readURL("https://nucastrodata2.ornl.gov/snsdata/" + currentValue + ".masked");
			File file = File.createTempFile("beam", null);
			file.deleteOnExit();
			IOUtilities.writeFile(file, bytes);
			SNSData sd = new SNSData();
			sd.setFFParam(Double.valueOf(currentValue));
			d.getSNSDataSet().getDataMap().put(Double.valueOf(currentValue), sd);
			sd.populateFromResultsZipFile(file);
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ErrorResult();
	}
	
	protected void done(){
		try{
			ErrorResult result = get();
			if(!result.isError()){
				owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				parent.updateAfterLoadValue(currentValue);
			}else{
				owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				ErrorResultDialog.createErrorResultDialog(owner, result);
			}
		}catch(Exception e){
			owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			CaughtExceptionHandler.handleException(e, owner);
		}
	}
}
