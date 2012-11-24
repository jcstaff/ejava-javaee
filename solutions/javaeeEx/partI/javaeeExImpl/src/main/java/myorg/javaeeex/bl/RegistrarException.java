package myorg.javaeeex.bl;

public class RegistrarException extends Exception {
    private static final long serialVersionUID = -7034018600981925939L;
    public RegistrarException() {}
    public RegistrarException(Throwable ex) { super(ex); }
    public RegistrarException(String msg, Throwable ex) { super(msg, ex); }
    public RegistrarException(String msg) { super(msg); }
}
