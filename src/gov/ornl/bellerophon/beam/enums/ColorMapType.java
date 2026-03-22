package gov.ornl.bellerophon.beam.enums;

import org.jzy3d.colors.colormaps.*;

public enum ColorMapType {

	RAINBOW("Rainbow"),
	HOT_COLD("Hot and Cold"),
	RGB("RGB"),
	GRAYSCALE("Grayscale"),
	RED_AND_GREEN("Red and Green"),
	WHITE_AND_BLUE("White and Blue"), 
	WHITE_AND_GREEN("White and Green"),
	WHITE_AND_RED("White and Red");
	
	private String string;
	
	/**
	 * Instantiates a new Instrument.
	 *
	 * @param string the string
	 */
	ColorMapType(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}
	
	public IColorMap getColorMap(){
		IColorMap colorMap = null;
		switch(this){
			case GRAYSCALE:
				colorMap = new ColorMapGrayscale();
				break;
			case HOT_COLD:
				colorMap = new ColorMapHotCold();
				break;
			case RAINBOW:
				colorMap = new ColorMapRainbow();
				break;
			case RGB:
				colorMap = new ColorMapRBG();
				break;
			case RED_AND_GREEN:
				colorMap = new ColorMapRedAndGreen();
				break;
			case WHITE_AND_BLUE:
				colorMap = new ColorMapWhiteBlue();
				break;
			case WHITE_AND_GREEN:
				colorMap = new ColorMapWhiteGreen();
				break;
			case WHITE_AND_RED:
				colorMap = new ColorMapWhiteRed();
				break;
		}
		return colorMap;
		
	}
	
}
