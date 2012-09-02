package ejava.examples.txhotel.ejbclient;

import static org.junit.Assert.*;

import java.util.Calendar;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bl.InvalidParameterException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.ejb.HotelRegistrationRemote;
import ejava.examples.txhotel.ejb.TestUtilRemote;
import ejava.util.ejb.EJBClient;

public class HotelReservationIT {
    static final Log log = LogFactory.getLog(HotelReservationIT.class);
    static String registrarJNDI = System.getProperty("jndi.name.hotel",
    	EJBClient.getEJBLookupName("txHotelEAR", "txHotelEJB", "", 
    		"HotelRegistrationEJB", HotelRegistrationRemote.class.getName()));
    static final String requiredJNDI = System.getProperty("jndi.name.hotel.required",
    	EJBClient.getEJBLookupName("txHotelEAR", "txHotelEJB", "", 
    		"RequiredEJB", HotelRegistrationRemote.class.getName()));
    static final String requiresNewJNDI = System.getProperty("jndi.name.hotel.requiresNew",
    	EJBClient.getEJBLookupName("txHotelEAR", "txHotelEJB", "", 
    		"RequiresNewEJB", HotelRegistrationRemote.class.getName()));

    static final Map<String,HotelReservationist> reservationists = 
            new HashMap<String, HotelReservationist>();

    
    @BeforeClass
    public static void setUpClass() throws Exception {
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
        log.debug("jndi name:" + registrarJNDI);
        reservationists.putAll(getReservationists());
    }
    
    public static Map<String,HotelReservationist> getReservationists() 
    		throws NamingException {
        Context jndi = new InitialContext();
        try {
	        Map<String,HotelReservationist> reservationists = 
	                new HashMap<String, HotelReservationist>();
	        reservationists.put("registrar", (HotelReservationist)jndi.lookup(registrarJNDI));
	        reservationists.put("required", (HotelReservationist)jndi.lookup(requiredJNDI));
	        reservationists.put("requiresNew", (HotelReservationist)jndi.lookup(requiresNewJNDI));
	        return reservationists;
        }
        finally {
            if (jndi != null) { jndi.close(); }
        }
    }
    
    @Before() 
    public void setUp() throws NamingException {
        String hotelHelperName = EJBClient.getEJBLookupName("txHotelEAR", "txHotelEJB", "", 
        		"TestUtilEJB", TestUtilRemote.class.getName());
        ((TestUtilRemote)new InitialContext().lookup(hotelHelperName)).reset();
    }

    @Test
    public void testCreateReservation() throws Exception {
        for(String key: reservationists.keySet()) {
            HotelReservationist reservationist = reservationists.get(key);
            doTestCreateReservation(key, reservationist);
        }
        reservationists.clear();
    }        
    public void doTestCreateReservation(
            String name, 
            HotelReservationist reservationist) throws Exception {
        log.info("*** testCreateReservation:" + name + " ***");
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);

        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        
        Reservation reservation = reservationist.createReservation(
                person, start.getTime(), end.getTime());
        log.debug("created reservation:" + reservation);
        assertNotNull("unexpected confirmation id", 
                reservation.getConfirmation());
    }
    
    @Test
    public void testGetReservation() throws Exception {
        for(String key: reservationists.keySet()) {
            HotelReservationist reservationist = reservationists.get(key);
            doTestGetReservation(key, reservationist);
        }
        reservationists.clear();
    }        
    public void doTestGetReservation(
            String name, 
            HotelReservationist reservationist) throws Exception {
        log.info("*** testGetReservation:" + name + " ***");
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);

        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        
        Reservation reservation = reservationist.createReservation(
                person, start.getTime(), end.getTime());
        log.debug("created reservation:" + reservation);
        assertNotNull("unexpected confirmation id", 
                reservation.getConfirmation());
        
        Reservation reservation2 = 
            reservationist.getReservationByConfirmation(
                    reservation.getConfirmation());
        assertNotNull("reservation wasn't found", reservation2);
    }

    @Test
    public void testBadCreateReservation() throws Exception {
        for(String key: reservationists.keySet()) {
            HotelReservationist reservationist = reservationists.get(key);
            doTestBadCreateReservation(key, reservationist);
        }
        reservationists.clear();
    }        
    public void doTestBadCreateReservation(
            String name, 
            HotelReservationist reservationist) throws Exception {
        log.info("*** testBadCreateReservation:" + name + " ***");
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);

        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, -2);
        
        @SuppressWarnings("unused")
        Reservation reservation = null;
        try {
            reservation = reservationist.createReservation(
                person, null, end.getTime());
            fail("didn't catch null start date");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }

        try {
            reservation = reservationist.createReservation(
                person, start.getTime() , null);
            fail("didn't catch null end date");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }

        try {
            reservation = reservationist.createReservation(
                person, start.getTime() , end.getTime());
            fail("didn't catch start date before end date");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }
    }
}
