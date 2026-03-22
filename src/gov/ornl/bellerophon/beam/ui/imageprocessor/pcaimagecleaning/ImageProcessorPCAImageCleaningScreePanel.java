package gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.feature.ImageProcessorData;
import gov.ornl.bellerophon.beam.data.util.PCAImageCleaningDataSet;
import gov.ornl.bellerophon.beam.enums.ChartScaleType;
import gov.ornl.bellerophon.beam.exception.CaughtExceptionHandler;
import gov.ornl.bellerophon.beam.file.CustomFileFilter;
import gov.ornl.bellerophon.beam.file.FileType;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import gov.ornl.bellerophon.beam.ui.dialog.CautionDialog;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ExcelWriter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.export.TextSaver;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.util.PlainFileChooserFactory;
import gov.ornl.bellerophon.beam.ui.worker.GeneratePCAImagesWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GeneratePCAImagesListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImageProcessorPCAImageCleaningScreePanel extends JPanel implements ChangeListener, 
																			ActionListener,
																			KeyListener, 
																			ImageExporter,
																			DataExporter,
																			ExcelWriter, 
																			ComponentListener{

	private Frame frame;
	private ImageProcessorData d;
	private ImageProcessorPCAImageCleaningScreePlotPanel plotPanel;
	private JScrollPane plotPane;
	private JSlider pcSlider;
	private JTextField valueField;
	private JSpinner pcSpinner;
	private SpinnerNumberModel pcModel;
	private JComboBox<ChartScaleType> scaleBoxX, scaleBoxY;
	private PCAImageCleaningDataSet picds;
	private GeneratePCAImagesListener gpil;
	
	//Excel objects
	private Workbook wb;
	private CellStyle dateCellStyle, wrapCellStyle, headerCellStyle
							, rowHeaderCellStyle, rowHeaderWrapCellStyle, defaultCellStyle
							, boldCellStyle;

	public ImageProcessorPCAImageCleaningScreePanel(Frame frame, ImageProcessorData d, GeneratePCAImagesListener gpil){
		
		this.frame = frame;
		this.d = d;
		this.gpil = gpil;
		
		addComponentListener(this);
		
		plotPanel = new ImageProcessorPCAImageCleaningScreePlotPanel(this);
		plotPanel.setPreferredSize(new Dimension(800, 500));
		plotPane = new JScrollPane(plotPanel);
		
		pcSlider = new JSlider();
		pcSlider.setPaintLabels(true);
		pcSlider.setPaintTicks(true);
		pcSlider.setSnapToTicks(true);
		
		pcModel = new SpinnerNumberModel();
		pcSpinner = new JSpinner(pcModel);
		((JSpinner.DefaultEditor) pcSpinner.getEditor()).getTextField().addKeyListener(this);
		
		valueField = new JTextField(7);
		valueField.setEditable(false);
		
		scaleBoxX = new JComboBox<ChartScaleType>();
		for(ChartScaleType type: ChartScaleType.values()){
			scaleBoxX.addItem(type);
		}
		scaleBoxX.setSelectedItem(ChartScaleType.LOG);
		scaleBoxX.addActionListener(this);
		
		scaleBoxY = new JComboBox<ChartScaleType>();
		for(ChartScaleType type: ChartScaleType.values()){
			scaleBoxY.addItem(type);
		}
		scaleBoxY.setSelectedItem(ChartScaleType.LOG);
		scaleBoxY.addActionListener(this);
		
		JLabel scaleXLabel = new JLabel("PC Scale:");
		JLabel scaleYLabel = new JLabel("Variance Scale:");
		JLabel pcLabel = new JLabel("PC Limit:");
		JLabel valueLabel = new JLabel("Variance:");
		
		JPanel scalePanel = new JPanel();
		double[] columnScale = {TableLayoutConstants.PREFERRED, 7
										, TableLayoutConstants.FILL, 10
										, TableLayoutConstants.PREFERRED, 7
										, TableLayoutConstants.FILL};
		double[] rowScale = {TableLayoutConstants.PREFERRED};
		scalePanel.setLayout(new TableLayout(columnScale, rowScale));
		scalePanel.add(scaleXLabel,  	"0, 0, r, c");
		scalePanel.add(scaleBoxX,    	"2, 0, f, c");
		scalePanel.add(scaleYLabel,  	"4, 0, r, c");
		scalePanel.add(scaleBoxY,    	"6, 0, f, c");
		
		JPanel pcCutoffPanel = new JPanel();
		double[] columnPCCutoff = {TableLayoutConstants.PREFERRED, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 10
										, TableLayoutConstants.PREFERRED, 7
										, TableLayoutConstants.PREFERRED};
		double[] rowPCCutoff = {TableLayoutConstants.PREFERRED};
		pcCutoffPanel.setLayout(new TableLayout(columnPCCutoff, rowPCCutoff));
		pcCutoffPanel.add(pcLabel,   	"0, 0, r, c");
		pcCutoffPanel.add(pcSlider,   	"2, 0, 10, 0, f, c");
		pcCutoffPanel.add(pcSpinner,  	"12, 0, f, c");
		pcCutoffPanel.add(valueLabel,  	"14, 0, f, c");
		pcCutoffPanel.add(valueField,  	"16, 0, f, c");
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(Borders.getBorder("Scree Plot View Options"));
		double[] columnOptions = {10, TableLayoutConstants.FILL, 10};
		double[] rowOptions = {10, TableLayoutConstants.PREFERRED, 10
									, TableLayoutConstants.PREFERRED, 10};
		optionsPanel.setLayout(new TableLayout(columnOptions, rowOptions));
		optionsPanel.add(scalePanel,  	"1, 1, f, c");
		optionsPanel.add(pcCutoffPanel, "1, 3, f, c");
		
		double[] column = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10, TableLayoutConstants.PREFERRED, 10};
		setLayout(new TableLayout(column, row));
		add(plotPane, 		"1, 1, f, f");
		add(optionsPanel,   "1, 3, f, c");
		
	}
	
	public void setCurrentState(){
		
		this.picds = d.getDataFile().getPCAImageCleaningDataSet();

		pcSlider.removeChangeListener(this);
		pcSlider.setMinimum(1);
		pcSlider.setMaximum(picds.getPCADataList().size());
		pcSlider.setValue(picds.getMaxComponentIndex()+1);
		pcSlider.addChangeListener(this);
		
		pcSpinner.removeChangeListener(this);
		pcModel.setMinimum(1);
		pcModel.setMaximum(picds.getPCADataList().size());
		pcModel.setValue(pcSlider.getValue());
		pcSpinner.addChangeListener(this);
		
		valueField.setText(new DecimalFormat("0.000E0").format(picds.getPCADataList().get(pcSlider.getValue()-1).getS()));
		
		plotPanel.setCurrentData(picds);
		plotPanel.setPlotMode((ChartScaleType)scaleBoxX.getSelectedItem(), (ChartScaleType)scaleBoxY.getSelectedItem());
		plotPanel.setPCLimit(pcSlider.getValue());
		
		plotPanel.setVisible(false);
		plotPanel.setVisible(true);
		
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource()==pcSlider){
			pcModel.setValue(pcSlider.getValue());
			valueField.setText(new DecimalFormat("0.000E0").format(picds.getPCADataList().get(pcSlider.getValue()-1).getS()));
			plotPanel.setPCLimit(pcSlider.getValue());
			if(!pcSlider.getValueIsAdjusting()){
				picds.setMaxComponentIndex(pcSlider.getValue()-1);
				GeneratePCAImagesWorker worker = new GeneratePCAImagesWorker(gpil, picds, frame);
				worker.execute();
			}
		}else if(ce.getSource()==pcSpinner){
			pcSlider.setValue((int) pcModel.getValue());
			plotPanel.setPCLimit(pcSlider.getValue());
		}
	}

	public Dimension getPlotBounds(){
		Dimension d = plotPane.getViewport().getBounds().getSize();
		Dimension newD = new Dimension((int)(d.getWidth() - 20), (int)(d.getHeight() - 20));
		return newD;
	}
	
	public void refreshPlot(){
		plotPanel.setPreferredSize(getPlotBounds());
	}
	
	public void componentResized(ComponentEvent ce){
		plotPanel.setPreferredSize(getPlotBounds());
	}
	public void componentMoved(ComponentEvent ce) {}
	public void componentShown(ComponentEvent ce) {}
	public void componentHidden(ComponentEvent ce) {}
	
	public void setPCCutOff(int pcCutOff){
		pcSlider.removeChangeListener(this);
		pcSlider.setValue(pcCutOff);
		plotPanel.setPCLimit(pcSlider.getValue());
		valueField.setText(new DecimalFormat("0.000E0").format(picds.getPCADataList().get(pcSlider.getValue()-1).getS()));
		pcSlider.addChangeListener(this);
	}
	
	private boolean goodPCCutOff(){
		boolean goodPCCutOff = true;
		try{
			int cutoffValue = Integer.valueOf(((JSpinner.DefaultEditor) pcSpinner.getEditor()).getTextField().getText());
			if(cutoffValue<1 ||  cutoffValue>picds.getPCADataList().size()){
				AttentionDialog.createDialog(frame, "Please enter an integer value between 1 and " + picds.getPCADataList().size() + ".");
				goodPCCutOff = false;
			}
		}catch(NumberFormatException nfe){
			AttentionDialog.createDialog(frame, "Please enter an integer value between 1 and " + picds.getPCADataList().size() + ".");
			goodPCCutOff = false;
		}
		return goodPCCutOff;
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==scaleBoxX || ae.getSource()==scaleBoxY){
			plotPanel.setPlotMode((ChartScaleType)scaleBoxX.getSelectedItem(), (ChartScaleType)scaleBoxY.getSelectedItem());
			plotPanel.setPCLimit(pcSlider.getValue());
		}
	}

	public void keyTyped(KeyEvent ke){}
	public void keyPressed(KeyEvent ke){}
	public void keyReleased(KeyEvent ke){
		if(ke.getKeyCode()==KeyEvent.VK_ENTER){
			if(!goodPCCutOff()){
				pcModel.setValue(pcSlider.getValue());
				valueField.setText(new DecimalFormat("0.000E0").format(picds.getPCADataList().get(pcSlider.getValue()-1).getS()));
				picds.setMaxComponentIndex(pcSlider.getValue()-1);
				plotPanel.setPCLimit(pcSlider.getValue());
				GeneratePCAImagesWorker worker = new GeneratePCAImagesWorker(gpil, picds, frame);
				worker.execute();
			}
		}
	}
	
	public void exportCurrentData() {
		
		String suffix = "_scree_plot_data";
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
		
		String xlsSheetName = "Scree Plot";
		Row row;
		Cell cell;
		int rowCounter=0;
		int cellCounter=0;
		
		Sheet sheet = wb.createSheet(xlsSheetName);
		
		row = sheet.createRow(rowCounter++);
		cell = row.createCell(cellCounter++);
	    cell.setCellValue("Data File Name");
	    cell = row.createCell(cellCounter++);
	    cell.setCellValue(d.getDataFile().getFullPath());
	    
	    row = sheet.createRow(rowCounter++);
	    cellCounter = 0;
	   
	    row = sheet.createRow(rowCounter++);
	    cellCounter = 0;
		cell = row.createCell(cellCounter++);
	    cell.setCellValue("X");
	    cell = row.createCell(cellCounter++);
	    cell.setCellValue("Y");
	    
	    row = sheet.createRow(rowCounter++);
	    cellCounter = 0;
		cell = row.createCell(cellCounter++);
	    cell.setCellValue("Principal Components");
	    cell = row.createCell(cellCounter++);
	    cell.setCellValue("Variance");
	    
	    int kmax = picds.getPCADataList().size();
	    double[] x = new double[kmax];
	    double[] y = new double[kmax];
	    
	    for(int i=1; i<=kmax; i++){
			x[i-1] = i;
			y[i-1] = picds.getPCADataList().get(i-1).getS();
		}
	    
	    for (int i =0; i<kmax; i++) {
		    row = sheet.createRow(rowCounter++);
		    cellCounter = 0;
			cell = row.createCell(cellCounter++);
		    cell.setCellValue((Double) x[i]);
		    cell = row.createCell(cellCounter++);
		    cell.setCellValue((Double) y[i]);
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
		
		String suffix = "_scree_plot";
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
				BufferedImage bi = new BufferedImage(plotPanel.getSize().width, plotPanel.getSize().height, BufferedImage.TYPE_INT_ARGB); 
				Graphics g = bi.createGraphics();
				plotPanel.paint(g);
				g.dispose();
				ImageIO.write(bi, "png", file);
				break;
		}
	}

}
