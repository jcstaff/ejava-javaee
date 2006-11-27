package ejava.examples.jmsmechanics;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This test case demonstrates some basic use of message properties.
 *
 * @author jcstaff
 */
public class MessagePropertiesTest extends TestCase {
    static Log log = LogFactory.getLog(MessagePropertiesTest.class);
    InitialContext jndi;
    String connFactoryJNDI = System.getProperty("jndi.name.connFactory");
    String destinationJNDI = System.getProperty("jndi.name.testTopic");
    
    ConnectionFactory connFactory;
    Destination destination;        
    MessageCatcher catcher1;
    MessageCatcher catcher2;
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
    }
    
    protected void tearDown() throws Exception {
    }
    
    

    public void testMessageProperties() throws Exception {
        log.info("*** testMessageProperties ***");
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            connection = connFactory.createConnection();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            consumer = session.createConsumer(destination);
            connection.start();
            Message message = session.createMessage();
            
            message.setBooleanProperty("booleanProperty", true);
            message.setByteProperty("byteProperty", (byte)0x01);
            message.setDoubleProperty("doubleProperty", 1.01);
            message.setFloatProperty("floatProperty", (float)1.02);
            message.setIntProperty("intProperty", 3);
            message.setLongProperty("longProperty", 5L);
            message.setObjectProperty("intPropertyAsObject", 3);
            message.setShortProperty("shortProperty", (short)4);
            message.setStringProperty("stringProperty", "hello JMS world");            
            
            producer.send(message);            
            Message message2 = consumer.receive(1000);
            assertNotNull("no message received", message2);
            
            log.debug("message2.JMSMessageID=" + message2.getJMSMessageID());
            log.debug("message2.JMSTimestamp=" + message2.getJMSTimestamp());
            try { 
                log.debug("message2.JMSCorrelationIDAsBytes=" + 
                        message2.getJMSCorrelationIDAsBytes());
            }
            catch (JMSException ex) {
                log.debug("message2.JMSCorrelationIDAsBytes=" + 
                        ex);
            }
            log.debug("message2.JMSCorrelationID=" + message2.getJMSCorrelationID());
            log.debug("message2.JMSReplyTo=" + message2.getJMSReplyTo());
            log.debug("message2.JMSDestination=" + message2.getJMSDestination());
            log.debug("message2.JMSDeliveryMode=" + message2.getJMSDeliveryMode());
            log.debug("message2.JMSRedelivered=" + message2.getJMSRedelivered());
            log.debug("message2.JMSType=" + message2.getJMSType());
            log.debug("message2.JMSExpiration=" + message2.getJMSExpiration());
            log.debug("message2.JMSPriority=" + message2.getJMSPriority());

            
            for(Enumeration e=message2.getPropertyNames();e.hasMoreElements();){
                String name = (String)e.nextElement();
                Object property = message2.getObjectProperty(name);
                log.debug("message2." + name +
                        " (:" + property.getClass().getName() +
                        ")=" + property);
            }
            
            assertEquals(message2.getBooleanProperty("booleanProperty"), true);
            assertEquals(message2.getByteProperty("byteProperty"), (byte)0x01);
            assertEquals(message2.getDoubleProperty("doubleProperty"), 1.01);
            assertEquals(message2.getFloatProperty("floatProperty"),
                    (float)1.02);
            assertEquals(message2.getIntProperty("intProperty"), 3);
            assertEquals(message2.getLongProperty("longProperty"), 5L);
            assertEquals(message2.getObjectProperty("intPropertyAsObject"), 3);
            assertEquals(message2.getShortProperty("shortProperty"), (short)4);
            assertEquals(message2.getStringProperty("stringProperty"), 
                    "hello JMS world");            
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
