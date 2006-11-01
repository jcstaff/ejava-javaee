package ejava.examples.txagent.blimpl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txagent.bl.AgentReservationException;
import ejava.examples.txagent.bl.AgentReservationSession;
import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.dao.BookingDAO;
import ejava.examples.txhotel.bl.HotelReservationException;
import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.bl.InvalidParameterException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

public class AgentSessionImpl implements AgentReservationSession {
    @SuppressWarnings("unused")
    private static final Log log = 
        LogFactory.getLog(AgentSessionImpl.class);
    private Booking booking = new Booking();
    private BookingDAO bookingDAO;
    private HotelReservationSession reservationist;
    private long counter = 0L;
    
    public void createBooking() throws AgentReservationException {
    }

    public void addReservation(Person person, Date startDate, Date endDate) 
        throws AgentReservationException {
        try {
            reservationist.createReservation(person, startDate, endDate);
        } 
        catch (InvalidParameterException ex) {
            throw new ejava.examples.txagent.bl.InvalidParameterException(
                    "hotel reported invalid parameters", ex);
        }
        catch (HotelReservationException ex2) {
            throw new AgentReservationException(
                    "error adding hotel reservation", ex2);
        }
    }

    public void cancelBooking() throws AgentReservationException {
        try {
            reservationist.cancelReservations();
        } 
        catch (HotelReservationException ex2) {
            throw new AgentReservationException(
                    "error cancelling hotel reservation", ex2);
        }
    }

    public Booking commit() throws AgentReservationException {
        try {
            log.debug("committing booking: " + booking);
            String confirmation = 
                new Long(System.currentTimeMillis()).toString() + "-" + 
                ++counter;
            booking.setConfirmation(confirmation);
            bookingDAO.createBooking(booking);
            log.debug("committing hotels for booking");
            for (Reservation r: reservationist.commit()) {
                booking.addHotelReservation(r);
            }
            log.debug("committed booking:" + booking);
            return booking;
        } 
        catch (HotelReservationException ex) {
            log.info("error commiting booking, hotel reported issue:" + ex);
            throw new AgentReservationException(
                    "error committing booking", ex);
        }
    }
    public void setBookingDAO(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }
    public void setReservationist(HotelReservationSession reservationist) {
        this.reservationist = reservationist;
    }
}
