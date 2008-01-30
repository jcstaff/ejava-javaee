package ejava.projects.edmv.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.dao.PersonDAO;
import ejava.projects.edmv.jpa.JPAPersonDAO;

import junit.framework.TestCase;

/**
 * This test case provides an example of one might test the JPA DAO. It 
 * tests only a minor set of functionality. However, this can be used
 * as a starting point for more detailed tests. 
 * 
 * @author jcstaff
 *
 */
public class JPAPersonDAOTest extends TestCase {
	private static Log log = LogFactory.getLog(JPAPersonDAOTest.class);
	//this code assumes all the JDBC properties were placed in 
	//META-INF/persistence.xml when the file was copied from src to the 
	//target tree
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private PersonDAO dao;
	
	public void setUp() throws Exception {
		emf = Persistence.createEntityManagerFactory("eDmvBO");
		em = emf.createEntityManager();
		
	    dao = new JPAPersonDAO();
	    ((JPAPersonDAO)dao).setEntityManager(em);
		
		cleanup();
		
		em.getTransaction().begin();
	}

	protected void tearDown() throws Exception {
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
		Query query = em.createQuery("select p from Person p");
		for (Person person : (List<Person>)query.getResultList()) {
			em.remove(person);
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
	public void testJPACreate() throws Exception {
		log.info("*** testJPACreate ***");
		
    	Person person = new Person();
    	person.setLastName("foobar");
    	log.debug("created person:" + person);
    	
    	dao.createPerson(person);
    	log.debug("persisted person:" + person);
    	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JPA resources.
    	
    	em.flush();
    	assertTrue(em.contains(person));
    	em.clear();
    	em.getTransaction().commit();
    	assertFalse(em.contains(person));
    	
    	Person person2 = em.find(Person.class, person.getId());
      	log.debug("checking person...");

    	assertNotNull(person2);
    	assertEquals("unexpected name", 
    	        person.getLastName(), 
    	        person2.getLastName());
    	
      	log.debug("JPA person looked good...");
	}
}
