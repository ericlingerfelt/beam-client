package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.UserFacility;

public class Allocation implements Data{
	
	private int index;
	private String name;
	private UserFacility userFacility;
	
	public Allocation(){
		initialize();
	}
	
	public AnalysisFunction clone(){
		AnalysisFunction af = new AnalysisFunction();
		return af;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Allocation)){
			return false;
		}
		Allocation a = (Allocation)o;
		return a.index==index;
	}
	
	public void initialize(){
		index = -1;
		name = "";
		userFacility = null;
	}
	
	public int getIndex(){return index;}
	public void setIndex(int index){this.index = index;}
	
	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
	public UserFacility getUserFacility(){return userFacility;}
	public void setUserFacility(UserFacility userFacility){this.userFacility = userFacility;}
	
}