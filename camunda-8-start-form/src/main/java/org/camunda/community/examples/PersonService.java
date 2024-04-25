package org.camunda.community.examples;
public interface PersonService {

    public Person saveUpdatePerson(Person person);
    public Person findPersonById(Integer id);
}