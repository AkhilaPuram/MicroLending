package org.camunda.community.examples;

import java.util.List;
import java.util.Map;

import org.camunda.community.examples.StartFormRestController.LoanDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")

public class UserController {
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
	
	  @GetMapping("/GetUser/{id}")
	  public ResponseEntity<User> GetUsers(@PathVariable Integer id) {
	  
		  String uri = "https://jsonplaceholder.typicode.com/users1/"+id;
		  RestTemplate restTemplate = new RestTemplate();
		  User user = restTemplate.getForObject(uri, User.class);
		  

		  System.out.println("User: " + user);
		  return ResponseEntity.status(HttpStatus.OK).body( user);
	  }
	  @PostMapping(
			  value = "/createPerson", consumes = "application/json", produces = "application/json")
	  public ResponseEntity<String> createPerson(@PathVariable String Category,@RequestHeader(name = "X-COM-PERSIST", required = true) String headerPersist,
		        @RequestHeader(name = "X-COM-LOCATION", defaultValue = "ASIA") String headerLocation,
		        @RequestBody  Person person ) throws Exception  {
	  
		  return ResponseEntity.status(HttpStatus.OK).body( "test");
	  }
	  @PutMapping("{id}")
	    public ResponseEntity<Person> updatePerson(@PathVariable long id,@RequestBody Person PersonDetails) {
		  return ResponseEntity.ok(PersonDetails);
		  
	  }
	  
	  @PatchMapping("/complete1/{id}")
	  public ResponseEntity<String> completeTask(@PathVariable("id") String id) { 
		  
		  String uri = "http://localhost:8082/v1/tasks/"+id+"/complete";
		  User user=new User();
		  user.setId("1");
		  user.setName("saleem");
		  System.out.println(" inside Patch ::"+uri);
		  
		  RestTemplate restTemplate = new RestTemplate();
		  
		  try {
			  

			  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			  requestFactory.setConnectTimeout(0);
			  requestFactory.setReadTimeout(0);

			  restTemplate.setRequestFactory(requestFactory);
			  HttpHeaders headers = new HttpHeaders();
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  String AuthToken= "Bearer "+getToken();
			  headers.add("Authorization", AuthToken);
			  MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			  map.add("variables", "[{\"name\": \"hello\",\"value\": \"\\\"hello\\\"\"}");
			  


			  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			  	
			  System.out.println("NNNNN :"+request.getBody());
		        ResponseEntity<Task> response = restTemplate.exchange(uri, HttpMethod.PATCH, request, Task.class);
		        System.out.println(" Task State ::: " +response.getBody().getTaskState());
		        System.out.println("BODY::::"+response.getBody());
		        System.out.println("Result - status ("+ response.getStatusCode() + ") has body: " + response.hasBody());
		    }
		    catch (Exception eek) {
		        System.out.println("** Exception: "+ eek.getMessage());
		    }
		
		  
		  	  
			/*
			 * user =restTemplate.postForObject(uri, User.class, null);
			 * System.out.println(" accss toekn ::: " +response.getAccess_token());
			 */
		  return ResponseEntity.ok("completed");
	  }
	  
	  @PostMapping("/AllTasks/{user}")
	  public List<TaskCompleteRequest> getAllTasks(@PathVariable("user") String user) { 
		  
		  String uri = "http://localhost:8082/v1/tasks/search";
		  System.out.println(" inside Post ::"+uri);
		  TaskCompleteRequest TaskRes=new TaskCompleteRequest();
		  RestTemplate restTemplate = new RestTemplate();
		  
		  try {
			  
			  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			  requestFactory.setConnectTimeout(0);
			  requestFactory.setReadTimeout(0);
			  restTemplate.setRequestFactory(requestFactory);
			  HttpHeaders headers = new HttpHeaders();
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  String AuthToken= "Bearer "+getToken();
			  headers.add("Authorization", AuthToken);
			  MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			  map.add("taskState", "CREATED");
			  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			  ResponseEntity<List<TaskCompleteRequest>> response = restTemplate.exchange(uri, HttpMethod.POST, request, new ParameterizedTypeReference<List<TaskCompleteRequest>>() {});
			  System.out.println(" Task Response ::: " +response.getBody());
		      System.out.println("Result - status ("+ response.getStatusCode() + ") has body: " );
		      return response.getBody();
		    }
		    catch (Exception eek) {
		        System.out.println("** Exception: "+ eek.getMessage());
		    }
		
		 
		  return (List<TaskCompleteRequest>) ResponseEntity.ok(TaskRes);
	  }
	  
	  
public String getToken()
{
		  String token="";
		  String uri = "http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token";
		  System.out.println(" inside Patch ::"+uri);
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
		      System.out.println(" accss toekn ::: " +response.getBody().getAccess_token());
		      System.out.println("Result - status ("+ response.getStatusCode() + ") has body: " + response.hasBody());
		    }
		    catch (Exception eek) {
		        System.out.println("** Exception: "+ eek.getMessage());
		    }
		
		return token;  
	  }
}

	  


