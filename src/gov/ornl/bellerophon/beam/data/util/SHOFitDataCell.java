package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.commons.math3.complex.Complex;

public class SHOFitDataCell implements Data{

	private ArrayList<SHOFitData> shoFitDataList;
	private ArrayList<TreeMap<ComplexValueType, double[]>> mainDataList;
	private ArrayList<TreeMap<ComplexValueType, double[]>> fitDataList;
	private GridPoint gridPoint;
	private ArrayList<Double> wList;
	
	public SHOFitDataCell(){
		initialize();
	}
	
	public SHOFitDataCell clone(){
		SHOFitDataCell sfdc = new SHOFitDataCell();
		return sfdc;
	}
	
	public void initialize(){
		shoFitDataList = new ArrayList<SHOFitData>();
		mainDataList = new ArrayList<TreeMap<ComplexValueType, double[]>>();
		fitDataList = new ArrayList<TreeMap<ComplexValueType, double[]>>();
		gridPoint = null;
		wList = null;
	}
	
	public void populateFitDataList(){
		
		if(fitDataList.size()==0){
			
			for(SHOFitData sfd: shoFitDataList){
				
				TreeMap<ComplexValueType, double[]> map = new TreeMap<ComplexValueType, double[]>();
				fitDataList.add(map);
				
				int wListSize = wList.size();
				
				double[] tempArrayReal = new double[wListSize];
				double[] tempArrayImag = new double[wListSize];
				double[] tempArrayAmp = new double[wListSize];
				double[] tempArrayPhase = new double[wListSize];
				
				map.put(ComplexValueType.REAL, 		tempArrayReal);
				map.put(ComplexValueType.IMAG, 		tempArrayImag);
				map.put(ComplexValueType.AMP,   	tempArrayAmp);
				map.put(ComplexValueType.PHASE, 	tempArrayPhase);
				
				for(int i=0; i<wListSize; i++){
					double freq = wList.get(i);
					Complex complexValue = getComplexParamValue(freq, sfd);
					tempArrayReal[i] = complexValue.getReal();
					tempArrayImag[i] = complexValue.getImaginary();
					tempArrayAmp[i] = Math.sqrt(Math.pow(tempArrayReal[i], 2) + Math.pow(tempArrayImag[i], 2));
					tempArrayPhase[i] = Math.atan2(tempArrayImag[i], tempArrayReal[i]);
				}

			}
		
		}
		
	}
	
	private Complex getComplexParamValue(double freq, SHOFitData sfd){
		
		double a = sfd.getA();
		double w = sfd.getW();
		double q = sfd.getQ();
		double p = sfd.getP();
		
		Complex complexTopValue = Complex.I.multiply(p).exp().multiply(a*Math.pow(w, 2));
		Complex complexBottomValue = Complex.I.multiply(w*freq).divide(q).multiply(-1).subtract(Math.pow(w, 2)).add(Math.pow(freq, 2));
		return complexTopValue.divide(complexBottomValue);
	}
	
	public double[] getFitDataArray(int index, ComplexValueType type){
		return fitDataList.get(index).get(type);
	}
	public double[] getMainDataArray(int index, ComplexValueType type){
		if(mainDataList.size()==0){
			return null;
		}
		return mainDataList.get(index).get(type);
	}
	
	public void setMainDataList(ArrayList<TreeMap<ComplexValueType, double[]>> mainDataList){
		this.mainDataList = mainDataList;
	}
	
	public ArrayList<SHOFitData> getSHOFitDataList(){return shoFitDataList;}
	public void setSHOFitDataList(ArrayList<SHOFitData> shoFitDataList){this.shoFitDataList = shoFitDataList;}
	
	public GridPoint getGridPoint(){return gridPoint;}
	public void setGridPoint(GridPoint gridPoint){this.gridPoint = gridPoint;}
	
	public ArrayList<Double> getWList(){return wList;}
	public double[] getWListInKHz(){
		double[] array = new double[wList.size()];
		for(int i=0; i<wList.size(); i++){
			array[i] = wList.get(i) / 1000.0;
		}
		return array;
	}
	public void setWList(ArrayList<Double> wList){this.wList = wList;}
	
}
