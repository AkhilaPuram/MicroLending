package org.camunda.community.examples;

import java.util.Map;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

public class ProcessConstants {

  public static final String BPMN_PROCESS_ID = "sample-process";
@JobWorker(type = "io.camunda.zeebe:userTask")
  
  public void handleJob(final JobClient client, final ActivatedJob job) {
    // Element Id
	  System.out.println("inside task worker");
    System.out.println(job.getElementId());
    // get variables
    Map<String, Object> variables = job.getVariablesAsMap();
    // business logic
    System.out.println("inside task worker 1"+client.newActivateJobsCommand().toString());
    System.out.println("job.getKey()) ::::"+job.getKey());
    // ...
    // complete the tasks
    for (Map.Entry<String,Object> entry : variables.entrySet())  
        System.out.println("Key = " + entry.getKey() + 
                         ", Value = " + entry.getValue());
    
    
   
	/*
	 * if (job.getElementId()!="Activity_Task2") {
	 * client.newCompleteCommand(job.getKey())
	 * .variables(Map.of("newVariable","VariableFromClient")) .send() .join(); }
	 */  }
}
