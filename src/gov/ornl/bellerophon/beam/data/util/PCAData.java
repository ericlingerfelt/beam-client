package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;

import java.util.TreeMap;

public class PCAData implements Data {
	
	private double s;
	private double[][] uArray;
	private TreeMap<ComplexValueType, double[]> vPlotMap;
	private TreeMap<String, PCADataCell[][]> vCellMap; 
	
	public PCAData(){
		initialize();
	}
	
	public PCADataSet clone(){
		PCADataSet bfds = new PCADataSet();
		return bfds;
	}
	
	public void initialize(){
		s = 0.0;
		uArray = null;
		vPlotMap = null;
		vCellMap = null;
	}

	public double getS(){return s;}
	public void setS(double s){this.s = s;}

	public double[][] getUArray(){return uArray;}
	public void setUArray(double[][] uArray){this.uArray = uArray;}
	
	public TreeMap<ComplexValueType, double[]> getVPlotMap(){return vPlotMap;}
	public void setVPlotMap(TreeMap<ComplexValueType, double[]> vPlotMap){this.vPlotMap = vPlotMap;}
	
	public TreeMap<String, PCADataCell[][]> getVCellMap(){return vCellMap;}
	public void setVCellMap(TreeMap<String, PCADataCell[][]> vCellMap){this.vCellMap = vCellMap;}

	
}
