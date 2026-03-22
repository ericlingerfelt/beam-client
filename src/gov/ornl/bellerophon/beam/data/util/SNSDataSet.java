package gov.ornl.bellerophon.beam.data.util;

import java.util.TreeMap;
import gov.ornl.bellerophon.beam.data.Data;

public class SNSDataSet implements Data{
	
	private TreeMap<Double, SNSData> dataMap;
	
	/**
	 * The Constructor.
	 */
	public SNSDataSet(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public SNSDataSet clone(){
		SNSDataSet sds = new SNSDataSet();
		return sds;
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.data.Data#initialize()
	 */
	public void initialize(){
		dataMap = new TreeMap<Double, SNSData>();
	}
	
	public TreeMap<Double, SNSData> getDataMap(){return dataMap;}
}
	