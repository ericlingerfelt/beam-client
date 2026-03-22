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
package gov.ornl.bellerophon.beam.ui.beanalyzer.meanspectrogram;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import gov.ornl.bellerophon.beam.data.util.MeanSpectrogramData;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;

import javax.swing.JPanel;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseController;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.*;
import org.jzy3d.events.IViewPointChangedListener;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.global.Settings;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axes.layout.providers.RegularTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;

public class BEAnalyzerMeanSpectrogramChartPanel extends JPanel implements IViewPointChangedListener {

	private Coord3d lastViewPoint;
	private String type;
	private Shape surface;
	private IColorMap colorMap;
	private ComplexValueType complexValueType;
	private MeanSpectrogramData msd;
	private Chart chart;
	
	public BEAnalyzerMeanSpectrogramChartPanel(){
		Settings.getInstance().setHardwareAccelerated(true);
		type = "swing";
		if(System.getProperty("os.name").contains("Linux")){
        	type = "awt";
        }
		colorMap = new ColorMapRainbow();
		drawChart();
		setOpaque(false);
	}
	
	public String getXTitle(){
		return "DC Step";
	}
	
	public String getYTitle(){
		return "Frequency (kHz)";
	}
	
	public String getZTitle(){
		return complexValueType.toString();
	}
	
	public int[] getXValueArray(){
		return msd.getXArray();
	}
	
	public double[] getYValueArray(){
		return msd.getYArray();
	}
	
	public double[][] getZValueArray(){
		return msd.getZArrayMap().get(complexValueType);
	}
	
	public BufferedImage getBufferedImage(){
		return chart.screenshot();
	}
	
	public void setColorMap(IColorMap colorMap){
		this.colorMap = colorMap;
		surface.setColorMapper(new ColorMapper(colorMap, surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .9f)));
		drawChart();
	}
	
	public void setCurrentData(MeanSpectrogramData msd, ComplexValueType complexValueType){
		
		this.msd = msd;
		this.complexValueType = complexValueType;
		
		Range xRange = new Range(msd.getXArray()[0], msd.getXArray()[msd.getXArray().length-1]);
		Range yRange = new Range(msd.getYArray()[0], msd.getYArray()[msd.getYArray().length-1]);
		int xSteps = msd.getXArray().length;
		int ySteps = msd.getYArray().length;
		OrthonormalGrid grid = new OrthonormalGrid(xRange, xSteps, yRange, ySteps);
		
		BEAnalyzerSpectrogramAverageChartMapper mapper = new BEAnalyzerSpectrogramAverageChartMapper(msd, complexValueType);
		surface = Builder.buildOrthonormal(grid, mapper);
		surface.setColorMapper(new ColorMapper(colorMap, surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .9f)));
		surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
        surface.setWireframeColor(Color.BLACK);
		
        drawChart();
        
	}
	
	public void drawChart(){
		
		if(surface!=null){
		
			chart = new Chart(Quality.Nicest, type);
	        chart.getView().addViewPointChangedListener(this);
	        
	        chart.getAxeLayout().setXAxeLabel("DC Step");
	        chart.getAxeLayout().setYAxeLabel("Frequency (kHz)");
	        chart.getAxeLayout().setZAxeLabel(complexValueType.toString());
	        chart.getAxeLayout().setZTickRenderer(new FixedDecimalTickRenderer(3));
	        
			new CameraMouseController(chart);
			chart.getScene().getGraph().add(surface);
	        if(lastViewPoint!=null){
	        	chart.setViewPoint(lastViewPoint);
	        }
	        ColorbarLegend colorbarLegend = new ColorbarLegend(surface, new RegularTickProvider(10), new FixedDecimalTickRenderer(3));
	        colorbarLegend.setMinimumSize(new Dimension(100, 400));
	        surface.setLegend(colorbarLegend);
	        
	        removeAll();
	        
	        double[] col = {10, TableLayoutConstants.FILL, 10};
			double[] row = {10, TableLayoutConstants.FILL, 10};
			setLayout(new TableLayout(col, row));
			add((Component) chart.getCanvas(), "1, 1, f, f");
		
		}
		
		validate();
		
	}

	public void viewPointChanged(ViewPointChangedEvent vpce){
		lastViewPoint = vpce.getViewPoint();
	}

}

class BEAnalyzerSpectrogramAverageChartMapper extends Mapper{

	private MeanSpectrogramData msd;
	private ComplexValueType complexValueType;
	
	public BEAnalyzerSpectrogramAverageChartMapper(MeanSpectrogramData msd, ComplexValueType complexValueType){
		this.msd = msd;
		this.complexValueType = complexValueType;
	}
	
	public double f(double x, double y) {

		int xIndex = -1;
		int yIndex = -1;
		
		for(int i=0; i<msd.getXArray().length; i++){
			if(msd.getXArray()[i]==x){
				xIndex = i;
				break;
			}
		}
		
		for(int i=0; i<msd.getYArray().length; i++){
			if(y>=(0.999*msd.getYArray()[i]) && y<=(1.001*msd.getYArray()[i])){
				yIndex = i;
				break;
			}
		}
		
		return msd.getZArrayMap().get(complexValueType)[xIndex][yIndex];
	}
	
}
