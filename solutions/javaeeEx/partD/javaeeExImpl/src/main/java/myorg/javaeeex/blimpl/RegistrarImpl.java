package myorg.javaeeex.blimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.dao.PersonDAO;

public class RegistrarImpl implements Registrar {
    protected PersonDAO dao;
    public void setDAO(PersonDAO dao) {
        this.dao = dao;
    }

    /**
     * Returns the person associated with the ID
     */
    public Person getPersonById(long id) {
        return dao.getPerson(id);
    }

    /**
     * Although the business object allows for 0..N addresses, the
     * business logic overrides this by checking that only one address
     * exists.
     */
    public Person createPerson(Person person)
            throws RegistrarException {
        
        //constrain the DAO to only a single managed address
        if (person.getAddresses().size() != 1) {
            throw new RegistrarException("Person must have 1 address");
        }
        
        try {
            return dao.createPerson(person);
        }
        catch (Throwable ex) {
            throw new RegistrarException("error creating person", ex);
        }
    }

    /**
     * creates a single person using only their name and not address.
     */
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

    
    /**
     * Changes the one and only address for the Person.
     */
    public Person changeAddress(Person person, Address address)
        throws RegistrarException {
        
        Person dbPerson = dao.getPerson(person.getId());
        if (dbPerson == null) {
            throw new RegistrarException(
                    "person not found, id=" + person.getId()); 
        }
        else {
            Collection<Address> addressCollection = 
                new ArrayList<Address>(dbPerson.getAddresses()); 
            for (Address oldAddress : addressCollection) {
                dbPerson.getAddresses().remove(oldAddress);
                dao.removeAddress(oldAddress);                
            }
            dbPerson.getAddresses().add(address);
        }
        return dbPerson;
    }
    
    public List<Person> getPeopleByName(String firstName, String lastName)
        throws RegistrarException {
        try {
            return dao.findPeopleByName(firstName, lastName);
        }
        catch (Throwable ex) {
            throw new RegistrarException("error getting people",ex);
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

}
