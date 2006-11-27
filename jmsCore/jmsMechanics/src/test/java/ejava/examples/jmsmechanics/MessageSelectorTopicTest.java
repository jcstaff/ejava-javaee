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
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This test case performs a demonstration of using a message selector with
 * a MessageConsumer and a Topic. In the specific case tested, the number
 * of messages received will be 2x the number sent for 'warn' and 'fatal' plus
 * 1x the number sent for 'info' plus zero for number sent for 'debug'.
 *
 * @author jcstaff
 */
public class MessageSelectorTopicTest extends TestCase {
    static Log log = LogFactory.getLog(MessageSelectorTopicTest.class);
    InitialContext jndi;
    String connFactoryJNDI = System.getProperty("jndi.name.connFactory");
    String destinationJNDI = System.getProperty("jndi.name.testTopic");
    String msgCountStr = System.getProperty("multi.message.count");
    
    ConnectionFactory connFactory;
    Destination destination;        
    int msgCount;
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        
        assertNotNull("jndi.name.connFactory not supplied", connFactoryJNDI);
        log.debug("connection factory name:" + connFactoryJNDI);
        connFactory = (ConnectionFactory)jndi.lookup(connFactoryJNDI);
        
        assertNotNull("jndi.name.testTopic not supplied", destinationJNDI);
        log.debug("destination name:" + destinationJNDI);
        destination = (Topic) jndi.lookup(destinationJNDI);
        
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
                        ", level=" + message.getStringProperty("level"));
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
                        "):" + message.getJMSMessageID() +
                        ", level=" + message.getStringProperty("level"));
            }
            return message;
        }
    }

    public void testMessageSelector() throws Exception {
        log.info("*** testMessageSelector ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            List<MyClient> clients = new ArrayList<MyClient>();

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            String selector1 = "level in ('warn', 'fatal')";
            asyncConsumer = session.createConsumer(destination, selector1);
            AsyncClient asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);

            //create a client to synchronously poll for messages with 
            //receive calls
            String selector2 = "level in ('debug', 'info','warn', 'fatal')";
            syncConsumer = session.createConsumer(destination, selector2);
            SyncClient syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);
            
            String levels[] = {"info", "warn", "fatal"}; //no "debug"
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            for (String level : levels) {
                message.setStringProperty("level", level);
                producer.send(message);
                log.info("sent msgId=" + message.getJMSMessageID() +
                        ", level=" + message.getStringProperty("level"));
            }
            
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10; i++) {
                for(MyClient client: clients) {
                    Message m = client.getMessage();
                    receivedCount += (m != null ? 1 : 0);
                }
                if (receivedCount == 5) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(2, asyncClient.getCount());
            assertEquals(3, syncClient.getCount());
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
    
    public void testMessageSelectorMulti() throws Exception {
        log.info("*** testMessageSelectorMulti ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            List<MyClient> clients = new ArrayList<MyClient>();

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            String selector1 = "level in ('warn', 'fatal')";
            asyncConsumer = session.createConsumer(destination, selector1);
            AsyncClient asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);

            //create a client to synchronously poll for messages with 
            //receive calls
            String selector2 = "level in ('debug', 'info','warn', 'fatal')";
            syncConsumer = session.createConsumer(destination, selector2);
            SyncClient syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);
            
            String levels[] = {"info", "warn", "fatal"}; // no "debug",             
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            for (int i=0; i<msgCount; i++) {
                for (String level : levels) {
                    message.setStringProperty("level", level);
                    producer.send(message);
                    log.info("sent msgId=" + message.getJMSMessageID() +
                            ", level=" + message.getStringProperty("level"));
                }
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
                if (receivedCount == (3*msgCount + 2*msgCount)) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(10);
            }
            assertEquals(msgCount*2, asyncClient.getCount());
            assertEquals(msgCount*3, syncClient.getCount());
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
