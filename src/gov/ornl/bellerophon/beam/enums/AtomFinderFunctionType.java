package gov.ornl.bellerophon.beam.enums;

public enum AtomFinderFunctionType {

	FILTER_IMAGE("Filter Image")
	, FIND_ATOMS("Find Atoms")
	, ANALYZE_IMAGES("Analyze Images");
	
	private String string;
	
	/**
	 * Instantiates a new Instrument.
	 *
	 * @param string the string
	 */
	AtomFinderFunctionType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}	
	
}
