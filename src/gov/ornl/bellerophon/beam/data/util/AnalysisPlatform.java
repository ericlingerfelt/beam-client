package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.AnalysisPlatformType;
import gov.ornl.bellerophon.beam.enums.UserFacility;

public class AnalysisPlatform implements Data{
	
	private String name;
	private UserFacility userFacility;
	private AnalysisPlatformType analysisPlatformType;
	private int numNodesMax, numCoresPerNode;
	
	public AnalysisPlatform(){
		initialize();
	}
	
	public AnalysisPlatform clone(){
		AnalysisPlatform ap = new AnalysisPlatform();
		return ap;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof AnalysisPlatform)){
			return false;
		}
		AnalysisPlatform ap = (AnalysisPlatform)o;
		return ap.name==name;
	}
	
	public void initialize(){
		name = "";
		userFacility = null;
		analysisPlatformType = null;
		numNodesMax = -1;
		numCoresPerNode = -1;
	}
	
	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
	public AnalysisPlatformType getAnalysisPlatformType(){return analysisPlatformType;}
	public void setAnalysisPlatformType(AnalysisPlatformType analysisPlatformType){this.analysisPlatformType = analysisPlatformType;}
	
	public UserFacility getUserFacility(){return userFacility;}
	public void setUserFacility(UserFacility userFacility){this.userFacility = userFacility;}
	
	public int getNumNodesMax(){return numNodesMax;}
	public void setNumNodesMax(int numNodesMax){this.numNodesMax = numNodesMax;}
	
	public int getNumCoresPerNode(){return numCoresPerNode;}
	public void setNumCoresPerNode(int numCoresPerNode){this.numCoresPerNode = numCoresPerNode;}
	
}