package gov.ornl.bellerophon.beam.ui.insoptimizer;

import gov.ornl.bellerophon.beam.data.util.CustomFile;
import gov.ornl.bellerophon.beam.io.IOUtilities;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.format.Buttons;
import gov.ornl.bellerophon.beam.ui.format.Colors;
import gov.ornl.bellerophon.beam.ui.util.FramePanel;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.*;

public class INSOptimizerPanel extends JPanel implements ActionListener{
	
	private FramePanel expPanel, theoryPanel;
	private JScrollPane expPane, theoryPane;
	private JButton button;
	private JPanel mainPanel;
	private JLabel colorScaleLabel, sliceLabel;
	private JComboBox<String> colorScaleBox, sliceBox;
	
	public INSOptimizerPanel(Frame frame){

		button = Buttons.getIconButton("Execute INS Optimization Workflow"
										, "icons/system-run.png"
										, Buttons.IconPosition.RIGHT
										, Colors.BLUE
										, this
										, new Dimension(300, 50)
										, 12);
		
		colorScaleBox = new JComboBox<String>();
		colorScaleBox.addItem("BRG");
		colorScaleBox.addItem("CubeHelix");
		colorScaleBox.addItem("Gray");
		colorScaleBox.addItem("HSV");
		colorScaleBox.addItem("Jet");
		colorScaleBox.addItem("Spectral");
		colorScaleBox.addItem("Ocean");
		colorScaleBox.addItem("Paired");
		colorScaleBox.addItem("PRG");
		colorScaleBox.addItem("Seismic");
		colorScaleBox.setSelectedIndex(0);
		colorScaleBox.addActionListener(this);
		
		colorScaleLabel = new JLabel("Colormap");
		
		sliceBox = new JComboBox<String>();
		sliceBox.addItem("Si_000_220");
		sliceBox.addItem("Si_100_400");
		sliceBox.setSelectedIndex(0);
		sliceBox.addActionListener(this);
		
		sliceLabel = new JLabel("Data Slice");
		
		expPanel = new FramePanel();
		theoryPanel = new FramePanel();
		
		expPane = new JScrollPane(expPanel);
		theoryPane = new JScrollPane(theoryPanel);
		
		expPanel.setScrollPane(expPane);
		theoryPanel.setScrollPane(theoryPane);
		
		JPanel buttonPanel = new JPanel();
		double[] columnButton = {10, TableLayoutConstants.PREFERRED, 70, 
									TableLayoutConstants.PREFERRED, 10,
									TableLayoutConstants.FILL, 70,
									TableLayoutConstants.PREFERRED, 10,
									TableLayoutConstants.FILL, 10};
		double[] rowButton = {10, TableLayoutConstants.PREFERRED, 10, 10};
		buttonPanel.setLayout(new TableLayout(columnButton, rowButton));
		buttonPanel.add(button, 			"1, 1, l, c");
		buttonPanel.add(colorScaleLabel, 	"3, 1, r, c");
		buttonPanel.add(colorScaleBox, 		"5, 1, f, c");
		buttonPanel.add(sliceLabel, 		"7, 1, r, c");
		buttonPanel.add(sliceBox, 			"9, 1, f, c");
		
		mainPanel = new JPanel();
		mainPanel.setBorder(Borders.getBorder("INS Optimizer Data Viewer"));
		
		double[] colMain = {10, TableLayoutConstants.FILL, 10
								, TableLayoutConstants.FILL, 10};
		double[] rowMain = {10,	TableLayoutConstants.FILL, 5, 
								TableLayoutConstants.PREFERRED, 10};
		mainPanel.setLayout(new TableLayout(colMain, rowMain));
		mainPanel.add(expPane, 		"1, 1, f, f");
		mainPanel.add(theoryPane, 	"3, 1, f, f");
		mainPanel.add(buttonPanel,  "1, 3, 3, 3, f, c");
		
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		add(mainPanel, "1, 1, f, f");

	}
	
	public void setCurrentState(){
		setCurrentState(colorScaleBox.getSelectedItem().toString(), sliceBox.getSelectedItem().toString());
	}
	
	private void setCurrentState(String colorScale, String slice){
			
		try {
			
			byte[] bytesExp = IOUtilities.readURL("https://nucastrodata2.ornl.gov/snsdata2/" + colorScale + "/" + slice + "_DFT.png");
			gov.ornl.bellerophon.beam.data.util.CustomFile expFile = new CustomFile();
			expFile.setImg(true);
			expFile.setContents(bytesExp);
			expPanel.setFile(expFile);
			expPanel.setFitToWindow(true);
			
			byte[] bytesTheory = IOUtilities.readURL("https://nucastrodata2.ornl.gov/snsdata2/" + colorScale + "/" + slice + "_Opt.png");
			gov.ornl.bellerophon.beam.data.util.CustomFile theoryFile = new CustomFile();
			theoryFile.setImg(true);
			theoryFile.setContents(bytesTheory);
			theoryPanel.setFile(theoryFile);
			theoryPanel.setFitToWindow(true);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource()==colorScaleBox || ae.getSource()==sliceBox){
			setCurrentState(colorScaleBox.getSelectedItem().toString(), sliceBox.getSelectedItem().toString());
		}
	}
}
