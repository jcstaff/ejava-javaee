package myorg.javaeeex.ejb;

import java.util.ArrayList;


import java.util.Collection;
import javax.annotation.security.RolesAllowed;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.SessionContext;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.dto.AddressDTO;
import myorg.javaeeex.dto.PersonDTO;

@Stateless
@RolesAllowed({"user"})
public class RegistrarEJB implements RegistrarLocal, RegistrarRemote {
    private static Log log = LogFactory.getLog(RegistrarEJB.class);
    @Inject
    private Registrar registrar;
    @Resource
    protected SessionContext ctx;

    //@Inject @Named("javaeeEx")
    //private EntityManager em;

    @PostConstruct
    public void init() {
        try {
            log.debug("**** init ****");
            //log.debug("em=" + em);
            //PersonDAO dao = new JPAPersonDAO();
            //((JPAPersonDAO)dao).setEntityManager(em);

            //registrar = new RegistrarImpl();
            //((RegistrarImpl)registrar).setDAO(dao);
            log.debug("init complete, registrar=" + registrar);
        }
        catch (Throwable ex) {
            log.error("error in init", ex);
            throw new EJBException("error in init" + ex);
        }
    }

    @PreDestroy
    public void close() {
        log.debug("*** close() ***");
    }

    @PermitAll
    public void ping() {
        log.debug("ping called");
        log.debug("caller=" + ctx.getCallerPrincipal().getName());
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

    public Person getPersonById(long id)
        throws RegistrarException {
        log.debug("*** getPersonById(" + id + ") ***");
        return registrar.getPersonById(id);
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

    private void hydratePerson(Person person) {
        for (Address address : person.getAddresses()) {
            address.getZip();
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

    public Collection<Person> getAllPeople(int index, int count)
            throws RegistrarException {
        log.debug(String.format("*** getAllPeople(index=%d, count=%d) ***", index, count));

        try {
            return registrar.getAllPeople(index, count);
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
    }

    public Collection<Person> getAllPeopleHydrated(int index, int count)
        throws RegistrarException {
        log.debug(String.format("*** getAllPeopleHydrated(index=%d, count=%d) ***", index, count));

        try {
            Collection<Person> people = registrar.getAllPeople(index, count);
            for (Person person : people) {
                hydratePerson(person);
            }
            return people;
        }
        catch (Throwable ex) {
            log.error(ex);
            throw new RegistrarException(ex.toString());
        }
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
}
