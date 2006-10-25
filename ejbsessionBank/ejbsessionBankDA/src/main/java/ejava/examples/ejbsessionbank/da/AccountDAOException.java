package ejava.examples.ejbsessionbank.da;

public class AccountDAOException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public AccountDAOException() {}
    public AccountDAOException(String msg) { super(msg); }
    public AccountDAOException(Throwable ex) { super(ex); }
    public AccountDAOException(String msg, Throwable ex) { super(msg, ex); }    
}
