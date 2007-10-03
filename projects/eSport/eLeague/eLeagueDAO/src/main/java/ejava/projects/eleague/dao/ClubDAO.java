package ejava.projects.eleague.dao;

import java.util.List;

import ejava.projects.eleague.bo.Venue;

public interface ClubDAO {
	void createVenue(Venue venue) 
		throws ClubDAOException;
	List<Venue> getVenues(int index, int count)
	    throws ClubDAOException;
}
