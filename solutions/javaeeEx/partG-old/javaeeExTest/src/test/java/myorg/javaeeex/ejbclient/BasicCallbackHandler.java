package myorg.javaeeex.ejbclient;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.log4j.Logger;

public class BasicCallbackHandler implements CallbackHandler {
    Logger log = Logger.getLogger(BasicCallbackHandler.class);
    private String name;
    private char[] password;

    public BasicCallbackHandler() {}
    public BasicCallbackHandler(String name, char[] password) {
        setName(name);
        setPassword(password);
    }
    public BasicCallbackHandler(String name, String password) {
        setName(name);
        setPassword(password);
    }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) {
        this.password = new char[password.length()];
        password.getChars(0,this.password.length, this.password,0);
    }
    public void setPassword(char[] password) {
        this.password = new char[password.length];
        System.arraycopy(password,0,this.password,0,this.password.length);
    }

    public void handle(Callback[] callbacks)
        throws UnsupportedCallbackException {

        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback) {
                log.debug("name callback:" + name);
                ((NameCallback)cb).setName(name);
            }
            else if (cb instanceof PasswordCallback) {
                log.debug("password callback:" + new String(password));
                ((PasswordCallback)cb).setPassword(password);
            }
            else {
                log.debug("unknown callback");
                throw new UnsupportedCallbackException(cb);
            }
        }
    }
}