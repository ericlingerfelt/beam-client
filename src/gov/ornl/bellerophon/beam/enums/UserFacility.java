package gov.ornl.bellerophon.beam.enums;

public enum UserFacility {

	OLCF("OLCF"), 
	NERSC("NERSC"), 
	CADES("CADES"), 
	ORNL_PHYSICS_DIVISION("ORNL Physics Division");
	
	private String string;
	
	/**
	 * Instantiates a new Instrument.
	 *
	 * @param string the string
	 */
	UserFacility(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}	
	
}
