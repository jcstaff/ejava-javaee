package myorg.javaeeex.ejbclient;

import javax.naming.InitialContext;

import myorg.javaeeex.ejb.RegistrarRemote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class RegistrarIT {
    private static final Log log = LogFactory.getLog(RegistrarIT.class);
    private InitialContext jndi;

    private static final String registrarJNDI = System.getProperty("jndi.name.registrar",
        "javaeeExEAR/javaeeExEJB/RegistrarEJB!myorg.javaeeex.ejb.RegistrarRemote");
    private RegistrarRemote registrar;

    @Before
    public void setUp() throws Exception {
        assertNotNull("jndi.name.registrar not supplied", registrarJNDI);

        log.debug("getting jndi initial context");
        jndi = new InitialContext();
        log.debug("jndi=" + jndi.getEnvironment());
        jndi.lookup("/"); //do a quick comms check of JNDI

        log.debug("jndi name:" + registrarJNDI);
        registrar = (RegistrarRemote)jndi.lookup(registrarJNDI);
        log.debug("registrar=" + registrar);
    }

    @Test
    public void testPing() {
        log.info("*** testPing ***");
        registrar.ping();
    }
}
