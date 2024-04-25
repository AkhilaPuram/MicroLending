package org.camunda.community.examples;

import java.util.ArrayList;

public class KeyCloakUser {
	ArrayList<KeyCloakCredentials> credentials = new ArrayList<KeyCloakCredentials>();
	  private String username;
	  private String firstName;
	  private String lastName;
	  private String email;
	  private boolean emailVerified;
	  private boolean enabled;


	 // Getter Methods 

	  public ArrayList<KeyCloakCredentials> getCredentials() {
		return credentials;
	}

	public void setCredentials(ArrayList<KeyCloakCredentials> credentials) {
		this.credentials = credentials;
	}

	public String getUsername() {
	    return username;
	  }

	  public String getFirstName() {
	    return firstName;
	  }

	  public String getLastName() {
	    return lastName;
	  }

	  public String getEmail() {
	    return email;
	  }

	  public boolean getEmailVerified() {
	    return emailVerified;
	  }

	  public boolean getEnabled() {
	    return enabled;
	  }

	 // Setter Methods 

	  
	  public void setUsername( String username ) {
	    this.username = username;
	  }

	  public void setFirstName( String firstName ) {
	    this.firstName = firstName;
	  }

	  public void setLastName( String lastName ) {
	    this.lastName = lastName;
	  }

	  public void setEmail( String email ) {
	    this.email = email;
	  }

	  public void setEmailVerified( boolean emailVerified ) {
	    this.emailVerified = emailVerified;
	  }

	  public void setEnabled( boolean enabled ) {
	    this.enabled = enabled;
	  }
	}
	

	