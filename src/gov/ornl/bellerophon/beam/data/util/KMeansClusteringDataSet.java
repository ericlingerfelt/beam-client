package gov.ornl.bellerophon.beam.data.util;

import java.io.File;
import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.HDF5DataType;
import hdf.object.FileFormat;
import hdf.object.h5.H5File;
import hdf.object.h5.H5ScalarDS;

public class KMeansClusteringDataSet implements Data {
	
	private int dataFileIndex;
	private String groupPath, imagePath;
	private double[][] valueArray;
	
	public KMeansClusteringDataSet(){
		initialize();
	}
	
	public KMeansClusteringDataSet clone(){
		KMeansClusteringDataSet pds = new KMeansClusteringDataSet();
		return pds;
	}
	
	public void initialize(){
		groupPath = "";
		dataFileIndex = -1;
		valueArray = null;
		imagePath = "";
	}
	
	public int getDataFileIndex(){return dataFileIndex;}
	public void setDataFileIndex(int dataFileIndex){this.dataFileIndex = dataFileIndex;}
	
	public String getGroupPath(){return groupPath;}
	public void setGroupPath(String groupPath){this.groupPath = groupPath;}

	public double[][] getValueArray(){return valueArray;}
	public void setValueArray(double[][] valueArray){this.valueArray = valueArray;}
	
	public String getImagePath(){return imagePath;}
	public void setImagePath(String imagePath){this.imagePath = imagePath;}
	
	public void populateResultsFromHDF5File(File file) {
		
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		H5File h5File = null;
		
		try{
	
			h5File = (H5File) fileFormat.createInstance(file.getPath(), FileFormat.READ);
			h5File.open();
			
			H5ScalarDS ds = (H5ScalarDS) h5File.get(groupPath + "/default-Plot_Group/Labels");
			ds.init();
			
			long[] dims = ds.getDims();
			int xDim = (int) dims[0];
			int yDim = (int) dims[1];
			
			int xCounter = 0;
			int yCounter = -1;
			
			HDF5DataType dataType = HDF5DataType.getHDF5DataType(ds);
			if(dataType==HDF5DataType.FLOAT){
				
				float[] dsArray = (float[]) ds.getData();
				valueArray = new double[xDim][yDim];

				for(int i=0; i<dsArray.length; i++){
					
					yCounter++;
					if(yCounter==yDim){
						yCounter = 0;
						xCounter++;
					}
					
					if(xCounter==xDim){
						xCounter = 0;
					}
					
					valueArray[xCounter][yCounter] = dsArray[i];

				}
			}else if(dataType==HDF5DataType.DOUBLE){
				
				double[] dsArray = (double[]) ds.getData();
				valueArray = new double[xDim][yDim];

				for(int i=0; i<dsArray.length; i++){
					
					yCounter++;
					if(yCounter==yDim){
						yCounter = 0;
						xCounter++;
					}
					
					if(xCounter==xDim){
						xCounter = 0;
					}
					
					valueArray[xCounter][yCounter] = dsArray[i];
					
				}
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
		try{
			if(h5File!=null){
				h5File.close();
			}
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
	}
	
}
