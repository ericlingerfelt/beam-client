package gov.ornl.bellerophon.beam.enums;

public enum AnalysisPlatformType {

	SUPERCOMPUTER("Supercomputer"),
	WEB_SERVER("Web Server"), 
	COMPUTE_CLUSTER("Compute Cluster"),
	COMPUTE_NODE("Compute Node");
	
	private String string;
	
	/**
	 * Instantiates a new Instrument.
	 *
	 * @param string the string
	 */
	AnalysisPlatformType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}	
	
}
