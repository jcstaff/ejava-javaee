package ejava.examples.jmsmechanics;

import java.util.LinkedList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Queue;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This test case performs a demonstration of using a message priorities.
 * Messages will be sent in an ad-hoc priority order and then later received.
 * Although the provider is not actually specified in the actual behavior,
 * you would expect some type of priority ordering in this case.
 *
 * @author jcstaff
 */
public class MessagePriorityTest extends TestCase {
    static Log log = LogFactory.getLog(MessagePriorityTest.class);
    InitialContext jndi;
    String connFactoryJNDI = System.getProperty("jndi.name.connFactory");
    String destinationJNDI = System.getProperty("jndi.name.testQueue");
    String msgCountStr = System.getProperty("multi.message.count");
    
    ConnectionFactory connFactory;
    Destination destination;        
    int msgCount;
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        
        assertNotNull("jndi.name.testQueue not supplied", destinationJNDI);
        new JMSAdmin().destroyQueue("queue1")
                      .deployQueue("queue1", destinationJNDI);        
        
        assertNotNull("jndi.name.connFactory not supplied", connFactoryJNDI);
        log.debug("connection factory name:" + connFactoryJNDI);
        connFactory = (ConnectionFactory)jndi.lookup(connFactoryJNDI);
        
        log.debug("destination name:" + destinationJNDI);
        destination = (Queue) jndi.lookup(destinationJNDI);
        
        assertNotNull("multi.message.count not supplied", msgCountStr);
        msgCount = Integer.parseInt(msgCountStr);        
    }
    
    protected void tearDown() throws Exception {
    }
    
    private interface MyClient {
        int getCount();
        Message getMessage() throws Exception;
    }
    private class AsyncClient implements MessageListener, MyClient {
        private int count=0;
        LinkedList<Message> messages = new LinkedList<Message>();
        public void onMessage(Message message) {
            try {
                log.debug("onMessage received (" + ++count + 
                        "):" + message.getJMSMessageID() +
                        ", priority=" + message.getJMSPriority());
                messages.add(message);
            } catch (JMSException ex) {
                log.fatal("error handling message", ex);
            }
        }        
        public int getCount() { return count; }
        public Message getMessage() {
            return (messages.isEmpty() ? null : messages.remove());
        }
    }
    
    public void testProducerPriority() throws Exception {
        log.info("*** testProducerPriority ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            consumer = session.createConsumer(destination);
            AsyncClient client = new AsyncClient();
            consumer.setMessageListener(client);
            
            int priorities[] = {9,0,8,1,7,2,6,3,6,4,5};             
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            for (int i=0; i<msgCount; i++) {
                for (int priority : priorities) {
                    producer.setPriority(priority);
                    producer.send(message);
                    log.info("sent (" + i + 
                            ")msgId=" + message.getJMSMessageID() +
                            ", priority=" + message.getJMSPriority());
                }
            }
            
            connection.start();
            int receivedCount=0;
            int prevPriority = 9;
            for(int i=0; i<10 || i<msgCount; i++) {
                Message m=null;
                do {
                   m = client.getMessage();
                   if (m != null) {
                       receivedCount += 1;
                       int priority = m.getJMSPriority();
                       if (priority > prevPriority) {
                           log.warn("previous priority=" + prevPriority +
                                   " received " + priority);
                       }
                       prevPriority = priority;
                   }
                } while (m != null);
                if (receivedCount == (priorities.length*msgCount)) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            log.info("client received " +client.getCount()+ " msgs");
            assertEquals(msgCount*priorities.length, 
                    client.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (consumer != null) { consumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
            if (connection != null) { connection.close(); }
        }
    }    

    public void testSendPriority() throws Exception {
        log.info("*** testSendPriority ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            consumer = session.createConsumer(destination);
            AsyncClient client = new AsyncClient();
            consumer.setMessageListener(client);
            
            int priorities[] = {9,0,8,1,7,2,6,3,6,4,5};             
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            for (int i=0; i<msgCount; i++) {
                for (int priority : priorities) {
                    producer.send(message, 
                                  Message.DEFAULT_DELIVERY_MODE,
                                  priority,
                                  Message.DEFAULT_TIME_TO_LIVE);
                    log.info("sent (" + i + 
                            ")msgId=" + message.getJMSMessageID() +
                            ", priority=" + message.getJMSPriority());
                }
            }
            
            connection.start();
            int receivedCount=0;
            int prevPriority = 9;
            for(int i=0; i<10 || i<msgCount; i++) {
                Message m=null;
                do {
                   m = client.getMessage();
                   if (m != null) {
                       receivedCount += 1;
                       int priority = m.getJMSPriority();
                       if (priority > prevPriority) {
                           log.warn("previous priority=" + prevPriority +
                                   " received " + priority);
                       }
                       prevPriority = priority;
                   }
                } while (m != null);
                if (receivedCount == (priorities.length*msgCount)) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            log.info("client received " +client.getCount()+ " msgs");
            assertEquals(msgCount*priorities.length, 
                    client.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (consumer != null) { consumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
            if (connection != null) { connection.close(); }
        }
    }    

    public void testMessagePriority() throws Exception {
        log.info("*** testMessagePriority ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            consumer = session.createConsumer(destination);
            AsyncClient client = new AsyncClient();
            consumer.setMessageListener(client);
            
            int priorities[] = {9,0,8,1,7,2,6,3,6,4,5};             
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            for (int i=0; i<msgCount; i++) {
                for (int priority : priorities) {
                    message.setJMSPriority(priority);
                    producer.send(message);
                    log.info("sent (" + i + 
                            ")msgId=" + message.getJMSMessageID() +
                            ", priority=" + message.getJMSPriority());
                }
            }
            
            connection.start();
            int receivedCount=0;
            int prevPriority = 9;
            for(int i=0; i<10 || i<msgCount; i++) {
                Message m=null;
                do {
                   m = client.getMessage();
                   if (m != null) {
                       receivedCount += 1;
                       int priority = m.getJMSPriority();
                       if (priority > prevPriority) {
                           log.warn("previous priority=" + prevPriority +
                                   " received " + priority);
                       }
                       prevPriority = priority;
                   }
                } while (m != null);
                if (receivedCount == (priorities.length*msgCount)) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            log.info("client received " +client.getCount()+ " msgs");
            assertEquals(msgCount*priorities.length, 
                    client.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (consumer != null) { consumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
            if (connection != null) { connection.close(); }
        }
    }    

}
