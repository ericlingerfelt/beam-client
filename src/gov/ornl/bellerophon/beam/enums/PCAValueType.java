package gov.ornl.bellerophon.beam.enums;

public enum PCAValueType {

	FULL ("Complex Data"),
	AMPLITUDE ("Absolute Value"),
	PHASE ("Phase Component"),
	REAL ("Real Component"), 
	IMAGINARY ("Imag Component");
	
	private String string;

	PCAValueType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	
	public String toString(){return string;}
	
}