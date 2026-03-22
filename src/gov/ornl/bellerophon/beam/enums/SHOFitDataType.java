package gov.ornl.bellerophon.beam.enums;

import java.text.DecimalFormat;

public enum SHOFitDataType {
	
	A ("A", new DecimalFormat("0.000E0"), new DecimalFormat("#####0.000")), 
	W ("W (kHz)", new DecimalFormat("#####0.0"), new DecimalFormat("#####0.000")),
	Q ("Q", new DecimalFormat("#####0.0"), new DecimalFormat("#####0.000")), 
	P ("Phi (rad)", new DecimalFormat("#####0.000"), new DecimalFormat("#####0.000"));
	
	private String string;
	private DecimalFormat dflin, dflog;

	SHOFitDataType(String string, DecimalFormat dflin, DecimalFormat dflog){
		this.string = string;
		this.dflin = dflin;
		this.dflog = dflog;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}
	public DecimalFormat getDecimalFormatLin(){return dflin;}
	public DecimalFormat getDecimalFormatLog(){return dflog;}
	
}
