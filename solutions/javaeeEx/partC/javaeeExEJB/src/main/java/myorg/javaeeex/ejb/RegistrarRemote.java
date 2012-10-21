package myorg.javaeeex.ejb;

import javax.ejb.Remote;

import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Person;

@Remote
public interface RegistrarRemote {
    void ping();
    Person createPerson(Person person)
        throws RegistrarException;

    Person getPersonById(long id)
        throws RegistrarException;
}
