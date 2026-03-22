package gov.ornl.bellerophon.beam.data.util;

import java.util.TreeMap;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.AnalysisFunctionType;

public class AnalysisProcess implements Data{

	private int index;
	private DataFile dataFile;
	private AnalysisFunction analysisFunction;
	private AnalysisFunctionType analysisFunctionType;
	private Allocation allocation;
	private int numNodes, numCores, numCoresUsed;
	private TreeMap<Integer, WorkflowUpdate> statusMap;
	private String username, password;
	private boolean executing;
	private double totalTime;
	private String inputParameters;
	
	public AnalysisProcess(){
		initialize();
	}
	
	public AnalysisProcess clone(){
		AnalysisProcess ap = new AnalysisProcess();
		return ap;
	}
	
	public void initialize(){
		index = -1;
		dataFile = null;
		analysisFunction = null;
		statusMap = new TreeMap<Integer, WorkflowUpdate>();
		numNodes = -1;
		numCores = -1;
		numCoresUsed = -1;
		executing = false;
		totalTime = 0.0;
		username = "";
		password = "";
		inputParameters = "";
		analysisFunctionType = null;
		allocation = null;
	}
	
	public AnalysisFunctionType getAnalysisFunctionType(){return analysisFunctionType;}
	public void setAnalysisFunctionType(AnalysisFunctionType analysisFunctionType){this.analysisFunctionType = analysisFunctionType;}
	
	public int getIndex(){return index;}
	public void setIndex(int index){this.index = index;}
	
	public Allocation getAllocation(){return allocation;}
	public void setAllocation(Allocation allocation){this.allocation = allocation;}
	
	public String getUsername(){return username;}
	public void setUsername(String username){this.username = username;}
	
	public String getInputParameters(){return inputParameters;}
	public void setInputParameters(String inputParameters){this.inputParameters = inputParameters;}
	
	public String getPassword(){return password;}
	public void setPassword(String password){this.password = password;}
	
	public double getTotalTime(){return totalTime;}
	public void setTotalTime(double totalTime){this.totalTime = totalTime;}
	
	public boolean isExecuting(){return executing;}
	public void setExecuting(boolean executing){this.executing = executing;}
	
	public DataFile getDataFile(){return dataFile;}
	public void setDataFile(DataFile dataFile){this.dataFile = dataFile;}
	
	public AnalysisFunction getAnalysisFunction(){return analysisFunction;}
	public void setAnalysisFunction(AnalysisFunction analysisFunction){this.analysisFunction = analysisFunction;}
	
	public int getNumCores(){return numCores;}
	public void setNumCores(int numCores){this.numCores = numCores;}
	
	public int getNumCoresUsed(){return numCoresUsed;}
	public void setNumCoresUsed(int numCoresUsed){this.numCoresUsed = numCoresUsed;}
	
	public int getNumNodes(){return numNodes;}
	public void setNumNodes(int numNodes){this.numNodes = numNodes;}
	
	public TreeMap<Integer, WorkflowUpdate> getStatusMap(){return statusMap;}
	
	public int getLastWorkflowUpdateIndex(){
		if(statusMap.size()==0){
			return -1;
		}
		return statusMap.lastKey();
	}
	
	public void clearStatusMap(){
		statusMap = new TreeMap<Integer, WorkflowUpdate>();
	}
}
