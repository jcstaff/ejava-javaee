package ejava.examples.txagent.jpa;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.jpa.DemoBase;
import ejava.examples.txhotel.bo.Person;

public class AgentDemo extends DemoBase {
    
    public void testCreateBooking() throws Exception {
        log.info("*** testCreateBooking ***");
        agentSession.createBooking();
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<10; i++) {
            agentSession.addReservation(person, start.getTime(), end.getTime());
        }
        
        
        List<Booking> bookings = agent.getBookings(0, 100);
        assertEquals(0,bookings.size());

        Booking booking = agentSession.commit();
        
        bookings = agent.getBookings(0, 100);
        assertEquals(1,bookings.size());
        assertEquals(10, booking.getHotelConfirmations());
        assertEquals(10, booking.getHotelReservations());        
    }
    /*
    public void testCreateBadReservations() throws Exception {
        log.info("*** testCreateBadReservations ***");
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<9; i++) {
            reservationSession.createReservation(
                    person, start.getTime(), end.getTime());
        }
        //now create a bad one
        reservationSession.createReservation(
                person, end.getTime(), start.getTime());
        
        List<Reservation> mine = 
            reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(0,mine.size());
        
        try {
            reservationSession.commit();
            fail("invalid reservation wasn't detected");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
            em.flush(); //this won't do anything since we are rolling back
            em.getTransaction().rollback(); //un-write any updates
            em.getTransaction().begin(); //now go check for updates
        }
        
        mine = reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(0,mine.size());
    } 
    */     

}
