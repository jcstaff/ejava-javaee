package ejava.examples.jndidemo.ejbclient;

import java.io.InputStream;


import javax.naming.InitialContext;
import javax.naming.NamingException;


import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.jndidemo.Scheduler;
import ejava.examples.jndidemo.ejb.AidSchedulerRemote;
import ejava.examples.jndidemo.ejb.BakeSchedulerRemote;
import ejava.util.ejb.EJBClient;

/**
 * Performs a basic set of calls in the EJBs deployed to demonstrate 
 * aspects of how the EJBs are configured.
 */
public class JndiIT  {
    private static final Log log = LogFactory.getLog(JndiIT.class);
    private InitialContext jndi;
    
    static String aidName = System.getProperty("jndi.name.aid",
    	EJBClient.getEJBClientLookupName(
    			"jndiDemoEAR", "jndiDemoEJB", "","AidScheduler",
        		AidSchedulerRemote.class.getName(), false));
    static String bakeName = System.getProperty("jndi.name.bake",
    	EJBClient.getEJBClientLookupName(
    			"jndiDemoEAR", "jndiDemoEJB", "","BakeScheduler",
        		BakeSchedulerRemote.class.getName(), true));
    
    @BeforeClass
    public static void waitForServerDeploy() throws InterruptedException {
    	/*
    	 * this wait seems periodically necessary when using the cargo-startstop
    	 * profile rather than the cargo-deploy profile to an already 
    	 * running server. 
    	 */
    	if (Boolean.parseBoolean(System.getProperty("cargo.startstop", "false"))) {
    		long waitTime=10000;
	    	log.info(String.format("pausing %d secs for server deployment to complete", waitTime/1000));
	    	Thread.sleep(10000);
    	}
    }
    
    @Before
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();
        //can't call this when java.naming.factory.initial not specified
        //log.debug("jndi=" + jndi.getEnvironment());
        
        //instead -- dump the jndi.properties and jboss-ejb-client.properties files
        InputStream is = getClass().getResourceAsStream("/jndi.properties");
        assertNotNull("jndi.properties not found", is);
        log.debug("jndi.properties\n" + IOUtils.toString(is));
        is.close();
        is = getClass().getResourceAsStream("/jboss-ejb-client.properties");
        assertNotNull("jboss-ejb-client.properties 1 not found", is);
        log.debug("jboss-ejb-client.properties\n" + IOUtils.toString(is));
        is.close();
        
        
        log.debug("aidName=" + aidName);
        log.debug("bakeName=" + bakeName);
    }
    
    @After
    public void tearDown() throws NamingException {
        jndi.close();
    }

    /**
     * Calls the EJBs that used POJOs with XML deployment descriptors.
     * @throws Exception
     */
    @Test
    public void testXMLPopulation() throws Exception {
        log.info("*** testXMLPopulation ***");
        
        Object object = jndi.lookup(aidName);
        log.debug(aidName + "=" + object);
        
        Scheduler s = (Scheduler)object;        
        log.debug("got scheduler:" + s);
        String name = s.getName();
        log.debug("scheduler.name:" + name);
        assertEquals("", "AidScheduler", name);
        
        String encname = "ejb/hospital";
        String jndiname = "java:comp/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));

        encname = "persistence/jndidemo";
        jndiname = "java:comp/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));
        
        log.debug("java:comp/env=" + s.getEnv());
    }
    
    /**
     * Calls the EJBs that used annotated classes and no deployment descriptors.
     * @throws Exception
     */
    @Test
    public void testAnnotationPopulation() throws Exception {
        log.info("*** testAnnotationPopulation ***");
        
        Object object = jndi.lookup(bakeName);
        log.debug(bakeName + "=" + object);
        
        Scheduler s = (Scheduler)object;        
        log.debug("got scheduler:" + s);
        String name = s.getName();
        log.debug("scheduler.name:" + name);
        assertEquals("", "BakeSchedulerEJB", name);
        
        String encname = "ejb/cook";
        String jndiname = "java:comp/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));

        encname = "persistence/jndidemo";
        jndiname = "java:comp/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));
        
        log.debug("java:comp/env=" + s.getEnv());
    }
}
