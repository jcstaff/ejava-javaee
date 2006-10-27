package myorg.javaeeex.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.blimpl.RegistrarImpl;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.jpa.JPAPersonDAO;
import myorg.javaeeex.jpa.JPAUtil;

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
        registrar = new RegistrarImpl();
        ((RegistrarImpl)registrar).setDao(new JPAPersonDAO());
        JPAUtil.setEntityManager(em);
    }
    
    @PreDestroy
    public void close() {
        log.debug("*** close() ***");
        JPAUtil.setEntityManager(null);
    }

    public Person createPerson(String firstName, String lastName) 
        throws RegistrarException {
        return registrar.createPerson(firstName, lastName);
    }

    public List<Person> getAllPeople(int index, int count) 
        throws RegistrarException {
        return registrar.getAllPeople(index, count);
    }
}
