package myorg.javaeeex.blimpl;

import java.util.List;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.da.PersonDAO;

public class RegistrarImpl implements Registrar {
    protected PersonDAO dao;

    public Person createPerson(String firstName, String lastName)
            throws RegistrarException {
        try {
            Person person = new Person();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            return dao.createPerson(person);
        }
        catch (Throwable ex) {
            throw new RegistrarException("error creating person", ex);
        }
    }

    public List<Person> getAllPeople(int index, int count)
            throws RegistrarException {
        try {
            return dao.findPeople(
                    PersonDAO.GET_ALL_PEOPLE_QUERY, null, index, count);
        }
        catch (Throwable ex) {
            throw new RegistrarException("error getting people", ex);
        }
    }

    public void setDao(PersonDAO dao) {
        this.dao = dao;
    }
}
