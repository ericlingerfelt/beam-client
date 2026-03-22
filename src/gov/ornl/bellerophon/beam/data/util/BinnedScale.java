package gov.ornl.bellerophon.beam.data.util;

import java.util.ArrayList;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.ChartScaleType;

public class BinnedScale implements Data{

	private int binMin, binMax, numBins;
	private ArrayList<Double> binListLin = new ArrayList<Double>();
	private ArrayList<Double> binListLog = new ArrayList<Double>();
	private ChartScaleType chartScaleType;
	
	public BinnedScale(){
		initialize();
	}
	
	public BinnedScale(double minValue, double maxValue, int numBins){
		
		this.numBins = numBins;
		
		binMin = 0;
		binMax = numBins;
		chartScaleType = ChartScaleType.LIN;

		double increment = (maxValue-minValue)/numBins;
		double binValue = minValue;
		binListLin.add(binValue);
		binValue+=increment;
		for(int i=0; i<numBins; i++){
			binListLin.add(binValue);
			binValue+=increment;
		}
		
		increment = (Math.log10(maxValue)-Math.log10(minValue))/numBins;
		binValue = Math.log10(minValue);
		binListLog.add(binValue);
		binValue+=increment;
		for(int i=0; i<numBins; i++){
			binListLog.add(binValue);
			binValue+=increment;
		}
		
	}
	
	public BinnedScale clone(){
		return null;
	}
	
	public void initialize(){
		binMin = -1;
		binMax = -1;
		numBins = -1;
		binListLin = new ArrayList<Double>();
		binListLog = new ArrayList<Double>();
		chartScaleType = null;
	}
	
	public ChartScaleType getChartScaleType(){return chartScaleType;}
	public void setChartScaleType(ChartScaleType chartScaleType){this.chartScaleType = chartScaleType;}
	
	public int getBinMin(){return binMin;}
	public void setBinMin(int binMin){this.binMin = binMin;}
	
	public int getBinMax(){return binMax;}
	public void setBinMax(int binMax){this.binMax = binMax;}
	
	public int getNumBins(){return numBins;}
	public void setNumBins(int numBins){this.numBins = numBins;}
	
	public static ArrayList<Double> generateBinList(double min, double max, int numBins, ChartScaleType scaleType){
		ArrayList<Double> binList = new ArrayList<Double>();
		if(scaleType==ChartScaleType.LIN){
			double increment = (max-min)/numBins;
			double binValue = min;
			binList.add(binValue);
			binValue+=increment;
			for(int i=0; i<numBins; i++){
				binList.add(binValue);
				binValue+=increment;
			}
		}else if(scaleType==ChartScaleType.LOG){
			double increment = (Math.log10(max)-Math.log10(min))/numBins;
			double binValue = Math.log10(min);
			binList.add(binValue);
			binValue+=increment;
			for(int i=0; i<numBins; i++){
				binList.add(binValue);
				binValue+=increment;
			}
		}
		return binList;
	}
	
	public int getBinIndexFromValue(double value){
		int binIndex = -1;
		if(chartScaleType==ChartScaleType.LIN){
			for(int i=binMin; i<binMax; i++){
				double lowValue = binListLin.get(i);
				double highValue = binListLin.get(i+1);
				
				if(value>=0){
					lowValue = 0.999*lowValue;
					highValue = 1.001*highValue;
				}else{
					lowValue = 1.001*lowValue;
					highValue = 0.999*highValue;
				}
				
				if(value>lowValue && value<highValue){
					binIndex = i;
					break;
				}
			}
		}else if(chartScaleType==ChartScaleType.LOG){
			for(int i=binMin; i<binMax; i++){
				double lowValue = binListLog.get(i);
				double highValue = binListLog.get(i+1);
				if(Math.log10(value)>(0.999999*lowValue) && Math.log10(value)<(1.000001*highValue)){
					binIndex = i;
					break;
				}
			}
		}
		return binIndex;
	}
	
	public ArrayList<Double> getBinList(){
		switch(chartScaleType){
			case LIN:
				return binListLin;
			case LOG:
				return binListLog;
		}
		return null;
	}
	
	public ArrayList<Double> getBinList(ChartScaleType chartScaleType){
		switch(chartScaleType){
			case LIN:
				return binListLin;
			case LOG:
				return binListLog;
		}
		return null;
	}
	
	public ArrayList<Double> getLimitedBinList(ChartScaleType chartScaleType){
		switch(chartScaleType){
			case LIN:
				return new ArrayList<Double>(binListLin.subList(binMin, binMax+1));
			case LOG:
				return new ArrayList<Double>(binListLog.subList(binMin, binMax+1));
		}
		return null;
	}
	
}