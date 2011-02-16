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
import ejava.projects.edmv.bo.VehicleRegistration;
import ejava.projects.edmv.dao.PersonDAO;
import ejava.projects.edmv.dao.VehicleDAO;
import ejava.projects.edmv.jpa.JPAPersonDAO;
import ejava.projects.edmv.jpa.JPAVehicleDAO;

import junit.framework.TestCase;

/**
 * This test case provides an example of one might test the JPA DAO. It 
 * tests only a minor set of functionality. However, this can be used
 * as a starting point for more detailed tests. 
 * 
 * @author jcstaff
 *
 */
public class JPAVehicleDAOTest extends TestCase {
	private static Log log = LogFactory.getLog(JPAVehicleDAOTest.class);
	//this code assumes all the JDBC properties were placed in 
	//META-INF/persistence.xml when the file was copied from src to the 
	//target tree
	
	private EntityManagerFactory emf;
	private EntityManager em;
	private VehicleDAO vehicleDAO;
	private PersonDAO personDAO;
	
	public void setUp() throws Exception {
		emf = Persistence.createEntityManagerFactory("eDmvBO-test");
		em = emf.createEntityManager();
		
	    vehicleDAO = new JPAVehicleDAO();
	    ((JPAVehicleDAO)vehicleDAO).setEntityManager(em);
	    personDAO = new JPAPersonDAO();
	    ((JPAPersonDAO)personDAO).setEntityManager(em);
		
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
		Query query = em.createQuery("select vr from VehicleRegistration vr");
		for (VehicleRegistration reg : 
		    (List<VehicleRegistration>)query.getResultList()) {
		    reg.getOwners().clear(); //remove entries from m-m link table
			em.remove(reg);          //remove row from vehicle table
		}
        query = em.createQuery("select p from Person p");
        for (Person p : 
            (List<Person>)query.getResultList()) {
            em.remove(p);            //remove row from person table
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
		
        Person owner1 = new Person();
        owner1.setLastName("Brown");
        personDAO.createPerson(owner1);
        log.debug("created owner1:" + owner1);
        
        Person owner2 = new Person();
        owner2.setLastName("Harper");
        personDAO.createPerson(owner2);
        log.debug("created owner2:" + owner2);

        VehicleRegistration registration = new VehicleRegistration();
        registration.setVin("456");
        registration.getOwners().add(owner1);
        registration.getOwners().add(owner2);
        vehicleDAO.createRegistration(registration);
        log.debug("created registration:" + registration);
            	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JPA resources.
    	
    	em.flush();
    	assertTrue(em.contains(registration));
    	em.clear();
    	em.getTransaction().commit();
    	assertFalse(em.contains(registration));
    	
    	VehicleRegistration registration2 = 
    	    em.find(VehicleRegistration.class, registration.getId());
      	log.debug("checking registration...");

    	assertNotNull(registration2);
    	assertEquals("unexpected vin", 
    	        registration.getVin(), 
    	        registration2.getVin());
    	assertEquals("unexpected owners", 
    	        2, registration2.getOwners().size());
    	for (Person owner : registration.getOwners()) {
    	    assertTrue("unexpected owner name:" + owner.getLastName(), 
    	            owner1.getLastName().equals(owner.getLastName()) ||
    	            owner2.getLastName().equals(owner.getLastName()));
    	}
    	
      	log.debug("JPA registration and owners looked good...");
	}
}
