package ejava.examples.jmsnotifier;


import java.io.InputStream;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import ejava.examples.jmsmechanics.JMSAdmin;
import ejava.examples.jmsmechanics.JMSAdminHornetQ;
import ejava.examples.jmsmechanics.JMSAdminJMX;
import ejava.examples.jmsmechanics.JMSTestBase;

public class SimpleDemoTest {
	static final Log log = LogFactory.getLog(SimpleDemoTest.class);
    protected static boolean jmsEmbedded = Boolean.parseBoolean( 
		System.getProperty("jms.embedded", "true"));
    protected static String adminUser = System.getProperty("admin.user", "admin1");
    protected static String adminPassword = System.getProperty("admin.password", "password");
    private static EmbeddedJMS server; //used when JMS server embedded in JVM
    private static Context jndi; //used when JMS server is remote

    protected static Properties props;
	public static String topicName;
	public static String topicJNDI;
	public static String connFactoryJNDI;

	/** Creates the topic for use in tests */
	@BeforeClass
	public static void init() throws Exception {
		log.info("*** init() ***");
		
		InputStream in = Thread.currentThread()
        			           .getContextClassLoader()
                               .getResourceAsStream("jmsNotifier.properties");

		assertNotNull("could not locate properties file",in);
        props = new Properties();
        props.load(in);
        //mangle these names to keep from overlapping with statically defined
        topicName = props.getProperty("jmx.name.testTopic");
		topicJNDI = props.getProperty("jndi.name.testTopic");
		connFactoryJNDI = props.getProperty("jndi.name.connFactory");

		JMSAdmin jmsAdmin=null;
		ConnectionFactory connFactory=null;
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
	        connFactory = (ConnectionFactory)jndi.lookup(connFactoryJNDI);
	        jmsAdmin=new JMSAdminHornetQ(connFactory, adminUser, adminPassword)
	        	.setJNDIPrefix("/jboss/exported");
		}		
		
		//jmsAdmin.destroyTopic(topicName)
		//        .deployTopic(topicName, topicJNDI);
		jmsAdmin.close();
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		if (server != null) {
			server.stop();
		}
		if (jndi != null) {
			jndi.close();
		}
	}
	
	protected Object lookup(String name) throws NamingException {
		log.debug("lookup:" + name);
		return (server != null) ?
			server.lookup(name) :
			jndi.lookup(name);	
	}
	
	@Test
	public void verifyResources() throws Exception {
		InitialContext jndi = new InitialContext();
		log.info("looking up JNDI factory:" + connFactoryJNDI);
		
		@SuppressWarnings("unused")
		ConnectionFactory connFactory = (ConnectionFactory)lookup(connFactoryJNDI);
		assertNotNull("connFactory not found", connFactory);
		
		log.info("looking up topic:" + topicJNDI);
		@SuppressWarnings("unused")
		Topic topic = (Topic) lookup(topicJNDI);
		assertNotNull("topic not found", topic);
	}

	@Test
	public void publishToSubscribers() {
		log.info("*** publishToSubscribers ***");
				
		String name = props.getProperty("publisher.name");
		String sleep = props.getProperty("publisher.sleep");
		//String max = props.getProperty("publisher.max");		
		String pubArgs[] = {
				"-jndi.name.connFactory", connFactoryJNDI,
				"-jndi.name.destination", topicJNDI,
				"-name", name, 
                "-sleep", sleep,
                "-max", "10"};

		String name0 = props.getProperty("subscriber.name");
		String sleep0 = props.getProperty("subscriber.sleep");
		String durable0 = props.getProperty("subscriber.durable");
		String selector0 = props.getProperty("subscriber.selector");
		final String sub0Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name0,
            "-sleep", sleep0,
            "-max", "10",
            "-durable", durable0,
            "-selector", selector0};
		
		new Thread() {
			public void run() {
				Subscriber.main(sub0Args);
			}			
		}.start();
		
		String name1 = props.getProperty("subscriber1.name");
		String sleep1 = props.getProperty("subscriber1.sleep");
		String durable1 = props.getProperty("subscriber1.durable");
		String selector1 = props.getProperty("subscriber1.selector");
		final String sub1Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name1,
            "-sleep", sleep1,
            "-max", "2",
            "-durable", durable1,
            "-selector", selector1};
		new Thread() {
			public void run() {
				Subscriber.main(sub1Args);
			}			
		}.start();

		String name2 = props.getProperty("subscriber2.name");
		String sleep2 = props.getProperty("subscriber2.sleep");
		String durable2 = props.getProperty("subscriber2.durable");
		String selector2 = props.getProperty("subscriber2.selector");
		final String sub2Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name2,
            "-sleep", sleep2,
            "-max", "2",
            "-durable", durable2,
            "-selector", selector2};
		new Thread() {
			public void run() {
				Subscriber.main(sub2Args);
			}			
		}.start();

		String name3 = props.getProperty("subscriber3.name");
		String sleep3 = props.getProperty("subscriber3.sleep");
		String durable3 = props.getProperty("subscriber3.durable");
		String selector3 = props.getProperty("subscriber3.selector");
		final String sub3Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name3,
            "-sleep", sleep3,
            "-max", "2",
            "-durable", durable3,
            "-selector", selector3};
		new Thread() {
			public void run() {
				Subscriber.main(sub3Args);
			}			
		}.start();

		String name4 = props.getProperty("subscriber4.name");
		String sleep4 = props.getProperty("subscriber4.sleep");
		String durable4 = props.getProperty("subscriber4.durable");
		String selector4 = props.getProperty("subscriber4.selector");
		final String sub4Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name4,
            "-sleep", sleep4,
            "-max", "2",
            "-durable", durable4,
            "-selector", selector4};
		new Thread() {
			public void run() {
				Subscriber.main(sub4Args);
			}			
		}.start();

		Publisher.main(pubArgs);
	}
}