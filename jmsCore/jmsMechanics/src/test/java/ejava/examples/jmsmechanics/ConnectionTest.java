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

public class ConnectionTest extends TestCase {
    static Log log = LogFactory.getLog(ConnectionTest.class);
    InitialContext jndi;
    String connFactoryJNDI = System.getProperty("jndi.name.connFactory");
    String destinationJNDI = System.getProperty("jndi.name.testTopic");
    
    ConnectionFactory connFactory;
        
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        
        assertNotNull("jndi.name.connFactory not supplied", connFactoryJNDI);
        connFactory = (ConnectionFactory)jndi.lookup(connFactoryJNDI);
        
        //assertNotNull("jndi.name.testTopic not supplied", destinationJNDI);
    }
    
    protected void tearDown() throws Exception {
    }

    public void testConnectionMetadata() throws Exception {
        log.info("*** testConnectionMetadata ***");
        Connection connection=null;
        try {
            connection = connFactory.createConnection();
            log.info("connection.metaData=" + connection.getMetaData());
            log.info("connection.metaData.JMSMajorVersion=" + 
                    connection.getMetaData().getJMSMajorVersion());
            log.info("connection.metaData.JMSMinorVersion=" + 
                    connection.getMetaData().getJMSMinorVersion());
            log.info("connection.metaData.JMSProviderName=" + 
                    connection.getMetaData().getJMSProviderName());
            log.info("connection.metaData.JMSVersion=" + 
                    connection.getMetaData().getJMSVersion());
            log.info("connection.metaData.providerMajorVersion=" + 
                    connection.getMetaData().getProviderMajorVersion());
            log.info("connection.metaData.providerMinorVersion=" + 
                    connection.getMetaData().getProviderMinorVersion());
            log.info("connection.metaData.providerVersion=" + 
                    connection.getMetaData().getProviderVersion());
        }
        finally {
            if (connection != null) { connection.close(); }
        }
    }
    
}
