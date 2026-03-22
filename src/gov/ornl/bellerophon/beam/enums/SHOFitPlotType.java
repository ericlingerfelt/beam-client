package gov.ornl.bellerophon.beam.enums;

public enum SHOFitPlotType{

	FIT_PLOT ("Fitted Data"), 
	LOOP_PLOT ("Hysteresis Loops");
	
	private String string;

	SHOFitPlotType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}
	
}
