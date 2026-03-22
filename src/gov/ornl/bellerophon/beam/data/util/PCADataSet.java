package gov.ornl.bellerophon.beam.data.util;

import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import hdf.object.*;
import hdf.object.h5.*;
import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.ComplexValueType;
import gov.ornl.bellerophon.beam.enums.HDF5DataType;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;

public class PCADataSet implements Data {
	
	private int dataFileIndex;
	protected int maxComponentIndex;
	protected String groupPath;
	protected ArrayList<PCAData> pcaDataList;
	
	public PCADataSet(){
		initialize();
	}
	
	public PCADataSet clone(){
		PCADataSet pds = new PCADataSet();
		return pds;
	}
	
	public void initialize(){
		groupPath = "";
		dataFileIndex = -1;
		maxComponentIndex = -1;
		pcaDataList = null;
	}
	
	public int getMaxComponentIndex(){return maxComponentIndex;}
	public void setMaxComponentIndex(int maxComponentIndex){this.maxComponentIndex = maxComponentIndex;}
	
	public int getDataFileIndex(){return dataFileIndex;}
	public void setDataFileIndex(int dataFileIndex){this.dataFileIndex = dataFileIndex;}
	
	public String getGroupPath(){return groupPath;}
	public void setGroupPath(String groupPath){this.groupPath = groupPath;}
	
	public ArrayList<PCAData> getPCADataList(){return pcaDataList;}

	public void populateSFromHDF5File(File file) {
		
		pcaDataList = new ArrayList<PCAData>();
		
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		
		try{
			
			H5File h5File = (H5File) fileFormat.createInstance(file.getPath(), FileFormat.READ);
			h5File.open();
			
			H5ScalarDS sDS = (H5ScalarDS) h5File.get(groupPath + "/default-Plot_Group/S");
			sDS.init();
			
			HDF5DataType dataType = HDF5DataType.getHDF5DataType(sDS);
			if(dataType==HDF5DataType.FLOAT){
				float[] sDSArray = (float[]) sDS.getData();
				for(int i=0; i<sDSArray.length; i++){
					PCAData pd = new PCAData();
					pcaDataList.add(pd);
					pd.setS((double) sDSArray[i]);
					
					TreeMap<String, PCADataCell[][]> vCellMap = new TreeMap<String, PCADataCell[][]>();
					pd.setVCellMap(vCellMap);
				}
			}else if(dataType==HDF5DataType.DOUBLE){
				double[] sDSArray = (double[]) sDS.getData();
				for(int i=0; i<sDSArray.length; i++){
					PCAData pd = new PCAData();
					pcaDataList.add(pd);
					pd.setS(sDSArray[i]);
					
					TreeMap<String, PCADataCell[][]> vCellMap = new TreeMap<String, PCADataCell[][]>();
					pd.setVCellMap(vCellMap);
				}
			}

			h5File.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	protected String getDataFileType(H5File h5File) throws Exception{
		String dataFileType = "";
		
		H5Group rootGroup = (H5Group) h5File.get("/");
		List<Attribute> list = rootGroup.getMetadata();
		Iterator<Attribute> itrRoot = list.iterator();
		while(itrRoot.hasNext()){
			Attribute a = itrRoot.next();
			if(a.getName().equals("data_type")){
				dataFileType = ((String[]) a.getValue())[0];
				break;
			}
		}
		
		return dataFileType;
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
		
			//////////EIGEN VECTORS///////////////////////////////////////////////
			H5Group group = (H5Group) h5File.get(groupPath);
			List<HObject> shoFitGroupList = group.getMemberList();
			for(HObject o: shoFitGroupList){
				
				if(o instanceof H5Group){
					
					H5Group g = (H5Group) o;
					String groupName = g.getName();
					String[] groupNameArray = groupName.split("-");
					
					if(groupNameArray[1].equals("Plot_Group") && h5File.get(groupPath + "/" + groupName + "/V")!=null){
						
						HObject vObject = h5File.get(groupPath + "/" + groupName + "/V");
						String pGroupName = groupPath + "/" + groupNameArray[0];
						
						if(vObject instanceof H5CompoundDS && (dataFileType.equalsIgnoreCase("BEPSData") || dataFileType.equalsIgnoreCase("BELineData"))){
							
							H5CompoundDS vDS = (H5CompoundDS) vObject;
							vDS.init();
								
							TreeMap<String, Integer> specLabelsMap = getSpecIndices(h5File, vDS);
							
							int freqIndex = specLabelsMap.get("Frequency");
							int dcIndex = specLabelsMap.get("DC_Offset");
							int fieldIndex = specLabelsMap.get("Field");
							int cycleIndex = specLabelsMap.get("Cycle");
							int forcIndex = specLabelsMap.get("FORC");
							
							dims = vDS.getDims();
							selectedDims = vDS.getSelectedDims();
							int[] selectedIndices = vDS.getSelectedIndex();
							
							pcDim = (int) dims[0];
							selectedDims[0] = 1;
							
							int freqDim = 1;
							int dcDim = 1;
							int cycleDim = 1;
							int forcDim = 1;
							int fieldDim = 1;

							boolean useCycle = true;
							
							if(freqIndex!=-1){
								freqDim = (int) dims[freqIndex];
								selectedDims[freqIndex] = freqDim;
								selectedIndices[0] = freqIndex;
							}
							
							if(dcIndex!=-1){
								dcDim = (int) dims[dcIndex];
								selectedDims[dcIndex] = dcDim;
								selectedIndices[1] = dcIndex;
							}
							
							if(cycleIndex!=-1){
								cycleDim = (int) dims[cycleIndex];
								selectedDims[cycleIndex] = cycleDim;
								selectedIndices[2] = cycleIndex;
							}
							
							if(forcIndex!=-1){
								forcDim = (int) dims[forcIndex];
								selectedDims[forcIndex] = forcDim;
								selectedIndices[2] = forcIndex;
								useCycle = false;
							}
							
							if(fieldIndex!=-1){
								fieldDim = (int) dims[fieldIndex];
								selectedDims[fieldIndex] = 1;
							}

							long[] startDims = vDS.getStartDims();

							double[] arraySpecValuesFrequency = getSpecValues(h5File, vDS, "Frequency");
							double[] arraySpecValuesDCOffset = getSpecValues(h5File, vDS, "DC_Offset");

							for(int pcCounter = 0; pcCounter<pcDim; pcCounter++){
								
								startDims[0] = pcCounter;

								PCAData pd = pcaDataList.get(pcCounter);
								
								TreeMap<String, PCADataCell[][]> vCellMap = pd.getVCellMap();
								vCellMap.put(pGroupName, new PCADataCell[dcDim*cycleDim][freqDim]);

								int vPlotMapDim = freqDim * dcDim * cycleDim * fieldDim * forcDim;
								double[] valueArrayReal = new double[vPlotMapDim];
								double[] valueArrayImag = new double[vPlotMapDim];
								double[] valueArrayAmp = new double[vPlotMapDim];
								double[] valueArrayPhase = new double[vPlotMapDim];
								
								TreeMap<ComplexValueType, double[]> vPlotMap = new TreeMap<ComplexValueType, double[]>();
								vPlotMap.put(ComplexValueType.REAL, 	valueArrayReal);
								vPlotMap.put(ComplexValueType.IMAG,  	valueArrayImag);
								vPlotMap.put(ComplexValueType.AMP, 	 	valueArrayAmp);
								vPlotMap.put(ComplexValueType.PHASE, 	valueArrayPhase);
								
								pd.setVPlotMap(vPlotMap);
								
								PCADataCell[][] cellArray = vCellMap.get(pGroupName);
									
								for(int fieldCounter = 0; fieldCounter<fieldDim; fieldCounter++){

									if(fieldIndex!=-1){
										startDims[fieldIndex] = fieldCounter;
									}

									Vector vDSVector = (Vector) vDS.getData();
									
									double[] vDSArrayReal = null;
									double[] vDSArrayImag = null;
									
									HDF5DataType dataType = HDF5DataType.getHDF5DataType(vDS).get(0);
									if(dataType==HDF5DataType.FLOAT){
										float[] tempReal = (float[]) vDSVector.get(0);
										float[] tempImag = (float[]) vDSVector.get(1);
										
										vDSArrayReal = new double[tempReal.length];
										vDSArrayImag = new double[tempImag.length];
										
										for(int i=0; i<tempReal.length; i++){
											vDSArrayReal[i] = (double) tempReal[i];
											vDSArrayImag[i] = (double) tempImag[i];
										}
										
									}else if(dataType==HDF5DataType.DOUBLE){
										vDSArrayReal = (double[]) vDSVector.get(0);
										vDSArrayImag = (double[]) vDSVector.get(1);
									}
									
									int freqCounter = 0;
									int dcCounter = 0;
									int cycleCounter = -1;
									int vPlotMapCounter = 0;

									for(int i=0; i<vDSArrayReal.length; i++){

										cycleCounter++;
										if(useCycle && cycleCounter==cycleDim){
											cycleCounter = 0;
											dcCounter++;
										}else if(!useCycle && cycleCounter==forcDim){
											cycleCounter = 0;
											dcCounter++;
										}
										
										if(dcCounter==dcDim){
											dcCounter = 0;
											freqCounter++;
										}
										
										if(freqCounter==freqDim){
											freqCounter = 0;
										}
										
										PCADataCell cell = new PCADataCell();
										cellArray[dcDim*cycleCounter+dcCounter][freqCounter] = cell;
										
										cell.setXValue(arraySpecValuesDCOffset[dcCounter]);
										cell.setYValue(arraySpecValuesFrequency[freqCounter]);
										
										double realValue = vDSArrayReal[i];
										double imagValue = vDSArrayImag[i];
										
										TreeMap<ComplexValueType, Double> valueMap = new TreeMap<ComplexValueType, Double>();
										cell.setValueMap(valueMap);
										
										valueMap.put(ComplexValueType.REAL, realValue);
										valueMap.put(ComplexValueType.IMAG, imagValue);
										valueMap.put(ComplexValueType.AMP, Math.sqrt(Math.pow(realValue, 2) + Math.pow(imagValue, 2)));
										valueMap.put(ComplexValueType.PHASE, Math.atan2(imagValue, realValue));
										
										valueArrayReal[vPlotMapCounter] = realValue;
										valueArrayImag[vPlotMapCounter] = imagValue;
										valueArrayAmp[vPlotMapCounter] = Math.sqrt(Math.pow(realValue, 2) + Math.pow(imagValue, 2));
										valueArrayPhase[vPlotMapCounter] = Math.atan2(imagValue, realValue);
										
										vPlotMapCounter++;
										
									}
									
									vDS.clear();
									
								}
								
							}
							
						}else if(vObject instanceof H5ScalarDS && (dataFileType.equalsIgnoreCase("BEPSData") || dataFileType.equalsIgnoreCase("BELineData"))){
	
							H5ScalarDS vDS = (H5ScalarDS) vObject;
							vDS.init();
								
							TreeMap<String, Integer> specLabelsMap = getSpecIndices(h5File, vDS);
							
							int freqIndex = specLabelsMap.get("Frequency");
							int dcIndex = specLabelsMap.get("DC_Offset");
							int fieldIndex = specLabelsMap.get("Field");
							int cycleIndex = specLabelsMap.get("Cycle");
							int forcIndex = specLabelsMap.get("FORC");
							
							dims = vDS.getDims();
							selectedDims = vDS.getSelectedDims();
							int[] selectedIndices = vDS.getSelectedIndex();
							
							pcDim = (int) dims[0];
							selectedDims[0] = 1;
							
							int freqDim = 1;
							int dcDim = 1;
							int cycleDim = 1;
							int forcDim = 1;
							int fieldDim = 1;

							if(freqIndex!=-1){
								freqDim = (int) dims[freqIndex];
								selectedDims[freqIndex] = freqDim;
								selectedIndices[0] = freqIndex;
							}
							
							if(dcIndex!=-1){
								dcDim = (int) dims[dcIndex];
								selectedDims[dcIndex] = dcDim;
								selectedIndices[1] = dcIndex;
							}
							
							if(cycleIndex!=-1){
								cycleDim = (int) dims[cycleIndex];
								selectedDims[cycleIndex] = cycleDim;
								selectedIndices[2] = cycleIndex;
							}
							
							if(forcIndex!=-1){
								forcDim = (int) dims[forcIndex];
								selectedDims[forcIndex] = 1;
								selectedIndices[2] = forcIndex;
							}
							
							if(fieldIndex!=-1){
								fieldDim = (int) dims[fieldIndex];
								selectedDims[fieldIndex] = 1;
							}

							long[] startDims = vDS.getStartDims();

							double[] arraySpecValuesFrequency = getSpecValues(h5File, vDS, "Frequency");
							double[] arraySpecValuesDCOffset = getSpecValues(h5File, vDS, "DC_Offset");

							for(int pcCounter = 0; pcCounter<pcDim; pcCounter++){
								
								startDims[0] = pcCounter;

								PCAData pd = pcaDataList.get(pcCounter);
								
								TreeMap<String, PCADataCell[][]> vCellMap = pd.getVCellMap();
								vCellMap.put(pGroupName, new PCADataCell[dcDim*cycleDim][freqDim]);

								int vPlotMapDim = freqDim * dcDim * cycleDim * fieldDim * forcDim;
								double[] valueArrayReal = new double[vPlotMapDim];
								
								TreeMap<ComplexValueType, double[]> vPlotMap = new TreeMap<ComplexValueType, double[]>();
								vPlotMap.put(ComplexValueType.REAL, 	valueArrayReal);
								
								pd.setVPlotMap(vPlotMap);
								
								PCADataCell[][] cellArray = vCellMap.get(pGroupName);
									
								for(int forcCounter = 0; forcCounter<forcDim; forcCounter++){
									
									if(forcIndex!=-1){
										startDims[forcIndex] = forcCounter;
									}
									
									for(int fieldCounter = 0; fieldCounter<fieldDim; fieldCounter++){

										if(fieldIndex!=-1){
											startDims[fieldIndex] = fieldCounter;
										}

										double[] vDSArrayReal = null;
										
										HDF5DataType dataType = HDF5DataType.getHDF5DataType(vDS);
										if(dataType==HDF5DataType.FLOAT){
											float[] tempReal = (float[]) vDS.getData();
											vDSArrayReal = new double[tempReal.length];
											for(int i=0; i<tempReal.length; i++){
												vDSArrayReal[i] = (double) tempReal[i];
											}
											
										}else if(dataType==HDF5DataType.DOUBLE){
											vDSArrayReal = (double[]) vDS.getData();
										}
										
										int freqCounter = 0;
										int dcCounter = 0;
										int cycleCounter = -1;
										int vPlotMapCounter = 0;

										for(int i=0; i<vDSArrayReal.length; i++){

											cycleCounter++;
											if(cycleCounter==cycleDim){
												cycleCounter = 0;
												dcCounter++;
											}
											
											if(dcCounter==dcDim){
												dcCounter = 0;
												freqCounter++;
											}
											
											if(freqCounter==freqDim){
												freqCounter = 0;
											}

											PCADataCell cell = new PCADataCell();
											cellArray[dcDim*cycleCounter+dcCounter][freqCounter] = cell;
											
											cell.setXValue(arraySpecValuesDCOffset[dcCounter]);
											cell.setYValue(arraySpecValuesFrequency[freqCounter]);
											
											double realValue = vDSArrayReal[i];
											
											TreeMap<ComplexValueType, Double> valueMap = new TreeMap<ComplexValueType, Double>();
											cell.setValueMap(valueMap);
											
											valueMap.put(ComplexValueType.REAL, realValue);
											
											valueArrayReal[vPlotMapCounter] = realValue;
											
											vPlotMapCounter++;
											
										}
										
										vDS.clear();
										
									}
									
								}
								
							}
							
						}else if(dataFileType.equalsIgnoreCase("PtychographyData") || dataFileType.equalsIgnoreCase("ImageData")){
								
							H5ScalarDS vDS = (H5ScalarDS) vObject;
							vDS.init();
								
							TreeMap<String, Integer> specLabelsMap = getSpecIndices(h5File, vDS);
							
							int uIndex = specLabelsMap.get("U");
							int vIndex = specLabelsMap.get("V");
							
							dims = vDS.getDims();
							selectedDims = vDS.getSelectedDims();
							int[] selectedIndices = vDS.getSelectedIndex();
							
							pcDim = (int) dims[0];
							selectedDims[0] = 1;
							
							pcDim = (int) dims[0];
							int uDim = (int) dims[uIndex];
							int vDim = (int) dims[vIndex];

							selectedDims[0] = 1;
							selectedDims[uIndex] = uDim;
							selectedDims[vIndex] = vDim;

							selectedIndices[0] = uIndex;
							selectedIndices[1] = vIndex;
							
							long[] startDims = vDS.getStartDims();

							for(int pcCounter = 0; pcCounter<pcDim; pcCounter++){
								
								startDims[0] = pcCounter;

								PCAData pd = pcaDataList.get(pcCounter);
								
								TreeMap<String, PCADataCell[][]> vCellMap = pd.getVCellMap();
								vCellMap.put(pGroupName, new PCADataCell[vDim][uDim]);

								int vPlotMapDim = uDim * vDim;
								double[] valueArrayReal = new double[vPlotMapDim];
								
								TreeMap<ComplexValueType, double[]> vPlotMap = new TreeMap<ComplexValueType, double[]>();
								vPlotMap.put(ComplexValueType.REAL, 	valueArrayReal);

								pd.setVPlotMap(vPlotMap);
								
								PCADataCell[][] cellArray = vCellMap.get(pGroupName);
								double[] vDSArrayReal = null;
								
								HDF5DataType dataType = HDF5DataType.getHDF5DataType(vDS);
								if(dataType==HDF5DataType.FLOAT){
									float[] tempReal = (float[]) vDS.getData();
									vDSArrayReal = new double[tempReal.length];
									for(int i=0; i<tempReal.length; i++){
										vDSArrayReal[i] = (double) tempReal[i];
									}
									
								}else if(dataType==HDF5DataType.DOUBLE){
									vDSArrayReal = (double[]) vDS.getData();
								}
										
								int uCounter = 0;
								int vCounter = -1;
								int vPlotMapCounter = 0;

								for(int i=0; i<vDSArrayReal.length; i++){

									vCounter++;
									if(vCounter==vDim){
										vCounter = 0;
										uCounter++;
									}
									
									if(uCounter==uDim){
										uCounter = 0;
									}
											
									PCADataCell cell = new PCADataCell();
									cellArray[vCounter][uCounter] = cell;
									
									cell.setXValue(vCounter);
									cell.setYValue(uCounter);
									
									double realValue = vDSArrayReal[i];
									
									TreeMap<ComplexValueType, Double> valueMap = new TreeMap<ComplexValueType, Double>();
									cell.setValueMap(valueMap);
									
									valueMap.put(ComplexValueType.REAL, realValue);
									
									valueArrayReal[vPlotMapCounter] = realValue;
									
									vPlotMapCounter++;
									
								}
								
								vDS.clear();
								
							}

						}
						
					}
				
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
	
	protected double[] getSpecValues(H5File h5File, Dataset ds, String name) throws Exception{
		
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
		
		ArrayList<Double> outputList = new ArrayList<Double>();
		HDF5DataType dataType = HDF5DataType.getHDF5DataType(specValuesDS);
		if(dataType==HDF5DataType.FLOAT){
			float[] specValueDSArray = (float[]) specValuesDS.getData();
			for(int i=0; i<specValueDSArray.length; i++){
				float value = specValueDSArray[i];
				if(i==0){
					outputList.add((double) value);
				}else if(i!=0 && specValueDSArray[i-1]!=(double)value){
					outputList.add((double) value);
				}
			}
		}else if(dataType==HDF5DataType.DOUBLE){
			double[] specValueDSArray = (double[]) specValuesDS.getData();
			for(int i=0; i<specValueDSArray.length; i++){
				double value = specValueDSArray[i];
				if(i==0){
					outputList.add((double) value);
				}else if(i!=0 && specValueDSArray[i-1]!=(double)value){
					outputList.add((double) value);
				}
			}
		}

		double[] outputArray = new double[outputList.size()];
		for(int i=0; i<outputArray.length; i++){
			outputArray[i] = outputList.get(i);
		}
		
		return outputArray;
	}
	
	protected TreeMap<String, Integer> getSpecIndices(H5File h5File, Dataset ds) throws Exception{
		
		ArrayList<String> labelsList = new ArrayList<String>();
		labelsList.add("Frequency");
		labelsList.add("DC_Offset");
		labelsList.add("Field");
		labelsList.add("Cycle");
		labelsList.add("FORC");
		labelsList.add("Single_Step");
		labelsList.add("U");
		labelsList.add("V");
		labelsList.add("Component");
		
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
					map.put(array[i], i+1);
				}
			}
		}
		
		return map;
	}
	
}
