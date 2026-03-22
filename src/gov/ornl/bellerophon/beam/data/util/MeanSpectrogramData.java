/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: SpectrogramAverageData.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.BEDataType;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;
import gov.ornl.bellerophon.beam.enums.HDF5DataType;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import hdf.object.h5.H5CompoundDS;
import hdf.object.h5.H5ScalarDS;

public class MeanSpectrogramData implements Data{
	
	private double[] yArray;
	private int[] xArray;
	private TreeMap<ComplexValueType, double[][]> zArrayMap;
	private ArrayList<Integer> excludeList;
	private BEDataType type;
	
	public MeanSpectrogramData(){
		initialize();
	}
	
	public MeanSpectrogramData clone(){
		MeanSpectrogramData msd = new MeanSpectrogramData();
		return msd;
	}
	
	public void initialize(){
		xArray = null;
		yArray = null;
		zArrayMap = new TreeMap<ComplexValueType, double[][]>();
		excludeList = new ArrayList<Integer>();
		type = BEDataType.BEPS;
	}
	
	public void populateFromH5Datasets(H5CompoundDS meanSpectrogram, H5ScalarDS binFrequencies){
		
		try{

			meanSpectrogram.init();
			
			long[] dims = meanSpectrogram.getDims();
			int xDim = (int)dims[0];
			int yDim = (int)dims[1];

			if(xDim==1){
				
				type = BEDataType.BE_LINE;
				double[][] valueArrayReal = new double[xDim][yDim];
				double[][] valueArrayImag = new double[xDim][yDim];
				double[][] valueArrayAmp = new double[xDim][yDim];
				double[][] valueArrayPhase = new double[xDim][yDim];
				
				Vector dsVector = (Vector) meanSpectrogram.getData();
				float[] dsArrayReal = (float[])dsVector.get(0);
				float[] dsArrayImag = (float[])dsVector.get(1);
				
				for(int i=0; i<yDim; i++){
					valueArrayReal[0][i] = dsArrayReal[i];
				}
					
				for(int i=0; i<yDim; i++){
					valueArrayImag[0][i] = dsArrayImag[i];
				}
				
				for(int i=0; i<valueArrayReal.length; i++){
					for(int j=0; j<valueArrayReal[i].length; j++){
						double realValue = valueArrayReal[i][j];
						double imagValue = valueArrayImag[i][j];
						valueArrayAmp[i][j] = Math.sqrt(Math.pow(realValue, 2) + Math.pow(imagValue, 2));
						valueArrayPhase[i][j] = Math.atan2(imagValue, realValue);
					}
				}
				
				zArrayMap.put(ComplexValueType.REAL, valueArrayReal);
				zArrayMap.put(ComplexValueType.IMAG, valueArrayImag);
				zArrayMap.put(ComplexValueType.AMP, valueArrayAmp);
				zArrayMap.put(ComplexValueType.PHASE, valueArrayPhase);
				
			}else{
				
				type = BEDataType.BEPS;
				double[][] valueArrayReal = new double[xDim][yDim];
				double[][] valueArrayImag = new double[xDim][yDim];
				double[][] valueArrayAmp = new double[xDim][yDim];
				double[][] valueArrayPhase = new double[xDim][yDim];
				
				Vector dsVector = (Vector) meanSpectrogram.getData();
				float[] dsArrayReal = (float[])dsVector.get(0);
				float[] dsArrayImag = (float[])dsVector.get(1);
				
				for(int i=0; i<xDim; i++){
					for(int j=0; j<yDim; j++){
						valueArrayReal[i][j] = dsArrayReal[i*yDim+j];
					}
				}
				for(int i=0; i<xDim; i++){
					for(int j=0; j<yDim; j++){
						valueArrayImag[i][j] = dsArrayImag[i*yDim+j];
					}
				}
				
				for(int i=0; i<valueArrayReal.length; i++){
					for(int j=0; j<valueArrayReal[i].length; j++){
						double realValue = valueArrayReal[i][j];
						double imagValue = valueArrayImag[i][j];
						valueArrayAmp[i][j] = Math.sqrt(Math.pow(realValue, 2) + Math.pow(imagValue, 2));
						valueArrayPhase[i][j] = Math.atan2(imagValue, realValue);
					}
				}
				
				xArray = new int[valueArrayReal.length];
				for(int i=0; i<xArray.length; i++){
					xArray[i] = i;
				}
				
				zArrayMap.put(ComplexValueType.REAL, valueArrayReal);
				zArrayMap.put(ComplexValueType.IMAG, valueArrayImag);
				zArrayMap.put(ComplexValueType.AMP, valueArrayAmp);
				zArrayMap.put(ComplexValueType.PHASE, valueArrayPhase);
			}

			binFrequencies.init();

			HDF5DataType dataType = HDF5DataType.getHDF5DataType(binFrequencies);
			if(dataType==HDF5DataType.FLOAT){
				float[] yArrayHz = (float[]) binFrequencies.getData();
				yArray = new double[yArrayHz.length];
				for(int i=0; i<yArrayHz.length; i++){
					yArray[i] = yArrayHz[i]/1000.0;
				}
			}else if(dataType==HDF5DataType.DOUBLE){
				double[] yArrayHz = (double[]) binFrequencies.getData();
				yArray = new double[yArrayHz.length];
				for(int i=0; i<yArrayHz.length; i++){
					yArray[i] = yArrayHz[i]/1000.0;
				}
			}

		}catch(OutOfMemoryError e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public int[] getXArray(){return xArray;}
	public void setXArray(int[] xArray){this.xArray = xArray;}
	
	public double[] getYArray(){return yArray;}
	public void setYArray(double[] yArray){this.yArray = yArray;}
	
	public BEDataType getType(){return type;}
	public void setType(BEDataType type){this.type = type;}
	
	public TreeMap<ComplexValueType, double[][]> getZArrayMap(){return zArrayMap;}
	public void setZArrayMap(TreeMap<ComplexValueType, double[][]> zArrayMap){this.zArrayMap = zArrayMap;}
	
	public ArrayList<Integer> getExcludeList(){return excludeList;}
	public void setExcludeList(ArrayList<Integer> excludeList){this.excludeList = excludeList;}
	
}
