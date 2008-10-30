package ejava.examples.txagent.jpa;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txagent.bl.AgentReservationSession;
import ejava.examples.txagent.bl.BookingAgent;
import ejava.examples.txagent.blimpl.AgentImpl;
import ejava.examples.txagent.blimpl.AgentSessionImpl;
import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.BookingDAO;
import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.ejb.HotelRegistrationRemote;
import ejava.examples.txhotel.ejb.HotelReservationSessionRemote;


import junit.framework.TestCase;

public abstract class DemoBase extends TestCase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "txagent";
    protected HotelReservationist reservationist;
    protected HotelReservationSession reservationSession;
    protected BookingDAO bookingDAO;
    protected BookingAgent agent;
    protected AgentReservationSession agentSession;
    protected EntityManager em;
    protected String reservationistName = 
        System.getProperty("jndi.name.hotel");
    protected String reservationistSessionName = 
        System.getProperty("jndi.name.hotelsession");
    protected String hotelJndiUrl = 
        System.getProperty("hotel.jndi.url");
    protected String hotelJndiFactory =
        System.getProperty("hotel.jndi.factory");
    protected String hotelJndiPkgs =
        System.getProperty("hotel.jndi.pkgs");

    protected void setUp() throws Exception {
        EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        em = emf.createEntityManager();
        bookingDAO = new JPABookingDAO();
        ((JPABookingDAO)bookingDAO).setEntityManager(em);
        
        //initialize JNDI tree to txHotel
        Properties jndiProps = new Properties();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, hotelJndiFactory);
        jndiProps.put(Context.PROVIDER_URL, hotelJndiUrl);
        jndiProps.put(Context.URL_PKG_PREFIXES, hotelJndiPkgs);
        log.debug("jndi props=" + jndiProps);
        InitialContext jndi = new InitialContext(jndiProps);
        log.debug("jndi=" + jndi.getEnvironment());
        
        //setup the stateless hotel reservation impl
        log.debug("looking up:" + reservationistName);
        reservationist = 
            (HotelRegistrationRemote)
            jndi.lookup(reservationistName);
        
        //setup the stateful hotel reservation impl 
        log.debug("looking up:" + reservationistSessionName);
        reservationSession = 
            (HotelReservationSessionRemote)
            jndi.lookup(reservationistSessionName);
        
        log.debug("jndi look ups complete");
        
        //setup the stateless agent impl
        agent = new AgentImpl();
        ((AgentImpl)agent).setBookingDAO(bookingDAO);
        ((AgentImpl)agent).setReservationist(reservationist);
        
        //setup the stateful agent impl
        agentSession = new AgentSessionImpl();
        ((AgentSessionImpl)agentSession).setBookingDAO(bookingDAO);
        ((AgentSessionImpl)agentSession).setReservationist(reservationSession);
                
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
        em.createNativeQuery("delete from TXHOTEL_RESERVATION")
          .executeUpdate();
        em.createNativeQuery("delete from TXHOTEL_PERSON")
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
