package ejava.examples.txagent.jpa;

import static org.junit.Assert.assertEquals;


import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import ejava.examples.txagent.bl.AgentReservationException;
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
import ejava.examples.txhotel.ejb.TestUtilRemote;
import ejava.util.ejb.EJBClient;

public abstract class DemoBase {
    protected static final Log log = LogFactory.getLog(DemoBase.class);
    private static final String PERSISTENCE_UNIT = "txagent-test";
    protected HotelReservationist reservationist;
    protected HotelReservationSession reservationSession;
    protected BookingDAO bookingDAO;
    protected BookingAgent agent;
    protected AgentReservationSession agentSession;
    protected EntityManager em;
    
    protected String reservationistName = 
        System.getProperty("jndi.name.hotel", 
        	EJBClient.getRemoteLookupName("txHotelEAR", "txHotelEJB", 
        		"HotelRegistrationEJB", HotelRegistrationRemote.class.getName()));    
    protected String reservationistSessionName = 
        System.getProperty("jndi.name.hotelsession", 
			EJBClient.getRemoteLookupName("txHotelEAR", "txHotelEJB", 
	        	"HotelReservationSessionEJB", HotelReservationSessionRemote.class.getName()));
    

    @BeforeClass
    public static void waitForServerDeploy() throws InterruptedException {
    	/*
    	 * this wait seems periodically necessary when using the cargo-startstop
    	 * profile rather than the cargo-deploy profile to an already 
    	 * running server. 
    	 */
    	if (Boolean.parseBoolean(System.getProperty("cargo.startstop", "false"))) {
    		long waitTime=10000;
	    	log.info(String.format("pausing %d secs for server deployment to complete", waitTime/1000));
	    	Thread.sleep(10000);
    	}
    }
    
    @Before
    public void setUp() throws Exception {
        EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        em = emf.createEntityManager();
        bookingDAO = new JPABookingDAO();
        ((JPABookingDAO)bookingDAO).setEntityManager(em);
        
        //initialize JNDI tree to txHotel
        Properties jndiProps = new Properties();
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

    @After
    public void tearDown() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() throws NamingException, AgentReservationException {
        log.info("cleaning up database");
        em.getTransaction().begin();
        List<Booking> bookings = 
            em.createQuery("select b from Booking b").getResultList();
        for(Booking b: bookings) {
            em.remove(b);
        }
        em.getTransaction().commit();
        

        assertEquals("unexpected bookings", 0, agent.getBookings(0, 100).size());
        String hotelHelperName = EJBClient.getRemoteLookupName("txHotelEAR", "txHotelEJB", 
        		"TestUtilEJB", TestUtilRemote.class.getName());
        ((TestUtilRemote)new InitialContext().lookup(hotelHelperName)).reset();
    }
    
    protected void populate() {
        log.info("populating database");
        //EntityManager em = JPAUtil.getEntityManager();
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
