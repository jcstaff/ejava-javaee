package ejava.examples.jmsmechanics;

import java.util.Arrays;

import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

/**
 * This administers the JMS Destinations on JBoss 5. Thus, this class
 * is specific to JBoss 5. 
 *
 * @author jcstaff
 */
public class JMSAdmin {
    private static final Log log = 
        LogFactory.getLog(JMSAdmin.class); 
    protected String MSG_SERVICE = 
        "jboss.messaging:service=ServerPeer";
    private static final String DEPLOY_TOPIC_OPERATION = 
        "deployTopic";
    private static final String DEPLOY_QUEUE_OPERATION = 
        "deployQueue";
    private static final String DESTROY_TOPIC_OPERATION = 
        "destroyTopic";
    private static final String DESTROY_QUEUE_OPERATION = 
        "destroyQueue";
    private static final String jndiName = 
        System.getProperty("jmx.invoker","jmx/invoker/RMIAdaptor");

    RMIAdaptor remote;
    ObjectName jmxName;
    
    protected JMSAdmin init() throws Exception {
    	if (remote == null) {
	        //get the JMX Adaptor from the JNDI tree
	        Context jndi = new InitialContext(); //rely on a jndi.properties file
	        log.debug("jndi=" + jndi.getEnvironment());
	        log.debug("looking up:" + jndiName);
	        Object object = jndi.lookup(jndiName);
	        
	        remote = (RMIAdaptor) object;
	        jmxName = new ObjectName(MSG_SERVICE);
    	}
        return this;
    }
    
    public JMSAdmin deployTopic(String name, String jndiName) throws Exception {
    	init();
        Object[] params = { name, jndiName };
        String[] signature = { "java.lang.String", "java.lang.String" };
        log.debug(jmxName + "." + DEPLOY_TOPIC_OPERATION + "(" +
                Arrays.toString(params) + ")");
        remote.invoke(jmxName,DEPLOY_TOPIC_OPERATION, params, signature);
        return this;
    }

    public JMSAdmin deployQueue(String name, String jndiName) throws Exception {
    	init();
        Object[] params = { name, jndiName };
        String[] signature = { "java.lang.String", "java.lang.String" };
        log.debug(jmxName + "." + DEPLOY_QUEUE_OPERATION + "(" +
                Arrays.toString(params) + ")");
        remote.invoke(jmxName,DEPLOY_QUEUE_OPERATION, params, signature);
        return this;
    }

    public JMSAdmin destroyTopic(String name) throws Exception {
    	init();
        Object[] params = { name };
        String[] signature = { "java.lang.String" };
        log.debug(jmxName + "." + DESTROY_TOPIC_OPERATION + "(" +
                Arrays.toString(params) + ")");        
        remote.invoke(jmxName,DESTROY_TOPIC_OPERATION, params, signature);
        return this;
    }

    public JMSAdmin destroyQueue(String name) throws Exception {
    	init();
        Object[] params = { name };
        String[] signature = { "java.lang.String"};
        log.debug(jmxName + "." + DESTROY_QUEUE_OPERATION + "(" +
                Arrays.toString(params) + ")");
        remote.invoke(jmxName,DESTROY_QUEUE_OPERATION, params, signature);
        return this;
    }
}