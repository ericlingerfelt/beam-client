package gov.ornl.bellerophon.beam.enums;

public enum ChartScaleType {

	LIN("Lin"),
	LOG("Log");
	
	private String string;
	
	/**
	 * Instantiates a new Instrument.
	 *
	 * @param string the string
	 */
	ChartScaleType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}	
	
}
