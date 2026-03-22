/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: BeamFrame.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui;

import info.clearthought.layout.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.enums.*;
import gov.ornl.bellerophon.beam.enums.Action;
import gov.ornl.bellerophon.beam.io.*;
import gov.ornl.bellerophon.beam.ui.beanalyzer.BEAnalyzerPanel;
import gov.ornl.bellerophon.beam.ui.datamanager.DataManagerPanel;
import gov.ornl.bellerophon.beam.ui.dialog.*;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.format.*;
import gov.ornl.bellerophon.beam.ui.imageprocessor.ImageProcessorPanel;
import gov.ornl.bellerophon.beam.ui.insoptimizer.INSOptimizerPanel;
import gov.ornl.bellerophon.beam.ui.multivariateanalyzer.MultivariateAnalyzerPanel;
import gov.ornl.bellerophon.beam.ui.qensoptimizer.QENSOptimizerPanel;
import gov.ornl.bellerophon.beam.ui.util.FullScreenModeListener;

/**
 * The Class BeamFrame.
 *
 * @author Eric J. Lingerfelt
 */
public class BeamFrame extends JFrame implements ActionListener, FullScreenModeListener{

	private FeatureButton logoutButton, beAnalyzerButton, atomFinderButton
							, dataManagerButton, imageProcessorButton, multivariateAnalyzerButton
							, qensOptimizerButton, insOptimizerButton, ginsSimulatorButton, accountManagerButton;
	private IntroPanel introPanel;
	private Container c;
	private JPanel toolPanel, buttonPanel;
	private BEAnalyzerPanel beAnalyzerPanel = new BEAnalyzerPanel(this);
	private ImageProcessorPanel imageProcessorPanel = new ImageProcessorPanel(this);
	private MultivariateAnalyzerPanel multivariateAnalyzerPanel = new MultivariateAnalyzerPanel(this);
	private DataManagerPanel dataManagerPanel = new DataManagerPanel(this);
	private QENSOptimizerPanel qensOptimizerPanel = new QENSOptimizerPanel(this);
	private INSOptimizerPanel insOptimizerPanel = new INSOptimizerPanel(this);
	private JButton windowButton, dataButton, imageButton, fullButton;
	private boolean fullScreenMode;
	private int windowState = Frame.NORMAL;
	private FullScreenModeListener currentFullScreenModeListener;
	private ImageExporter currentImageExporter;
	private DataExporter currentDataExporter;
	private int windowIndex;
	
	/**
	 * Instantiates a new bellerophon frame.
	 */
	public BeamFrame(){
		
		setSize(1450, 900);
		setTitle("BEAM");
		setLocationRelativeTo(null);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				if(MainData.isDownloading() && MainData.getNumberOfBeamWindows()==1){
					String string = "One or more downloads are currently active. Log out and quit anyway?";
					int returnValue = CautionDialog.createCautionDialog(BeamFrame.this, string, "Attention!");
					if(returnValue==CautionDialog.YES){
						exit();
					}
					return;
				}
				if(MainData.getNumberOfBeamWindows()==1){
					String string = "You have selected to log out and exit BEAM. Are you sure?";
					int returnValue = CautionDialog.createCautionDialog(BeamFrame.this, string, "Attention!");
					if(returnValue==CautionDialog.YES){
						exit();
					}
					return;
				}else{
					MainData.removeBeamWindow(BeamFrame.this);
				}
			} 
		});
		
		double[] col = {TableLayoutConstants.FILL};
		double[] row = {TableLayoutConstants.PREFERRED
							, 75, TableLayoutConstants.FILL};
		
		c = getContentPane();
		c.setLayout(new TableLayout(col, row));
		
		windowButton = Buttons.getIconButton("Open New Window"
						, "icons/window-new.png"
						, Buttons.IconPosition.RIGHT
						, Colors.BLUE
						, this
						, new Dimension(200, 50));
		
		dataButton = Buttons.getIconButton("Save Plot Data"
						, "icons/media-floppy.png"
						, Buttons.IconPosition.RIGHT
						, Colors.BLUE
						, this
						, new Dimension(200, 50));
		
		imageButton = Buttons.getIconButton("Save Plot Image"
						, "icons/media-floppy.png"
						, Buttons.IconPosition.RIGHT
						, Colors.BLUE
						, this
						, new Dimension(200, 50));
		
		fullButton = Buttons.getIconButton("Enter Full Screen Mode"
						, "icons/view-fullscreen.png"
						, Buttons.IconPosition.RIGHT
						, Colors.BLUE
						, this
						, new Dimension(200, 50));
		
		buttonPanel = new JPanel();
		double[] columnButton = {10,TableLayoutConstants.FILL
									, 10, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.FILL
									, 10, TableLayoutConstants.FILL, 10};
		double[] rowButton = {TableLayoutConstants.PREFERRED, 10};
		buttonPanel.setLayout(new TableLayout(columnButton, rowButton));
		JButton tmp = new JButton();
		tmp.setVisible(false);
		JButton tmp2 = new JButton();
		tmp2.setVisible(false);
		buttonPanel.add(windowButton,  	"1, 0, f, c");
		buttonPanel.add(tmp,  			"3, 0, f, c");
		buttonPanel.add(tmp2,  			"5, 0, f, c");
		buttonPanel.add(dataButton,    	"7, 0, f, c");
		buttonPanel.add(imageButton,   	"9, 0, f, c");
		buttonPanel.add(fullButton, 	"11, 0, f, c");
		
		double[] colTool = {10, TableLayoutConstants.FILL
								, 10, TableLayoutConstants.FILL
								, 10, TableLayoutConstants.FILL
								, 10, TableLayoutConstants.FILL
								, 10, TableLayoutConstants.FILL
								, 10, TableLayoutConstants.FILL
								, 10, TableLayoutConstants.FILL, 10};
		double[] rowTool = {10, TableLayoutConstants.PREFERRED};
		
		toolPanel = new JPanel(new TableLayout(colTool, rowTool));
		
		beAnalyzerButton = new FeatureButton(Feature.BE_ANALYZER);
		beAnalyzerButton.addActionListener(this);
		beAnalyzerButton.setPreferredSize(new Dimension(1000, 50));
		
		atomFinderButton = new FeatureButton(Feature.ATOM_FINDER);
		atomFinderButton.addActionListener(this);
		atomFinderButton.setPreferredSize(new Dimension(1000, 50));
		
		imageProcessorButton = new FeatureButton(Feature.IMAGE_PROCESSOR);
		imageProcessorButton.addActionListener(this);
		imageProcessorButton.setPreferredSize(new Dimension(1000, 50));
		
		dataManagerButton = new FeatureButton(Feature.DATA_MANAGER);
		dataManagerButton.addActionListener(this);
		dataManagerButton.setPreferredSize(new Dimension(1000, 50));
		
		accountManagerButton = new FeatureButton(Feature.ACCOUNT_MANAGER);
		accountManagerButton.addActionListener(this);
		accountManagerButton.setPreferredSize(new Dimension(1000, 50));
		
		multivariateAnalyzerButton = new FeatureButton(Feature.MULTIVARIATE_ANALYZER);
		multivariateAnalyzerButton.addActionListener(this);
		multivariateAnalyzerButton.setPreferredSize(new Dimension(1000, 50));
		
		qensOptimizerButton = new FeatureButton(Feature.QENS_OPTIMIZER);
		qensOptimizerButton.addActionListener(this);
		qensOptimizerButton.setPreferredSize(new Dimension(1000, 50));
		
		insOptimizerButton = new FeatureButton(Feature.INS_OPTIMIZER);
		insOptimizerButton.addActionListener(this);
		insOptimizerButton.setPreferredSize(new Dimension(1000, 50));
		
		ginsSimulatorButton = new FeatureButton(Feature.GINS_SIMULATOR);
		ginsSimulatorButton.addActionListener(this);
		ginsSimulatorButton.setPreferredSize(new Dimension(1000, 50));
		
		logoutButton = new FeatureButton(Feature.LOGOUT);
		logoutButton.addActionListener(this);
		logoutButton.setPreferredSize(new Dimension(1000, 50));
		
		if(MainData.doSNSDemo){
			
			JButton tempButton1 = new JButton();
			tempButton1.setPreferredSize(new Dimension(1000, 50));
			
			JButton tempButton2 = new JButton();
			tempButton2.setPreferredSize(new Dimension(1000, 50));
			
			JButton tempButton3 = new JButton();
			tempButton3.setPreferredSize(new Dimension(1000, 50));
			
			toolPanel.add(qensOptimizerButton, 		"1, 1, f, c");
			toolPanel.add(insOptimizerButton, 		"3, 1, f, c");
			toolPanel.add(ginsSimulatorButton, 		"5, 1, f, c");
			toolPanel.add(tempButton3, 				"7, 1, f, c");
			toolPanel.add(dataManagerButton, 		"9, 1, f, c");
			toolPanel.add(accountManagerButton, 	"11, 1, f, c");
			toolPanel.add(logoutButton, 			"13, 1, f, c");
			
		}else{
			
			toolPanel.add(beAnalyzerButton, 			"1, 1, f, c");
			toolPanel.add(multivariateAnalyzerButton, 	"3, 1, f, c");
			toolPanel.add(imageProcessorButton, 		"5, 1, f, c");
			toolPanel.add(atomFinderButton, 			"7, 1, f, c");
			toolPanel.add(dataManagerButton, 			"9, 1, f, c");
			toolPanel.add(accountManagerButton, 		"11, 1, f, c");
			toolPanel.add(logoutButton, 				"13, 1, f, c");
			
		}

		introPanel = new IntroPanel();
		
		c.add(toolPanel, "0, 0, f, f");
		c.add(introPanel, "0, 2, c, t");
		
	}
	
	public void setWindowIndex(int windowIndex){
		this.windowIndex = windowIndex;
	}
	
	public int getWindowIndex(){
		return windowIndex;
	}
	
	/**
	 * Sets the selected button.
	 *
	 * @param button the new selected button
	 */
	public void setSelectedButton(JButton button){
		beAnalyzerButton.setForeground(Colors.frontColor);
		atomFinderButton.setForeground(Colors.frontColor);
		imageProcessorButton.setForeground(Colors.frontColor);
		dataManagerButton.setForeground(Colors.frontColor);
		accountManagerButton.setForeground(Colors.frontColor);
		multivariateAnalyzerButton.setForeground(Colors.frontColor);
		qensOptimizerButton.setForeground(Colors.frontColor);
		ginsSimulatorButton.setForeground(Colors.frontColor);
		insOptimizerButton.setForeground(Colors.frontColor);
		logoutButton.setForeground(Colors.frontColor);
		button.setForeground(Colors.RED);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae){
		
		if(ae.getSource() instanceof FeatureButton){
			FeatureButton button = (FeatureButton)ae.getSource();
			setSelectedButton(button);
			setTitle("BEAM | " + button.getFeature());
		}
		
		if(ae.getSource()==beAnalyzerButton){
			currentFullScreenModeListener = beAnalyzerPanel;
			currentImageExporter = beAnalyzerPanel;
			currentDataExporter = beAnalyzerPanel;
			beAnalyzerPanel.setCurrentState();
			addPanelWithButtons(beAnalyzerPanel);
		}else if(ae.getSource()==atomFinderButton){
			MessageDialog.createMessageDialog(this, "The Atom Finder software feature is currently under development", "Attention!");
		}else if(ae.getSource()==imageProcessorButton){
			currentFullScreenModeListener = imageProcessorPanel;
			currentImageExporter = imageProcessorPanel;
			currentDataExporter = imageProcessorPanel;
			imageProcessorPanel.setCurrentState();
			addPanelWithButtons(imageProcessorPanel);
		}else if(ae.getSource()==multivariateAnalyzerButton){
			currentFullScreenModeListener = multivariateAnalyzerPanel;
			currentImageExporter = multivariateAnalyzerPanel;
			currentDataExporter = multivariateAnalyzerPanel;
			multivariateAnalyzerPanel.setCurrentState();
			addPanelWithButtons(multivariateAnalyzerPanel);
		}else if(ae.getSource()==accountManagerButton){
			MessageDialog.createMessageDialog(this, "The My Account software feature is currently under development", "Attention!");
		}else if(ae.getSource()==dataManagerButton){
			if(MainData.doSNSDemo){
				MessageDialog.createMessageDialog(this, "The Data Manager software feature for neutron data science is currently under development", "Attention!");
			}else{
				currentFullScreenModeListener = null;
				currentImageExporter = null;
				currentDataExporter = null;
				dataManagerPanel.setCurrentState();
				addPanel(dataManagerPanel);
			}
		}else if(ae.getSource()==qensOptimizerButton){
			currentFullScreenModeListener = null;
			currentImageExporter = null;
			currentDataExporter = null;
			qensOptimizerPanel.setCurrentState();
			addPanelWithButtons(qensOptimizerPanel);
		}else if(ae.getSource()==insOptimizerButton){
			currentFullScreenModeListener = null;
			currentImageExporter = null;
			currentDataExporter = null;
			insOptimizerPanel.setCurrentState();
			addPanelWithButtons(insOptimizerPanel);
		}else if(ae.getSource()==ginsSimulatorButton){
			MessageDialog.createMessageDialog(this, "The GINS Simulator software feature is currently under development", "Attention!");
		}else if(ae.getSource()==logoutButton){
			if(MainData.isDownloading()){
				String string = "One or more downloads are currently active. Log out and quit anyway?";
				int returnValue = CautionDialog.createCautionDialog(BeamFrame.this, string, "Attention!");
				if(returnValue==CautionDialog.YES){
					exit();
				}
			}
			if(MainData.getNumberOfBeamWindows()>1){
				String string = "You currently have multiple BEAM windows open. Log out and quit anyway?";
				int returnValue = CautionDialog.createCautionDialog(BeamFrame.this, string, "Attention!");
				if(returnValue==CautionDialog.YES){
					exit();
				}
			}else{
				String string = "You have selected to log out and exit BEAM. Are you sure?";
				int returnValue = CautionDialog.createCautionDialog(BeamFrame.this, string, "Attention!");
				if(returnValue==CautionDialog.YES){
					exit();
				}
			}
		}else if(ae.getSource()==fullButton){
			if(!fullScreenMode){
				enterFullScreenMode();
			}else{
				exitFullScreenMode();
			}
		}else if(ae.getSource()==imageButton){
			currentImageExporter.exportCurrentImage();
		}else if(ae.getSource()==dataButton){
			currentDataExporter.exportCurrentData();
		}else if(ae.getSource()==windowButton){
			BeamFrame frame = new BeamFrame();
			MainData.addBeamWindow(frame);
			frame.setVisible(true);
		}
	}
	
	/**
	 * Exit.
	 */
	public void exit(){
		setVisible(false);
		Thread.setDefaultUncaughtExceptionHandler(null);
		WebServiceCom.getInstance().doWebServiceComCall(null, Action.LOGOUT);
		System.exit(0);
	}

	public void addPanelWithButtons(JPanel panel){
		double[] col = {TableLayoutConstants.FILL};
		double[] row = {TableLayoutConstants.PREFERRED
							, 0, TableLayoutConstants.FILL
							, 0, TableLayoutConstants.PREFERRED};
		c.setLayout(new TableLayout(col, row));
		c.removeAll();
		c.add(toolPanel, 	"0, 0, f, c");
		c.add(panel, 		"0, 2, f, f");
		c.add(buttonPanel, 	"0, 4, f, c");
		validate();
		repaint();
	}
	
	/**
	 * Adds the panel.
	 *
	 * @param panel the panel
	 */
	public void addPanel(JPanel panel){
		double[] col = {TableLayoutConstants.FILL};
		double[] row = {TableLayoutConstants.PREFERRED
							, 0, TableLayoutConstants.FILL};
		c.setLayout(new TableLayout(col, row));
		c.removeAll();
		c.add(toolPanel, "0, 0, f, c");
		c.add(panel, "0, 2, f, f");
		validate();
		repaint();
	}

	public void enterFullScreenMode(){
		fullButton.setIcon(Icons.createImageIcon("/resources/images/icons/view-restore.png"));
		fullButton.setText("Exit Full Screen Mode");
		fullScreenMode = true;
		windowState = getExtendedState();
		setExtendedState(Frame.MAXIMIZED_BOTH);
		if(currentFullScreenModeListener!=null){
			currentFullScreenModeListener.enterFullScreenMode();
		}
	}

	public void exitFullScreenMode(){
		fullButton.setIcon(Icons.createImageIcon("/resources/images/icons/view-fullscreen.png"));
		fullButton.setText("Enter Full Screen Mode");
		fullScreenMode = false;
		setExtendedState(windowState);
		if(currentFullScreenModeListener!=null){
			currentFullScreenModeListener.exitFullScreenMode();
		}
	}

}

class FeatureButton extends JButton{
	
	private Feature feature;
	
	public FeatureButton(Feature feature){
		super(feature.getHTMLString());
		this.feature = feature;
	}
	
	public Feature getFeature(){return feature;}
}