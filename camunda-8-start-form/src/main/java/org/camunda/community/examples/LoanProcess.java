package org.camunda.community.examples;

public class LoanProcess {

	private String Loantype;
	private Double Loanamount;
	private String Firstname;
	private String Lastname;
	private String Phonenumber;
	private String Email;
	private String requestFrom;
	private String ProcessID;
	private String TaskID;
	
	
	public String getProcessID() {
		return ProcessID;
	}
	public void setProcessID(String processID) {
		ProcessID = processID;
	}
	public String getTaskID() {
		return TaskID;
	}
	public void setTaskID(String taskID) {
		TaskID = taskID;
	}
	public String getRequestFrom() {
		return requestFrom;
	}
	public void setRequestFrom(String source) {
		requestFrom = source;
	}
	public String getLoantype() {
		return Loantype;
	}
	public void setLoantype(String loantype) {
		Loantype = loantype;
	}
	public Double getLoanamount() {
		return Loanamount;
	}
	public void setLoanamount(Double loanamount) {
		Loanamount = loanamount;
	}
	public String getFirstname() {
		return Firstname;
	}
	public void setFirstname(String firstname) {
		Firstname = firstname;
	}
	public String getLastname() {
		return Lastname;
	}
	public void setLastname(String lastname) {
		Lastname = lastname;
	}
	public String getPhonenumber() {
		return Phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		Phonenumber = phonenumber;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	
	
	
	}
