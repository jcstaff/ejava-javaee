package myorg.javaeeex.ejb;

import java.util.Collection;

import javax.ejb.Remote;

import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.dto.PersonDTO;

@Remote
public interface RegistrarRemote {
    void ping();
    Person createPerson(Person person)
        throws RegistrarException;

    Person getPersonById(long id)
        throws RegistrarException;
    Person getPersonByIdHydrated(long id)
    	throws RegistrarException;
    
    Person changeAddress(Person person, Address address)
    	throws RegistrarException;

    Collection<Person> getPeopleByName(String firstName, String lastName)
        throws RegistrarException;
    Collection<Person> getPeopleByNameHydrated(String firstName, String lastName)
        throws RegistrarException;
    Collection<Person> getPeopleByNameCleaned(String firstName, String lastName)
        throws RegistrarException;
    Collection<PersonDTO> getPeopleByNameDTO(String firstName, String lastName)
        throws RegistrarException;
    
    Collection<Person> getAllPeople(int index, int count)
    	throws RegistrarException;
    Collection<Person> getAllPeopleHydrated(int index, int count)
		throws RegistrarException;
}
