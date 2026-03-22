package gov.ornl.bellerophon.beam.data.feature;

import gov.ornl.bellerophon.beam.data.util.DataFile;

public class MultivariateAnalyzerData {

	private DataFile dataFile;
	
	/**
	 * The Constructor.
	 */
	public MultivariateAnalyzerData(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public MultivariateAnalyzerData clone(){
		MultivariateAnalyzerData mvad = new MultivariateAnalyzerData();
		mvad.dataFile = dataFile;
		return mvad;
	}
	
	/* (non-Javadoc)
	 * @see org.bellerophon.data.Data#initialize()
	 */
	public void initialize(){
		dataFile = null;
	}
	
	public DataFile getDataFile(){return dataFile;}
	public void setDataFile(DataFile dataFile){this.dataFile = dataFile;}
	
}
