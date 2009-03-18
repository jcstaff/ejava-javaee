package myorg.javaeeex.ejb;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.blimpl.RegistrarImpl;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.dao.PersonDAO;
import myorg.javaeeex.dto.AddressDTO;
import myorg.javaeeex.dto.PersonDTO;
import myorg.javaeeex.jpa.JPAPersonDAO;

@Stateless
public class RegistrarEJB implements RegistrarLocal, RegistrarRemote {
    private static Log log = LogFactory.getLog(RegistrarEJB.class);
    private Registrar registrar;
    
    @PersistenceContext(unitName="javaeeEx")
    private EntityManager em;
    
    @PostConstruct
    public void init() {
        log.debug("**** init ****");
        log.debug("em=" + em);
        PersonDAO dao = new JPAPersonDAO();
        ((JPAPersonDAO)dao).setEntityManager(em);
        
        registrar = new RegistrarImpl();
        ((RegistrarImpl)registrar).setDAO(dao);
    }
    
    @PreDestroy
    public void close() {
        log.debug("*** close() ***");
    }
    
    public Person getPersonById(long id) 
        throws RegistrarException {
        return registrar.getPersonById(id);
    }

	public Person getPersonByIdHydrated(long id) throws RegistrarException {
        log.debug("*** getPersonByIdHydrated() ***");

        try {
        	Person person = registrar.getPersonById(id);
        	hydratePerson(person);
            return person;
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
	}
	
	private void hydratePerson(Person person) {
    	for (Address address : person.getAddresses()) {
            address.getZip();
        }
	}

	public PersonDTO getPersonByIdDTO(long id) throws RegistrarException {
        log.debug("*** getPeopleByIdDTO() ***");

        try {
        	Person personBO = registrar.getPersonById(id);
        	PersonDTO personDTO = makeDTO(personBO);
        	return personDTO;                
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
	}

	private PersonDTO makeDTO(Person personBO) {
        PersonDTO personDTO = new PersonDTO(personBO.getId());
        personDTO.setFirstName(personBO.getFirstName());
        personDTO.setLastName(personBO.getLastName());
        //note that there is no SSN in the DTO
        for (Address addressBO : personBO.getAddresses()) {
            AddressDTO addressDTO = new AddressDTO(
                    addressBO.getId(),
                    addressBO.getStreet(),
                    addressBO.getCity(),
                    addressBO.getState(),
                    addressBO.getZip());
            personDTO.getAddresses().add(addressDTO);
        }
        return personDTO;
	}

	public Person createPerson(String firstName, String lastName)
	    throws RegistrarException {
        log.debug("*** createPerson() ***");
        
        //the person we return will have the PK set
        try {
            return registrar.createPerson(firstName, lastName);
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
	}

	public Person createPerson(Person person) 
        throws RegistrarException {
        log.debug("*** createPerson() ***");
        
        //the person we return will have the PK set
        try {
            return registrar.createPerson(person);
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
    }

    public Person changeAddress(Person person, Address address)
        throws RegistrarException {
        log.debug("*** changeAddress() ***");

        try {
            return registrar.changeAddress(person, address);
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
    }

    public Collection<Person> getPeopleByName(
        String firstName, String lastName) 
        throws RegistrarException {
        log.debug("*** getPeopleByName() ***");

        try {
            return registrar.getPeopleByName(firstName, lastName);
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
    }
    
    public Collection<Person> getPeopleByNameHydrated(
            String firstName, String lastName) 
            throws RegistrarException {
        log.debug("*** getPeopleByNameHydrated() ***");

        try {
            Collection<Person> people = 
                registrar.getPeopleByName(firstName, lastName);
            for (Person p: people) {
            	hydratePerson(p);
            }
            return people;
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
    }
    

    public Collection<Person> getPeopleByNameCleaned(
            String firstName, String lastName) 
            throws RegistrarException {
        log.debug("*** getPeopleByNameCleaned() ***");

        try {
            Collection<Person> people = new ArrayList<Person>();
            for (Person personBO : 
                registrar.getPeopleByName(firstName, lastName)) {
                
                Person personPOJO = new Person(personBO.getId());
                personPOJO.setFirstName(personBO.getFirstName());
                personPOJO.setLastName(personBO.getLastName());
                personPOJO.setSsn(personBO.getSsn());
                for (Address addressBO : personBO.getAddresses()) {
                    Address addressPOJO = new Address(
                            addressBO.getId(),
                            addressBO.getStreet(),
                            addressBO.getCity(),
                            addressBO.getState(),
                            addressBO.getZip());
                    personPOJO.getAddresses().add(addressPOJO);
                }
                people.add(personPOJO);
            }
            return people;
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
    }
    
    public Collection<PersonDTO> getPeopleByNameDTO(
            String firstName, String lastName) 
            throws RegistrarException {
        log.debug("*** getPeopleByNameDTO() ***");

        try {
            Collection<PersonDTO> people = new ArrayList<PersonDTO>();
            for (Person personBO : 
                registrar.getPeopleByName(firstName, lastName)) {
                
            	PersonDTO personDTO = makeDTO(personBO);
                people.add(personDTO);
            }
            return people;
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
    }


    public Collection<Person> getAllPeople(int index, int count) 
        throws RegistrarException {
        log.debug("*** getAllPeople() ***");

        try {
            return registrar.getAllPeople(index, count);
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }        
    }


}
