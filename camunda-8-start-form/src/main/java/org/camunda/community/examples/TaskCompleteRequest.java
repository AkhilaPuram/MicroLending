package org.camunda.community.examples;

import java.util.ArrayList;

public class TaskCompleteRequest {
		  private String state;
		  private boolean assigned;
		  private String assignee;
		  private String taskDefinitionId;
		  private String candidateGroup;
		  private String candidateUser;
		  private String processDefinitionKey;
		  private String processInstanceKey;
		  private float pageSize;
		  private String id;
		  private String name;
		  public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public String getCreationDate() {
			return creationDate;
		}

		public void setCreationDate(String creationDate) {
			this.creationDate = creationDate;
		}

		public String getCompletionDate() {
			return completionDate;
		}

		public void setCompletionDate(String completionDate) {
			this.completionDate = completionDate;
		}

		public String getTaskState() {
			return taskState;
		}

		public void setTaskState(String taskState) {
			this.taskState = taskState;
		}

		public String getFormKey() {
			return formKey;
		}

		public void setFormKey(String formKey) {
			this.formKey = formKey;
		}

		public boolean isFirst() {
			return isFirst;
		}

		public void setFirst(boolean isFirst) {
			this.isFirst = isFirst;
		}

		public ArrayList<TaskVariables> getTaskVariables() {
			return taskVariables;
		}

		public void setTaskVariables(ArrayList<TaskVariables> taskvaribales) {
			this.taskVariables = taskvaribales;
		}

				
		private String processName;
		  private String creationDate;
		  private String completionDate;
		  private String taskState;
		  private String formKey;
		  private boolean isFirst;
		  
		  
		  
		  ArrayList<TaskVariables> taskVariables = new ArrayList<TaskVariables>();
		

		 // Getter Methods 
		  
		  
		  

		  public String getState() {
		    return state;
		  }

		  public boolean getAssigned() {
		    return assigned;
		  }

		  public String getAssignee() {
		    return assignee;
		  }

		  public String getTaskDefinitionId() {
		    return taskDefinitionId;
		  }

		  public String getCandidateGroup() {
		    return candidateGroup;
		  }

		  public String getCandidateUser() {
		    return candidateUser;
		  }

		  public String getProcessDefinitionKey() {
		    return processDefinitionKey;
		  }

		  public String getProcessInstanceKey() {
		    return processInstanceKey;
		  }

		  public float getPageSize() {
		    return pageSize;
		  }

		 

		 // Setter Methods 

		  public void setState( String state ) {
		    this.state = state;
		  }

		  public void setAssigned( boolean assigned ) {
		    this.assigned = assigned;
		  }

		  public void setAssignee( String assignee ) {
		    this.assignee = assignee;
		  }

		  public void setTaskDefinitionId( String taskDefinitionId ) {
		    this.taskDefinitionId = taskDefinitionId;
		  }

		  public void setCandidateGroup( String candidateGroup ) {
		    this.candidateGroup = candidateGroup;
		  }

		  public void setCandidateUser( String candidateUser ) {
		    this.candidateUser = candidateUser;
		  }

		  public void setProcessDefinitionKey( String processDefinitionKey ) {
		    this.processDefinitionKey = processDefinitionKey;
		  }

		  public void setProcessInstanceKey( String processInstanceKey ) {
		    this.processInstanceKey = processInstanceKey;
		  }

		  public void setPageSize( float pageSize ) {
		    this.pageSize = pageSize;
		  }

		  
		}

	