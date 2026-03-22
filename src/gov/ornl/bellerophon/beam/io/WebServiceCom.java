/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: WebServiceCom.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.io;

import java.awt.Window;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.*;
import gov.ornl.bellerophon.beam.enums.*;
import gov.ornl.bellerophon.beam.ui.format.Calendars;

/**
 * The Class WebServiceCom.
 *
 * @author Eric J. Lingerfelt
 */
public class WebServiceCom {

	private String action
					, username
					, password
					, id
					, stack_trace
					, path
					, new_path
					, data_file_index
					, temp_data_file_name
					, name
					, new_name
					, instrument
					, project_name
					, project_id
					, sample_name
					, sample_desc
					, comments
					, exp_date
					, grid_size
					, num_cores
					, last_workflow_update_index
					, analysis_process_index
					, position_index
					, input_parameters
					, num_nodes
					, analysis_function_index
					, allocation_index
					, group_path
					, max_component_index
					, image_path
					, window_size;
	
	private int exceptionCounter = 0;

	private final String actionString = "ACTION";
	private final String usernameString = "USERNAME";
	private final String passwordString = "PASSWORD";
	private final String idString = "ID";
	private final String stack_traceString = "STACK_TRACE";
	private final String pathString = "PATH";
	private final String new_pathString = "NEW_PATH";
	private final String nameString = "NAME";
	private final String new_nameString = "NEW_NAME";
	private final String data_file_indexString = "DATA_FILE_INDEX";
	private final String temp_data_file_nameString = "TEMP_DATA_FILE_NAME";
	private final String instrumentString = "INSTRUMENT";
	private final String project_nameString = "PROJECT_NAME";
	private final String project_idString = "PROJECT_ID";
	private final String sample_nameString = "SAMPLE_NAME";
	private final String sample_descString = "SAMPLE_DESC";
	private final String commentsString = "COMMENTS";
	private final String exp_dateString = "EXP_DATE";
	private final String grid_sizeString = "GRID_SIZE";
	private final String num_coresString = "NUM_CORES";
	private final String last_workflow_update_indexString = "LAST_WORKFLOW_UPDATE_INDEX";
	private final String analysis_process_indexString = "ANALYSIS_PROCESS_INDEX";
	private final String position_indexString = "POSITION_INDEX";
	private final String input_parametersString = "INPUT_PARAMETERS";
	private final String num_nodesString = "NUM_NODES";
	private final String analysis_function_indexString = "ANALYSIS_FUNCTION_INDEX";
	private final String allocation_indexString = "ALLOCATION_INDEX";
	private final String group_pathString = "GROUP_PATH";
	private final String max_component_indexString = "MAX_COMPONENT_INDEX";
	private final String image_pathString = "IMAGE_PATH";
	private final String window_sizeString = "WINDOW_SIZE";
	
	private long totalBytesRead, totalBytesWritten;
	private WebServiceComParser parser = new WebServiceComParser();
	
	/**
	 * Gets the single instance of WebServiceCom.
	 *
	 * @return single instance of WebServiceCom
	 */
	public static WebServiceCom getInstance(){
		return new WebServiceCom();
	}
	
	/**
	 * Initialize.
	 */
	private void initialize(){
		action = "";
		username = "";
		password = "";
		id = "";
		stack_trace = "";
		path = "";
		new_path = "";
		name = "";
		new_name = "";
		data_file_index = "";
		temp_data_file_name = "";
		instrument = "";
		project_name = "";
		project_id = "";
		sample_name = "";
		sample_desc = "";
		comments = "";
		exp_date = "";
		grid_size = "";
		num_cores = "";
		last_workflow_update_index = "";
		analysis_process_index = "";
		position_index = "";
		input_parameters = "";
		num_nodes = "";
		analysis_function_index = "";
		allocation_index = "";
		group_path = "";
		max_component_index = "";
		image_path = "";
		window_size = "";
		totalBytesRead = 0L;
		totalBytesWritten = 0L;
	}
	
	/**
	 * Gets the total bytes read.
	 *
	 * @return the total bytes read
	 */
	public long getTotalBytesRead(){
		return totalBytesRead;
	}
	
	/**
	 * Gets the total bytes written.
	 *
	 * @return the total bytes written
	 */
	public long getTotalBytesWritten(){
		return totalBytesWritten;
	}
	
	/**
	 * Do web service com call.
	 *
	 * @param d the d
	 * @param action the action
	 * @return the error result
	 */
	public ErrorResult doWebServiceComCall(Data d, Action action, File downloadFile, BytesReadListener brl, Window owner){
		return doWebServiceComCall(d, action, downloadFile, null, brl, null, owner);
	}
	
	/**
	 * Do web service com call.
	 *
	 * @param d the d
	 * @param action the action
	 * @param filepath the filepath
	 * @return the error result
	 */
	public ErrorResult doWebServiceComCall(Data d, Action action, File uploadFile, BytesWrittenListener bwl, Window owner){
		return doWebServiceComCall(d, action, null, uploadFile, null, bwl, owner);
	}
	
	/**
	 * Do web service com call.
	 *
	 * @param d the d
	 * @param action the action
	 * @param frame the frame
	 * @return the error result
	 */
	public ErrorResult doWebServiceComCall(Data d, Action action, Window owner){
		return doWebServiceComCall(d, action, null, null, null, null, owner);
	}
	
	public ErrorResult doWebServiceComCall(Data d, Action action, BytesReadListener brl, Window owner){
		return doWebServiceComCall(d, action, null, null, brl, null, owner);
	}
	
	/**
	 * Do web service com call.
	 *
	 * @param d the d
	 * @param action the action
	 * @param frame the frame
	 * @return the error result
	 */
	public ErrorResult doWebServiceComCall(Data d, Action action){
		return doWebServiceComCall(d, action, null, null, null, null, null);
	}
	
	/**
	 * Do web service com call.
	 *
	 * @param d the d
	 * @param action the action
	 * @param filepath the filepath
	 * @param frame the frame
	 * @return the error result
	 */
	public ErrorResult doWebServiceComCall(Data d, Action action, 
												File downloadFile, File uploadFile, 
												BytesReadListener brl, BytesWrittenListener bwl, 
												Window owner){
		
		initialize();

		ErrorResult result = new ErrorResult();
		HashMap<String, String> map = getWebServiceComSubmitPropertyMap(action, d);
		String outputString = getOutputString(map);
		String formattedOutputString = getFormattedOutputString(map);
		String inputString = "";
		ArrayList<WebServiceComInputProperty> inputList = null;
		
		try{
			
			if(downloadFile==null){
				if(MainData.isDebug()){
					if(action==Action.GET_PCA_IMAGE_CLEANING_DIMS){
						downloadFile = new File("/Users/eric/Desktop/" + action + ".txt");
					}else{
						downloadFile = new File("/Users/eric/Desktop/" + action + ".h5");
					}
				}else{
					downloadFile = File.createTempFile("beam", null);
					downloadFile.deleteOnExit();
				}	
			}
			
			switch(action){
			
				//Data Actions
				case DOWNLOAD_DATA_FILE:
				case GET_DATA_FILE_TREE:
			
				//Analysis Actions
				case GET_RAW_DATA:
				case GET_PCA_IMAGE_CLEANING_DIMS:
				case GET_PCA_IMAGE_CLEANING_RESULTS:
				case GET_KMEANS_CLUSTERING_RESULTS:
				case GET_PCA_S_RESULTS:
				case GET_PCA_UV_RESULTS:
				case GET_SHO_FIT_RESULTS:
				case GET_INS_OPTIMIZATION_RESULTS:
				case GET_QENS_OPTIMIZATION_RESULTS:
				case GET_MEAN_SPECTROGRAM:
					if(MainData.isDebug()){
						System.out.println("***ACTION OUTPUT TO SERVER***");
						System.out.println(formattedOutputString);
					}
					inputString = transmitWebServiceComString(outputString, downloadFile, brl);
					if(!inputString.equals("")){
						inputList = getInputList(inputString);
						if(inputList.get(0).getProperty().equals("ERROR")){
							result.setError(true);
							result.setString(inputList.get(0).getValue());
							if(MainData.isDebug()){
								printExchange(formattedOutputString, inputString);
							}
							return result;
						}
					}
					parser.parse(action, d, downloadFile);
					break;
					
				//Session Actions
				case GET_ID:
				case GET_USER_DATA:
				case LOGOUT:
				case LOG_JAVA_EXCEPTION:
					
				//Data Actions
				case CREATE_DATA_FILE:
				case CREATE_DIR:
				case DATA_FILE_EXISTS:
				case DELETE_DATA_FILE:
				case DELETE_DIR:
				case GET_DATA_FILE_INFO:
				case GET_DIR_LISTING:
				case MOVE_DATA_FILE:
				case MOVE_DIR:
				case MOVE_TEMP_DATA_FILE:
				case RENAME_DATA_FILE:
				case RENAME_DIR:
				
				//Workflow Actions
				case ABORT_WORKFLOW:
				case GET_WORKFLOW_UPDATES:
					
				//Analysis Actions
				case APPLY_ANALYSIS_FUNCTION:
				case EXECUTE_INS_OPTIMIZATION:
				case EXECUTE_QENS_OPTIMIZATION:
				case GET_ANALYSIS_FUNCTIONS:
				case GET_ANALYSIS_PLATFORMS:
					inputString = transmitWebServiceComString(outputString, uploadFile, bwl);
					inputList = getInputList(inputString);
					if(inputList.size()==0){
						if(MainData.isDebug()){
							printExchange(formattedOutputString, inputString);
						}
						return result;
					}
					if(inputList.get(0).getProperty().equals("ERROR")){
						result.setError(true);
						result.setString(inputList.get(0).getValue());
						if(MainData.isDebug()){
							printExchange(formattedOutputString, inputString);
						}
						return result;
					}
					parser.parse(action, d, inputList);
					if(MainData.isDebug()){
						printExchange(formattedOutputString, inputString);
					}
					return result;
			}
			
		}catch(Exception e){
			e.printStackTrace();
			if(exceptionCounter==0){
				exceptionCounter++;
				doWebServiceComCall(d, action, downloadFile, uploadFile, brl, bwl, owner);
			}
			e.printStackTrace();
			inputString = "ERROR=An error has occurred connecting to the BEAM web server. "
							+ "Please check your internet connection or restart the software.";
		}
		
		return result;
	}
	
	/**
	 * Prints the exchange.
	 *
	 * @param out the out
	 * @param in the in
	 */
	private void printExchange(String formattedOutputString, String in){
		try{
			System.out.println("***ACTION OUTPUT TO SERVER***");
			System.out.println(formattedOutputString);
			if(!in.trim().equals("")){
				System.out.println("***ACTION INPUT FROM SERVER***");
				System.out.println(in.trim());
			}
			System.out.println("");
		}catch(Exception e){
			System.out.println("***ACTION OUTPUT TO SERVER***");
			System.out.println(formattedOutputString.trim());
			if(!in.trim().equals("")){
				System.out.println("***ACTION INPUT FROM SERVER***");
				System.out.println(in.trim());
			}
			System.out.println("");
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the file header.
	 *
	 * @param filename the filename
	 * @return the file header
	 */
	private String getFileHeader(String filename)
    {
        return "--" 
                + id
                + "\r\nContent-Disposition: form-data; name=\"beamfile\"; filename=\"" 
                + filename
                + "\"\r\nContent-type: binary\r\n\r\n";
    }
	
	/**
	 * Write file.
	 *
	 * @param os the os
	 * @param filepath the filepath
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void writeFile(OutputStream os, File uploadFile, BytesWrittenListener bwl) throws IOException
    {
        int buffer = 1024 * 10;
        FileInputStream is = new FileInputStream(uploadFile);
        byte[] data = new byte[buffer];
        int bytes;
        while ((bytes = is.read(data, 0, buffer)) > 0)
        {
        	os.write(data, 0, bytes);
        	totalBytesWritten += (long)bytes;
        	bwl.setBytesWritten(totalBytesWritten);
        }
        is.close();
    }
	
	private String transmitWebServiceComString(String inputString, File downloadFile, BytesReadListener brl) throws Exception{	
		
		URL url = new URL(MainData.PHP_URL);
		HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
		urlConnection.setRequestProperty("Content-type", "multipart/form-data; boundary=" + id);
		urlConnection.setDoOutput(true);
		
		String trailer = "\r\n--" + id + "--\r\n";
		OutputStream os = urlConnection.getOutputStream();
		os.write(inputString.getBytes());
		os.write(trailer.getBytes());
		os.close();
		
		if(brl!=null){
			brl.setContentLength(urlConnection.getContentLengthLong());
		}
		
		InputStream is = urlConnection.getInputStream();
		FileOutputStream fos = new FileOutputStream(downloadFile);
		IOUtilities.readStream(is, fos, brl);
		is.close();
		fos.close();
		
		//System.out.println(new String(IOUtilities.readFile(downloadFile)));
		
		String outputString = "";
		
		FileInputStream fis = new FileInputStream(downloadFile);
		byte[] array = new byte[5];
		fis.read(array);
		for(byte b: array){
			char c = (char) b;
			outputString += c;
		}
		fis.close();
		if(outputString.equals("ERROR")){
			fis = new FileInputStream(downloadFile);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtilities.readStream(fis, baos);
			outputString = baos.toString();
			baos.close();
			fis.close();
			downloadFile.delete();
		}else{
			outputString = "";
		}
		return outputString;
		
	}
	
	/**
	 * Transmit web service com string.
	 *
	 * @param inputString the input string
	 * @param filepath the filepath
	 * @return the string
	 */
	private String transmitWebServiceComString(String inputString, File uploadFile, BytesWrittenListener bwl) throws Exception{	
		URL url = new URL(MainData.PHP_URL);
		HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
		urlConnection.setRequestProperty("Content-type", "multipart/form-data; boundary=" + id);
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		if(uploadFile!=null){
			urlConnection.setChunkedStreamingMode(-1);
		}
		
		String trailer = "\r\n--" + id + "--\r\n";
		OutputStream os = urlConnection.getOutputStream();
		os.write(inputString.getBytes());
		if (uploadFile!=null){
			totalBytesWritten = 0L;
			String header = getFileHeader(uploadFile.getName());
			os.write(header.getBytes());
			writeFile(os, uploadFile, bwl);
		}
		os.write(trailer.getBytes());
		os.close();
		
		InputStream is = urlConnection.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtilities.readStream(is, baos);
		is.close();
		return baos.toString();
	}
	
	/**
	 * Gets the web service com submit property map.
	 *
	 * @param webServiceComAction the web service com action
	 * @param d the d
	 * @return the web service com submit property map
	 */
	private HashMap<String, String> getWebServiceComSubmitPropertyMap(Action webServiceComAction, Data d){
		
		HashMap<String, String> map = new HashMap<String, String>();

		action = webServiceComAction.toString();
		
		id = MainData.getID();
		if(d!=null){
			if(d instanceof User){
				User u = (User)d;
				username = u.getUsername();
				password = u.getPassword();
			}
			if(d instanceof UncaughtException){
				UncaughtException ued = (UncaughtException)d;
				stack_trace = ued.getStackTrace();
			}
			if(d instanceof CustomFile){
				CustomFile cf = (CustomFile)d;
				path = String.valueOf(cf.getPath());
				name = String.valueOf(cf.getName());
				new_path = String.valueOf(cf.getNewPath());
				new_name = String.valueOf(cf.getNewName());
			}
			if(d instanceof DataFile){
				DataFile df = (DataFile)d;
				data_file_index = String.valueOf(df.getIndex());
				temp_data_file_name = String.valueOf(df.getTempName());
				if(df.getInstrument()!=null){
					instrument = df.getInstrument().name();
				}
				project_name = df.getProjectName();
				project_id = df.getProjectId();
				sample_name = df.getSampleName();
				sample_desc = df.getSampleDesc();
				comments = df.getComments();
				exp_date = Calendars.getFormattedDateString(df.getExpDate());
				if(df.getGridSize()!=null){
					grid_size = df.getGridSize().toString();
				}
			}
			if(d instanceof MeanSpectrogramDataSet){
				MeanSpectrogramDataSet msds = (MeanSpectrogramDataSet)d;
				data_file_index = String.valueOf(msds.getDataFileIndex());
			}
			if(d instanceof SHOFitDataSet){
				SHOFitDataSet sfds = (SHOFitDataSet)d;
				data_file_index = String.valueOf(sfds.getDataFileIndex());
				if(sfds.getSelectedGridPoint()!=null){
					position_index = String.valueOf(sfds.getSelectedGridPoint().getPositionIndex());
				}
			}
			if(d instanceof PCADataSet){
				PCADataSet pds = (PCADataSet)d;
				data_file_index = String.valueOf(pds.getDataFileIndex());
				group_path = pds.getGroupPath();
				max_component_index = String.valueOf(pds.getMaxComponentIndex());
			}
			if(d instanceof PCAImageCleaningDataSet){
				PCAImageCleaningDataSet picds = (PCAImageCleaningDataSet)d;
				image_path = picds.getImagePath();
				window_size = String.valueOf(picds.getWindowSize());
			}
			if(d instanceof KMeansClusteringDataSet){
				KMeansClusteringDataSet kmcds = (KMeansClusteringDataSet)d;
				data_file_index = String.valueOf(kmcds.getDataFileIndex());
				group_path = kmcds.getGroupPath();
			}
			if(d instanceof AnalysisProcess){
				AnalysisProcess ap = (AnalysisProcess) d;
				analysis_process_index = String.valueOf(ap.getIndex());
				num_cores = String.valueOf(ap.getNumCoresUsed());
				num_nodes = String.valueOf(ap.getNumNodes());
				if(ap.getDataFile()!=null){
					data_file_index = String.valueOf(ap.getDataFile().getIndex());
				}
				if(ap.getAllocation()!=null){
					allocation_index = String.valueOf(ap.getAllocation().getIndex());
				}
				if(ap.getAnalysisFunction()!=null){
					analysis_function_index = String.valueOf(ap.getAnalysisFunction().getIndex());
				}
				last_workflow_update_index = String.valueOf(ap.getLastWorkflowUpdateIndex());
				username = ap.getUsername();
				password = ap.getPassword();
				input_parameters = ap.getInputParameters();
			}
		}
		
		map.put(actionString, action);
		if(webServiceComAction!=Action.GET_ID){
			map.put(idString, id);
		}
		
		switch(webServiceComAction){
		
			//Session Actions
			case GET_ID:
				map.put(usernameString, username);
				map.put(passwordString, password);
				break;
				
			case GET_USER_DATA:
				map.put(usernameString, username);
				break;
				
			case LOG_JAVA_EXCEPTION:
				map.put(stack_traceString, stack_trace);
				break;
			
			//Data Actions
			case CREATE_DATA_FILE:
				map.put(pathString, 		path);
				map.put(nameString, 		name);
				map.put(instrumentString, 	instrument);
				map.put(project_nameString, project_name);
				map.put(project_idString, 	project_id);
				map.put(sample_nameString, 	sample_name);
				map.put(sample_descString, 	sample_desc);
				map.put(commentsString, 	comments);
				map.put(grid_sizeString, 	grid_size);
				map.put(exp_dateString, 	exp_date);
				break;
				
			case CREATE_DIR:
				map.put(pathString, path);
				map.put(nameString, name);
				break;
			
			case DATA_FILE_EXISTS:
				map.put(pathString, path);
				map.put(nameString, name);
				break;
				
			case DELETE_DATA_FILE:
				map.put(data_file_indexString, data_file_index);
				break;
				
			case DELETE_DIR:
				map.put(pathString, path);
				map.put(nameString, name);
				break;
	
			case DOWNLOAD_DATA_FILE:
				map.put(data_file_indexString, data_file_index);
				break;
				
			case GET_DATA_FILE_INFO:
				map.put(pathString, path);
				map.put(nameString, name);
				break;
				
			case GET_DATA_FILE_TREE:
				map.put(pathString, path);
				map.put(nameString, name);
				break;
				
			case GET_DIR_LISTING:
				map.put(pathString, path);
				map.put(nameString, name);
				break;
				
			case MOVE_DATA_FILE:
				map.put(pathString, path);
				map.put(nameString, name);
				map.put(new_pathString, new_path);
				break;
				
			case MOVE_DIR:
				map.put(pathString, path);
				map.put(nameString, name);
				map.put(new_pathString, new_path);
				break;
				
			case MOVE_TEMP_DATA_FILE:
				map.put(pathString, path);
				map.put(nameString, name);
				map.put(temp_data_file_nameString, temp_data_file_name);
				break;
		
			case RENAME_DATA_FILE:
				map.put(data_file_indexString, data_file_index);
				map.put(new_nameString, new_name);
				break;
				
			case RENAME_DIR:
				map.put(pathString, path);
				map.put(nameString, name);
				map.put(new_nameString, new_name);
				break;
				
			//Analysis Actions
			case APPLY_ANALYSIS_FUNCTION:
				map.put(data_file_indexString, data_file_index);
				map.put(allocation_indexString, allocation_index);
				map.put(num_coresString, num_cores);
				map.put(num_nodesString, num_nodes);
				map.put(analysis_function_indexString, analysis_function_index);
				map.put(usernameString, username);
				map.put(passwordString, password);
				map.put(input_parametersString, input_parameters);
				break;
				
			case EXECUTE_INS_OPTIMIZATION:
				map.put(input_parametersString, input_parameters);
				map.put(num_coresString, num_cores);
				break;
				
			case EXECUTE_QENS_OPTIMIZATION:
				map.put(input_parametersString, input_parameters);
				map.put(num_coresString, num_cores);
				break;
			
			case GET_RAW_DATA:
				map.put(data_file_indexString, data_file_index);
				map.put(position_indexString, position_index);
				break;
			
			case GET_PCA_IMAGE_CLEANING_DIMS:
				map.put(data_file_indexString, data_file_index);
				map.put(image_pathString, image_path);
				map.put(window_sizeString, window_size);
				break;	
			
			case GET_KMEANS_CLUSTERING_RESULTS:
				map.put(data_file_indexString, data_file_index);
				map.put(group_pathString, group_path);
				break;	
			
			case GET_PCA_IMAGE_CLEANING_RESULTS:
				map.put(data_file_indexString, data_file_index);
				map.put(group_pathString, group_path);
				break;
				
			case GET_PCA_S_RESULTS:
				map.put(data_file_indexString, data_file_index);
				map.put(group_pathString, group_path);
				break;
				
			case GET_PCA_UV_RESULTS:
				map.put(data_file_indexString, data_file_index);
				map.put(group_pathString, group_path);
				map.put(max_component_indexString, max_component_index);
				break;
				
			case GET_SHO_FIT_RESULTS:
				map.put(data_file_indexString, data_file_index);
				break;
				
			case GET_MEAN_SPECTROGRAM:
				map.put(data_file_indexString, data_file_index);
				break;
				
			//Workflow Actions
			case ABORT_WORKFLOW:
				map.put(analysis_process_indexString, analysis_process_index);
				map.put(usernameString, username);
				map.put(passwordString, password);
				break;	
			
			case GET_WORKFLOW_UPDATES:
				map.put(last_workflow_update_indexString, last_workflow_update_index);
				map.put(analysis_process_indexString, analysis_process_index);
				break;

		}
		
		return map;
		
	}
	
	/**
	 * Gets the output string.
	 *
	 * @param map the map
	 * @return the output string
	 */
	private String getOutputString(HashMap<String, String> map){
		String string = "";
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()){
			String key = itr.next();
			string += "--" 
                + id
                + "\r\n" 
                + "Content-Disposition: form-data; name=" 
                + "\""
                + key
                + "\"\r\n\r\n" 
                + map.get(key)
                + "\r\n";
		}
		return string;
	}
	
	private String getFormattedOutputString(HashMap<String, String> map){
		String string = "";
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()){
			String key = itr.next();
			string += key
						+ "=" 
						+ map.get(key)
						+ "\n";
		}
		return string;
	}
	
	/**
	 * Gets the input list.
	 *
	 * @param string the string
	 * @return the input list
	 */
	private ArrayList<WebServiceComInputProperty> getInputList(String string){
		ArrayList<WebServiceComInputProperty> list = new ArrayList<WebServiceComInputProperty>();
		if(string.indexOf("\n")!=-1){
			String[] array = string.split("\n");
			for(String substring: array){
				if(substring.indexOf("=")!=-1){
					String[] subarray = substring.split("=", 2);
					String property = subarray[0].trim();
					String value = "";
					if(subarray.length==2){
						value = subarray[1].trim();
					}
					if(!addToInputList(list, property, value)){
						return list;
					}
				}
			}
		}else{
			if(string.indexOf("=")!=-1){
				String[] array = string.split("=");
				String property = array[0].trim();
				String value = array[1].trim();
				if(!addToInputList(list, property, value)){
					return list;
				}
			}
		}
		return list;
	}
	
	/**
	 * Adds the to input list.
	 *
	 * @param list the list
	 * @param property the property
	 * @param value the value
	 * @return true, if successful
	 */
	private boolean addToInputList(ArrayList<WebServiceComInputProperty> list, String property, String value){
		if(!property.equals("ERROR")){
			if(!value.equals("")){
				WebServiceComInputProperty prop = new WebServiceComInputProperty(property, value);
				list.add(prop);
			}
			return true;
		}
		list.clear();
		WebServiceComInputProperty prop = new WebServiceComInputProperty(property, value);
		list.add(prop);
		return false;
	}
	
}

class WebServiceComInputProperty{
	private String property;
	private String value;
	
	public WebServiceComInputProperty(String property, String value){
		this.property = property;
		this.value = value;
	}
	
	public String getProperty(){return property;}
	public String getValue(){return value;}
}

class WebServiceComParser{
	
	public void parse(Action action
							, Data d
							, ArrayList<WebServiceComInputProperty> inputList){
		
		switch(action){
			
			//Session Actions
			case GET_ID:
				parseGET_ID(inputList);
				break;
			case GET_USER_DATA:
				parseGET_USER_DATA(inputList, (User)d);
				break;
				
			//Data Actions
			case CREATE_DATA_FILE:
				parseCREATE_DATA_FILE(inputList, (DataFile)d);
				break;
			case DATA_FILE_EXISTS:
				parseDATA_FILE_EXISTS(inputList, (DataFile)d);
				break;
			case GET_DATA_FILE_INFO:
				parseGET_DATA_FILE_INFO(inputList, (DataFile)d);
				break;
			case GET_DIR_LISTING:
				parseGET_DIR_LISTING(inputList, (CustomFile)d);
				break;
			
			//Analysis Actions
			case GET_ANALYSIS_FUNCTIONS:
				parseGET_ANALYSIS_FUNCTIONS(inputList, (Data)d);
				break;
			case GET_ANALYSIS_PLATFORMS:
				parseGET_ANALYSIS_PLATFORMS(inputList, (Data)d);
				break;
			case APPLY_ANALYSIS_FUNCTION:
				parseAPPLY_ANALYSIS_FUNCTION(inputList, (AnalysisProcess)d);
				break;
				
			//Workflow Actions
			case GET_WORKFLOW_UPDATES:
				parseGET_WORKFLOW_UPDATES(inputList, (AnalysisProcess)d);
				break;
			
		}
	}
	
	public void parse(Action action
							, Data d
							, File file){
		
		switch(action){
		
			//Analysis Actions
			case GET_RAW_DATA:
				parseGET_RAW_DATA(file, (SHOFitDataSet)d);
				break;
			case GET_PCA_IMAGE_CLEANING_DIMS:
				parseGET_PCA_IMAGE_CLEANING_DIMS(file, (PCAImageCleaningDataSet)d);
				break;
			case GET_PCA_IMAGE_CLEANING_RESULTS:
				parseGET_PCA_IMAGE_CLEANING_RESULTS(file, (PCAImageCleaningDataSet)d);
				break;
			case GET_KMEANS_CLUSTERING_RESULTS:
				parseGET_KMEANS_CLUSTERING_RESULTS(file, (KMeansClusteringDataSet)d);
				break;
			case GET_PCA_S_RESULTS:
				parseGET_PCA_S_RESULTS(file, (PCADataSet)d);
				break;
			case GET_PCA_UV_RESULTS:
				parseGET_PCA_UV_RESULTS(file, (PCADataSet)d);
				break;
			case GET_SHO_FIT_RESULTS:
				parseGET_SHO_FIT_RESULTS(file, (SHOFitDataSet)d);
				break;
			case GET_INS_OPTIMIZATION_RESULTS:
				parseGET_INS_OPTIMIZATION_RESULTS(file, (SNSData)d);
				break;
			case GET_QENS_OPTIMIZATION_RESULTS:
				parseGET_QENS_OPTIMIZATION_RESULTS(file, (SNSData)d);
				break;
			case GET_MEAN_SPECTROGRAM:
				parseGET_MEAN_SPECTROGRAM(file, (MeanSpectrogramDataSet)d);
				break;
				
			//Data Actions
			case GET_DATA_FILE_TREE:
				parseGET_DATA_FILE_TREE(file, (DataFile)d);
				break;
			
		}
	}
	
	//Session Actions
	private void parseGET_ID(ArrayList<WebServiceComInputProperty> inputList){
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("ID")){
				MainData.setID(value);
			}
		}
	}
	
	private void parseGET_USER_DATA(ArrayList<WebServiceComInputProperty> inputList, User u){
		TreeMap<Integer, Allocation> map = new TreeMap<Integer, Allocation>();
		u.setAllocationMap(map);
		Allocation allocation = null;
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("EMAIL")){
				u.setEmail(value);
			}else if(prop.getProperty().equals("FIRST_NAME")){
				u.setFirstName(value);
			}else if(prop.getProperty().equals("LAST_NAME")){
				u.setLastName(value);
			}else if(prop.getProperty().equals("SCP_PRIVATE_KEY")){
				u.setScpPrivateKey(value);
			}else if(prop.getProperty().equals("ALLOCATION_INDEX")){
				allocation = new Allocation();
				allocation.setIndex(Integer.valueOf(value));
				map.put(allocation.getIndex(), allocation);
			}else if(prop.getProperty().equals("ALLOCATION")){
				allocation.setName(value);
			}else if(prop.getProperty().equals("USER_FACILITY")){
				allocation.setUserFacility(UserFacility.valueOf(value.toUpperCase()));
			}else if(prop.getProperty().equals("LAST_LOGIN_IP")){
				u.setLastLoginIP(value);
			}else if(prop.getProperty().equals("LAST_LOGIN_HOST")){
				u.setLastLoginHost(value);
			}else if(prop.getProperty().equals("LAST_LOGIN_DATE")){
				u.setLastLogInDate(Calendars.getCalendar(value));
			}
		}
	}
	
	//Data Actions
	private void parseCREATE_DATA_FILE(ArrayList<WebServiceComInputProperty> inputList, DataFile df){
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("DATA_FILE_INDEX")){
				df.setIndex(Integer.valueOf(value));
			}else if(prop.getProperty().equals("UPLOAD_DATE")){
				df.setUploadDate(Calendars.getCalendar(value));
			}
		}
	}
	
	private void parseDATA_FILE_EXISTS(ArrayList<WebServiceComInputProperty> inputList, DataFile df){
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("DATA_FILE_EXISTS")){
				df.setExists(value.equals("0") ? false : true);
			}
		}
	}
	
	private void parseGET_DATA_FILE_INFO(ArrayList<WebServiceComInputProperty> inputList, DataFile df){
		GridPoint gridSize = null;
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("DATA_FILE_INDEX")){
				df.setIndex(Integer.valueOf(value));
			}else if(prop.getProperty().equals("INSTRUMENT")){
				df.setInstrument(Instrument.valueOf(value.toUpperCase()));
			}else if(prop.getProperty().equals("PROJECT_NAME")){
				df.setProjectName(value);
			}else if(prop.getProperty().equals("PROJECT_ID")){
				df.setProjectId(value);
			}else if(prop.getProperty().equals("SAMPLE_NAME")){
				df.setSampleName(value);
			}else if(prop.getProperty().equals("SAMPLE_DESC")){
				df.setSampleDesc(value);
			}else if(prop.getProperty().equals("COMMENTS")){
				df.setComments(value);
			}else if(prop.getProperty().equals("GRID_SIZE_X")){
				gridSize = new GridPoint();
				df.setGridSize(gridSize);
				gridSize.setX(Integer.valueOf(value));
			}else if(prop.getProperty().equals("GRID_SIZE_Y")){
				gridSize.setY(Integer.valueOf(value));
			}else if(prop.getProperty().equals("UPLOAD_DATE")){
				df.setUploadDate(Calendars.getCalendar(value));
			}else if(prop.getProperty().equals("EXP_DATE")){
				df.setExpDate(Calendars.getCalendar(value));
			}else if(prop.getProperty().equals("SIZE")){
				df.setSize(Long.parseLong(value));
			}
		}
	}
	
	private void parseGET_DATA_FILE_TREE(File file, DataFile df){
		try{
			df.populateTreeModelFromHDF5File(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_DIR_LISTING(ArrayList<WebServiceComInputProperty> inputList, CustomFile cf){
		CustomFile f = null;
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("IS_DIR")){
				boolean isDir = Boolean.parseBoolean(value);
				if(isDir){
					f = new CustomFile();
				}else{
					f = new DataFile();
				}
				f.setDir(isDir);
			}else if(prop.getProperty().equals("NAME")){
				f.setName(value);
				f.setParent(cf);
				if(cf.getName().equals(MainData.getUser().getUsername())){
					f.setPath(cf.getName());
				}else{
					f.setPath(cf.getPath() + "/" + cf.getName());
				}
				cf.addFile(f);
			}else if(prop.getProperty().equals("IS_POP")){
				f.setPop(Boolean.parseBoolean(value));
			}else if(prop.getProperty().equals("SIZE")){
				f.setSize(Long.parseLong(value));
			}else if(prop.getProperty().equals("DATA_FILE_INDEX")){
				((DataFile)f).setIndex(Integer.parseInt(value));
			}
		}
	}
	
	//Analysis Actions
	private void parseAPPLY_ANALYSIS_FUNCTION(ArrayList<WebServiceComInputProperty> inputList, AnalysisProcess ap){
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("ANALYSIS_PROCESS_INDEX")){
				ap.setIndex(Integer.valueOf(value));
			}else if(prop.getProperty().equals("TOTAL_TIME")){
				ap.setTotalTime(Double.valueOf(value));
			}
		}
	}
	
	private void parseGET_MEAN_SPECTROGRAM(File file, MeanSpectrogramDataSet msds){
		try{
			msds.populateFromHDF5File(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_ANALYSIS_FUNCTIONS(ArrayList<WebServiceComInputProperty> inputList, Data ad){
		AnalysisFunction af = null;
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("ANALYSIS_FUNCTION_INDEX")){
				af = new AnalysisFunction();
				af.setIndex(Integer.valueOf(value));
				MainData.addAnalysisFunction(af);
			}else if(prop.getProperty().equals("ANALYSIS_FUNCTION_TYPE")){
				af.setAnalysisFunctionType(AnalysisFunctionType.valueOf(value.toUpperCase()));
			}else if(prop.getProperty().equals("ANALYSIS_PLATFORM_NAME")){
				af.setAnalysisPlatform(MainData.getAnalysisPlatform(value));
			}else if(prop.getProperty().equals("IMPLEMENTATION")){
				af.setAnalysisFunctionImplementation(AnalysisFunctionImplementation.valueOf(value.toUpperCase()));
			}
		}
	}
	
	private void parseGET_ANALYSIS_PLATFORMS(ArrayList<WebServiceComInputProperty> inputList, Data ad){
		AnalysisPlatform ap = null;
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("ANALYSIS_PLATFORM_NAME")){
				ap = new AnalysisPlatform();
				ap.setName(value);
				MainData.addAnalysisPlatform(ap);
			}else if(prop.getProperty().equals("ANALYSIS_PLATFORM_TYPE")){
				ap.setAnalysisPlatformType(AnalysisPlatformType.valueOf(value.toUpperCase()));
			}else if(prop.getProperty().equals("USER_FACILITY")){
				ap.setUserFacility(UserFacility.valueOf(value.toUpperCase()));
			}else if(prop.getProperty().equals("NUM_NODES_MAX")){
				ap.setNumNodesMax(Integer.valueOf(value));
			}else if(prop.getProperty().equals("NUM_CORES_PER_NODE")){
				ap.setNumCoresPerNode(Integer.valueOf(value));
			}
		}
	}
	
	private void parseGET_RAW_DATA(File file, SHOFitDataSet sfds){
		try{
			sfds.populateRawDataFromHDF5File(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_PCA_IMAGE_CLEANING_DIMS(File file, PCAImageCleaningDataSet picds){
		try{
			picds.populateDimsFromTextFile(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_PCA_IMAGE_CLEANING_RESULTS(File file, PCAImageCleaningDataSet picds){
		try{
			picds.populateResultsFromHDF5File(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_KMEANS_CLUSTERING_RESULTS(File file, KMeansClusteringDataSet kcds){
		try{
			kcds.populateResultsFromHDF5File(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_PCA_S_RESULTS(File file, PCADataSet pds){
		try{
			pds.populateSFromHDF5File(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_PCA_UV_RESULTS(File file, PCADataSet pds){
		try{
			pds.populateUVFromHDF5File(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_SHO_FIT_RESULTS(File file, SHOFitDataSet sfds){
		try{
			sfds.populateFromHDF5File(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_INS_OPTIMIZATION_RESULTS(File file, SNSData sd){
		try{
			sd.populateFromResultsZipFile(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseGET_QENS_OPTIMIZATION_RESULTS(File file, SNSData sd){
		try{
			sd.populateFromResultsZipFile(file);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//Workflow Actions
	private void parseGET_WORKFLOW_UPDATES(ArrayList<WebServiceComInputProperty> inputList, AnalysisProcess ap){
		WorkflowUpdate wu = null;
		for(WebServiceComInputProperty prop: inputList){
			String value = prop.getValue();
			if(prop.getProperty().equals("WORKFLOW_UPDATE_INDEX")){
				wu = new WorkflowUpdate();
				wu.setIndex(Integer.valueOf(value));
				ap.getStatusMap().put(wu.getIndex(), wu);
			}else if(prop.getProperty().equals("CREATE_DATE")){
				wu.setCreateDate(Calendars.getCalendar(value));
			}else if(prop.getProperty().equals("TYPE")){
				wu.setType(WorkflowUpdateType.valueOf(value));
			}else if(prop.getProperty().equals("VALUE")){
				wu.setValue(value);
			}
		}	
	}
	
}
