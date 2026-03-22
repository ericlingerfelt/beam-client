package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.AnalysisFunctionImplementation;
import gov.ornl.bellerophon.beam.enums.AnalysisFunctionType;

public class AnalysisFunction implements Data{
	
	private int index;
	private AnalysisFunctionType analysisFunctionType;
	private AnalysisPlatform analysisPlatform;
	private AnalysisFunctionImplementation analysisFunctionImplementation;
	
	public AnalysisFunction(){
		initialize();
	}
	
	public AnalysisFunction clone(){
		AnalysisFunction af = new AnalysisFunction();
		return af;
	}
	
	public String toString(){
		return analysisFunctionType.toString();
	}
	
	public boolean equals(Object o){
		if(!(o instanceof AnalysisFunction)){
			return false;
		}
		AnalysisFunction af = (AnalysisFunction)o;
		return af.index==index;
	}
	
	public void initialize(){
		index = -1;
		analysisFunctionType = null;
		analysisPlatform = null;
		analysisFunctionImplementation = null;
	}
	
	public int getIndex(){return index;}
	public void setIndex(int index){this.index = index;}
	
	public AnalysisFunctionType getAnalysisFunctionType(){return analysisFunctionType;}
	public void setAnalysisFunctionType(AnalysisFunctionType analysisFunctionType){this.analysisFunctionType = analysisFunctionType;}
	
	public AnalysisPlatform getAnalysisPlatform(){return analysisPlatform;}
	public void setAnalysisPlatform(AnalysisPlatform analysisPlatform){this.analysisPlatform = analysisPlatform;}
	
	public AnalysisFunctionImplementation getAnalysisFunctionImplementation(){return analysisFunctionImplementation;}
	public void setAnalysisFunctionImplementation(AnalysisFunctionImplementation analysisFunctionImplementation){this.analysisFunctionImplementation = analysisFunctionImplementation;}
	
	
}