package ejava.projects.eleague.dao;

public class ClubDAOException extends Error {
    public ClubDAOException(String message) { 
        super(message); 
    }
	public ClubDAOException(String message, Throwable th) { 
		super(message, th); 
    }
}
