package ejava.examples.jndidemo.ejbclient;

import javax.naming.InitialContext;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.jndidemo.Scheduler;

public class JNDITest  {
    Log log = LogFactory.getLog(JNDITest.class);
    InitialContext jndi;
    static String aidName = System.getProperty("jndi.name.aid");
    static String bakeName = System.getProperty("jndi.name.bake");
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    	//wait initial to avoid loosing a race condition with cargo
    	Thread.sleep(3000);
    }
    
    @Before
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
    }

    @Test
    public void testXMLPopulation() throws Exception {
        log.info("*** testXMLPopulation ***");
        
        Object object = jndi.lookup(aidName);
        log.debug(aidName + "=" + object);
        jndi.close();
        
        Scheduler s = (Scheduler)object;        
        log.debug("got scheduler:" + s.getName());
        
        String encname = "ejb/hospital";
        String jndiname = "java:comp/env/" + encname;
        //String jbossname = "java:comp.ejb3/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        //log.debug("jndi: " + jbossname + "=" + s.getJndiProperty(jbossname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));

        encname = "persistence/jndidemo";
        jndiname = "java:comp/env/" + encname;
        //jbossname = "java:comp.ejb3/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        //log.debug("jndi: " + jbossname + "=" + s.getJndiProperty(jbossname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));
    }
    
    @Test
    public void testAnnotationPopulation() throws Exception {
        log.info("*** testAnnotationPopulation ***");
        
        Object object = jndi.lookup(bakeName);
        log.debug(bakeName + "=" + object);
        jndi.close();
        
        Scheduler s = (Scheduler)object;        
        log.debug("got scheduler:" + s.getName());
        
        String encname = "ejb/cook";
        String jndiname = "java:comp/env/" + encname;
        //String jbossname = "java:comp.ejb3/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        //log.debug("jndi: " + jbossname + "=" + s.getJndiProperty(jbossname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));

        encname = "persistence/jndidemo";
        jndiname = "java:comp/env/" + encname;
        //jbossname = "java:comp.ejb3/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        //log.debug("jndi: " + jbossname + "=" + s.getJndiProperty(jbossname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));
    }
}
