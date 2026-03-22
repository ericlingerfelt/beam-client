package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;

public class PCAImage implements Data {
	
	private double[][] valueArray;
	
	public PCAImage(){
		initialize();
	}
	
	public PCAImage clone(){
		PCAImage bfds = new PCAImage();
		return bfds;
	}
	
	public void initialize(){
		valueArray = null;
	}
	
	public double[][] getValueArray(){return valueArray;}
	public void setValueArray(double[][] valueArray){
		this.valueArray = valueArray;
	}
	
}