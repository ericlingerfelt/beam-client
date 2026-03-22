package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.SHOFitDataType;

public class SHOFitData implements Data{

	private double a, w, q, p;
	
	public SHOFitData(){
		initialize();
	}
	
	public SHOFitData clone(){
		SHOFitData bfd = new SHOFitData();
		return bfd;
	}
	
	public void initialize(){
		 a = 0.0;
		 w = 0.0;
		 q = 0.0;
		 p = 0.0;
	}
	
	public double getA(){return a;}
	public void setA(double a){this.a = a;}
	
	public double getW(){return w;}
	public void setW(double w){this.w = w;}
	
	public double getQ(){return q;}
	public void setQ(double q){this.q = q;}
	
	public double getP(){return p;}
	public void setP(double p){this.p = p;}
	
	public double getValue(SHOFitDataType type){
		switch(type){
		case A:
			return a;
		case W:
			return w;
		case Q:
			return q;
		case P:
			return p;
		}
		return 0;
	}
	
	public String toString(){
		return a + " " + w + " " + q + " " + p;
	}

}
