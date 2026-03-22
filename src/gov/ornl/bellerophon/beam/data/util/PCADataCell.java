package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;

import java.util.TreeMap;

public class PCADataCell implements Data {
	
	private TreeMap<ComplexValueType, Double> valueMap; 
	private double xValue, yValue;
	
	public PCADataCell(){
		initialize();
	}
	
	public PCADataSet clone(){
		PCADataSet bfds = new PCADataSet();
		return bfds;
	}
	
	public void initialize(){
		valueMap = null;
		xValue = 0.0;
		yValue = 0.0;
	}

	public TreeMap<ComplexValueType, Double> getValueMap(){return valueMap;}
	public void setValueMap(TreeMap<ComplexValueType, Double> valueMap){this.valueMap = valueMap;}
	
	public double getXValue(){return xValue;}
	public void setXValue(double xValue){this.xValue = xValue;}
	
	public double getYValue(){return yValue;}
	public void setYValue(double yValue){this.yValue = yValue;}
	
}
