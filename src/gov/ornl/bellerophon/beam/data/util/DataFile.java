/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: DataFile.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.data.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import hdf.hdf5lib.exceptions.HDF5Exception;
import hdf.object.Attribute;
import hdf.object.Dataset;
import hdf.object.Datatype;
import hdf.object.FileFormat;
import hdf.object.HObject;
import hdf.object.h5.H5CompoundDS;
import hdf.object.h5.H5Datatype;
import hdf.object.h5.H5File;
import hdf.object.h5.H5Group;
import hdf.object.h5.H5ScalarDS;
import gov.ornl.bellerophon.beam.enums.HDF5DataType;
import gov.ornl.bellerophon.beam.enums.Instrument;
import gov.ornl.bellerophon.beam.ui.format.Calendars;

public class DataFile extends CustomFile{

	private int index;
	private Instrument instrument;
	private String projectName, projectId, sampleName, sampleDesc, comments, tempName;
	private GridPoint gridSize;
	private boolean exists;
	private MeanSpectrogramDataSet meanSpectrogramDataSet;
	private SHOFitDataSet shoFitDataSet;
	private PCADataSet pcaDataSet;
	private PCAImageCleaningDataSet pcaImageCleaningDataSet;
	private KMeansClusteringDataSet kmeansClusteringDataSet;
	private Calendar uploadDate, expDate;
	private File downloadFile;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode selectedTreeNode;
	private Dataset selectedDataset;
	private H5File treeH5File;
	private TreeMap<String, DefaultMutableTreeNode> treeNodeMap;

	public DataFile(){
		super.initialize();
		initialize();
	}
	
	public void initialize(){
		index = -1;
		tempName = "";
		instrument = null;
		uploadDate = Calendars.getDefaultCalendar();
		expDate = Calendars.getDefaultCalendar();
		projectName = "Not Entered";
		projectId = "Not Entered";
		sampleName = "Not Entered";
		sampleDesc = "Not Entered";
		comments = "Not Entered";
		gridSize = null;
		exists = false;
		meanSpectrogramDataSet = null;
		kmeansClusteringDataSet = null;
		shoFitDataSet = null;
		pcaDataSet = null;
		downloadFile = null;
		treeModel = null;
		selectedDataset = null;
		treeH5File = null;
		selectedTreeNode = null;
		pcaImageCleaningDataSet = null;
		treeNodeMap = null;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof DataFile)){
			return false;
		}
		DataFile df = (DataFile)o;
		return df.index==index;
	}
	
	public String toStringInfo(){
		DecimalFormat format = new DecimalFormat("########.0");
		String s = "";
		s += "Data File Index = " + index + "\n";
		s += "Filename = " + name + "\n";
		s += "Remote Path = " + path + "\n";
		s += "File Size = " + format.format(size/1E6) + " MB\n";
		if(instrument!=null){
			s += "Instrument = " + instrument.toString() + "\n";
		}
		s += "Project Name = " + projectName + "\n";
		s += "Project ID = " + projectId + "\n";
		s += "Sample Name = " + sampleName + "\n";
		s += "Sample Description = " + sampleDesc + "\n";
		s += "Comments = " + comments + "\n";
		s += "Grid Size = " + gridSize.toString() + "\n";
		s += "Experiment Date = " + Calendars.getFormattedOutputDateString(expDate) + "\n";
		s += "Upload Date = " + Calendars.getFormattedOutputDateString(uploadDate) + "\n";
		return s;
	}
	
	public DefaultMutableTreeNode getDefaultMutableTreeNode(String path){
		return treeNodeMap.get(path);
	}
	
	public DefaultMutableTreeNode getSelectedTreeNode(){return selectedTreeNode;}
	public void setSelectedTreeNode(DefaultMutableTreeNode selectedTreeNode){this.selectedTreeNode = selectedTreeNode;}
	
	public DefaultTreeModel getTreeModel(){return treeModel;} 
	public H5File getTreeH5File(){return treeH5File;} 
	public void populateTreeModelFromHDF5File(File file){
		treeNodeMap = new TreeMap<String, DefaultMutableTreeNode>();
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		try{
			treeH5File = (H5File) fileFormat.createInstance(file.getPath(), FileFormat.READ);
			treeH5File.open();
			H5Group rootH5Group = (H5Group) ((javax.swing.tree.DefaultMutableTreeNode) treeH5File.getRootNode()).getUserObject();
			DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(rootH5Group);
			treeModel = new DefaultTreeModel(rootTreeNode);
			createTreeNodesForHDF5Object(rootTreeNode, treeModel);
			treeH5File.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void createTreeNodesForHDF5Object(DefaultMutableTreeNode parentNode, DefaultTreeModel treeModel){
		
		HObject hObject = (HObject) parentNode.getUserObject();
		if(hObject instanceof H5Group){
			
			H5Group group = (H5Group) hObject;
			List<HObject> list = group.getMemberList();
			
			if(list.size()>0){
				for(HObject ho: list){
					DefaultMutableTreeNode treeNode = null;
					if(ho instanceof H5Group){
						treeNode = new DefaultMutableTreeNode((H5Group) ho);
					}else if(ho instanceof H5ScalarDS){
						H5ScalarDS sDS =  (H5ScalarDS) ho;
						sDS.init();
						sDS.getDatatype();
						treeNode = new DefaultMutableTreeNode(sDS);
					}else if(ho instanceof H5CompoundDS){
						H5CompoundDS cDS =  (H5CompoundDS) ho;
						cDS.init();
						cDS.getDatatype();
						treeNode = new DefaultMutableTreeNode(cDS);
					}
					treeModel.insertNodeInto(treeNode, parentNode, parentNode.getChildCount());
					TreePath treePath = new TreePath(treeNode.getPath());
					Object[] treePathArray = treePath.getPath();
					String key = "";
					for(int i=1; i<treePathArray.length; i++){
						key += "/" + treePathArray[i];
					}
					treeNodeMap.put(key, treeNode);
					createTreeNodesForHDF5Object(treeNode, treeModel);
				}
			}
		}
	}
	
	public File getDownloadFile(){return downloadFile;}
	public void setDownloadFile(File downloadFile){this.downloadFile = downloadFile;}
	
	public Dataset getSelectedDataset(){return selectedDataset;}
	public void setSelectedDataset(Dataset selectedDataset){this.selectedDataset = selectedDataset;}

	public PCAImageCleaningDataSet getPCAImageCleaningDataSet(){return pcaImageCleaningDataSet;}
	public void setPCAImageCleaningDataSet(PCAImageCleaningDataSet pcaImageCleaningDataSet){this.pcaImageCleaningDataSet = pcaImageCleaningDataSet;}
	
	public PCADataSet getPCADataSet(){return pcaDataSet;}
	public void setPCADataSet(PCADataSet pcaDataSet){this.pcaDataSet = pcaDataSet;}
	
	public KMeansClusteringDataSet getKMeansClusteringDataSet(){return kmeansClusteringDataSet;}
	public void setKMeansClusteringDataSet(KMeansClusteringDataSet kmeansClusteringDataSet){this.kmeansClusteringDataSet = kmeansClusteringDataSet;}
	
	public SHOFitDataSet getSHOFitDataSet(){return shoFitDataSet;}
	public void setSHOFitDataSet(SHOFitDataSet shoFitDataSet){this.shoFitDataSet = shoFitDataSet;}
	
	public MeanSpectrogramDataSet getMeanSpectrogramDataSet(){return meanSpectrogramDataSet;}
	public void setMeanSpectrogramDataSet(MeanSpectrogramDataSet meanSpectrogramDataSet){this.meanSpectrogramDataSet = meanSpectrogramDataSet;}
	
	public boolean exists(){return exists;}
	public void setExists(boolean exists){this.exists = exists;}
	
	public String getTempName(){return tempName;}
	public void setTempName(String tempName){this.tempName = tempName;}
	
	public int getIndex(){return index;}
	public void setIndex(int index){this.index = index;}
	
	public Calendar getExpDate(){return expDate;}
	public void setExpDate(Calendar expDate){this.expDate = expDate;}
	
	public Calendar getUploadDate(){return uploadDate;}
	public void setUploadDate(Calendar uploadDate){this.uploadDate = uploadDate;}
	
	public Instrument getInstrument(){return instrument;}
	public void setInstrument(Instrument instrument){this.instrument = instrument;}
	
	public String getProjectName(){return projectName;}
	public void setProjectName(String projectName){
		this.projectName = projectName;
		fireCustomFileChanged();
	}
	
	public String getProjectId(){return projectId;}
	public void setProjectId(String projectId){
		this.projectId = projectId;
		fireCustomFileChanged();
	}
	
	public String getSampleName(){return sampleName;}
	public void setSampleName(String sampleName){
		this.sampleName = sampleName;
		fireCustomFileChanged();
	}
	
	public String getSampleDesc(){return sampleDesc;}
	public void setSampleDesc(String sampleDesc){
		this.sampleDesc = sampleDesc;
		fireCustomFileChanged();
	}
	
	public String getComments(){return comments;}
	public void setComments(String comments){
		this.comments = comments;
		fireCustomFileChanged();
	}
	
	public GridPoint getGridSize(){return gridSize;}
	public void setGridSize(GridPoint gridSize){
		this.gridSize = gridSize;
		fireCustomFileChanged();
	}
	
	public void populateFromHDF5File(File file){
		
		name = file.getName();

		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

		try{

			H5File h5File = (H5File) fileFormat.createInstance(file.getPath(), FileFormat.READ);
			h5File.open();
			H5Group rootH5Group = (H5Group) ((javax.swing.tree.DefaultMutableTreeNode) h5File.getRootNode()).getUserObject();
			List<Attribute> list = rootH5Group.getMetadata();

			gridSize = new GridPoint();
			projectName = "Not Entered";
			projectId = "Not Entered";
			sampleName = "Not Entered";
			sampleDesc = "Not Entered";
			comments = "Not Entered";
			expDate = Calendars.getCalendarFromUnixTimestamp(new java.util.Date().getTime()/1000.0);
			
			for(Attribute a: list){
				String name = a.getName();
				if(name.equals("grid_size_x")){
					HDF5DataType dataType = HDF5DataType.getHDF5DataType(a);
					if(dataType==HDF5DataType.INT){
						gridSize.setX(((int[]) a.getValue())[0]);
					}else if(dataType==HDF5DataType.CHAR){
						gridSize.setX((int)((byte[]) a.getValue())[0]);
					}
				}else if(name.equals("grid_size_y")){
					HDF5DataType dataType = HDF5DataType.getHDF5DataType(a);
					if(dataType==HDF5DataType.INT){
						gridSize.setY(((int[]) a.getValue())[0]);
					}else if(dataType==HDF5DataType.CHAR){
						gridSize.setY((int)((byte[]) a.getValue())[0]);
					}
				}else if(name.equals("instrument")){
					instrument = Instrument.valueOf(((String[]) a.getValue())[0].toUpperCase());
				}else if(name.equals("project_name")){
					projectName = ((String[]) a.getValue())[0];
				}else if(name.equals("project_id")){
					projectId = ((String[]) a.getValue())[0];
				}else if(name.equals("sample_name")){
					sampleName = ((String[]) a.getValue())[0];
				}else if(name.equals("sample_desc")){
					sampleDesc = ((String[]) a.getValue())[0];
				}else if(name.equals("comments")){
					comments = ((String[]) a.getValue())[0];
				}else if(name.equals("experiment_unix_time")){
					expDate = Calendars.getCalendarFromUnixTimestamp(((double[]) a.getValue())[0]);
				}
			}
			
			h5File.close();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void populateHDF5File(File file){
		
		name = file.getName();
		
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

		try{

			H5File h5File = (H5File) fileFormat.createInstance(file.getPath(), FileFormat.WRITE);
			h5File.open();
			H5Group rootH5Group = (H5Group) ((javax.swing.tree.DefaultMutableTreeNode) h5File.getRootNode()).getUserObject();
			
			//Delete the metadata that is already in the file if it exists
			deleteValueFromHDF5Group(rootH5Group, "data_file_index");
			deleteValueFromHDF5Group(rootH5Group, "sample_name");
			deleteValueFromHDF5Group(rootH5Group, "sample_desc");
			deleteValueFromHDF5Group(rootH5Group, "comments");
			
			//Write the data_file_index value
			writeIntValueToHDF5Group(h5File, rootH5Group, "data_file_index", index);
			
			//Write the 3 text values
			writeStringValueToHDF5Group(h5File, rootH5Group, "sample_name", sampleName);
			writeStringValueToHDF5Group(h5File, rootH5Group, "sample_desc", sampleDesc);
			writeStringValueToHDF5Group(h5File, rootH5Group, "comments", comments);
			
			h5File.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void deleteValueFromHDF5Group(H5Group h5Group, String nameToDelete) throws HDF5Exception{
		
		List<Attribute> list = h5Group.getMetadata();
		Attribute selectedAttribute = null;
		
		for(Attribute a: list){
			String name = a.getName();
			if(name.equals(nameToDelete)){
				selectedAttribute = a;
			}
		}

		if(selectedAttribute!=null){
			h5Group.removeMetadata(selectedAttribute);
		}
		
	}
	
	private void writeIntValueToHDF5Group(H5File h5File, H5Group h5Group, String name, int value) throws Exception{
		
		//Create an int data type for the value
		H5Datatype datatype = (H5Datatype) h5File.createDatatype(Datatype.CLASS_INTEGER, 4, Datatype.NATIVE, Datatype.NATIVE);
		
		//1D of size 1
		long[] dims = {1};

		//Create the value
		int[] values = {value};

		//Create an attribute object
		Attribute attribute = new Attribute(name, datatype, dims, values);

		//Write the attribute
		h5Group.writeMetadata(attribute);
		
	}
	
	private void writeStringValueToHDF5Group(H5File h5File, H5Group h5Group, String name, String value) throws Exception{
		
		//Create a custom data type for the value
		H5Datatype datatype = (H5Datatype) h5File.createDatatype(Datatype.CLASS_STRING, value.length(), Datatype.NATIVE, Datatype.NATIVE);
		
		//Create a String array of size one to hold the value
		String[] values = new String[1];

		//Assign the value to the first array index
		values[0] = value;

		//Create a byte array from values using the stringToByte method
		byte[] bvalue = Dataset.stringToByte(values, value.length());

		//Create an attribute object
		Attribute attribute = new Attribute(name, datatype, new long[]{1});

		//Set the value of the attribute to bvalue
		attribute.setValue(bvalue);

		//Write the attribute to the group's metadata
		h5Group.writeMetadata(attribute);
		
	}
	
}
