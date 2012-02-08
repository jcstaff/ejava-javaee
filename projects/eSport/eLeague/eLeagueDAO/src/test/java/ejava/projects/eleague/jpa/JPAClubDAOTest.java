package ejava.projects.eleague.jpa;

import static org.junit.Assert.*;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ejava.projects.eleague.bo.Address;
import ejava.projects.eleague.bo.Venue;
import ejava.projects.eleague.dao.ClubDAO;
import ejava.projects.eleague.jpa.JPAClubDAO;

/**
 * This test case provides an example of one might test the JPA DAO. It 
 * tests only a minor set of functionality. However, this can be used
 * as a starting point for more detailed tests. 
 * 
 * @author jcstaff
 *
 */
public class JPAClubDAOTest {
	private static Log log = LogFactory.getLog(JPAClubDAO.class);
	//this code assumes all the JDBC properties were placed in 
	//META-INF/persistence.xml when the file was copied from src to the 
	//target tree
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private ClubDAO dao;
	
	@Before
	public void setUp() throws Exception {
		emf = Persistence.createEntityManagerFactory("eLeagueBO-test");
		em = emf.createEntityManager();
		
	    dao = new JPAClubDAO();
	    ((JPAClubDAO)dao).setEntityManager(em);
		
		cleanup();
		
		em.getTransaction().begin();
	}

	@After
	public void tearDown() throws Exception {
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
	private void cleanup() throws Exception {
		Query query = em.createQuery("select v from Venue v");
		for (Venue venue : (List<Venue>)query.getResultList()) {
			//the Venue entity declared cascade=All to the Address
			//so this should delete the address as well
			em.remove(venue);
		}
	}

	/**
	 * This method tests a single create into the database using the DAO. 
	 * This tests some core functionality, but clearly more types of inserts
	 * should also be tested. For example, what happens when the same userId 
	 * is added a second time.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJPACreate() throws Exception {
		log.info("*** testJPACreate ***");
		
    	String name = "myVenue";
    	
    	Venue venue = new Venue();
    	venue.setName(name);
    	venue.setAddress(new Address(0, "Laurel"));
    	
    	log.debug("instantiated Venue:" + venue);
    	
    	dao.createVenue(venue);
    	log.debug("dao created Venue:" + venue);
    	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JPA resources.
    	
    	em.flush();
    	assertTrue(em.contains(venue));
    	em.clear();
    	em.getTransaction().commit();
    	assertFalse(em.contains(venue));
    	
    	Venue venue2 = em.find(Venue.class, venue.getId());
      	log.debug("checking venue...");

    	assertNotNull(venue2);
    	assertEquals("unexpected name", name, venue2.getName());
    	assertNotNull("missing address", venue.getAddress());
    	assertEquals("unexpected city", 
    	        venue.getAddress().getCity(),
    	        venue2.getAddress().getCity());
    	
      	log.debug("JPA venue looked good...");
	}
}
