package ejava.examples.jmsmechanics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This test case performs a demonstration of the two mechanisms that a 
 * MessageConsumer has for receiving messages using a Queue.
 *
 * @author jcstaff
 */
public class MessageConsumerQueueTest extends TestCase {
    static Log log = LogFactory.getLog(MessageConsumerQueueTest.class);
    InitialContext jndi;
    String connFactoryJNDI = System.getProperty("jndi.name.connFactory",
        "ConnectionFactory");
    String destinationJNDI = System.getProperty("jndi.name.testQueue",
        "queue/ejava/examples/jmsMechanics/queue1");
    String msgCountStr = System.getProperty("multi.message.count", "20");
    
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
                        "):" + message.getJMSMessageID());
                messages.add(message);
                message.acknowledge();
            } catch (JMSException ex) {
                log.fatal("error handling message", ex);
            }
        }        
        public int getCount() { return count; }
        public Message getMessage() {
            return (messages.isEmpty() ? null : messages.remove());
        }
    }
    
    private class SyncClient implements MyClient {
        private MessageConsumer consumer;
        private int count=0;
        public SyncClient(MessageConsumer consumer) {
            this.consumer = consumer;
        }
        public int getCount() { return count; }
        public Message getMessage() throws JMSException {
            Message message=consumer.receiveNoWait();
            if (message != null) {
                log.debug("receive (" + ++count + 
                        "):" + message.getJMSMessageID());
                message.acknowledge();
            }        
            return message;
        }
    }

    public void testMessageConsumer() throws Exception {
        log.info("*** testMessageConsumer ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection = connFactory.createConnection();
            //need to use CLIENT_ACK to avoid race condition within this app
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            List<MyClient> clients = new ArrayList<MyClient>();

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            asyncConsumer = session.createConsumer(destination);
            AsyncClient asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);

            //create a client to synchronously poll for messages with 
            //receive calls
            syncConsumer = session.createConsumer(destination);
            SyncClient syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);
            
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            producer.send(message);
            log.info("sent msgId=" + message.getJMSMessageID());
            
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10; i++) {
                for(MyClient client: clients) {
                    Message m = client.getMessage();
                    receivedCount += (m != null ? 1 : 0);
                }
                if (receivedCount == 1) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(1, asyncClient.getCount() + syncClient.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (asyncConsumer != null) { asyncConsumer.close(); }
            if (syncConsumer != null) { syncConsumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
            if (connection != null) { connection.close(); }
        }
    }
    
    public void testMessageConsumerMulti() throws Exception {
        log.info("*** testMessageConsumerMulti ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection = connFactory.createConnection();
            //need to use CLIENT_ACK to avoid race condition within this app
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            List<MyClient> clients = new ArrayList<MyClient>();

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            asyncConsumer = session.createConsumer(destination);
            AsyncClient asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);

            //create a client to synchronously poll for messages with 
            //receive calls
            syncConsumer = session.createConsumer(destination);
            SyncClient syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);
            
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            for (int i=0; i<msgCount; i++) {
                producer.send(message);
                log.info("sent msgId=" + message.getJMSMessageID());
            }
            
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10 || i<msgCount; i++) {
                for(MyClient client: clients) {
                    Message m=null;
                    do {
                       m = client.getMessage();
                       receivedCount += (m != null ? 1 : 0);
                    } while (m != null);
                }
                if (receivedCount == msgCount) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(10);
            }
            if (msgCount > 10) {
                assertTrue("asyncClient did not get messages; " +
                        "not really an error",
                        asyncClient.getCount() > 0); 
                assertTrue("syncClient did not get messages; " +
                        "not really an error",
                        syncClient.getCount() > 0);
            }
            assertEquals(msgCount, 
                    asyncClient.getCount() + syncClient.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (asyncConsumer != null) { asyncConsumer.close(); }
            if (syncConsumer != null) { syncConsumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
            if (connection != null) { connection.close(); }
        }
    }    
    
}
