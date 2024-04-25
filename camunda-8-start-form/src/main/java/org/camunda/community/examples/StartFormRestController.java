package org.camunda.community.examples;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.ActivateJobsCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.model.bpmn.impl.instance.zeebe.ZeebeTaskDefinitionImpl;
import io.camunda.zeebe.model.bpmn.instance.Task;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.camunda.zeebe.spring.client.annotation.JobWorker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")

public class StartFormRestController {

	public String BPMN_PROCESS_ID="LoanApplication";
  private static final Logger LOG = LoggerFactory.getLogger(StartFormRestController.class);
  
  @Autowired
  private ZeebeClient zeebe;
  
  
  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/start")
  public ResponseEntity<LoanDetails> startProcessInstance(@RequestBody Map<String, Object> variables) {

    LOG.info(
        "Starting process `" + BPMN_PROCESS_ID + "` with variables: " + variables);
    System.out.println(
            "Starting process `" + BPMN_PROCESS_ID + "` with variables: " + variables);
        LoanDetails loaninfo=new LoanDetails();
    loaninfo.setLoanamount( Double.parseDouble(variables.get("loanamount").toString()));
    loaninfo.setNoofmonths(Double.parseDouble(variables.get("noofmonths").toString()));
    loaninfo.setRate(Double.parseDouble(variables.get("rate").toString()));
    loaninfo.setEMI(loaninfo.loanamount,loaninfo.noofmonths,loaninfo.rate);
    loaninfo.setRequestFrom("React");
    
    System.out.println(" loan amount from request "+ variables.get("loanamount"));
  
 
    
    
    
  long processID=  zeebe
        .newCreateInstanceCommand()
        .bpmnProcessId(BPMN_PROCESS_ID)
        .latestVersion()
        .variables(loaninfo)
        .send().join().getProcessInstanceKey();
  
  		System.out.println("process instnace id is "+processID);
  		
		/*
		 * zeebe.newPublishMessageCommand() .messageName("order canceled")
		 * .correlationKey("TestMessage") .variables(loaninfo) .send();
		 */		 

  		
  return ResponseEntity.status(HttpStatus.OK).body( loaninfo);
  }
  
  @PostMapping("/StartLoan")
  public ResponseEntity<Long> startLoanProcess(@RequestBody Map<String, Object> variables) {

	  String Loanprocess="LoanApplication";
	  
    LOG.info(
        "Starting process `" + Loanprocess + "` with variables: " + variables);
    System.out.println(
            "Starting process `" + Loanprocess + "` with variables: " + variables);
        LoanProcess loaninfo=new LoanProcess();
    loaninfo.setLoanamount( Double.parseDouble(variables.get("loanamount").toString()));
    loaninfo.setFirstname(variables.get("firstname").toString());
    loaninfo.setLastname(variables.get("lastname").toString());
    loaninfo.setEmail(variables.get("email").toString());
    loaninfo.setRequestFrom("React");
    loaninfo.setLoantype(variables.get("loantype").toString());
    loaninfo.setPhonenumber(variables.get("phonenumber").toString());
    long processID=  zeebe
        .newCreateInstanceCommand()
        .bpmnProcessId(Loanprocess)
        .latestVersion()
        .variables(loaninfo)
        .send().join().getProcessInstanceKey();
  
  		System.out.println("process instnace id is "+processID);
  	  		HttpHeaders responseheaders=new HttpHeaders();
  	  	responseheaders.setContentType(MediaType.APPLICATION_JSON);
  	  String PID=processID+"";
  	//  CompleteTaskByProcessID(PID);
  return ResponseEntity.status(HttpStatus.OK).headers(responseheaders).body( processID);
  }
  
  
  
  @PostMapping("/GetAllOpenLoanTasks")
  public ResponseEntity<ArrayList<TaskCompleteRequest>>  GetTasks(@RequestBody Map<String, Object> variables) {
	  String uri = "http://10.13.1.180:8082/v1/tasks/search";
	  ResponseEntity<TaskCompleteRequest[]> Resentity = null;
	  ArrayList<TaskCompleteRequest> Task1 = new ArrayList<TaskCompleteRequest>();
	  String Response = null;
	  RestTemplate restTemplate = new RestTemplate();		  
	  try {
		  
		  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		  requestFactory.setConnectTimeout(0);
		  requestFactory.setReadTimeout(0);
		  restTemplate.setRequestFactory(requestFactory);			
		  System.out.println("get User URI::: "+uri);		
		  HttpHeaders headers = new HttpHeaders();
		  headers.setContentType(MediaType.APPLICATION_JSON);
		  String AuthToken= "Bearer "+getTasklistToken();
		  headers.add("Authorization", AuthToken);
		  MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		  map.add("taskState", "CREATED");
		  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		  Resentity = restTemplate.exchange(uri, HttpMethod.POST,request,
				  TaskCompleteRequest[].class);
		  
		  TaskCompleteRequest[] task=Resentity.getBody();
		  for(int i=0;i<task.length;i++)
		  {
			  String taskid=task[i].getId();
			  if(task[i].getTaskState().equals("CREATED")&& task[i].getProcessName().equals("LoanApplication"))
			  {
				  TaskVariables[] taskvaribales=getTaskvariables(taskid);
				  ArrayList<TaskVariables> taskvaribalesList=new ArrayList<TaskVariables>();
				  			  
				  for(int j=0;j<taskvaribales.length;j++)
				  {
					  //System.out.println("Varaible name "+taskvaribales[j].getName());
					  //System.out.println("Varaible value "+taskvaribales[j].getValue());
					  String formatedTaskvalue=taskvaribales[j].getValue().replace("\"", "");
					  taskvaribales[j].setValue(formatedTaskvalue);
					  taskvaribalesList.add(taskvaribales[j]);
				  }
				  
				  task[i].setTaskVariables(taskvaribalesList);
				  
				  Task1.add(task[i]);
		  }
		  }
		 
		  
		  		    }
	    catch (Exception eek) {
	        System.out.println("** Exception: "+ eek.getMessage());
	    }
	
	  HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.set("Content-Type", "application/json");
	    
	    //return ResponseEntity.status(HttpStatus.OK).body( Task1);
	    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(Task1);
	   // return new ResponseEntity<Task1>(Resentity.getBody(), responseHeaders, HttpStatus.CREATED);
	    
	    }
  
  @PostMapping("/GetAllOpenLoanTasks1")
  public ResponseEntity<ArrayList<LoanProcess>>  GetOpenTasks(@RequestBody Map<String, Object> variables) {
	  String uri = "http://10.13.1.180:8082/v1/tasks/search";
	  ResponseEntity<TaskCompleteRequest[]> Resentity = null;
	  ArrayList<TaskCompleteRequest> Task1 = new ArrayList<TaskCompleteRequest>();
	  String Response = null;
	  ArrayList<LoanProcess> result=new ArrayList<LoanProcess>();
	  RestTemplate restTemplate = new RestTemplate();		  
	  try {
		  
		  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		  requestFactory.setConnectTimeout(0);
		  requestFactory.setReadTimeout(0);
		  restTemplate.setRequestFactory(requestFactory);			
		  System.out.println("get User URI::: "+uri);		
		  HttpHeaders headers = new HttpHeaders();
		  headers.setContentType(MediaType.APPLICATION_JSON);
		  String AuthToken= "Bearer "+getTasklistToken();
		  headers.add("Authorization", AuthToken);
		  
		  String requestJson = "{\"taskState\":\"CREATED\"}";
		 
		  
		  
		  HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
		  
		  
		  //HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		  Resentity = restTemplate.exchange(uri, HttpMethod.POST,entity,
				  TaskCompleteRequest[].class);
		  
		  TaskCompleteRequest[] task=Resentity.getBody();
		  System.out.println("BBBBB"+task.length);
		  LoanProcess SingleInstanceVaraibles= new LoanProcess();
			
		  for(int i=0;i<task.length;i++)
		  {
			  //System.out.println(" inside outer loop"+i);
			  String taskid=task[i].getId();
			  String processID=task[i].getProcessInstanceKey();
			  String TaskName =task[i].getName(); 
			  //System.out.println("process id:::"+processID+"Task Name"+TaskName +" TaskState()"+task[i].getTaskState());
	if(task[i].getTaskState().equals("CREATED")&& task[i].getProcessName().equals("LoanApplication")&& TaskName.equals("Review Loan"))
			  {
				   SingleInstanceVaraibles= new LoanProcess();
				  TaskVariables[] taskvaribales=getTaskvariables(taskid);
				  
				  SingleInstanceVaraibles.setProcessID(processID);
				  SingleInstanceVaraibles.setTaskID(taskid);
				  			  
				  for(int j=0;j<taskvaribales.length;j++)
				  {
					 // System.out.println(" inside inner loop"+j+1);
					  String formatedTaskvalue=taskvaribales[j].getValue().replace("\"", "");
					  if(taskvaribales[j].getName().equals("firstname"))
					  {
						  SingleInstanceVaraibles.setFirstname(formatedTaskvalue);
					  }
					  if(taskvaribales[j].getName().equals("lastname"))
					  {
						  SingleInstanceVaraibles.setLastname(formatedTaskvalue);
					  }
					  if(taskvaribales[j].getName().equals("loantype"))
					  {
						  SingleInstanceVaraibles.setLoantype(formatedTaskvalue);
					  }
					  if(taskvaribales[j].getName().equals("loanamount"))
					  {
						  SingleInstanceVaraibles.setLoanamount(Double.parseDouble(formatedTaskvalue));
					  }
					  if(taskvaribales[j].getName().equals("email"))
					  {
						  SingleInstanceVaraibles.setEmail(formatedTaskvalue);
					  }
					  if(taskvaribales[j].getName().equals("phonenumber"))
					  {
						  SingleInstanceVaraibles.setPhonenumber(formatedTaskvalue);
					  }
					  //System.out.println("Varaible value "+taskvaribales[j].getValue());
					//  String formatedTaskvalue=taskvaribales[j].getValue().replace("\"", "");
					  //taskvaribales[j].setValue(formatedTaskvalue);
					  //taskvaribalesList.add(taskvaribales[j]);
				  }
				  result.add(SingleInstanceVaraibles);
				 }
			 
			  
		  }
		 
		  
		  		    }
	    catch (Exception eek) {
	        System.out.println("** Exception: "+ eek.getMessage());
	    }
	
	  HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.set("Content-Type", "application/json");
	    
	   
	    //return ResponseEntity.status(HttpStatus.OK).body( Task1);
	    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(result);
	   // return new ResponseEntity<Task1>(Resentity.getBody(), responseHeaders, HttpStatus.CREATED);
	    
	    }
  
  @PostMapping("/GetAllOpenLoanTasks2")
  public ResponseEntity<ArrayList<LoanProcess>>  GetOpenTasksForManager(@RequestBody Map<String, Object> variables) {
	  String uri = "http://10.13.1.180:8082/v1/tasks/search";
	  ResponseEntity<TaskCompleteRequest[]> Resentity = null;
	  ArrayList<TaskCompleteRequest> Task1 = new ArrayList<TaskCompleteRequest>();
	  String Response = null;
	  ArrayList<LoanProcess> result=new ArrayList<LoanProcess>();
	  RestTemplate restTemplate = new RestTemplate();		  
	  try {
		  
		  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		  requestFactory.setConnectTimeout(0);
		  requestFactory.setReadTimeout(0);
		  restTemplate.setRequestFactory(requestFactory);			
		  System.out.println("get User URI::: "+uri);		
		  HttpHeaders headers = new HttpHeaders();
		  headers.setContentType(MediaType.APPLICATION_JSON);
		  String AuthToken= "Bearer "+getTasklistToken();
		  headers.add("Authorization", AuthToken);
		  
		  String requestJson = "{\"taskState\":\"CREATED\"}";
		 
		  
		  
		  HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
		  
		  
		  //HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		  Resentity = restTemplate.exchange(uri, HttpMethod.POST,entity,
				  TaskCompleteRequest[].class);
		  
		  TaskCompleteRequest[] task=Resentity.getBody();
		  System.out.println("BBBBB"+task.length);
		  LoanProcess SingleInstanceVaraibles= new LoanProcess();
			
		  for(int i=0;i<task.length;i++)
		  {
			 // System.out.println(" inside outer loop"+i);
			  String taskid=task[i].getId();
			  String processID=task[i].getProcessInstanceKey();
			  String TaskName =task[i].getName();  //
			 // System.out.println("TaskState():::"+task[i].getTaskState() +" Process :"+task[i].getProcessName()+ " TaskName "+TaskName);
			  
			  if(task[i].getTaskState().equals("CREATED")&& task[i].getProcessName().equals("LoanApplication") && TaskName.equals("Manager Approver"))
			  {
				   SingleInstanceVaraibles= new LoanProcess();
				  TaskVariables[] taskvaribales=getTaskvariables(taskid);
				  
				  SingleInstanceVaraibles.setProcessID(processID);
				  SingleInstanceVaraibles.setTaskID(taskid);
				  			  
				  for(int j=0;j<taskvaribales.length;j++)
				  {
					 // System.out.println(" inside inner loop"+j+1);
					  String formatedTaskvalue=taskvaribales[j].getValue().replace("\"", "");
					  if(taskvaribales[j].getName().equals("firstname"))
					  {
						  SingleInstanceVaraibles.setFirstname(formatedTaskvalue);
					  }
					  if(taskvaribales[j].getName().equals("lastname"))
					  {
						  SingleInstanceVaraibles.setLastname(formatedTaskvalue);
					  }
					  if(taskvaribales[j].getName().equals("loantype"))
					  {
						  SingleInstanceVaraibles.setLoantype(formatedTaskvalue);
					  }
					  if(taskvaribales[j].getName().equals("loanamount"))
					  {
						  SingleInstanceVaraibles.setLoanamount(Double.parseDouble(formatedTaskvalue));
					  }
					  if(taskvaribales[j].getName().equals("email"))
					  {
						  SingleInstanceVaraibles.setEmail(formatedTaskvalue);
					  }
					  if(taskvaribales[j].getName().equals("phonenumber"))
					  {
						  SingleInstanceVaraibles.setPhonenumber(formatedTaskvalue);
					  }
					  //System.out.println("Varaible value "+taskvaribales[j].getValue());
					//  String formatedTaskvalue=taskvaribales[j].getValue().replace("\"", "");
					  //taskvaribales[j].setValue(formatedTaskvalue);
					  //taskvaribalesList.add(taskvaribales[j]);
				  }
				  result.add(SingleInstanceVaraibles);
				 }
			 
			  
		  }
		 
		  
		  		    }
	    catch (Exception eek) {
	        System.out.println("** Exception: "+ eek.getMessage());
	    }
	
	  HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.set("Content-Type", "application/json");
	    
	   
	    //return ResponseEntity.status(HttpStatus.OK).body( Task1);
	    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(result);
	   // return new ResponseEntity<Task1>(Resentity.getBody(), responseHeaders, HttpStatus.CREATED);
	    
	    }
  
  
  public String getActiveTaskID( String ProcessID )
  {
	  String TaskID="";
	  
	  
	  String uri = "http://10.13.1.180:8082/v1/tasks/search";
	  ResponseEntity<TaskCompleteRequest[]> Resentity = null;
	  ArrayList<TaskCompleteRequest> Task1 = new ArrayList<TaskCompleteRequest>();
	  String Response = null;
	  ArrayList<LoanProcess> result=new ArrayList<LoanProcess>();
	  RestTemplate restTemplate = new RestTemplate();		  
	  try {
		  
		  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		  requestFactory.setConnectTimeout(0);
		  requestFactory.setReadTimeout(0);
		  restTemplate.setRequestFactory(requestFactory);			
		  System.out.println("get tasklist URI::: "+uri);		
		  HttpHeaders headers = new HttpHeaders();
		  headers.setContentType(MediaType.APPLICATION_JSON);
		  String AuthToken= "Bearer "+getTasklistToken();
		  headers.add("Authorization", AuthToken);
		  
		  String requestJson = "{\"taskState\":\"CREATED\",\"processInstanceKey\":\""
		  		+ ProcessID
		  		+ "\"}";
		 
		  //String requestJson = "{\"taskState\":\"CREATED\"}";
		  
		  HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
		  Resentity = restTemplate.exchange(uri, HttpMethod.POST,entity,
				  TaskCompleteRequest[].class);
		  
		  TaskCompleteRequest[] task=Resentity.getBody();
		  System.out.println("BBBBB"+task.length);
		  System.out.println(" Active task response :::"+Resentity.getBody().toString());
		  for(int i=0;i<task.length;i++)
		  {
			  System.out.println("Results :"+i+" "+task[i].getId()   +" for process "+task[i].getProcessInstanceKey()+"task name:"+task[i].getName()+"Task State"+task[i].getTaskState());
			  if(task[i].getProcessInstanceKey().equals(ProcessID) && task[i].getTaskState().equals("CREATED") )
			  {
				  System.out.println(" Match found at "+i);
				  TaskID=task[i].getId();

			  }
		  
		  }
		  
		 	  }
	  catch (Exception eek) {
	        System.out.println("** Exception: "+ eek.getMessage());
	    }
	
	  
	  return TaskID;
  }
  @CrossOrigin(origins = "http://localhost:3000")
  @PatchMapping("/FinishTaskWithPID/{pid}")
  public String  CompleteTaskByProcessID( @PathVariable("pid") String ProcessID) throws InterruptedException {
	  
	  //Thread.sleep(2000);
	  System.out.println(" ID from Path"+ProcessID);
	  String id=getActiveTaskID(ProcessID);
	  System.out.println(" Active Task ID for"+id +" for process ::"+ProcessID);
	  String uri = "http://10.13.1.180:8082/v1/tasks/"+id+"/complete";
	  ResponseEntity<TaskCompleteRequest> Resentity = null;
	  ResponseEntity<String> response=null;
	  String Response = null;
	  RestTemplate restTemplate = new RestTemplate();		  
	  try {
		  
		  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		  requestFactory.setConnectTimeout(0);
		  requestFactory.setReadTimeout(0);
		  restTemplate.setRequestFactory(requestFactory);			
		  System.out.println("complete task URI::: "+uri);		
		  HttpHeaders headers = new HttpHeaders();
		  headers.setContentType(MediaType.APPLICATION_JSON);
		  String AuthToken= "Bearer "+getTasklistToken();
		  headers.add("Authorization", AuthToken);
		  
		  //String requestJson = "{\"variables\": [{\"name\": \"strVarExample\",\"value\": \"\\\"hello\\\"\"},{\"name\": \"intVarExample\",\"value\": \"15\"},{\"name\": \"booleanVarExample\",\"value\": \"false\"},{\"name\": \"arrayVarExample\",\"value\": \"[1, 2, 3, 5, 8, 13, 21]\"}]}";
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  
			  String requestJson="";
			  HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
			  System.out.println("BBB:"+entity.getBody());
			   response = restTemplate.exchange(uri, HttpMethod.PATCH, entity, String.class);
			   return "success";
			  }
	    catch (Exception eek) {
	        System.out.println("** Exception: "+ eek.getMessage());
	        return "fail";
	    }
		       
	   
	  }

  

  @PatchMapping("/CompleteTask/{TaskID}")
  public ResponseEntity<String>  CompleteTaskByID(@PathVariable("TaskID") String id,@RequestBody Map<String, Object> variables) {
	  String uri = "http://10.13.1.180:8082/v1/tasks/"+id+"/complete";
	  ResponseEntity<TaskCompleteRequest> Resentity = null;
	  ResponseEntity<String> response=null;
	  System.out.println(" Complete Varaibles"+variables);
	  System.out.println("Complete request"+variables.toString().contains("Approve"));
	  String Response = null;
	  RestTemplate restTemplate = new RestTemplate();		  
	  try {
		  
		  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		  requestFactory.setConnectTimeout(0);
		  requestFactory.setReadTimeout(0);
		  restTemplate.setRequestFactory(requestFactory);			
		  System.out.println("complete task URI::: "+uri);		
		  HttpHeaders headers = new HttpHeaders();
		  headers.setContentType(MediaType.APPLICATION_JSON);
		  String AuthToken= "Bearer "+getTasklistToken();
		  headers.add("Authorization", AuthToken);
		  String requestJson="";
		  if(variables.toString().contains("Approve"))
		  {
			  requestJson = "{\"variables\": [{\"name\": \"loanstatus\",\"value\": \"\\\"Approved\\\"\"}]}";   
		  }
		  else if (variables.toString().contains("Reject"))
		  {
			  requestJson = "{\"variables\": [{\"name\": \"loanstatus\",\"value\": \"\\\"Rejected\\\"\"}]}";
		  }
		  else {
			  requestJson = "{\"variables\": [{\"name\": \"strVarExample\",\"value\": \"\\\"hello\\\"\"},{\"name\": \"intVarExample\",\"value\": \"15\"},{\"name\": \"booleanVarExample\",\"value\": \"false\"},{\"name\": \"arrayVarExample\",\"value\": \"[1, 2, 3, 5, 8, 13, 21]\"}]}";
			   
		  }
		  
		
		 
		  headers.setContentType(MediaType.APPLICATION_JSON);
			  
			  
			  HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
			  System.out.println("BBB:"+entity.getBody());
			   response = restTemplate.exchange(uri, HttpMethod.PATCH, entity, String.class);
			  }
	    catch (Exception eek) {
	        System.out.println("** Exception: "+ eek.getMessage());
	    }
		        return ResponseEntity.status(HttpStatus.OK).body( "hello");
	   
	  }
	    
  
  public TaskVariables[] getTaskvariables(String TaskID)
  {
	  String uri = "http://10.13.1.180:8082/v1/tasks/"+TaskID+"/variables/search";
	  ResponseEntity<TaskVariables[]> Resentity = null;
	  //ArrayList<LoanProcess> Task1 = new ArrayList<LoanProcess>();
	  String Response = null;
	  RestTemplate restTemplate = new RestTemplate();		  
	  try {
		  
		  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		  requestFactory.setConnectTimeout(0);
		  requestFactory.setReadTimeout(0);
		  restTemplate.setRequestFactory(requestFactory);			
		  System.out.println("get User URI::: "+uri);		
		  HttpHeaders headers = new HttpHeaders();
		  headers.setContentType(MediaType.APPLICATION_JSON);
		  String AuthToken= "Bearer "+getTasklistToken();
		  headers.add("Authorization", AuthToken);
		  String variableRequest="{\"variableNames\" :[\"loantype\",\"loanamount\",\"firstname\",\"lastname\",\"phonenumber\",\"email\"]}";
		  HttpEntity<String> entity = new HttpEntity<String>(variableRequest,headers);
		  Resentity = restTemplate.exchange(uri, HttpMethod.POST,entity,
				  TaskVariables[].class);
		  
		  TaskVariables[] task=Resentity.getBody();
		  
		  		    }
	    catch (Exception eek) {
	        System.out.println("** Exception: "+ eek.getMessage());
	    }
	
	  	
	    return Resentity.getBody();
	
	    
  }

public String getTasklistToken()
{
		  String token="";
		  String uri = "http://10.13.1.180:18080/auth/realms/camunda-platform/protocol/openid-connect/token";
		  System.out.println(" inside Tasklist ::"+uri);
		  RestTemplate restTemplate = new RestTemplate();
		  	  try {
			  HttpHeaders headers = new HttpHeaders();
			  headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			  MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			  map.add("email", "first.last@example.com");
			  map.add("client_secret", "XALaRPl5qwTEItdwCMiPS62nVpKs7dL7");
			  map.add("client_id", "tasklist");
			  map.add("grant_type", "client_credentials");
			  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		      ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.POST, request, User.class);
		      token=response.getBody().getAccess_token();
		     // System.out.println(" accss toekn ::: " +response.getBody().getAccess_token());
		      //System.out.println("Result - status ("+ response.getStatusCode() + ") has body: " + response.hasBody());
		    }
		    catch (Exception eek) {
		        System.out.println("** Exception: "+ eek.getMessage());
		    }
		
		return token;  
	  }


  

  

@JobWorker(type = "SendApprovalNotification")  
public void SendApprovalNotification(final JobClient client, final ActivatedJob job) {
  
	  
	  
	  System.out.println("Approval notification sent");
	 
 
}
@JobWorker(type = "SendRejectNotification")  
public void SendRejectNotification(final JobClient client, final ActivatedJob job) {
  
	  
	  
	  System.out.println("Reject notification sent");
	 
 
}

  @JobWorker(type = "io.camunda.zeebe:userTask", autoComplete=false)  
  public void handleJob(final JobClient client, final ActivatedJob job) {
    
	  
	  
	  System.out.println("inside task worker");
	  System.out.println(" JOB "+job);
       Map<String, Object> variables = job.getVariablesAsMap();
       
      
       System.out.println("Job Variables :::"+variables); 
    System.out.println("Activated job commands  :::"+client.newActivateJobsCommand());
    System.out.println("job.getKey()) ::::"+job.getKey());
    
    String From="";
    for (Map.Entry<String,Object> entry : variables.entrySet()) 
    {
        if (entry.getKey()=="requestFrom")
        {
        	From=(String) entry.getValue();
        	System.out.println("Key = " + entry.getKey() + 
                    ", Value = " + entry.getValue());
        }
    	
    }
    
   if(From.equals("React"))
   {
	 if (job.getElementId().equals("Activity_10cnu02")) {
		 
		 
	  client.newCompleteCommand(job.getKey())
	 .variables(Map.of("newVariable","VariableFromClient")) .send() .join(); 
	  }
 }
   }
  
  
  @JobWorker(type = "service-test")  
  public void handleService(final JobClient client, final ActivatedJob job) {
    
	  System.out.println(" from service task"+job);
	 // zeebe.newPublishMessageCommand() .messageName("TestMessage")
	  //.correlationKey("TestMessage").variables("") .send();
	  
  }
  
  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/sendmessage")
  public ResponseEntity<String> SendMessage(@RequestBody Map<String, Object> variables) {

	  
	  System.out.println("inside message"+variables);
	  zeebe.newPublishMessageCommand() .messageName("TestMessage")
		  .correlationKey("TestMessage") .variables(variables) .send();
	  return ResponseEntity.status(HttpStatus.OK).body( "message sent");
  }

  
  public static class LoanDetails {
	    private double loanamount;
	    private double rate;
	    private double noofmonths;
	    private double EMI;
	    private String requestFrom;
	    
	    
		public String getRequestFrom() {
			return requestFrom;
		}
		public void setRequestFrom(String requestFrom) {
			this.requestFrom = requestFrom;
		}
		public double getEMI() {
			return EMI;
		}
		public void setEMI(double loanamount2, double noofmonths2, double rate2) {
			// TODO Auto-generated method stub
			
			EMI=(loanamount2+((loanamount2*noofmonths2*rate)/1200))/noofmonths2;
			
		}
		
		public double getLoanamount() {
			return loanamount;
		}
		public void setLoanamount(double loanamount) {
			this.loanamount = loanamount;
		}
		public double getRate() {
			return rate;
		}
		public void setRate(double rate) {
			this.rate = rate;
		}
		public double getNoofmonths() {
			return noofmonths;
		}
		public void setNoofmonths(double noofmonths) {
			this.noofmonths = noofmonths;
		}

	  }
}
