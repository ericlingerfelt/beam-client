package gov.ornl.bellerophon.beam.enums;

public enum AnalysisFunctionType {

	SHO_FIT("SHO Fit"), 
	SNS_SIMULATOR("SNS Simulator"),
	PCA("PCA"),
	PCA_IMAGE_CLEANING("PCA Image Cleaning"),
	FAST_PCA("Fast PCA"),
	FAST_PCA_IMAGE_CLEANING("Fast PCA Image Cleaning"),
	KMEANS_CLUSTERING("K-Means Clustering");
	
	private String string;
	
	/**
	 * Instantiates a new Instrument.
	 *
	 * @param string the string
	 */
	AnalysisFunctionType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}	
	
}
