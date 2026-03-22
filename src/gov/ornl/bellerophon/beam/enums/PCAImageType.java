package gov.ornl.bellerophon.beam.enums;

public enum PCAImageType {
	
	CLEAN ("Cleaned Image"),
	CLEAN_FFT ("FFT Cleaned Image"),
	NOISE ("Image Noise"),
	NOISE_FFT ("FFT Image Noise");
	
	private String string;

	PCAImageType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	
	public String toString(){return string;}
	
}