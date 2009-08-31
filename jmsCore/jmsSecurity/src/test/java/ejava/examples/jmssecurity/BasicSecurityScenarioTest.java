package ejava.examples.jmssecurity;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSSecurityException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test verifies the JMS configuration for the security demo. 
 *
 */
public class BasicSecurityScenarioTest {
	static final Log log = LogFactory.getLog(BasicSecurityScenarioTest.class);
	
	static final String requestQueueName = 
		System.getProperty("request.queue.name", "requestQueue");
	static String requestQueueJNDI; 
	static final String responseQueueName = 
		System.getProperty("response.queue.name", "responseQueue");
	static String responseQueueJNDI; 
	static final String dlqName = 
		System.getProperty("dlq.queue.name", "dlqQueue");
	static String dlqJNDI;
	static String factoryJNDI;
	
	static Properties props;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		InputStream in = null;
		try {
			log.info("*** init() ***");
			
			in = Thread.currentThread()
	                   .getContextClassLoader()
	                   .getResourceAsStream("jmsSecurity.properties");
	
			assertNotNull("could not locate properties file",in);
	        props = new Properties();
	        props.load(in);
			requestQueueJNDI = props.getProperty("jndi.name.requestQueue");
			responseQueueJNDI = props.getProperty("jndi.name.responseQueue");
			dlqJNDI = props.getProperty("jndi.name.DLQ");
			factoryJNDI = props.getProperty("jndi.name.connFactory");
		} 
		catch (Exception ex) {
			log.fatal("error in init()", ex);
			fail();
		}
		finally {
			if (in != null) { in.close(); }
		}
	}

	@Before
	public void setUp() throws Exception {
		try {
		int count = emptyQueue(requestQueueJNDI, 0);
		log.info("cleared " + count + " messages from " + requestQueueJNDI);
		count = emptyQueue(responseQueueJNDI, 0);
		log.info("cleared " + count + " messages from " + responseQueueJNDI);
		count = emptyQueue(dlqJNDI, 0);
		log.info("cleared " + count + " messages from " + dlqJNDI);
		}
		catch (Exception ex) {
			log.fatal("error in setUp():", ex);
			fail();
		}
	}

	static int emptyQueue(String queueName, int count) throws Exception {
		int total = 0;
		InitialContext jndi = new InitialContext();
		ConnectionFactory connFactory = 
			(ConnectionFactory)jndi.lookup(factoryJNDI);
		log.info("jndi.lookup:" + queueName);
		Queue queue = (Queue) jndi.lookup(queueName);
		Connection connection = connFactory.createConnection("admin", "password");
		Session session = null;
		MessageConsumer consumer = null;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			consumer = session.createConsumer(queue);
			connection.start();
			for (int i=0; i<count || count == 0; i++) {
				log.debug("cleaning queue of message");
				Message m = consumer.receive(100);
				if (m == null) { break; }
				total += 1;
			}
		}
		finally {
			if (consumer != null) { consumer.close(); }
			if (session != null) { session.close(); }
			if (connection != null) { connection.close(); }
		}
		return total;
	}

	@Test
	public void schedulerScenario() {
		log.info("*** schedulerScenario ***");
		
		String loginConfig = props.getProperty("loginConfig");
		
		String name = props.getProperty("requestor.name");
		String sleep = "0";//props.getProperty("requestor.sleep");
		String max = props.getProperty("requestor.max");
		String requestorUser = props.getProperty("requestor.username");
		String requestorPassword = props.getProperty("requestor.password");
		String sendArgs[] = {
				"-jndi.name.connFactory", factoryJNDI,
				"-jndi.name.requestQueue", requestQueueJNDI,
				"-jndi.name.responseQueue", responseQueueJNDI,
	            "-jndi.name.DLQ", dlqJNDI,
				"-name", name, 
                "-sleep", sleep,
                "-max", max,
		        "-username", requestorUser,
		        "-password", requestorPassword,
				"-loginConfig", loginConfig};

		String name0 = props.getProperty("worker.name");
		String sleep0 = props.getProperty("worker.sleep");
		String workerUser = props.getProperty("worker.username");
		String workerPassword = props.getProperty("worker.password");
		final String worker0Args[] = {
            "-jndi.name.connFactory", factoryJNDI,
			"-jndi.name.requestQueue", requestQueueJNDI,
			"-jndi.name.responseQueue", responseQueueJNDI,
            "-jndi.name.DLQ", dlqJNDI,
            "-name", name0,
            "-sleep", sleep0,
            "-max", max,
	        "-username", workerUser,
	        "-password", workerPassword,
	        "-loginConfig", loginConfig,
            "-noFail", "true"}; //don't fail getting last message
		
		new Thread() {
			public void run() {
				SecureWorker.main(worker0Args);
			}			
		}.start();

		SecureRequestor.main(sendArgs);
	}
	
	@Test
	public void testSendLogins() throws Exception {
		log.info("*** testSendLogins ***");
		
		InitialContext jndi = new InitialContext();
		ConnectionFactory connFactory = 
			(ConnectionFactory)jndi.lookup(factoryJNDI);
		Queue requestQueue =
			(Queue)jndi.lookup(requestQueueJNDI);
		
		String[] userNamePasswords = 
			{ props.getProperty("requestor.username"),
		      props.getProperty("requestor.password"),
		      props.getProperty("requestor1.username"),
		      props.getProperty("requestor1.password"),
		      props.getProperty("requestor2.username"),
		      props.getProperty("requestor2.password")};
		for (int i=0; i<userNamePasswords.length; i++) {
			String user = userNamePasswords[i];
			String password = userNamePasswords[++i];
			Connection connection = null;
			MessageProducer producer = null;
			Session session = null;
			try {
				log.info("createFactory(): " + user + "/" + password);
				connection = connFactory.createConnection(user, password);
			}
			catch (Exception ex) {
				log.info("login failed for: " + user + "/" + password + ": " + ex);
			}
			if (connection != null) {
				try {
					session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
					log.info("createProducer(): " + user + "/" + password);
					producer = session.createProducer(requestQueue);
				}
				catch (Exception ex) {
					connection.close();
					if (session != null) session.close(); 
					log.info("createProducer failed for: " + user + "/" + password + ": " + ex);
				}
			}
			if (producer != null) {
				try {
					Message m = session.createMessage();
					log.info("sendMessage(): " + user + "/" + password);
					producer.send(m);
				}
				catch (Exception ex){
					log.info("sendMessage failed for: " + user + "/" + password + ": " + ex);
				}
				finally {
					producer.close();
					session.close();
					connection.close();
				}
			}
		}
		assertEquals("unexpected number of messages in requestQueue",
				1, emptyQueue(requestQueueJNDI, 0));
	}
	
	@Test
	public void testReceiveLogins() throws Exception {
		log.info("*** testReceiveLogins ***");
		
		InitialContext jndi = new InitialContext();
		ConnectionFactory connFactory = 
			(ConnectionFactory)jndi.lookup(factoryJNDI);
		Queue requestQueue =
			(Queue)jndi.lookup(requestQueueJNDI);
		
		String[] userNamePasswords = 
			{ props.getProperty("worker.username"),
		      props.getProperty("worker.password"),
		      props.getProperty("worker1.username"),
		      props.getProperty("worker1.password"),
		      props.getProperty("worker2.username"),
		      props.getProperty("worker2.password")};
		for (int i=0; i<userNamePasswords.length; i++) {
			String user = userNamePasswords[i];
			String password = userNamePasswords[++i];
			Connection connection = null;
			MessageConsumer consumer = null;
			Session session = null;
			try {
				log.info("createFactory(): " + user + "/" + password);
				connection = connFactory.createConnection(user, password);
			}
			catch (Exception ex) {
				log.info("login failed for: " + user + "/" + password + ": " + ex);
			}
			if (connection != null) {
				try {
					session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
					log.info("createConsumer(): " + user + "/" + password);
					consumer = session.createConsumer(requestQueue);
				}
				catch (Exception ex) {
					connection.close();
					if (session != null) session.close(); 
					log.info("createConsumer failed for: " + user + "/" + password + ": " + ex);
				}
			}
			if (consumer != null) {
				try {
					connection.start();
					log.info("receive(): " + user + "/" + password);
					Message m = consumer.receive(100);
				}
				catch (Exception ex){
					log.info("receive failed for: " + user + "/" + password + ": " + ex);
				}
				finally {
					consumer.close();
					session.close();
					connection.close();
				}
			}
		}
	}

}
