/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: Action.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.enums;

/**
 * The Enum Action contains values for each type of allowable web service calls.
 *
 * @author Eric J. Lingerfelt
 */
public enum Action{
	
	//Session Actions
	GET_ID,
	GET_USER_DATA,
	LOGOUT,
	LOG_JAVA_EXCEPTION,
	
	//Data Actions
	CREATE_DATA_FILE,
	CREATE_DIR,
	DATA_FILE_EXISTS,
	DELETE_DATA_FILE,
	DELETE_DIR,
	DOWNLOAD_DATA_FILE,
	GET_DATA_FILE_INFO,
	GET_DATA_FILE_TREE,
	GET_DIR_LISTING,
	MOVE_DATA_FILE,
	MOVE_DIR,
	MOVE_TEMP_DATA_FILE,
	RENAME_DATA_FILE,
	RENAME_DIR,
	
	//Analysis Actions
	APPLY_ANALYSIS_FUNCTION,
	EXECUTE_QENS_OPTIMIZATION,
	EXECUTE_INS_OPTIMIZATION,
	GET_ANALYSIS_FUNCTIONS,
	GET_ANALYSIS_PLATFORMS,
	GET_RAW_DATA,
	GET_KMEANS_CLUSTERING_RESULTS,
	GET_MEAN_SPECTROGRAM,
	GET_PCA_IMAGE_CLEANING_RESULTS,
	GET_PCA_IMAGE_CLEANING_DIMS,
	GET_PCA_S_RESULTS,
	GET_PCA_UV_RESULTS,
	GET_SHO_FIT_RESULTS,
	GET_QENS_OPTIMIZATION_RESULTS,
	GET_INS_OPTIMIZATION_RESULTS,
	
	//Workflow actions
	ABORT_WORKFLOW,
	GET_WORKFLOW_UPDATES
	
}
