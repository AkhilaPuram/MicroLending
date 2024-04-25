package org.camunda.community.examples;

public class TaskVariables {

	private  String name="";
	private String id="";
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private  String value="";
	private  String operator="";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
}
