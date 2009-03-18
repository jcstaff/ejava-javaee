package myorg.javaeeex.bl;

import java.util.Collection;

import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;

public interface Registrar {
    Person getPersonById(long id)
        throws RegistrarException;
    Person createPerson(Person person) 
        throws RegistrarException;
	Person createPerson(String firstName, String lastName)
	    throws RegistrarException;
    Person changeAddress(Person person, Address address) 
        throws RegistrarException;    
    Collection<Person> getPeopleByName(String firstName, String lastName)
        throws RegistrarException;
    Collection<Person> getAllPeople(int position, int count)
        throws RegistrarException;
}
