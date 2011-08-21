package ejava.projects.eleague.dao;

public class ClubDAOException extends Error {
	private static final long serialVersionUID = 1L;
	public ClubDAOException(String message) { 
        super(message); 
    }
	public ClubDAOException(String message, Throwable th) { 
		super(message, th); 
    }
}
