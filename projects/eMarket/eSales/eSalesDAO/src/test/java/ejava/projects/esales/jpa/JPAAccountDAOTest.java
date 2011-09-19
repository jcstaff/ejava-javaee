package ejava.projects.esales.jpa;

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

import ejava.projects.esales.bo.Account;
import ejava.projects.esales.bo.Address;
import ejava.projects.esales.dao.AccountDAO;

/**
 * This test case provides an example of one might test the JPA DAO. It 
 * tests only a minor set of functionality. However, this can be used
 * as a starting point for more detailed tests. 
 * 
 * @author jcstaff
 *
 */
public class JPAAccountDAOTest {
	private static Log log = LogFactory.getLog(JPAAccountDAO.class);
	//this code assumes all the JDBC properties were placed in 
	//META-INF/persistence.xml when the file was copied from src to the 
	//target tree
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private AccountDAO dao;
	
	@Before
	public void setUp() throws Exception {
		emf = Persistence.createEntityManagerFactory("eSalesBO");
		em = emf.createEntityManager();
		
	    dao = new JPAAccountDAO();
	    ((JPAAccountDAO)dao).setEntityManager(em);
		
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
		Query query = em.createQuery("select a from Account a");
		for (Account account : (List<Account>)query.getResultList()) {
			//the Account entity declared cascade=All to the Address
			//so this should delete the address as well
			em.remove(account);
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
		
    	String userId = "foo";
    	String firstName = "bar";
    	
    	Account account = new Account(userId);
    	account.setFirstName(firstName);
    	account.getAddresses().add(new Address(0, "Shipping", "Laurel"));
    	account.getAddresses().add(new Address(0, "Billing", "Columbia"));
    	
    	log.debug("instantiated Account:" + account);
    	
    	dao.createAccount(account);
    	log.debug("dao created Account:" + account);
    	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JPA resources.
    	
    	em.flush();
    	assertTrue(em.contains(account));
    	em.clear();
    	em.getTransaction().commit();
    	assertFalse(em.contains(account));
    	
    	Account account2 = em.find(Account.class, userId);
      	log.debug("checking account...");

    	assertNotNull(account2);
    	assertEquals("unexpected first name", 
    			firstName, account2.getFirstName());
    	assertEquals("unexpected number of addresses", 
    			account.getAddresses().size(), account2.getAddresses().size());
    	
      	log.debug("account looked good, checking address info...");
    	for (Address address : account2.getAddresses()) {
    		if ("Shipping".equals(address.getName())) {
    			assertEquals("unexpected city", "Laurel", address.getCity());
    		}
    		else if ("Billing".equals(address.getName())) {
    			assertEquals("unexpected city", "Columbia", address.getCity());
    		}
    		else {
    			fail("unexpected address:" + address.getName());
    		}
    	}
      	log.debug("address info good too");
	}
}
