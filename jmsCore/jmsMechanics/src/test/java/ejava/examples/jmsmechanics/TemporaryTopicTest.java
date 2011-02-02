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
 * This test case tests the ability to create and send/receive messages 
 * to/from a temporary topic. 
 *
 * @author jcstaff
 */
public class TemporaryTopicTest extends TestCase {
    static Log log = LogFactory.getLog(TemporaryTopicTest.class);
    InitialContext jndi;
    String connFactoryJNDI = System.getProperty("jndi.name.connFactory",
        "ConnectionFactory");
    String msgCountStr = System.getProperty("multi.message.count", "20");
    
    protected ConnectionFactory connFactory;
    protected Connection connection = null;
    protected Session session = null;
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;
    protected int msgCount;
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        
        assertNotNull("jndi.name.connFactory not supplied", connFactoryJNDI);
        log.debug("connection factory name:" + connFactoryJNDI);
        connFactory = (ConnectionFactory)jndi.lookup(connFactoryJNDI);
        
        assertNotNull("multi.message.count not supplied", msgCountStr);
        msgCount = Integer.parseInt(msgCountStr);        
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
        if (session != null)  { session.close(); }
        if (connection != null) { connection.close(); }
    }
    
    protected void startCatchers(Session session, Destination destination) {
        catcher1 = new MessageCatcher("subscriber1");
        catcher1.setSession(session);
        catcher1.setDestination(destination);
        
        catcher2 = new MessageCatcher("subscriber2");
        catcher2.setSession(session);
        catcher2.setDestination(destination);
        
        //topics will only deliver messages to subscribers that are 
        //successfully registered prior to the message being published. We
        //need to wait for the catcher to start so it doesn't miss any 
        //messages.
        new Thread(catcher1).start();
        new Thread(catcher2).start();
        while (catcher1.isStarted() != true) {
            log.debug("waiting for catcher1 to start");
            try { Thread.sleep(2000); } catch (Exception ignored) {}
        }
        while (catcher2.isStarted() != true) {
            log.debug("waiting for catcher2 to start");
            try { Thread.sleep(2000); } catch (Exception ignored) {}
        }        
    }

    public void testTemporaryTopicSend() throws Exception {
        log.info("*** testTemporaryTopicSend ***");
        MessageProducer producer = null;
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            Topic destination = session.createTemporaryTopic();
            log.debug("created temporary topic=" + destination);
            startCatchers(session, destination);
            connection.start();
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
        }
    }
    
    public void testTemporaryTopicMultiSend() throws Exception {
        log.info("*** testTemporaryTopicMultiSend ***");
        MessageProducer producer = null;
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            Topic destination = session.createTemporaryTopic();
            log.debug("created temporary topic=" + destination);
            startCatchers(session, destination);
            connection.start();
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
        }
    }
}
