package gov.ornl.bellerophon.beam.data.util;

import java.awt.Frame;
import java.io.File;
import java.util.TreeMap;

import gov.ornl.bellerophon.beam.enums.HDF5DataType;
import gov.ornl.bellerophon.beam.enums.PCAImageType;
import gov.ornl.bellerophon.beam.fft.FFT;
import gov.ornl.bellerophon.beam.io.IOUtilities;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import hdf.object.FileFormat;
import hdf.object.h5.H5File;
import hdf.object.h5.H5ScalarDS;

public class PCAImageCleaningDataSet extends PCADataSet {
	
	private int xDim, yDim, windowSize;
	private String imagePath;
	private double[][][] imageArray;
	private TreeMap<PCAImageType, PCAImage> imageMap;
	
	public PCAImageCleaningDataSet(){
		initialize();
	}
	
	public PCAImageCleaningDataSet clone(){
		PCAImageCleaningDataSet pds = new PCAImageCleaningDataSet();
		return pds;
	}
	
	public void initialize(){
		super.initialize();
		windowSize = 0;
		xDim = 0;
		yDim = 0;
		imagePath = "";
		imageMap = null;
	}
	
	public int getWindowSize(){return windowSize;}
	public void setWindowSize(int windowSize){this.windowSize = windowSize;}
	
	public String getImagePath(){return imagePath;}
	public void setImagePath(String imagePath){this.imagePath = imagePath;}
	
	public int getXDim(){return xDim;}
	public int getYDim(){return yDim;}
	
	public PCAImage getImage(PCAImageType type){return imageMap.get(type);}
	
	public void generateImages(){
		imageMap = new TreeMap<PCAImageType, PCAImage>();
		imageMap.put(PCAImageType.CLEAN, createCleanedImage());
		imageMap.put(PCAImageType.NOISE, createNoiseImage());
		imageMap.put(PCAImageType.CLEAN_FFT, createImageFFT(imageMap.get(PCAImageType.CLEAN)));
		imageMap.put(PCAImageType.NOISE_FFT, createImageFFT(imageMap.get(PCAImageType.NOISE)));
	}
	
	private PCAImage createCleanedImage(){
		
		int width = imageArray[0].length;
		int height = imageArray[0][0].length;
		double[][] valueArray = new double[width][height];
		
		for(int i=0; i<=maxComponentIndex; i++){
			for(int j=0; j<width; j++){
				for(int k=0; k<height; k++){
					valueArray[j][k] += imageArray[i][j][k];
				}
			}	
		}

		PCAImage image = new PCAImage();
		image.setValueArray(valueArray);
		return image;
	}
	
	private PCAImage createNoiseImage(){
		
		int width = imageArray[0].length;
		int height = imageArray[0][0].length;
		double[][] valueArray = new double[width][height];
		
		for(int i=maxComponentIndex+1; i<imageArray.length; i++){
			for(int j=0; j<width; j++){
				for(int k=0; k<height; k++){
					valueArray[j][k] += imageArray[i][j][k];
				}
			}	
		}

		PCAImage image = new PCAImage();
		image.setValueArray(valueArray);
		return image;
	}
	
	private PCAImage createImageFFT(PCAImage inputImage){
		
		double[][] tempArray = inputImage.getValueArray();
		
		double[][] valueArray = new double[tempArray.length][2*tempArray[0].length];
		for(int i=0; i<tempArray.length; i++){
			System.arraycopy(tempArray[i], 0, valueArray[i], 0, tempArray[i].length);
		}
		
		FFT.perform2DFFT(valueArray, "FORWARD_FULL", "FFT_MODULUS");
		
		double[][] valueArray2 = new double[tempArray.length][tempArray[0].length];
		for(int i=0; i<valueArray2.length; i++){
			System.arraycopy(valueArray[i], 0, valueArray2[i], 0, valueArray2[i].length);
		}
		
		valueArray2 = FFT.fftshift(valueArray2, false, false);
		
		PCAImage image = new PCAImage();
		image.setValueArray(valueArray2);
		return image;
		
	}
	
	public void populateDimsFromTextFile(File file) {
		try{
			String contents = new String(IOUtilities.readFile(file));
			String[] array = contents.split("\n");
			for(String line: array){
				String[] lineArray = line.split("=");
				String key = lineArray[0].trim();
				String value = lineArray[1].trim();
				if(key.equals("DIM_X")){
					xDim = Integer.valueOf(value);
				}else if(key.equals("DIM_Y")){
					yDim = Integer.valueOf(value);
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void populateResultsFromHDF5File(File file) {
		
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		H5File h5File = null;
		
		try{
			
			h5File = (H5File) fileFormat.createInstance(file.getPath(), FileFormat.READ);
			h5File.open();
			
			H5ScalarDS ds = (H5ScalarDS) h5File.get("Measurement_000/Channel_000/Raw_Data-Windowing_000/Image_Windows-PCA_000/"
													+ "PCA-Cleaned_Image_000/default-Plot_Group/Cleaned_Image");
			ds.init();

			long[] dims = ds.getDims();
			int xDim = (int) dims[0];
			int yDim = (int) dims[1];
			int pcDim = (int) dims[2];
			
			imageArray = new double[pcDim][xDim][yDim];
			
			long[] selectedDims = ds.getSelectedDims();
			selectedDims[0] = xDim;
			selectedDims[1] = yDim;
			selectedDims[2] = pcDim;
			
			int pcCounter = -1;
			int xCounter = 0;
			int yCounter = 0;
			
			HDF5DataType dataType = HDF5DataType.getHDF5DataType(ds);
			if(dataType==HDF5DataType.FLOAT){
				
				float[] array = (float[]) ds.getData();
				for(int i=0; i<array.length; i++){
					
					pcCounter++;
					if(pcCounter==pcDim){
						pcCounter = 0;
						yCounter++;
					}
					
					if(yCounter==yDim){
						yCounter = 0;
						xCounter++;
					}
					
					if(xCounter==xDim){
						xCounter = 0;
					}
					
					double value = array[i];
					imageArray[pcCounter][yCounter][xCounter] = value;
					
				}
				
			}else if(dataType==HDF5DataType.DOUBLE){
				
				double[] array = (double[]) ds.getData();
				for(int i=0; i<array.length; i++){
					
					pcCounter++;
					if(pcCounter==pcDim){
						pcCounter = 0;
						yCounter++;
					}
					
					if(yCounter==yDim){
						yCounter = 0;
						xCounter++;
					}
					
					if(xCounter==xDim){
						xCounter = 0;
					}
					
					double value = array[i];
					imageArray[pcCounter][yCounter][xCounter] = value;
					
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
	
	public void populateUVFromHDF5File(File file) {
		
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		H5File h5File = null;
		
		try{
	
			h5File = (H5File) fileFormat.createInstance(file.getPath(), FileFormat.READ);
			h5File.open();
			
			String dataFileType = getDataFileType(h5File);
			
			if(dataFileType.equals("")){
				AttentionDialog.createDialog(new Frame(), "This data file does not have a data_type attribute.");
				return;
			}
			
			//////////U//////////////////////////////////////////////////////////////////
			H5ScalarDS uDS = (H5ScalarDS) h5File.get(groupPath + "/default-Plot_Group/U");
			uDS.init();
			
			long[] dims = uDS.getDims();
			int xDim = (int) dims[0];
			int yDim = (int) dims[1];
			int pcDim = (int) dims[2];
			
			long[] selectedDims = uDS.getSelectedDims();
			selectedDims[0] = xDim;
			selectedDims[1] = yDim;
			selectedDims[2] = 1;
			
			int xCounter = 0;
			int yCounter = -1;
				
			for(int pcCounter = 0; pcCounter<pcDim; pcCounter++){
				
				long[] startDims = uDS.getStartDims();
				startDims[2] = pcCounter;
				
				HDF5DataType dataType = HDF5DataType.getHDF5DataType(uDS);
				if(dataType==HDF5DataType.FLOAT){
					
					float[] uDSArray = (float[]) uDS.getData();
					double[][] uArray = new double[xDim][yDim];
					pcaDataList.get(pcCounter).setUArray(uArray);
					
					for(int i=0; i<uDSArray.length; i++){
						
						yCounter++;
						if(yCounter==yDim){
							yCounter = 0;
							xCounter++;
						}
						
						if(xCounter==xDim){
							xCounter = 0;
						}
						
						uArray[xCounter][yCounter] = uDSArray[i];
						
					}
				}else if(dataType==HDF5DataType.DOUBLE){
					double[] uDSArray = (double[]) uDS.getData();
					double[][] uArray = new double[xDim][yDim];
					pcaDataList.get(pcCounter).setUArray(uArray);
					
					for(int i=0; i<uDSArray.length; i++){
						
						yCounter++;
						if(yCounter==yDim){
							yCounter = 0;
							xCounter++;
						}
						
						if(xCounter==xDim){
							xCounter = 0;
						}
						
						uArray[xCounter][yCounter] = uDSArray[i];
						
					}
				}
				
				uDS.clear();
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