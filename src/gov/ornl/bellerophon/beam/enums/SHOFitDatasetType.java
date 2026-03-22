package gov.ornl.bellerophon.beam.enums;

public enum SHOFitDatasetType{

	SHO_FIT_RESULTS ("SHO Fit Results"), 
	SHO_FIT_GUESS ("SHO Fit Guess");
	
	private String string;

	SHOFitDatasetType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}
	
}
