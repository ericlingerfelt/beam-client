package gov.ornl.bellerophon.beam.enums;

public enum SHOFitLoopPlotType{

	A ("A"), 
	P ("Phi (rad)"),
	A_SIN_P ("A*sin(Phi)"), 
	A_COS_P ("A*cos(Phi)");
	
	private String string;

	SHOFitLoopPlotType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}
	
}
