package ejava.examples.jmsscheduler;

import static org.junit.Assert.*;


import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.jmsmechanics.JMSAdminJMX;
import ejava.examples.jmsscheduler.Requestor;
import ejava.examples.jmsscheduler.Worker;

public class BasicScenarioTest {
	static final Log log = LogFactory.getLog(BasicScenarioTest.class);
	public static String requestQueueName;
	public static String requestQueueJNDI;
    public static String dlqName;
	public static String dlqJNDI;
	public static String factoryJNDI;
	
	protected static Properties props;

	@BeforeClass
	public static void init() throws Exception {
		log.info("*** init() ***");
		
		InputStream in = Thread.currentThread()
        			           .getContextClassLoader()
                               .getResourceAsStream("jmsScheduler.properties");

		assertNotNull("could not locate properties file",in);
        props = new Properties();
        props.load(in);
        in.close();
        //mangle the names to not conflict with static definition names
        requestQueueName = props.getProperty("jmx.name.requestQueue") + "-test";
		requestQueueJNDI = props.getProperty("jndi.name.requestQueue") + "-test";
		dlqName = props.getProperty("jmx.name.DLQ") + "-test";
		dlqJNDI = props.getProperty("jndi.name.DLQ") + "-test";
		factoryJNDI = props.getProperty("jndi.name.connFactory");

		new JMSAdminJMX().destroyQueue(requestQueueName)
		              .deployQueue(requestQueueName, requestQueueJNDI);		
		new JMSAdminJMX().destroyQueue(dlqName)
                      .deployQueue(dlqName, dlqJNDI);		
	}
	
	@Before
	public void setUp() throws Exception {
		int count = emptyQueue(requestQueueJNDI, 0);
		log.info("cleared " + count + " messages from " + requestQueueJNDI);
		count = emptyQueue(dlqJNDI, 0);
		log.info("cleared " + count + " messages from " + dlqJNDI);
	}

	@Test
	public void verifyResources() throws Exception {
		InitialContext jndi = new InitialContext();
		log.info("looking up JNDI factory:" + factoryJNDI);
		
		@SuppressWarnings("unused")
		ConnectionFactory connFactory = 
			(ConnectionFactory)jndi.lookup(factoryJNDI);
		
		log.info("looking up queue:" + requestQueueJNDI);
		@SuppressWarnings("unused")
		Queue queue = (Queue) jndi.lookup(requestQueueJNDI);		
		@SuppressWarnings("unused")
		Queue dlq = (Queue) jndi.lookup(dlqJNDI);
	}
	
	static int emptyQueue(String queueName, int count) throws Exception {
		int total = 0;
		InitialContext jndi = new InitialContext();
		ConnectionFactory connFactory = 
			(ConnectionFactory)jndi.lookup(factoryJNDI);
		Queue queue = (Queue) jndi.lookup(queueName);
		Connection connection = connFactory.createConnection();
		Session session = null;
		MessageConsumer consumer = null;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			consumer = session.createConsumer(queue);
			connection.start();
			for (int i=0; i<count || count == 0; i++) {
				log.debug("cleaning queue of message");
				Message m = consumer.receive(250);
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
		
		String name = props.getProperty("requestor.name");
		String sleep = "0";//props.getProperty("requestor.sleep");
		String max = props.getProperty("requestor.max");		
		String sendArgs[] = {
				"-jndi.name.connFactory", factoryJNDI,
				"-jndi.name.requestQueue", requestQueueJNDI,
	            "-jndi.name.DLQ", dlqJNDI,
				"-name", name, 
                "-sleep", sleep,
                "-max", max};

		String name0 = props.getProperty("worker.name");
		String sleep0 = props.getProperty("worker.sleep");
		final String worker0Args[] = {
            "-jndi.name.connFactory", factoryJNDI,
            "-jndi.name.requestQueue", requestQueueJNDI,
            "-jndi.name.DLQ", dlqJNDI,
            "-name", name0,
            "-sleep", sleep0,
            "-max", max,
            "-noFail", "true"}; //don't fail getting last message
		
		new Thread() {
			public void run() {
				Worker.main(worker0Args);
			}			
		}.start();

		Requestor.main(sendArgs);
	}
	
	@Test
	public void failScenario() {
		log.info("*** failScenario ***");

		String name = props.getProperty("requestor.name");
		String sleep = "0";//props.getProperty("requestor.sleep");
		String max = props.getProperty("requestor.max");		
		final String sendArgs[] = {
				"-jndi.name.connFactory", factoryJNDI,
				"-jndi.name.requestQueue", requestQueueJNDI,
	            "-jndi.name.DLQ", dlqJNDI,
				"-name", name, 
                "-sleep", sleep,
                "-max", max};

		String name1 = props.getProperty("worker1.name");
		String sleep1 = props.getProperty("worker1.sleep");
		String max1 = "3"; //props.getProperty("worker1.max");		
		final String worker1Args[] = {
            "-jndi.name.connFactory", factoryJNDI,
            "-jndi.name.requestQueue", requestQueueJNDI,
            "-jndi.name.DLQ", dlqJNDI,
            "-name", name1,
            "-sleep", sleep1,
            "-max", max1,
            "-noFail", "false"}; //fail on last message
		
		String name2 = props.getProperty("worker2.name");
		String sleep2 = props.getProperty("worker2.sleep");
		String max2 = "3"; //props.getProperty("worker2.max");		
		final String worker2Args[] = {
            "-jndi.name.connFactory", factoryJNDI,
            "-jndi.name.requestQueue", requestQueueJNDI,
            "-jndi.name.DLQ", dlqJNDI,
            "-name", name2,
            "-sleep", sleep2,
            "-max", max2,
            "-noFail", "false"}; //fail on last message

		
		new Thread() { //each of these will try 3, but only complete 2
			public void run() { 
				Worker.main(worker1Args);
				Worker.main(worker2Args);
				Worker.main(worker1Args);
				Worker.main(worker2Args);
				Worker.main(worker1Args);
				Worker.main(worker2Args);
				Worker.main(worker1Args);
				Worker.main(worker2Args);
			}			
		}.start();
		
		Requestor.main(sendArgs);
	}
}
