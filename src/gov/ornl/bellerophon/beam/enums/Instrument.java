/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: Instrument.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.enums;

public enum Instrument {

	CYPHER_NORTH("Cypher North"), 
	CYPHER_SOUTH("Cypher South"),
	CYPHER_EAST("Cypher East"),
	CYPHER_WEST("Cypher West"),
	UWAVE_MFP3D("Uwave MFP3D"),
	LIQUID_MFP3D("Liquid MFP3D"),
	NANOTRANSPORT("Nanotransport"),
	OMNICRON_PERSEI("Omicron Persei"),
	AFM_RAMAN_NTMDT("AFM-Raman NTMDT"),
	ICON("Icon"),
	VEECO_NANOMAN("Veeco Nanoman"),
	TITAN_STEM("Titan STEM"),
	NION_STEM("Nion STEM"),
	G_STEM("G-STEM"),
	UNKNOWN("Unknown");
	
	private String string;
	
	/**
	 * Instantiates a new Instrument.
	 *
	 * @param string the string
	 */
	Instrument(String string){
		this.string = string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}
	
	public static Instrument getInstrument(String string){
		for(Instrument i: Instrument.values()){
			if(i.toString().equals(string)){
				return i;
			}
		}
		return null;
	}
	
}
