package ejava.examples.jmsmechanics;

import java.util.Arrays;


import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to provide runtime administration of JMS resources
 * versus more static approaches using deployment descriptors. The 
 * implementation of this class is specific to JBoss5/JBoss Messaging and 
 * JBoss6/HornetQ.
 *
 * @author jcstaff
 */
public class JMSAdminJMX {	
    public static final String JBM_MSG_SERVICE = 
        "jboss.messaging:service=ServerPeer";
    public static final String HQ_MSG_SERVICE = 
        "org.hornetq:module=JMS,type=Server";
    public static enum JMSProvider { 
    	HORNETQ("createTopic", "createQueue"), 
    	JBM("deployTopic", "deployQueue");
    	
    	public final String deployTopic;
    	public final String deployQueue;
    	public final String destroyTopic = "destroyTopic";
    	public final String destroyQueue = "destroyQueue";
    	JMSProvider(String deployTopic, String deployQueue) {
    		this.deployTopic = deployTopic;
    		this.deployQueue = deployQueue;
    	}
    }

    private static final Log log = 
        LogFactory.getLog(JMSAdmin.class); 
    private static final String jndiName = 
        System.getProperty("jmx.invoker","jmx/invoker/RMIAdaptor");

    MBeanServerConnection remote;
    ObjectName jmxName;
    JMSProvider jmsProvider;
    
    protected JMSAdminJMX init() throws Exception {
    	if (remote == null) {
	        //get the JMX Adaptor from the JNDI tree
	        Context jndi = new InitialContext(); //rely on a jndi.properties file
	        log.debug("jndi=" + jndi.getEnvironment());
	        log.debug("looking up:" + jndiName);
	        Object object = jndi.lookup(jndiName);
	        log.debug("object=" + object);
	        
	        remote = (MBeanServerConnection)object;
	        jmxName = new ObjectName(HQ_MSG_SERVICE);
        	jmsProvider=JMSProvider.HORNETQ;
	        
	        try {
	        	//verify we have JBoss6/HornetQ first
	        	remote.invoke(jmxName, "listConnectionIDs", 
	        			new Object[]{}, new String[]{});
	        }
	        catch (InstanceNotFoundException ex) {
	        	log.info("not JBoss6/HornetQ, trying JBoss5/JBM");
	        	jmxName = new ObjectName(JBM_MSG_SERVICE);
	        	jmsProvider=JMSProvider.JBM;
	        	remote.invoke(jmxName, "listMessageCountersAsHTML", 
	        			new Object[]{}, new String[]{});
	        }
        	log.info("using " + jmsProvider.name());
    	}
        return this;
    }
    
    public JMSAdminJMX deployTopic(String name, String jndiName) throws Exception {
    	init();
        Object[] params = { name, jndiName };
        String[] signature = { "java.lang.String", "java.lang.String" };
        log.debug(jmxName + "." + jmsProvider.deployTopic + "(" +
                Arrays.toString(params) + ")");
        remote.invoke(jmxName,jmsProvider.deployTopic, params, signature);
        return this;
    }

    public JMSAdminJMX deployQueue(String name, String jndiName) throws Exception {
    	init();
        Object[] params = { name, jndiName };
        String[] signature = { "java.lang.String", "java.lang.String" };
        log.debug(jmxName + "." + jmsProvider.deployQueue + "(" +
                Arrays.toString(params) + ")");
        remote.invoke(jmxName,jmsProvider.deployQueue, params, signature);
        return this;
    }

    public JMSAdminJMX destroyTopic(String name) throws Exception {
    	init();
        Object[] params = { name };
        String[] signature = { "java.lang.String" };
        log.debug(jmxName + "." + jmsProvider.destroyTopic + "(" +
                Arrays.toString(params) + ")");        
        try { //JBoss6/HonetQ throws exception when deleting non-existent dest
        	remote.invoke(jmxName,jmsProvider.destroyTopic, params, signature);
        } catch (Exception ex) {
        	log.warn(ex);
        }
        return this;
    }

    public JMSAdminJMX destroyQueue(String name) throws Exception {
    	init();
        Object[] params = { name };
        String[] signature = { "java.lang.String"};
        log.debug(jmxName + "." + jmsProvider.destroyQueue + "(" +
                Arrays.toString(params) + ")");
        try {//JBoss6/HonetQ throws exception when deleting non-existent dest
        	remote.invoke(jmxName,jmsProvider.destroyQueue, params, signature);
	    } catch (Exception ex) {
	    	log.warn(ex);
	    }
        return this;
    }
}