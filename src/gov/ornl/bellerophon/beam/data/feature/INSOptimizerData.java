package gov.ornl.bellerophon.beam.data.feature;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.data.util.SNSDataSet;

public class INSOptimizerData implements Data{
	
	private SNSDataSet snsDataSet;
	
	/**
	 * The Constructor.
	 */
	public INSOptimizerData(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public INSOptimizerData clone(){
		INSOptimizerData sdd = new INSOptimizerData();
		return sdd;
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.data.Data#initialize()
	 */
	public void initialize(){
		snsDataSet = new SNSDataSet();
	}
	
	public SNSDataSet getSNSDataSet(){return snsDataSet;}
	public void setSNSDataSet(SNSDataSet snsDataSet){this.snsDataSet = snsDataSet;}
	
}
