package ejava.projects.eleague.blimpl;

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

import ejava.projects.eleague.blimpl.ELeagueIngestor;
import ejava.projects.eleague.bo.Venue;
import ejava.projects.eleague.dao.ClubDAO;
import ejava.projects.eleague.jdbc.JDBCClubDAO;

import junit.framework.TestCase;

public class ELeagueIngestorTest extends TestCase {
	private static Log log = LogFactory.getLog(ELeagueIngestorTest.class);
	private static String jdbcDriver = System.getProperty("jdbc.driver");
	private static String jdbcURL = System.getProperty("jdbc.url");
	private static String jdbcUser = System.getProperty("jdbc.user");
	private static String jdbcPassword = System.getProperty("jdbc.password");
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private ClubDAO clubDAO;
	private Connection connection;
	
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
		
	    clubDAO = new JDBCClubDAO();
	    ((JDBCClubDAO)clubDAO).setConnection(connection);
		
		connection.setAutoCommit(false);
		
		emf = Persistence.createEntityManagerFactory("eLeagueBO");
		em = emf.createEntityManager();
		//we could easily switch this to the JPA version here
		//clubDAO = new JPAClubDAO();
		//((JPAAccountDAO)clubDAO).setEntityManager(em);
		
		cleanup();
		em.getTransaction().begin();
	}
	
	protected void tearDown() throws Exception {
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
	}

}
