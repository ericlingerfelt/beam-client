/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: SpectrogramAverageDataSet.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import hdf.object.Attribute;
import hdf.object.FileFormat;
import hdf.object.h5.H5CompoundDS;
import hdf.object.h5.H5File;
import hdf.object.h5.H5Group;
import hdf.object.h5.H5ScalarDS;
import gov.ornl.bellerophon.beam.data.Data;

public class MeanSpectrogramDataSet implements Data{

	private TreeMap<String, MeanSpectrogramData> dataMap;
	private int dataFileIndex;

	public MeanSpectrogramDataSet(){
		initialize();
	}
	
	public MeanSpectrogramDataSet clone(){
		MeanSpectrogramDataSet msds = new MeanSpectrogramDataSet();
		return msds;
	}
	
	public void initialize(){
		dataFileIndex = -1;
		dataMap = new TreeMap<String, MeanSpectrogramData>();
	}
	
	public TreeMap<String, MeanSpectrogramData> getDataMap(){
		return dataMap;
	}
	
	public int getDataFileIndex(){return dataFileIndex;}
	public void setDataFileIndex(int dataFileIndex){this.dataFileIndex = dataFileIndex;}
	
	public void populateFromHDF5File(File file){
		
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

		try{

			H5File h5File = (H5File) fileFormat.createInstance(file.getPath(), FileFormat.READ);
			h5File.open();
			
			DecimalFormat df = new DecimalFormat("000");
			
			int mCounter = 0;
			String mGroupPath = "/Measurement_" + df.format(mCounter);
			while(h5File.get(mGroupPath) != null){
				
				int cCounter = 0;
				String cGroupPath = mGroupPath + "/Channel_" + df.format(cCounter);
				
				while(h5File.get(cGroupPath) != null){
					
					int pCounter = 0;
					String pGroupPath = cGroupPath + "/Spatially_Averaged_Plot_Group_" + df.format(pCounter);
					while(h5File.get(pGroupPath) != null){
						H5Group pGroup = (H5Group) h5File.get(pGroupPath);
						MeanSpectrogramData msd = new MeanSpectrogramData();
						msd.populateFromH5Datasets((H5CompoundDS) h5File.get(pGroupPath + "/default-Plot_Group/Mean_Spectrogram"), (H5ScalarDS) h5File.get(pGroupPath + "/Bin_Frequencies"));
						String pGroupName = "";
						List<Attribute> metadataList = pGroup.getMetadata();
						Iterator<Attribute> itr = metadataList.iterator();
						while(itr.hasNext()){
							Attribute a = itr.next();
							if(a.getName().equals("Name")){
								pGroupName = ((String[]) a.getValue())[0];
							}
						}
						dataMap.put(cGroupPath + "/" + pGroupName, msd); 
				
						pGroupPath = cGroupPath + "/Spatially_Averaged_Plot_Group_" + df.format(++pCounter);
					}		
					
					cGroupPath = mGroupPath + "/Channel_" + df.format(++cCounter);
				}
				
				mGroupPath = "/Measurement_" + df.format(++mCounter);
			}

			h5File.close();

		}catch(Exception e){
			e.printStackTrace();
		}		
		
	}
	
}
