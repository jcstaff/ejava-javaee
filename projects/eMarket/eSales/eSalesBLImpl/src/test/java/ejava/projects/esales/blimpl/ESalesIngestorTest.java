package ejava.projects.esales.blimpl;

import static org.junit.Assert.*;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ejava.projects.esales.dao.AccountDAO;
import ejava.projects.esales.bl.AccountMgmt;
import ejava.projects.esales.bo.Account;
import ejava.projects.esales.jdbc.JDBCAccountDAO;
import ejava.projects.esales.jpa.JPAAccountDAO;

public class ESalesIngestorTest {
	private static Log log = LogFactory.getLog(ESalesIngestorTest.class);
	private static String jdbcURL = 
			System.getProperty("jdbc.url", "jdbc:hsqldb:hsql://localhost:9001");	
	private static String jdbcDriver = System.getProperty("jdbc.driver", "org.hsqldb.jdbcDriver");
	private static String jdbcUser = System.getProperty("jdbc.user", "sa");
	private static String jdbcPassword = System.getProperty("jdbc.password","");
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private AccountDAO accountDAO;
	private Connection connection;
	private JPAAccountDAO jpaDAO;
	private AccountMgmt mgmt;
	
	@Before
	public void setUp() throws Exception {
		assertNotNull("jdbc.driver not supplied", jdbcDriver);
		assertNotNull("jdbc.url not supplied", jdbcURL);
		assertNotNull("jdbc.user not supplied", jdbcUser);
		assertNotNull("jdbc.password not supplied", jdbcPassword);
		
		log.debug("loading JDBC driver:" + jdbcDriver);
		Thread.currentThread()
		      .getContextClassLoader()
		      .loadClass(jdbcDriver)
		      .newInstance();
		
		log.debug("getting connection(" + jdbcURL +
				", user=" + jdbcUser + ", password=" + jdbcPassword + ")");
		connection = 
			DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
		
	    accountDAO = new JDBCAccountDAO();
	    ((JDBCAccountDAO)accountDAO).setConnection(connection);
		
		connection.setAutoCommit(false);
		
		emf = Persistence.createEntityManagerFactory("eSalesBO");
		em = emf.createEntityManager();
		//we could easily switch this to the JPA version here
		//accountDAO = new JPAAccountDAO();
		//((JPAAccountDAO)accountDAO).setEntityManager(em);
		
		//lets verify we have something using the JPA DAO
		jpaDAO = new JPAAccountDAO();
		jpaDAO.setEntityManager(em);
		mgmt = new AccountMgmtImpl();
		((AccountMgmtImpl)mgmt).setAccountDAO(jpaDAO);

		cleanup();
		em.getTransaction().begin();
	}
	
	@After
	public void tearDown() throws Exception {
		if (connection != null) {
			connection.commit();
			connection.close();
		}
		
		if (em != null) {
			EntityTransaction tx = em.getTransaction();
			if (tx.isActive()) {
				if (tx.getRollbackOnly()) { tx.rollback(); }
				else                      { tx.commit(); }
			}
			em.close();
		}
		if (emf != null) {
			emf.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void cleanup() {
		List<Account> accounts = em.createQuery("select a from Account a")
		                           .getResultList();
		for (Account account : accounts) {
			em.remove(account);
		}
		em.getTransaction().begin();
		em.getTransaction().commit();
	}

	@Test
	public void testIngest1() throws Exception {
		log.info("*** testIngest1 ***");
		
		String fileName = "xml/eSales-1.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		ESalesIngestor ingestor = new ESalesIngestor();
		ingestor.setAccountDAO(accountDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		//verify we have the expected number of accounts		
		assertEquals("unexpected number of accounts",2,
				mgmt.getAccounts(0, 1000).size());
	}
	
	@Test
	public void testIngest10() throws Exception {
		log.info("*** testIngest10 ***");
		
		String fileName = "xml/eSales-10.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		ESalesIngestor ingestor = new ESalesIngestor();
		ingestor.setAccountDAO(accountDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		//verify we have the expected number of accounts		
		assertEquals("unexpected number of accounts",19,
				mgmt.getAccounts(0, 1000).size());
	}

	@Test
	public void testIngest100() throws Exception {
		log.info("*** testIngest10 ***");
		
		String fileName = "xml/eSales-100.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		ESalesIngestor ingestor = new ESalesIngestor();
		ingestor.setAccountDAO(accountDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		//verify we have the expected number of accounts		
		assertEquals("unexpected number of accounts",209,
				mgmt.getAccounts(0, 1000).size());
	}

	@Test
	public void testIngestAll() throws Exception {
		log.info("*** testIngestAll ***");
		
		String fileName = "xml/eSales-all.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		ESalesIngestor ingestor = new ESalesIngestor();
		ingestor.setAccountDAO(accountDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		
		//verify we have the expected number of accounts		
		assertEquals("unexpected number of accounts",841,
				mgmt.getAccounts(0, 1000).size());
	}

}
