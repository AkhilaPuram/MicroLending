package org.camunda.community.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class KeyCloakService {
	
	@PostMapping("/CreateUser")
	  public ResponseEntity<String> Createuser(@RequestBody Map<String, Object> variables) { 
		  
		  String uri = "http://localhost:18080/auth/admin/realms/camunda-platform/users";
		  System.out.println(" inside Create user ::"+uri);
		  System.out.println(" Variables :::"+variables);
		  String lastName="";
		  String username="";
		  String firstName="";
		  String password="dummy";
		  String email="";
		  
		  
		  for (Map.Entry<String,Object> entry : variables.entrySet()) 
		    {
			  if (entry.getKey()=="credentials")
		        {
		        	java.util.ArrayList	Test=(java.util.ArrayList) entry.getValue();
		        	LinkedHashMap cd=(LinkedHashMap) Test.get(0);
		        	
		        	for (Iterator i = cd.entrySet().iterator(); i.hasNext(); ) 
		        	{
		        		String IText=i.next().toString();		        		
		        		if (IText.toString().contains("value"))
		        		{
		        			password=IText.substring(IText.lastIndexOf("=") + 1);
		        			 System.out.println("password"+password);
		        	    
		        	     }
		        	 
		             }
		        }
		        	
		        if (entry.getKey()=="username")
		        {
		        	username=(String) entry.getValue();
		        	System.out.println("username = " + entry.getKey() + 
		                    ", Value = " + username);
		        }
		        if (entry.getKey()=="firstName")
		        {
		        	firstName=(String) entry.getValue();
		        	System.out.println("firstName = " + entry.getKey() + 
		                    ", Value = " + firstName);
		        }
		        
		        if (entry.getKey()=="lastName")
		        {
		        	lastName=(String) entry.getValue();
		        	System.out.println("lastName = " + entry.getKey() + 
		                    ", Value = " + lastName);
		        }
		        if (entry.getKey()=="email")
		        {
		        	email=(String) entry.getValue();
		        	System.out.println("email = " + entry.getKey() + 
		                    ", Value = " + email);
		        }
		        
		    }
		  
		  RestTemplate restTemplate = new RestTemplate();
		  String result="";
		  
		  try {
			  
			  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			  requestFactory.setConnectTimeout(0);
			  requestFactory.setReadTimeout(0);
			  restTemplate.setRequestFactory(requestFactory);
			  HttpHeaders headers = new HttpHeaders();
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  String AuthToken= "Bearer "+getKeyCloakToken();
			  headers.add("Authorization", AuthToken);
			  username=firstName+" "+lastName;
			  String requestJson = "{\"attributes\":{\"attribute_key\":\"test_value\"},"
			  		+ "\"credentials\":[{\"temporary\":false,"
			  		+ "\"type\":\"password\","
			  		+ "\"value\":\"" + password + "\"}],"
			  		+ "\"username\":\""+ username+ "\","
			  		+ "\"firstName\":\""+ firstName+ "\","
			  		+ "\"lastName\":\""+lastName+"\","
			  		+ "\"email\":\""+ email+ "\","
			  		+ "\"emailVerified\":false,"
			  		+ "\"enabled\":true}";
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  
			  
			  HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
			  //String answer = restTemplate.postForObject(uri, entity, String.class);
			  

		        ResponseEntity<Task> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Task.class);
			  
			  List<String> UserIDHeader=response.getHeaders().get("Location");
			  String userID=UserIDHeader.get(0);
			  result=userID.substring(userID.lastIndexOf("/")+1);
			  AssignRelamToUser( userID.substring(userID.lastIndexOf("/")+1));
			  
			  System.out.println("USER ID ::::" +userID.substring(userID.lastIndexOf("/")+1));
			  		    }
		    catch (Exception eek) {
		        System.out.println("** Exception: "+ eek.getMessage());
		    }
		
		  System.out.println("User details "+getUsers());
		  HttpHeaders responseHeaders = new HttpHeaders();
		    responseHeaders.set("Content-Type", "application/json");
		    return new ResponseEntity<String>(result, responseHeaders, HttpStatus.CREATED);
		  
		  
		 
	  } 
	
	@GetMapping("/GetUsers")
	public ResponseEntity<String>  getUsers() { 
		  
		  String uri = "http://localhost:18080/auth/admin/realms/camunda-platform/users";
		  User user=new User();		
		  ResponseEntity<String> Resentity = null;
		  RestTemplate restTemplate = new RestTemplate();		  
		  try {
			  
			  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			  requestFactory.setConnectTimeout(0);
			  requestFactory.setReadTimeout(0);
			  restTemplate.setRequestFactory(requestFactory);			
			 System.out.println("get User URI::: "+uri);					 
			 MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		     String AuthToken= "Bearer "+getKeyCloakToken();
		     headers.add("Authorization", AuthToken);
		     headers.add("Content-Type", "application/json");
		      Resentity = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<Object>(headers),
		                String.class);
			 
		 
			  System.out.println(" User ID"+Resentity.getBody());
			  		    }
		    catch (Exception eek) {
		        System.out.println("** Exception: "+ eek.getMessage());
		    }
		
		  HttpHeaders responseHeaders = new HttpHeaders();
		    responseHeaders.set("Content-Type", "application/json");
		    return new ResponseEntity<String>(Resentity.getBody(), responseHeaders, HttpStatus.CREATED);
		  
		  
		
		  
		   
		

	  } 
	
	@DeleteMapping("/{id}") 
	  public ResponseEntity<String> deleteUser(@PathVariable("id") String id) { 
	         // Delete the user in this method with the id.
		 String uri = "http://localhost:18080/auth/admin/realms/camunda-platform/users/"+id;
		  ResponseEntity<String> Resentity = null;
		  RestTemplate restTemplate = new RestTemplate();	
		  String requestJson="";
		  try {
			  
			  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			  requestFactory.setConnectTimeout(0);
			  requestFactory.setReadTimeout(0);
			  restTemplate.setRequestFactory(requestFactory);			
			 System.out.println("get User URI::: "+uri);					 
			 MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		     String AuthToken= "Bearer "+getKeyCloakToken();
		     headers.add("Authorization", AuthToken);
		     headers.add("Content-Type", "application/json");
		     HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
		      Resentity = restTemplate.exchange(uri, HttpMethod.DELETE, entity,
		                String.class);
			 
		 
			  System.out.println(" Role assigned::::"+Resentity.getBody());
			  		    }
		    catch (Exception eek) {
		        System.out.println("** Exception: "+ eek.getMessage());
		    }
		
		  HttpHeaders responseHeaders = new HttpHeaders();
		    responseHeaders.set("Content-Type", "application/json");
		    return new ResponseEntity<String>(Resentity.getBody(), responseHeaders, HttpStatus.OK);
		  
		  
		
		  
	  }
	
	
	
	@PostMapping("/AssignRole")
	public ResponseEntity<String>  AssignRelamToUser(String UserID) { 
		  
		  String uri = "http://localhost:18080/auth/admin/realms/camunda-platform/users/"+UserID+"/role-mappings/realm";
		 	
		  ResponseEntity<String> Resentity = null;
		  RestTemplate restTemplate = new RestTemplate();	
		  String requestJson="[\r\n"
		  		+ "{\r\n"
		  		+ "\"id\": \""
		  		+ "bfc7aa0e-ee16-49aa-a39e-c12a441d7e6b"
		  		+ "\",\r\n"
		  		+ "\"name\": \"Tasklist\"\r\n"
		  		+ "}\r\n"
		  		+ "]";
		  try {
			  
			  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			  requestFactory.setConnectTimeout(0);
			  requestFactory.setReadTimeout(0);
			  restTemplate.setRequestFactory(requestFactory);			
			 System.out.println("get User URI::: "+uri);					 
			 MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		     String AuthToken= "Bearer "+getKeyCloakToken();
		     headers.add("Authorization", AuthToken);
		     headers.add("Content-Type", "application/json");
		     HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
		      Resentity = restTemplate.exchange(uri, HttpMethod.POST, entity,
		                String.class);
			 
		 
			  System.out.println(" Role assigned::::"+Resentity.getBody());
			  		    }
		    catch (Exception eek) {
		        System.out.println("** Exception: "+ eek.getMessage());
		    }
		
		  HttpHeaders responseHeaders = new HttpHeaders();
		    responseHeaders.set("Content-Type", "application/json");
		    return new ResponseEntity<String>(Resentity.getBody(), responseHeaders, HttpStatus.CREATED);
		  
		  
		
		  
		   
		

	  } 
	
	@PutMapping("/UpdateUser/{id}")
	
	public ResponseEntity<String>  UpdateUser(@PathVariable("id") String UserID,@RequestBody Map<String, Object> variables) { 
		  
		  String uri = "http://localhost:18080/auth/admin/realms/camunda-platform/users/"+UserID;
		 	
		  String lastName="";
		  String username="";
		  String firstName="";
		  String password="dummy";
		  String email="";
		  
		  
		  for (Map.Entry<String,Object> entry : variables.entrySet()) 
		    {
			  if (entry.getKey()=="username")
		        {
		        	username=(String) entry.getValue();
		        	System.out.println("username = " + entry.getKey() + 
		                    ", Value = " + username);
		        }
		        if (entry.getKey()=="firstName")
		        {
		        	firstName=(String) entry.getValue();
		        	System.out.println("firstName = " + entry.getKey() + 
		                    ", Value = " + firstName);
		        }
		        
		        if (entry.getKey()=="lastName")
		        {
		        	lastName=(String) entry.getValue();
		        	System.out.println("lastName = " + entry.getKey() + 
		                    ", Value = " + lastName);
		        }
		        if (entry.getKey()=="email")
		        {
		        	email=(String) entry.getValue();
		        	System.out.println("email = " + entry.getKey() + 
		                    ", Value = " + email);
		        }
		        
		    }
		  ResponseEntity<String> Resentity = null;
		  RestTemplate restTemplate = new RestTemplate();	
		  String requestJson="{\"username\":\""
		  		+ username
		  		+ "\",\"firstName\":\""
		  		+ firstName
		  		+ "\",\"lastName\":\""
		  		+ lastName
		  		+ "\",\"email\":\""
		  		+ email
		  		+ "\"}";
		  try {
			  
			  HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			  requestFactory.setConnectTimeout(0);
			  requestFactory.setReadTimeout(0);
			  restTemplate.setRequestFactory(requestFactory);			
			 System.out.println("put User URI::: "+uri);					 
			 MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		     String AuthToken= "Bearer "+getKeyCloakToken();
		     headers.add("Authorization", AuthToken);
		     headers.add("Content-Type", "application/json");
		     HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
		      Resentity = restTemplate.exchange(uri, HttpMethod.PUT, entity,
		                String.class);
			 
		 
			  System.out.println(" User updated::::"+Resentity.getBody());
			  		    }
		    catch (Exception eek) {
		        System.out.println("** Exception: "+ eek.getMessage());
		    }
		
		  HttpHeaders responseHeaders = new HttpHeaders();
		    responseHeaders.set("Content-Type", "application/json");
		    return new ResponseEntity<String>(Resentity.getBody(), responseHeaders, HttpStatus.CREATED);
		  
	  } 

	
public String getKeyCloakToken()
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
			  map.add("client_secret", "h1T8UErlByNpvtbi0XKKw9jGNrfib5WH");
			  map.add("client_id", "Sample_Client");
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

