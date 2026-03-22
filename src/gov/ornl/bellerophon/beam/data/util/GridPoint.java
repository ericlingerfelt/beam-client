/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: Size.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;

public class GridPoint implements Data, Comparable<GridPoint>{

	private int positionIndex;
	private int x, y;
	
	public GridPoint clone(){
		return null;
	}
	
	public GridPoint(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public GridPoint(){
		initialize();
	}
	
	public void initialize(){
		x = 0;
		y = 0;
		positionIndex = 0;
	}
	
	public int getPositionIndex(){return positionIndex;}
	
	public void setPositionIndex(int width){
		this.positionIndex = (y-1)*width + (x-1);
	}
	
	public int getX(){return x;}	
	public void setX(int x){this.x = x;}
	
	public int getY(){return y;}	
	public void setY(int y){this.y = y;}

	public int compareTo(GridPoint s){
		if(x!=s.x){
			return x-s.x;
		}
		return y-s.y;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object){
		if(object instanceof GridPoint){
			GridPoint s = (GridPoint)object;
			if(s.x==x && s.y==y){
				return true;
			}
			return false;
		}
		return false;
	}
	
	public String toString(){
		return x + " " + y;
	}
	
}
