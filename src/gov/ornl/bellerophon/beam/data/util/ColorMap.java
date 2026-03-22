package gov.ornl.bellerophon.beam.data.util;

import java.awt.Color;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.ColorMapType;

public class ColorMap implements Data{

	private double xR, xG, xB, aR, aG, aB;
	
	public ColorMap(){
		initialize();
	}
	
	public ColorMap(double xR, double xG, double xB, double aR, double aG, double aB){
		this.xR = xR;
		this.xG = xG;
		this.xB = xB;
		this.aR = aR;
		this.aG = aG;
		this.aB = aB;
	}
	
	public ColorMap clone(){
		ColorMap bfd = new ColorMap();
		return bfd;
	}
	
	public void initialize(){
		xR = 0.0;
		xG = 0.0;
		xB = 0.0;
		aR = 0.0;
		aG = 0.0;
		aB = 0.0;
	}
	
	public void setColorMapType(ColorMapType colorMapType){
		switch(colorMapType){
			case GRAYSCALE:
				xR = 1.0;
				xG = 1.0;
				xB = 1.0;
				aR = 0.5;
				aG = 0.5;
				aB = 0.5;
				break;
			case HOT_COLD:
				xR = 1.0;
				xG = 0.0;
				xB = 0.0;
				aR = 0.5;
				aG = 0.0;
				aB = 0.5;
				break;
			case RAINBOW:
				xR = 0.8;
				xG = 0.6;
				xB = 0.2;
				aR = 0.5;
				aG = 0.4;
				aB = 0.3; 
				break;
			case RGB:
				xR = 0.87;
				xG = 0.02;
				xB = 0.41;
				aR = 0.37;
				aG = 0.44;
				aB = 0.55;
				break;
			case RED_AND_GREEN:
				xR = 1.0;
				xG = 0.0;
				xB = 0.0;
				aR = 0.5;
				aG = 0.5;
				aB = 0.0;
				break;
			case WHITE_AND_BLUE:
				xR = 1.0;
				xG = 1.0;
				xB = 0.5;
				aR = 1.0;
				aG = 1.0;
				aB = 1.0;
				break;
			case WHITE_AND_GREEN:
				xR = 1.0;
				xG = 0.7;
				xB = 1.0;
				aR = 1.0;
				aG = 1.0;
				aB = 1.0;
				break;
			case WHITE_AND_RED:
				xR = 0.7;
				xG = 1.0;
				xB = 1.0;
				aR = 1.0;
				aG = 1.0;
				aB = 1.0;
				break;
		}
	}
	
	public Color getRGB(double x){
		if(x>=1.0){x = 1.0;}
		if(x<=0.0){x = 0.0;}
		int red = (int)(255*Math.exp(-(x-xR)*(x-xR)/aR/aR));
	    int green = (int)(255*Math.exp(-(x-xG)*(x-xG)/aG/aG));
	    int blue = (int)(255*Math.exp(-(x-xB)*(x-xB)/aB/aB));
	    return new Color(red,green,blue);
	}
}
