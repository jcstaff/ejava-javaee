package ejava.examples.txagent.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.BookingDAO;
import ejava.examples.txagent.jpa.JPABookingDAO;

import junit.framework.TestCase;

public abstract class DemoBase extends TestCase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "txagent";
    protected BookingDAO bookingDAO = null;
    protected EntityManager em;

    protected void setUp() throws Exception {
        EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        em = emf.createEntityManager();
        bookingDAO = new JPABookingDAO();
        ((JPABookingDAO)bookingDAO).setEntityManager(em);
        cleanup();
        em.getTransaction().begin();
    }

    protected void tearDown() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        em.getTransaction().begin();
        List<Booking> bookings = 
            em.createQuery("select b from Booking b").getResultList();
        for(Booking b: bookings) {
            em.remove(b);
        }
        
        em.createNativeQuery("delete from TXHOTEL_RESERVATION_PERSON")
          .executeUpdate();
        em.createNativeQuery("delete from TXHOTEL_PERSON")
        .executeUpdate();
        em.createNativeQuery("delete from TXHOTEL_RESERVATION")
        .executeUpdate();        
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //EntityManager em = JPAUtil.getEntityManager();
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
