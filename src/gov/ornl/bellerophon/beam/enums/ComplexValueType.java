package gov.ornl.bellerophon.beam.enums;

public enum ComplexValueType {

	AMP ("Amplitude"),
	PHASE ("Phase"),
	REAL ("Real"), 
	IMAG ("Imaginary");
	
	private String string;

	ComplexValueType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}
	
}