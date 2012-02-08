package ejava.projects.eleague.blimpl;

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

import ejava.projects.eleague.blimpl.ELeagueIngestor;
import ejava.projects.eleague.bo.Venue;
import ejava.projects.eleague.dao.ClubDAO;
import ejava.projects.eleague.jdbc.JDBCClubDAO;

/**
 * This class provides a test of the business logic classes in the league.
 * @author jcstaff
 *
 */
public class ELeagueIngestorTest {
	private static Log log = LogFactory.getLog(ELeagueIngestorTest.class);
	private static String jdbcDriver = System.getProperty("jdbc.driver","org.hsqldb.jdbcDriver");
	private static String jdbcURL = System.getProperty("jdbc.url","jdbc:hsqldb:hsql://localhost:9001");
	private static String jdbcUser = System.getProperty("jdbc.user","sa");
	private static String jdbcPassword = System.getProperty("jdbc.password","");
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private ClubDAO clubDAO;
	private Connection connection;

	@Before
	public void setUp() throws Exception {
		log.debug("loading JDBC driver:" + jdbcDriver);
		Thread.currentThread()
		      .getContextClassLoader()
		      .loadClass(jdbcDriver)
		      .newInstance();
		
		log.debug(String.format("getting connection(%s, %s, %s)",
				jdbcURL, jdbcUser, jdbcPassword));
		connection = 
			DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
		
	    clubDAO = new JDBCClubDAO();
	    ((JDBCClubDAO)clubDAO).setConnection(connection);
		
		connection.setAutoCommit(false);
		
		emf = Persistence.createEntityManagerFactory("eLeagueBO-test");
		em = emf.createEntityManager();
		//we could easily switch this to the JPA version here
		//clubDAO = new JPAClubDAO();
		//((JPAAccountDAO)clubDAO).setEntityManager(em);
		
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
		List<Venue> venues = em.createQuery("select v from Venue v")
		                           .getResultList();
		for (Venue venue : venues) {
			em.remove(venue);
		}
		em.getTransaction().begin();
		em.getTransaction().commit();
	}

	@Test
	public void testIngestAll() throws Exception {
		log.info("*** testIngestAll ***");
		
		String fileName = "xml/eLeague-all.xml";
		InputStream is = Thread.currentThread()
		                       .getContextClassLoader()
		                       .getResourceAsStream(fileName);
		assertNotNull(fileName + " not found", is);
		
		ELeagueIngestor ingestor = new ELeagueIngestor();
		ingestor.setClubDAO(clubDAO);
		ingestor.setInputStream(is);
		ingestor.ingest();
		
		assertEquals("unexpected number of addresses", 18,
			((Long)em.createQuery("select count(a) from Address a").getSingleResult()).intValue());
		assertEquals("unexpected number of venues", 18,
				((Long)em.createQuery("select count(v) from Venue v").getSingleResult()).intValue());
	}

}
