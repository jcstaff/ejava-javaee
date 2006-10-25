package ejava.examples.jndischeduler.ejbclient;

import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.jndischeduler.Scheduler;

public class JNDITest extends TestCase {
    Log log = LogFactory.getLog(JNDITest.class);
    InitialContext jndi;
    static String aidName = System.getProperty("jndi.name.aid");
    static String bakeName = System.getProperty("jndi.name.bake");
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
    }

    public void testXMLPopulation() throws Exception {
        log.info("*** testXMLPopulation ***");
        
        Object object = jndi.lookup(aidName);
        log.debug(aidName + "=" + object);
        
        Scheduler s = (Scheduler)object;        
        log.debug("got scheduler:" + s.getName());
        
        String encname = "ejb/hospital";
        String jndiname = "java:comp/env/" + encname;
        String jbossname = "java:comp.ejb3/env/" + encname;
        log.debug("jndi:" + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("jndi:" + jbossname + "=" + s.getJndiProperty(jbossname));
        log.debug("ctx :" + encname + "=" + s.getCtxProperty(encname));

        encname = "persistence/jndischeduler";
        jndiname = "java:comp/env/" + encname;
        jbossname = "java:comp.ejb3/env/" + encname;
        log.debug("jndi:" + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("jndi:" + jbossname + "=" + s.getJndiProperty(jbossname));
        log.debug("ctx :" + encname + "=" + s.getCtxProperty(encname));
    }
    
    public void testAnnotationPopulation() throws Exception {
        log.info("*** testAnnotationPopulation ***");
        
        Object object = jndi.lookup(bakeName);
        log.debug(bakeName + "=" + object);
        
        Scheduler s = (Scheduler)object;        
        log.debug("got scheduler:" + s.getName());
        
        String encname = "ejb/cook";
        String jndiname = "java:comp/env/" + encname;
        String jbossname = "java:comp.ejb3/env/" + encname;
        log.debug("jndi:" + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("jndi:" + jbossname + "=" + s.getJndiProperty(jbossname));
        log.debug("ctx :" + encname + "=" + s.getCtxProperty(encname));

        encname = "persistence/jndischeduler";
        jndiname = "java:comp/env/" + encname;
        jbossname = "java:comp.ejb3/env/" + encname;
        log.debug("jndi:" + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("jndi:" + jbossname + "=" + s.getJndiProperty(jbossname));
        log.debug("ctx :" + encname + "=" + s.getCtxProperty(encname));
    }
}
