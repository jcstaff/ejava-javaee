package ejava.examples.jmssecurity;

import java.util.Arrays;



import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

/**
 * This administers the JMS Destinations on the new JBoss 
 * Messaging System. This twist in the configuration is required
 * to get security support. It is currently not being used due to 
 * some difficulty getting roles to work through the JMX interface.
 * Currently all destinations and security configurations are through
 * the static XML files in the deployment descriptor.
 *
 * @author jcstaff
 */
public class JBMAdmin {
    private static final Log log = 
        LogFactory.getLog(JBMAdmin.class); 
    protected static final String MSG_SERVICE = 
    	"org.jboss.messaging:module=JMS,type=Server";
    protected static final String QUEUE_NAME_1 =
        "org.jboss.messaging:module=JMS,type=Queue,name=\"";
    protected static final String QUEUE_NAME_2 =
    	  "\"";
    protected static final String ADDRESS_NAME_1 =
    	"org.jboss.messaging:module=Core,type=Address,name=\"";
    protected static final String ADDRESS_NAME_2 =
    	"\"";
    private static final String CREATE_TOPIC_OPERATION = 
        "createTopic";
    private static final String CREATE_QUEUE_OPERATION = 
        "createQueue";
    private static final String DESTROY_TOPIC_OPERATION = 
        "destroyTopic";
    private static final String DESTROY_QUEUE_OPERATION = 
        "destroyQueue";
    private static final String REMOVE_ROLE_OPERATION = 
        "removeRole";
    private static final String ADD_ROLE_OPERATION = 
        "addRole";
    private static final String COUNT_MESSAGES_OPERATION = 
        "countMessages";
    private static final String jndiName = 
        System.getProperty("jmx.invoker","jmx/invoker/RMIAdaptor");

    RMIAdaptor remote;
    ObjectName serviceName;

    protected String getQueueName(String name) {
    	return QUEUE_NAME_1 + name + QUEUE_NAME_2;
    }    
    protected String getQueueAddress(String name) {
    	return ADDRESS_NAME_1 + "jms.queue." + name + ADDRESS_NAME_2;
    }    
    
    protected JBMAdmin init() throws Exception {
    	
    	if (remote == null) {
	        //get the JMX Adaptor from the JNDI tree
	        Context jndi = new InitialContext(); //rely on a jndi.properties file
	        log.debug("jndi=" + jndi.getEnvironment());
	        log.debug("looking up:" + jndiName);
	        Object object = jndi.lookup(jndiName);	
	        
	        remote = (RMIAdaptor) object;
	        serviceName = new ObjectName(MSG_SERVICE);
    	}
        return this;
    }
    
    protected Object invoke(
    	ObjectName object, String method, Object[] params, String[] signature)
    	throws Exception {
    	
       	init();
       	log.debug(object + "." + method + "(" + Arrays.toString(params) + ")");
        Object result = remote.invoke(object,
        		                      method, 
        		                      params, 
        		                      signature);
        log.info("result=" + result);
        return result;
    }
    
    public boolean queueExists(String name) throws Exception {
    	ObjectName queueName = 
    		new ObjectName(getQueueName(name));
        try {
        	invoke(queueName, COUNT_MESSAGES_OPERATION, 
	        		                      new Object[]{ "" }, 
	        		                      new String[]{ "java.lang.String" });
	        return true;
        }
        catch (Exception ex) {
        	log.fatal(ex);
        	return false;
        }
    }

    public void removeRole(String destName, String roleName) throws Exception {
    
    	ObjectName queueName = new ObjectName(getQueueAddress(destName));
    	invoke(queueName, REMOVE_ROLE_OPERATION, 
        		                      new Object[]{ roleName }, 
        		                      new String[]{ "java.lang.String" });
    }    
    
    public void addQueueRole(
    		String destName, String roleName, boolean canRead, boolean canWrite) 
    		throws Exception {
    	
    	ObjectName queueName = new ObjectName(getQueueAddress(destName));
    	addRole(queueName, roleName, canRead, canWrite);
    }

    public void addRole(ObjectName destName, String roleName, 
    		boolean canRead, boolean canWrite) throws Exception {

    	Object[] params = {
    			roleName,
    			canWrite, //send
    			canRead, //consume
    			false, //createDurableQueue
    			false, //deleteDurableQueue
    			false, //createNonDurableQueue
    			false, //deleteNonDurableQueue
    			false //manage
    	};
    	String[] signature = {
    			"java.lang.String",
    			"boolean",
    			"boolean",
    			"boolean",
    			"boolean",
    			"boolean",
    			"boolean",
    			"boolean",
    	};
        invoke(destName,ADD_ROLE_OPERATION, params, signature);
    }

    public JBMAdmin createTopic(String name, String jndiName) throws Exception {
        Object[] params = { name, jndiName };
        String[] signature = { "java.lang.String", "java.lang.String" };
        invoke(serviceName,CREATE_TOPIC_OPERATION, params, signature);
        return this;
    }

    public JBMAdmin createQueue(String name, String jndiName) throws Exception {
        Object[] params = { name, jndiName };
        String[] signature = { "java.lang.String", "java.lang.String" };
        invoke(serviceName,CREATE_QUEUE_OPERATION, params, signature);
        return this;
    }

    public JBMAdmin destroyTopic(String name) throws Exception {
        Object[] params = { name };
        String[] signature = { "java.lang.String" };
        invoke(serviceName,DESTROY_TOPIC_OPERATION, params, signature);
        return this;
    }

    public JBMAdmin destroyQueue(String name) throws Exception {
        Object[] params = { name };
        String[] signature = { "java.lang.String"};
        invoke(serviceName,DESTROY_QUEUE_OPERATION, params, signature);
        return this;
    }
}