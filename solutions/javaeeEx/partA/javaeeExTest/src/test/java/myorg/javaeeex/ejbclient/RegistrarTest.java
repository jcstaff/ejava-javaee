package myorg.javaeeex.ejbclient;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import myorg.javaeeex.ejb.RegistrarRemote;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class RegistrarTest {
    Log log = LogFactory.getLog(RegistrarTest.class);
    InitialContext jndi;

    String registrarJNDI = System.getProperty("jndi.name.registrar");
    RegistrarRemote registrar;

    @Before
    public void setUp() throws Exception {
        assertNotNull("jndi.name.registrar not supplied", registrarJNDI);

        log.debug("getting jndi initial context");
        jndi = new InitialContext();
        log.debug("jndi=" + jndi.getEnvironment());
        jndi.lookup("/"); //do a quick comms check of JNDI

        log.debug("jndi name:" + registrarJNDI);
        registrar = (RegistrarRemote)jndi.lookup(registrarJNDI);
    }

    @Test
    public void testPing() {
        log.info("*** testPing ***");
        registrar.ping();
    }
}
