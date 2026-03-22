/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: BEAnalyzerSpectrogramAverageChartPanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.qensoptimizer;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import gov.ornl.bellerophon.beam.data.util.SNSData;

import javax.swing.JPanel;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseController;
import org.jzy3d.colors.Color;
import org.jzy3d.events.IViewPointChangedListener;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.global.Settings;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class QENSOptimizerChartPanel extends JPanel implements IViewPointChangedListener, ComponentListener{

	private Coord3d lastViewPoint;
	private String type;
	private ArrayList<Scatter> scatterListExp;
	private ArrayList<Shape> shapeListFit;
	private Color[] colorArray;
	private SNSData sd;
	private boolean displayFit = true;
	private boolean displayExp = true;
	
	public QENSOptimizerChartPanel(){
		scatterListExp = new ArrayList<Scatter>();
		shapeListFit = new ArrayList<Shape>();
		Settings.getInstance().setHardwareAccelerated(true);
		type = "swing";
		if(System.getProperty("os.name").contains("Linux")){
        	type = "awt";
        }
		setColorArray();
	}
	
	public void displayFit(boolean displayFit) {
		this.displayFit = displayFit;
		setSNSData(sd);
	}
	
	public void displayExp(boolean displayExp) {
		this.displayExp = displayExp;
		setSNSData(sd);
	}
	
	public void setSNSData(SNSData sd){
		
		this.sd = sd;
		
		scatterListExp.clear();
		
		if(displayExp){

			for(int i=0; i<sd.getSignalArrayExp().length; i++){
				Coord3d[] dataArray = new Coord3d[sd.getSignalArrayExp()[i].length];
				Color[] colorArrayTemp = new Color[sd.getSignalArrayExp()[i].length];
				for(int j=0; j<sd.getSignalArrayExp()[i].length; j++){
					Coord3d coord3d = new Coord3d(sd.getQArray()[i], sd.getEnergyArrayExp()[j], sd.getSignalArrayExp()[i][j]);
					dataArray[j] = coord3d;
					colorArrayTemp[j] = colorArray[i];
				}
				Scatter scatter = new Scatter(dataArray, colorArrayTemp, 2.0f);
				scatter.setWidth(3);
				scatterListExp.add(scatter);
			}
		
		}

		shapeListFit.clear();
		
		if(displayFit){
			
			for(int i=0; i<sd.getSignalArrayFit().length; i++){
				ArrayList<Coord3d> dataList = new ArrayList<Coord3d>();
				for(int j=0; j<sd.getSignalArrayFit()[i].length; j++){
					Coord3d coord3d = new Coord3d(sd.getQArray()[i], sd.getEnergyArrayFit()[j], sd.getSignalArrayFit()[i][j]);
					dataList.add(coord3d);
					coord3d = new Coord3d(sd.getQArray()[i]+0.015, sd.getEnergyArrayFit()[j], sd.getSignalArrayFit()[i][j]);
					dataList.add(coord3d);
				}
				
				Shape shape = Builder.buildDelaunay(dataList);
				shape.setDisplayed(true);
				shape.setFaceDisplayed(true);
				shape.setWireframeDisplayed(false);
				shape.setColor(Color.BLACK);
				shapeListFit.add(shape);
				
			}
			
		}
		
        drawChart();
        
	}
	
	private void drawChart(){
	
		Chart chart = new Chart(Quality.Advanced, type);
        chart.getView().addViewPointChangedListener(this);
        chart.getAxeLayout().setXAxeLabel("Q");
        chart.getAxeLayout().setYAxeLabel("E");
        chart.getAxeLayout().setZAxeLabel("log10(S)");
		new CameraMouseController(chart);
		for(Scatter scatter: scatterListExp){
			chart.getScene().getGraph().add(scatter);
		}
		for(Shape shape: shapeListFit){
			chart.getScene().getGraph().add(shape);
		}
        if(lastViewPoint!=null){
        	chart.setViewPoint(lastViewPoint);
        }

        removeAll();
        
        double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		add((Component) chart.getCanvas(), "1, 1, f, f");
		
		validate();
		
	}
	
	private void setColorArray(){
		
        colorArray = new Color[9];
        colorArray[0] = new Color(255,153,0);
        colorArray[1] = Color.BLUE;
        colorArray[2] = Color.RED;
        colorArray[3] = Color.MAGENTA;
        colorArray[4] = Color.GRAY;
        colorArray[5] = new Color(0,220,0);
        colorArray[6] = Color.YELLOW;
        colorArray[7] = Color.CYAN;
        colorArray[8] = Color.GREEN;
 
	}
	
	public void viewPointChanged(ViewPointChangedEvent vpce){
		lastViewPoint = vpce.getViewPoint();
	}

	public void componentResized(ComponentEvent ce) {drawChart();}
	public void componentMoved(ComponentEvent ce) {}
	public void componentShown(ComponentEvent ce) {}
	public void componentHidden(ComponentEvent ce) {}

}
