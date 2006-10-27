package myorg.javaeeex.ejbclient;

import java.util.List;

import javax.naming.InitialContext;

import junit.framework.TestCase;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.ejb.RegistrarRemote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RegistrarTest extends TestCase {
    Log log = LogFactory.getLog(RegistrarTest.class);
    InitialContext jndi;
    String registrarJNDI = System.getProperty("jndi.name.registrar");
    Registrar registrar;
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        log.debug("jndi name:" + registrarJNDI);
        registrar = (RegistrarRemote)jndi.lookup(registrarJNDI);
    }

    public void testCreatePerson() throws Exception {
        log.info("*** testCreatePerson ***");
        
        Person person = registrar.createPerson("joe", "smith");
        assertTrue("unexpected id:" + person.getId(), person.getId()!=0);        
    }

    public void testGetAllPeople() throws Exception {
        log.info("*** testGetAllPeople ***");
        
        for(int i=0; i<10; i++) {
            registrar.createPerson("joe", "smith " + i);
        }
        List<Person> people = registrar.getAllPeople(0, 100);
        //we could have trash since we have no remote way to clean-up
        assertTrue("unexpected number of people:" + people.size(),
                people.size() >= 10);
    }
}
