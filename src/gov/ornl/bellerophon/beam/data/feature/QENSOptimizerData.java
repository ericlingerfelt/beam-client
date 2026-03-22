package gov.ornl.bellerophon.beam.data.feature;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.data.util.SNSDataSet;

public class QENSOptimizerData implements Data{
	
	private SNSDataSet snsDataSet;
	
	/**
	 * The Constructor.
	 */
	public QENSOptimizerData(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public QENSOptimizerData clone(){
		QENSOptimizerData sdd = new QENSOptimizerData();
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
