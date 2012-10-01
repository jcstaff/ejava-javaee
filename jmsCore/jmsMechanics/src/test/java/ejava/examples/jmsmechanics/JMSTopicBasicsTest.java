package ejava.examples.jmsmechanics;

import javax.jms.Connection;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This test case performs the basic steps to send/receive messages to/from
 * a JMS topic. Notice that each of the catchers in this test should each
 * receive the same messages.
 *
 * @author jcstaff
 */
public class JMSTopicBasicsTest extends JMSTestBase {
    static Log log = LogFactory.getLog(JMSTopicBasicsTest.class);
    String destinationJNDI = System.getProperty("jndi.name.testTopic",
        "/topic/ejava/examples/jmsMechanics/topic1");
    String msgCountStr = System.getProperty("multi.message.count", "20");
    
    Destination destination;        
    MessageCatcher catcher1;
    MessageCatcher catcher2;
    int msgCount;
    
    @Before
    public void setUp() throws Exception {
        
    	//dynamically create necessary JMS resources
        new JMSAdminHornetQ(connFactory, adminUser, adminPassword)
        	.destroyTopic("topic1")
            .deployTopic("topic1", jmsEmbedded ? destinationJNDI :
            	"/jboss/exported" + destinationJNDI)
            .close();
        
        log.debug("destination name:" + destinationJNDI);
        destination = (Topic) lookup(destinationJNDI);
        assertNotNull("destination null:" + destinationJNDI, destination);
        
        msgCount = Integer.parseInt(msgCountStr);
        
        catcher1 = new MessageCatcher("subscriber1");
        catcher1.setConnFactory(connFactory);
        catcher1.setUser(user);
        catcher1.setPassword(password);
        catcher1.setDestination(destination);
        
        catcher2 = new MessageCatcher("subscriber2");
        catcher2.setConnFactory(connFactory);
        catcher2.setUser(user);
        catcher2.setPassword(password);
        catcher2.setDestination(destination);
        
        //topics will only deliver messages to subscribers that are 
        //successfully registered prior to the message being published. We
        //need to wait for the catcher to start so it doesn't miss any 
        //messages.
        new Thread(catcher1).start();
        new Thread(catcher2).start();
        while (catcher1.isStarted() != true) {
            log.debug("waiting for catcher1 to start");
            Thread.sleep(2000);
        }
        while (catcher2.isStarted() != true) {
            log.debug("waiting for catcher2 to start");
            Thread.sleep(2000);
        }
    }
    
    @After
    public void tearDown() throws Exception {
    	if (catcher1 != null) {
    		catcher1.stop();
            while (catcher1.isStopped() != true) {
                log.debug("waiting for catcher1 to stop");
                Thread.sleep(2000);
            }
    	}
    	if (catcher2 != null) {
    		catcher2.stop();
            while (catcher2.isStopped() != true) {
                log.debug("waiting for catcher2 to stop");
                Thread.sleep(2000);
            }
    	}
    }

    @Test
    public void testTopicSend() throws Exception {
        log.info("*** testTopicSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            producer.send(message);
            log.info("sent msgId=" + message.getJMSMessageID());
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < 1 ||
                catcher2.getMessages().size() < 1); i++) {
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(1, catcher1.getMessages().size());
            assertEquals(1, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
    
    @Test
    public void testTopicMultiSend() throws Exception {
        log.info("*** testTopicMultiSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(message);
                log.info("sent msgId=" + message.getJMSMessageID());
            }
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < msgCount ||
                catcher2.getMessages().size() < msgCount); i++) {
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(msgCount, catcher1.getMessages().size());
            assertEquals(msgCount, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
}
