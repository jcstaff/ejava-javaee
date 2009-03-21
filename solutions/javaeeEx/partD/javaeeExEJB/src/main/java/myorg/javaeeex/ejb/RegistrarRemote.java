package myorg.javaeeex.ejb;

import java.util.Collection;

import javax.ejb.Remote;

import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.dto.PersonDTO;

@Remote
public interface RegistrarRemote {
    void ping();
    Person createPerson(Person person)
        throws RegistrarException;

    Person getPersonById(long id)
        throws RegistrarException;
    

    Collection<Person> getPeopleByName(String firstName, String lastName)
        throws RegistrarException;
    Collection<Person> getPeopleByNameHydrated(String firstName, String lastName)
        throws RegistrarException;
    Collection<Person> getPeopleByNameCleaned(String firstName, String lastName)
        throws RegistrarException;
    Collection<PersonDTO> getPeopleByNameDTO(String firstName, String lastName)
        throws RegistrarException;
}
