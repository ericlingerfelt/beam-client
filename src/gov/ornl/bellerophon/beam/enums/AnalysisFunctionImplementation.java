package gov.ornl.bellerophon.beam.enums;

public enum AnalysisFunctionImplementation {

	FORTRAN("Fortran"), 
	JAVA("Java"),
	PYTHON("Python"),
	MATLAB("Matlab"),
	R("R"),
	C("C"),
	CPP("C++");
	
	private String string;
	
	/**
	 * Instantiates a new Instrument.
	 *
	 * @param string the string
	 */
	AnalysisFunctionImplementation(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}	
	
}