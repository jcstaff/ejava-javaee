package ejava.examples.jmsmechanics;

import static ejava.examples.jmsmechanics.JMSTestBase.connFactory;
import static org.junit.Assert.assertNotNull;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hornetq.jms.server.embedded.EmbeddedJMS;

import org.junit.AfterClass;
import org.junit.BeforeClass;


public class JMSTestBase {
	private static final Log log = LogFactory.getLog(JMSTestBase.class);
    protected static boolean jmsEmbedded = Boolean.parseBoolean( 
		System.getProperty("jms.embedded", "true"));
    private static String connFactoryJNDI = 
		System.getProperty("jndi.name.connFactory", "ConnectionFactory");

    protected static String adminUser = System.getProperty("admin.user", "admin1");
    protected static String adminPassword = System.getProperty("admin.password", "password");
    protected static String user = System.getProperty("user", "user1");
    protected static String password = System.getProperty("password", "password");

    private static EmbeddedJMS server; //used when JMS server embedded in JVM
    protected static Context jndi;     //used when JMS server remote in JBoss
    protected static ConnectionFactory connFactory;
    protected static Connection connection;
    protected static JMSAdmin jmsAdmin;
    

	@BeforeClass
	public static final void setUpClass() throws Exception {
		if (jmsEmbedded) {
			log.info("using embedded JMS server");
			server = new EmbeddedJMS();
			server.start();
			
			connFactory=(ConnectionFactory) server.lookup(connFactoryJNDI);
	        jmsAdmin=new JMSAdminHornetQ(connFactory, adminUser, adminPassword);
		}
		else {
	        log.debug("getting jndi initial context");
	        jndi = new InitialContext();    
	        log.debug("jndi=" + jndi.getEnvironment());
			
	        log.debug("connection factory name:" + connFactoryJNDI);
	        connFactory = (ConnectionFactory)jndi.lookup("/jms/RemoteConnectionFactory");
	        jmsAdmin=new JMSAdminHornetQ(connFactory, adminUser, adminPassword)
	        	.setJNDIPrefix("/jboss/exported");
		}		
		connection = connFactory.createConnection(user, password);
		connection.start();
	}
	
	@AfterClass
	public static final void tearDownClass() throws Exception {
		jmsAdmin.close();
		connection.stop();
		connection.close();
		if (server != null) {
			server.stop();
		}
		if (jndi != null) {
			jndi.close();
		}
	}
	
	protected Object lookup(String name) throws NamingException {
		return (server != null) ?
			server.lookup(name) :
			jndi.lookup(name);	
	}
	
	protected MessageCatcher createCatcher(String name, Destination destination) {
        MessageCatcher catcher = new MessageCatcher(name);
        catcher.setConnFactory(connFactory);
        catcher.setDestination(destination);
        catcher.setUser(user);
        catcher.setPassword(password);
        return catcher;
	}
	
}
