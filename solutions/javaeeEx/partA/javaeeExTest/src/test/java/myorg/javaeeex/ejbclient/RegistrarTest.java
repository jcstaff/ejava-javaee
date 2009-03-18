package myorg.javaeeex.ejbclient;

import javax.naming.InitialContext;

import junit.framework.TestCase;

import myorg.javaeeex.ejb.RegistrarRemote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RegistrarTest extends TestCase {
    Log log = LogFactory.getLog(RegistrarTest.class);
    InitialContext jndi;
    String registrarJNDI = System.getProperty("jndi.name.registrar");
    RegistrarRemote registrar;
    
    public void setUp() throws Exception {
        assertNotNull("jndi.name.registrar not supplied", registrarJNDI);

        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        jndi.lookup("/"); //do a quick comms check of JNDI
        
        log.debug("jndi name:" + registrarJNDI);
        registrar = (RegistrarRemote)jndi.lookup(registrarJNDI);
    }

    public void testPing() {
        log.info("*** testPing ***");
        registrar.ping();
    }
    
}
