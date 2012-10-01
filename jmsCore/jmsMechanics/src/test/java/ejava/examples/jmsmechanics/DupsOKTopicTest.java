package ejava.examples.jmsmechanics;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This test case performs the basic steps to send/receive messages to/from
 * a JMS topic. In this test, the receiver allows session to lazily acknowledge
 * messages.
 *
 * @author jcstaff
 */
public class DupsOKTopicTest extends TestCase {
    static Log log = LogFactory.getLog(DupsOKTopicTest.class);
    InitialContext jndi;
    String connFactoryJNDI = System.getProperty("jndi.name.connFactory",
        "ConnectionFactory");
    String destinationJNDI = System.getProperty("jndi.name.testTopic",
        "topic/ejava/examples/jmsMechanics/topic1");
    String msgCountStr = System.getProperty("multi.message.count", "20");
    
    ConnectionFactory connFactory;
    Destination destination;        
    MessageCatcher catcher1;
    MessageCatcher catcher2;
    int msgCount;
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());

        assertNotNull("jndi.name.testTopic not supplied", destinationJNDI);
        new JMSAdminJMX().destroyTopic("topic1")
                      .deployTopic("topic1", destinationJNDI);

        assertNotNull("jndi.name.connFactory not supplied", connFactoryJNDI);
        log.debug("connection factory name:" + connFactoryJNDI);
        connFactory = (ConnectionFactory)jndi.lookup(connFactoryJNDI);
        
        log.debug("destination name:" + destinationJNDI);
        destination = (Topic) jndi.lookup(destinationJNDI);
        
        assertNotNull("multi.message.count not supplied", msgCountStr);
        msgCount = Integer.parseInt(msgCountStr);
        
        catcher1 = new MessageCatcher("subscriber1");
        catcher1.setConnFactory(connFactory);
        catcher1.setDestination(destination);
        catcher1.setAckMode(Session.DUPS_OK_ACKNOWLEDGE);
        
        catcher2 = new MessageCatcher("subscriber2");
        catcher2.setConnFactory(connFactory);
        catcher2.setDestination(destination);
        catcher2.setAckMode(Session.DUPS_OK_ACKNOWLEDGE);
        
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
    
    protected void tearDown() throws Exception {
        catcher1.stop();
        catcher2.stop();
        while (catcher1.isStopped() != true) {
            log.debug("waiting for catcher1 to stop");
            Thread.sleep(2000);
        }
        while (catcher2.isStopped() != true) {
            log.debug("waiting for catcher2 to stop");
            Thread.sleep(2000);
        }
    }

    public void testTopicSend() throws Exception {
        log.info("*** testTopicSend ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = connFactory.createConnection();
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
            if (connection != null) { connection.close(); }
        }
    }
    
    public void testTopicMultiSend() throws Exception {
        log.info("*** testTopicMultiSend ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = connFactory.createConnection();
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
            if (connection != null) { connection.close(); }
        }
    }
}
