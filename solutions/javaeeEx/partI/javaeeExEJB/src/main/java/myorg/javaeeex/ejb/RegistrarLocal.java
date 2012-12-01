package myorg.javaeeex.ejb;

import java.util.Collection;

import javax.ejb.Local;

import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;

@Local
public interface RegistrarLocal {
    void ping();

    Person getPersonById(long id)
        throws RegistrarException;
    Person getPersonByIdHydrated(long id)
        throws RegistrarException;

    Person changeAddress(Person person, Address address)
        throws RegistrarException;

    Collection<Person> getAllPeople(int index, int count)
                throws RegistrarException;
    Collection<Person> getAllPeopleHydrated(int index, int count)
                throws RegistrarException;
}
