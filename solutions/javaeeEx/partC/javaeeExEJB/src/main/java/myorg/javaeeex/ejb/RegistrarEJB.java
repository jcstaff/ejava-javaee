package myorg.javaeeex.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.blimpl.RegistrarImpl;
import myorg.javaeeex.dao.PersonDAO;
import myorg.javaeeex.jpa.JPAPersonDAO;
import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Person;

@Stateless
public class RegistrarEJB implements RegistrarLocal, RegistrarRemote {
    private static Log log = LogFactory.getLog(RegistrarEJB.class);
    private Registrar registrar;

    @PersistenceContext(unitName="javaeeEx")
    private EntityManager em;

    @PostConstruct
    public void init() {
        try {
            log.debug("**** init ****");
            log.debug("em=" + em);
            PersonDAO dao = new JPAPersonDAO();
            ((JPAPersonDAO)dao).setEntityManager(em);

            registrar = new RegistrarImpl();
            ((RegistrarImpl)registrar).setDAO(dao);
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

    public void ping() {
        log.debug("ping called");
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
}
