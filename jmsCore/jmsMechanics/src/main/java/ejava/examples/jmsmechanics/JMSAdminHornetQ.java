package ejava.examples.jmsmechanics;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.management.JMSManagementHelper;

/**
 * This class implements a client to dynamically create JMS resources on the
 * server.
 */
public class JMSAdminHornetQ implements JMSAdmin {
	private Connection connection;
	private Queue managementQueue;
	
	public JMSAdminHornetQ(ConnectionFactory connFactory, String adminUser, String adminPassword) throws JMSException {
		connection = connFactory.createConnection(adminUser, adminPassword);
		connection.start();
		managementQueue = HornetQJMSClient.createQueue("hornetq.management");   
	}
	
	@Override
	public void close() throws JMSException {
		if (connection != null) {
			connection.close();
		}
	}

	public JMSAdmin deployDestination(String method, String name, String jndiName) throws Exception {
		Session session=null;
		QueueRequestor requestor = null;
	   try {
		   session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		   requestor=new QueueRequestor((QueueSession) session, managementQueue);
		   Message message = session.createMessage();
		   JMSManagementHelper.putOperationInvocation(message, "jms.server", method, name, 
				   jndiName);
		   Message reply = requestor.request(message);
		   if (!JMSManagementHelper.hasOperationSucceeded(reply)) {
			   throw new RuntimeException("failed to create desintation:" + name);
			   }
	   } finally {
		   if (requestor != null) { requestor.close(); }
		   if (session != null) { session.close(); }
	   }
   
   return this;
   }

	public JMSAdmin destroyDestination(String method, String name) throws Exception {
		Session session=null;
		QueueRequestor requestor = null;
        try {
 		   session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		   requestor=new QueueRequestor((QueueSession) session, managementQueue);
		   Message message = session.createMessage();
		   JMSManagementHelper.putOperationInvocation(message, "jms.server", method, name);
		   Message reply = requestor.request(message);
		   if (!JMSManagementHelper.hasOperationSucceeded(reply)) {
			   throw new RuntimeException("failed to destroy desintation:" + name);
		   }
	    } finally {
		   if (requestor != null) { requestor.close(); }
		   if (session != null) { session.close(); }
	    }
	   
	   return this;
	}

	@Override
	public JMSAdmin deployTopic(String name, String jndiName) throws Exception {
		return deployDestination("createTopic", name, jndiName);
	}

	@Override
	public JMSAdmin deployQueue(String name, String jndiName) throws Exception {
		return deployDestination("createQueue", name, jndiName);
	}

	@Override
	public JMSAdmin destroyTopic(String name) throws Exception {
		return destroyDestination("destroyTopic", name);
	}

	@Override
	public JMSAdmin destroyQueue(String name) throws Exception {
		return destroyDestination("destroyQueue", name);
	}
}
