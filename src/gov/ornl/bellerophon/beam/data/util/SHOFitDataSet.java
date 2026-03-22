package gov.ornl.bellerophon.beam.data.util;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;
import gov.ornl.bellerophon.beam.enums.HDF5DataType;
import gov.ornl.bellerophon.beam.enums.SHOFitDatasetType;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import hdf.object.Attribute;
import hdf.object.Dataset;
import hdf.object.FileFormat;
import hdf.object.HObject;
import hdf.object.h5.H5CompoundDS;
import hdf.object.h5.H5File;
import hdf.object.h5.H5Group;
import hdf.object.h5.H5ScalarDS;

public class SHOFitDataSet implements Data{

	private TreeMap<String, SHOFitDataCell[][]> cellGridMap;
	private TreeMap<String, SHOFitDataCell[][]> cellGridMapGuess;
	private TreeMap<String, ArrayList<Double>> dcMap;
	private TreeMap<String, ArrayList<Double>> wMap;
	private boolean containsSHOFitResults;
	private GridPoint selectedGridPoint;
	private int dataFileIndex;
	
	public SHOFitDataSet(){
		initialize();
	}
	
	public SHOFitDataSet clone(){
		SHOFitDataSet bfds = new SHOFitDataSet();
		return bfds;
	}
	
	public void initialize(){
		dataFileIndex = -1;
		cellGridMap = new TreeMap<String, SHOFitDataCell[][]>();
		cellGridMapGuess = new TreeMap<String, SHOFitDataCell[][]>();
		dcMap = new TreeMap<String, ArrayList<Double>>();
		wMap = new TreeMap<String, ArrayList<Double>>();
		containsSHOFitResults = false;
		selectedGridPoint = null;
	}
	
	public int getDataFileIndex(){return dataFileIndex;}
	public void setDataFileIndex(int dataFileIndex){this.dataFileIndex = dataFileIndex;}
	
	public GridPoint getSelectedGridPoint(){return selectedGridPoint;}
	public void setSelectedGridPoint(GridPoint selectedGridPoint){this.selectedGridPoint = selectedGridPoint;}
	
	public boolean containsSHOFitResults(){return containsSHOFitResults;}
	
	public SHOFitDataCell[][] getCellArray(String plotGroupName, SHOFitDatasetType datasetType){
		if(datasetType==SHOFitDatasetType.SHO_FIT_RESULTS){
			return cellGridMap.get(plotGroupName);
		}
		return cellGridMapGuess.get(plotGroupName);
	}
	
	public ArrayList<Double> getWList(String plotGroupName){
		return wMap.get(plotGroupName);
	}
	
	public ArrayList<Double> getDCList(String plotGroupName){
		return dcMap.get(plotGroupName);
	}
	
	public ArrayList<String> getPlotGroupNames(){
		return new ArrayList<String>(dcMap.keySet());
	}
	
	public void populateRawDataFromHDF5File(File file){

		if(selectedGridPoint==null){
			return;
		}
		
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
					
					H5Group cGroup = (H5Group) h5File.get(cGroupPath);
					List<HObject> cGroupList = cGroup.getMemberList();
					for(HObject o: cGroupList){
						
						if(o instanceof H5Group){
						
							H5Group g = (H5Group) o;
							String groupName = g.getName();
							String[] groupNameArray = groupName.split("-");
							
							if(groupNameArray[1].equals("Plot_Group")){
							
								String pGroupPath = cGroupPath + "/" + groupName;
								String pGroupName = cGroupPath + "/" + groupNameArray[0];
								
								SHOFitDataCell cellFit = cellGridMap.get(pGroupName)[selectedGridPoint.getX()-1][selectedGridPoint.getY()-1];
								SHOFitDataCell cellGuess = cellGridMapGuess.get(pGroupName)[selectedGridPoint.getX()-1][selectedGridPoint.getY()-1];
								
								ArrayList<TreeMap<ComplexValueType,double[]>> mainDataList = new ArrayList<TreeMap<ComplexValueType,double[]>>();
								
								if(cellFit!=null){
									cellFit.setMainDataList(mainDataList);
								}
								cellGuess.setMainDataList(mainDataList);
								
								H5CompoundDS ds = (H5CompoundDS) h5File.get(pGroupPath + "/Raw_Data");
								ds.init();
								
								TreeMap<String, Integer> specLabelsMap = getSpecIndices(h5File, ds);
								
								int freqIndex = specLabelsMap.get("Frequency");
								int dcIndex = specLabelsMap.get("DC_Offset");
								int fieldIndex = specLabelsMap.get("Field");
								int cycleIndex = specLabelsMap.get("Cycle");
								int forcIndex = specLabelsMap.get("FORC");
								
								long[] dims = ds.getDims();
								long[] selectedDims = ds.getSelectedDims();
								int[] selectedIndices = ds.getSelectedIndex();
								
								int xDim = (int) dims[0];
								int yDim = (int) dims[1];
								
								selectedDims[0] = xDim;
								selectedDims[1] = yDim;
								
								selectedIndices[0] = 0;
								selectedIndices[1] = 1;
								
								int freqDim = 1;
								int dcDim = 1;
								int cycleDim = 1;
								int forcDim = 1;
								int fieldDim = 1;

								if(freqIndex!=-1){
									freqDim = (int) dims[freqIndex];
									selectedDims[freqIndex] = freqDim;
								}
								
								if(dcIndex!=-1){
									dcDim = (int) dims[dcIndex];
									selectedDims[dcIndex] = 1;
								}
								
								if(cycleIndex!=-1){
									cycleDim = (int) dims[cycleIndex];
									selectedDims[cycleIndex] = 1;
								}
								
								if(forcIndex!=-1){
									forcDim = (int) dims[forcIndex];
									selectedDims[forcIndex] = 1;
								}
								
								if(fieldIndex!=-1){
									fieldDim = (int) dims[fieldIndex];
									selectedDims[fieldIndex] = 1;
								}
								
								long[] startDims = ds.getStartDims();
								
								for(int cycleCounter = 0; cycleCounter<cycleDim; cycleCounter++){
									
									if(cycleIndex!=-1){
										startDims[cycleIndex] = cycleCounter;
									}

									for(int forcCounter = 0; forcCounter<forcDim; forcCounter++){
										
										if(forcIndex!=-1){
											startDims[forcIndex] = forcCounter;
										}
										
										for(int fieldCounter = 0; fieldCounter<fieldDim; fieldCounter++){

											if(fieldIndex!=-1){
												startDims[fieldIndex] = fieldCounter;
											}
	
											for(int dcCounter=0; dcCounter<dcDim; dcCounter++){
	
												if(dcIndex!=-1){
													startDims[dcIndex] = dcCounter;
												}
	
												TreeMap<ComplexValueType,double[]> map = new TreeMap<ComplexValueType, double[]>();
												
												Vector dsVector = (Vector) ds.getData();
												float[] dsArrayReal = (float[]) dsVector.get(0);
												float[] dsArrayImag = (float[]) dsVector.get(1);
												
												double[] valueArrayReal = new double[freqDim];
												double[] valueArrayImag = new double[freqDim];
												double[] valueArrayAmp = new double[freqDim];
												double[] valueArrayPhase = new double[freqDim];
												
												map.put(ComplexValueType.REAL, 		valueArrayReal);
												map.put(ComplexValueType.IMAG, 		valueArrayImag);
												map.put(ComplexValueType.AMP,   	valueArrayAmp);
												map.put(ComplexValueType.PHASE, 	valueArrayPhase);

												for(int i=0; i<dsArrayReal.length; i++){
													
													valueArrayReal[i] = (double) dsArrayReal[i];
													valueArrayImag[i] = (double) dsArrayImag[i];
													valueArrayAmp[i] = Math.sqrt(Math.pow(valueArrayReal[i], 2) + Math.pow(valueArrayImag[i], 2));
													valueArrayPhase[i] = Math.atan2(valueArrayImag[i], valueArrayReal[i]);
													
												}
												
												ds.clear();
												mainDataList.add(map);
												
											}
											
										}
											
									}
										
								}
			
							}
							
						}
						
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

	public void populateFromHDF5File(File file){

		cellGridMap = new TreeMap<String, SHOFitDataCell[][]>();
		cellGridMapGuess = new TreeMap<String, SHOFitDataCell[][]>();
		dcMap = new TreeMap<String, ArrayList<Double>>();
		wMap = new TreeMap<String, ArrayList<Double>>();
		containsSHOFitResults = false;
		selectedGridPoint = null;
		
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
					
					//Get the frequency spectrum for each channel
					ArrayList<Double> wList = new ArrayList<Double>();
					
					H5ScalarDS binFrequenciesDS = (H5ScalarDS) h5File.get(cGroupPath + "/Bin_Frequencies");
					binFrequenciesDS.init();

					HDF5DataType dataType = HDF5DataType.getHDF5DataType(binFrequenciesDS);	
					if(dataType==HDF5DataType.FLOAT){
						float[] binFrequenciesDSArray = (float[]) binFrequenciesDS.getData();
						for(int i=0; i<binFrequenciesDSArray.length; i++){
							wList.add((double) binFrequenciesDSArray[i]);
						}
					}else if(dataType==HDF5DataType.DOUBLE){
						double[] binFrequenciesDSArray = (double[]) binFrequenciesDS.getData();
						for(int i=0; i<binFrequenciesDSArray.length; i++){
							wList.add((double) binFrequenciesDSArray[i]);
						}
					}
					////////////////////////////////////////////
					
					String shoFitGroupPath = cGroupPath + "/Raw_Data-SHO_Fit_000";
					H5Group shoFitGroup = (H5Group) h5File.get(shoFitGroupPath);
					List<HObject> shoFitGroupList = shoFitGroup.getMemberList();
					for(HObject o: shoFitGroupList){
						
						if(o instanceof H5Group){
							
							H5Group g = (H5Group) o;
							String groupName = g.getName();
							String[] groupNameArray = groupName.split("-");
							
							if(groupNameArray[1].equals("Plot_Group")){
								
								ArrayList<Double> dcList = new ArrayList<Double>();
								
								String pGroupName = cGroupPath + "/" + groupNameArray[0];
								String pGroupPath = shoFitGroupPath + "/" + groupName;
								
								wMap.put(pGroupName, wList);
								dcMap.put(pGroupName, dcList);
								
								String fitPath = pGroupPath + "/Fit";
								String guessPath = pGroupPath + "/Guess";
								
								H5CompoundDS fitDS = (H5CompoundDS) h5File.get(fitPath);
								H5CompoundDS guessDS = (H5CompoundDS) h5File.get(guessPath);
								
								if(fitDS!=null){
								
									double[] arraySpecValues = getSpecValues(h5File, fitDS, "DC_Offset");
									for(int index=0; index<arraySpecValues.length; index++){
										dcList.add(arraySpecValues[index]);
									}
								
								}else{
									
									double[] arraySpecValues = getSpecValues(h5File, guessDS, "DC_Offset");
									for(int index=0; index<arraySpecValues.length; index++){
										dcList.add(arraySpecValues[index]);
									}
									
								}

								containsSHOFitResults = fitDS!=null;
								if(containsSHOFitResults){
									cellGridMap.put(pGroupName, processSHODataSet(h5File, fitDS, wList));
								}
								cellGridMapGuess.put(pGroupName, processSHODataSet(h5File, guessDS, wList));
								
							}
							
						}
						
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
	
	private SHOFitDataCell[][] processSHODataSet(H5File h5File, H5CompoundDS ds, ArrayList<Double> wList) throws OutOfMemoryError, Exception{
		
		ds.init();
			
		TreeMap<String, Integer> specLabelsMap = getSpecIndices(h5File, ds);
		
		int dcIndex = specLabelsMap.get("DC_Offset");
		int fieldIndex = specLabelsMap.get("Field");
		int cycleIndex = specLabelsMap.get("Cycle");
		int forcIndex = specLabelsMap.get("FORC");
		
		long[] dims = ds.getDims();
		long[] selectedDims = ds.getSelectedDims();
		int[] selectedIndices = ds.getSelectedIndex();
		
		int xDim = (int) dims[0];
		int yDim = (int) dims[1];
		int gridWidth = xDim;
		SHOFitDataCell[][] array = new SHOFitDataCell[xDim][yDim];
		
		selectedDims[0] = xDim;
		selectedDims[1] = yDim;
		
		selectedIndices[0] = 0;
		selectedIndices[1] = 1;
			
		int dcDim = 1;
		int cycleDim = 1;
		int forcDim = 1;
		int fieldDim = 1;
		
		if(dcIndex!=-1){
			dcDim = (int) dims[dcIndex];
			selectedDims[dcIndex] = dcDim;
		}
		
		if(cycleIndex!=-1){
			cycleDim = (int) dims[cycleIndex];
			selectedDims[cycleIndex] = 1;
		}
		
		if(forcIndex!=-1){
			forcDim = (int) dims[forcIndex];
			selectedDims[forcIndex] = 1;
		}
		
		if(fieldIndex!=-1){
			fieldDim = (int) dims[fieldIndex];
			selectedDims[fieldIndex] = 1;
		}
		
		int xCounter = 0;
		int yCounter = 0;
		int dcCounter = -1;
		
		long[] startDims = ds.getStartDims();
		
		for(int cycleCounter = 0; cycleCounter<cycleDim; cycleCounter++){
			
			if(cycleIndex!=-1){
				startDims[cycleIndex] = cycleCounter;
			}

			for(int forcCounter = 0; forcCounter<forcDim; forcCounter++){
				
				if(forcIndex!=-1){
					startDims[forcIndex] = forcCounter;
				}
				
				for(int fieldCounter = 0; fieldCounter<fieldDim; fieldCounter++){

					if(fieldIndex!=-1){
						startDims[fieldIndex] = fieldCounter;
					}
					
					Vector fitDSVector = (Vector) ds.getData();
					double[] fitDSAArray = null;
					double[] fitDSWArray = null;
					double[] fitDSQArray = null;
					double[] fitDSPArray = null;
					
					HDF5DataType dataType = HDF5DataType.getHDF5DataType(ds).get(0);
					if(dataType==HDF5DataType.FLOAT){
						
						float[] fitDSAArrayFloat = (float[]) fitDSVector.get(0);
						float[] fitDSWArrayFloat = (float[]) fitDSVector.get(1);
						float[] fitDSQArrayFloat = (float[]) fitDSVector.get(2);
						float[] fitDSPArrayFloat = (float[]) fitDSVector.get(3);
						
						fitDSAArray = new double[fitDSAArrayFloat.length];
						fitDSWArray = new double[fitDSWArrayFloat.length];
						fitDSQArray = new double[fitDSQArrayFloat.length];
						fitDSPArray = new double[fitDSPArrayFloat.length];
						
						for(int i=0; i<fitDSAArray.length; i++){
							fitDSAArray[i] = (double) fitDSAArrayFloat[i];
							fitDSWArray[i] = (double) fitDSWArrayFloat[i];
							fitDSQArray[i] = (double) fitDSQArrayFloat[i];
							fitDSPArray[i] = (double) fitDSPArrayFloat[i];
						}
						
					}else if(dataType==HDF5DataType.DOUBLE){
						
						fitDSAArray = (double[]) fitDSVector.get(0);
						fitDSWArray = (double[]) fitDSVector.get(1);
						fitDSQArray = (double[]) fitDSVector.get(2);
						fitDSPArray = (double[]) fitDSVector.get(3);
						
					}
					
					for(int i=0; i<fitDSAArray.length; i++){
						
						dcCounter++;
						if(dcCounter==dcDim){
							dcCounter = 0;
							xCounter++;
						}
						
						if(xCounter==xDim){
							xCounter = 0;
							yCounter++;
						}
						
						if(yCounter==yDim){
							yCounter = 0;
						}

						GridPoint gp = new GridPoint(xCounter+1, yCounter+1);
						SHOFitDataCell sfdc = null;
						if(array[xCounter][yCounter]!=null){
							sfdc = array[xCounter][yCounter];
						}else{
							sfdc = new SHOFitDataCell();
							array[xCounter][yCounter] = sfdc;
							sfdc.setGridPoint(gp);
							sfdc.setWList(wList);
							gp.setPositionIndex(gridWidth);
						}
						
						double a = fitDSAArray[i];
						double w = fitDSWArray[i];
						double q = fitDSQArray[i];
						double p = fitDSPArray[i];
						
						SHOFitData sfd = new SHOFitData();
						sfdc.getSHOFitDataList().add(sfd);
						sfd.setA(a);
						sfd.setW(w);
						sfd.setQ(q);
						sfd.setP(p);
						
					}
					
					ds.clear();
					
				}
				
			}

		}
		
		return array;
		
	}
	
	private double[] getSpecValues(H5File h5File, Dataset ds, String name) throws Exception{
		
		String specValuesPath = "";
		List<Attribute> list = ds.getMetadata();
		for(Attribute a: list){
			String attributeName = a.getName();
			if(attributeName.equals("Spectroscopic_Values")){
				specValuesPath = ((String[]) a.getValue())[0];
			}
		}
		
		H5ScalarDS specValuesDS = (H5ScalarDS) h5File.get(specValuesPath);
		specValuesDS.init();

		int index = -1;
		list = specValuesDS.getMetadata();
		for(Attribute a: list){
			String attributeName = a.getName();
			if(attributeName.equals("labels")){
				String[] array = (String[]) a.getValue();
				for(int i=0; i<array.length; i++){
					if(array[i].equals(name)){
						index = i;
					}
				}
			}
		}

		long[] dimsSpecValues = specValuesDS.getDims();
		int yDim = (int) dimsSpecValues[1];
		
		long[] startDims = specValuesDS.getStartDims();
		startDims[0] = index;
		
		long[] selectedDims = specValuesDS.getSelectedDims();
		selectedDims[0] = 1;
		selectedDims[1] = yDim;
		
		float[] specValueDSArray = (float[]) specValuesDS.getData();
		ArrayList<Double> outputList = new ArrayList<Double>();

		for(int i=0; i<specValueDSArray.length; i++){
			float value = specValueDSArray[i];
			if(!outputList.contains(value)){
				outputList.add((double) value);
			}
		}

		double[] outputArray = new double[outputList.size()];
		for(int i=0; i<outputArray.length; i++){
			outputArray[i] = outputList.get(i);
		}
		
		return outputArray;
	}
	
	private TreeMap<String, Integer> getSpecIndices(H5File h5File, Dataset ds) throws Exception{
		
		ArrayList<String> labelsList = new ArrayList<String>();
		labelsList.add("Frequency");
		labelsList.add("DC_Offset");
		labelsList.add("Field");
		labelsList.add("Cycle");
		labelsList.add("FORC");
		labelsList.add("Single_Step");
		
		TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		for(String label: labelsList){
			map.put(label, -1);
		}
		
		String specValuesPath = "";
		List<Attribute> list = ds.getMetadata();
		for(Attribute a: list){
			String attributeName = a.getName();
			if(attributeName.equals("Spectroscopic_Values")){
				specValuesPath = ((String[]) a.getValue())[0];
			}
		}
		
		H5ScalarDS specValuesDS = (H5ScalarDS) h5File.get(specValuesPath);
		specValuesDS.init();
		
		list = specValuesDS.getMetadata();
		for(Attribute a: list){
			String attributeName = a.getName();
			if(attributeName.equals("labels")){
				String[] array = (String[]) a.getValue();
				for(int i=0; i<array.length; i++){
					map.put(array[i], i+2);
				}
			}
		}
		
		return map;
	}
	
}
