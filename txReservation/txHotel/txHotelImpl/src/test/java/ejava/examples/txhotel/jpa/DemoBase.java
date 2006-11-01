package ejava.examples.txhotel.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.blimpl.HotelReservationImpl;
import ejava.examples.txhotel.blimpl.HotelReservationSessionImpl;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.dao.ReservationDAO;
import ejava.examples.txhotel.jpa.JPAReservationDAO;
import ejava.examples.txhotel.jpa.JPAUtil;


import junit.framework.TestCase;

public abstract class DemoBase extends TestCase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "txhotel";
    protected HotelReservationist reservationist;
    protected HotelReservationSession reservationSession;
    protected ReservationDAO reservationDAO = null;
    protected EntityManager em;

    protected void setUp() throws Exception {
        em = JPAUtil.getEntityManager(PERSISTENCE_UNIT);
        ReservationDAO dao = new JPAReservationDAO();
        reservationist = new HotelReservationImpl();
        ((HotelReservationImpl)reservationist).setDao(dao);
        reservationSession = new HotelReservationSessionImpl();
        ((HotelReservationSessionImpl)reservationSession)
            .setReservationist(reservationist);
        cleanup();
        em.getTransaction().begin();
    }

    protected void tearDown() throws Exception {
        EntityTransaction tx = JPAUtil.getEntityManager().getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        JPAUtil.closeEntityManager();
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        List<Reservation> reservations = 
            em.createQuery("select r from Reservation r").getResultList();
        for(Reservation r: reservations) {
            r.setPerson(null);
            em.remove(r);
        }
        List<Person> people = 
            em.createQuery("select p from Person p").getResultList();
        for(Person p: people) {
            em.remove(p);
        }
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //EntityManager em = JPAUtil.getEntityManager();
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
