package org.camunda.community.examples;

public class KeyCloakCredentials {

	public boolean temporary;
	public String type;  
	public String value;
	public boolean getTemporary() {
		return temporary;
	}
	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
