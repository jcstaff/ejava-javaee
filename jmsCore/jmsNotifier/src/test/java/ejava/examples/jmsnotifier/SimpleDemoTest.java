package ejava.examples.jmsnotifier;


import java.io.InputStream;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import ejava.examples.jmsmechanics.JMSAdmin;

public class SimpleDemoTest {
	static final Log log = LogFactory.getLog(SimpleDemoTest.class);
	protected static Properties props;
	public static String topicName;
	public static String topicJNDI;
	public static String factoryJNDI;

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
        topicName = props.getProperty("jmx.name.testTopic") + "-test";
		topicJNDI = props.getProperty("jndi.name.testTopic") + "-test";
		factoryJNDI = props.getProperty("jndi.name.connFactory");

		new JMSAdmin().destroyTopic(topicName)
		              .deployTopic(topicName, topicJNDI);
	}
	
	@Test
	public void verifyResources() throws Exception {
		InitialContext jndi = new InitialContext();
		log.info("looking up JNDI factory:" + factoryJNDI);
		
		@SuppressWarnings("unused")
		ConnectionFactory connFactory = (ConnectionFactory)jndi.lookup(factoryJNDI);
		
		log.info("looking up topic:" + topicJNDI);
		@SuppressWarnings("unused")
		Topic topic = (Topic) jndi.lookup(topicJNDI);		
	}

	@Test
	public void publishToSubscribers() {
		log.info("*** publishToSubscribers ***");
				
		String name = props.getProperty("publisher.name");
		String sleep = props.getProperty("publisher.sleep");
		//String max = props.getProperty("publisher.max");		
		String pubArgs[] = {
				"-jndi.name.connFactory", factoryJNDI,
				"-jndi.name.destination", topicJNDI,
				"-name", name, 
                "-sleep", sleep,
                "-max", "10"};

		String name0 = props.getProperty("subscriber.name");
		String sleep0 = props.getProperty("subscriber.sleep");
		String durable0 = props.getProperty("subscriber.durable");
		String selector0 = props.getProperty("subscriber.selector");
		final String sub0Args[] = {
            "-jndi.name.connFactory", factoryJNDI,
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
            "-jndi.name.connFactory", factoryJNDI,
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
            "-jndi.name.connFactory", factoryJNDI,
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
            "-jndi.name.connFactory", factoryJNDI,
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
            "-jndi.name.connFactory", factoryJNDI,
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