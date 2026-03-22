package gov.ornl.bellerophon.beam.data.util;

import java.util.Calendar;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.enums.WorkflowUpdateType;
import gov.ornl.bellerophon.beam.ui.format.Calendars;

public class WorkflowUpdate implements Data{

	private int index;
	private String value;
	private Calendar createDate;
	private WorkflowUpdateType type;
	
	public WorkflowUpdate(){
		initialize();
	}
	
	public WorkflowUpdate clone(){
		WorkflowUpdate ws = new WorkflowUpdate();
		return ws;
	}
	
	public void initialize(){
		index = -1;
		value = "";
		type = null;
		createDate = Calendars.getDefaultCalendar();
	}
	
	public Calendar getCreateDate(){return createDate;}
	public void setCreateDate(Calendar createDate){this.createDate = createDate;}
	
	public String getValue(){return value;}
	public void setValue(String value){this.value = value;}
	
	public int getIndex(){return index;}
	public void setIndex(int index){this.index = index;}
	
	public WorkflowUpdateType getType(){return type;}
	public void setType(WorkflowUpdateType type){this.type = type;}
	
}
