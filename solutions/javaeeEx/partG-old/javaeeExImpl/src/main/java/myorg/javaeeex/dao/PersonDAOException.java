package myorg.javaeeex.dao;

public class PersonDAOException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public PersonDAOException() {}
    public PersonDAOException(String msg) { super(msg); }
    public PersonDAOException(Throwable ex) { super(ex); }
    public PersonDAOException(String msg, Throwable ex) { super(msg, ex); }    
}
