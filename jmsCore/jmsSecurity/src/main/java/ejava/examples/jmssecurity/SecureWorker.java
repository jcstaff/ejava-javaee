package ejava.examples.jmssecurity;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.jmsscheduler.Worker;

/**
 * This class is used to make sure all security logins are complete for
 * the Worker demo.
 *
 * @author jcstaff
 */
public class SecureWorker extends Worker implements Runnable, CallbackHandler {
    private static final Log log = LogFactory.getLog(SecureWorker.class);
    private String loginConfig;
    protected String username;
    protected String password;
        

    public SecureWorker(String name) {
        super(name);
    }
    public void setLoginConfig(String loginConfig) {
        this.loginConfig = loginConfig;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Override createConnection to pass a username/password to authenticate
     * to provider.
     */
    protected Connection createConnection(ConnectionFactory connFactory) 
        throws Exception {
        log.debug("creating connection with account:" +
                username + "/" + password);
        return connFactory.createConnection(username, password);
    }

    public void execute() throws Exception {
        //LoginContext lc = new LoginContext(loginConfig, this);
        //lc.login();
        //log.debug("JAAS login complete with " + username + "/" + password);
        super.execute();
        //lc.logout();
    }
    
    public void run() {
        try {
            execute();
        }
        catch (Exception ex) {
            log.fatal("error running " + name, ex);
        }
    }    
    
    public void handle(Callback[] callbacks) 
        throws UnsupportedCallbackException {

        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback) {
                ((NameCallback)cb).setName(username);                
            }
            else if (cb instanceof PasswordCallback) {
                char passwd[] = new char[password.length()];
                password.getChars(0,passwd.length, passwd,0);
                ((PasswordCallback)cb).setPassword(passwd);
            }
            else {
                throw new UnsupportedCallbackException(cb);
            }
        }
    }


    public static void main(String args[]) {
        try {
            System.out.print("args:");
            for (String s: args) {
                System.out.print(s + " ");
            }
            System.out.println();

            String connFactoryJNDI=null;
            String destinationJNDI=null;
            String dlqJNDI=null;
            String name="";
            Integer maxCount=null;
            String username=null;
            String password=null;
            String loginConfig=null;
            for (int i=0; i<args.length; i++) {
                if ("-jndi.name.connFactory".equals(args[i])) {
                    connFactoryJNDI = args[++i];
                }
                else if ("-jndi.name.destination".equals(args[i])) {
                    destinationJNDI=args[++i];
                }
                else if ("-jndi.name.DLQ".equals(args[i])) {
                    dlqJNDI=args[++i];
                }
                else if ("-name".equals(args[i])) {
                    name=args[++i];
                }
                else if ("-max".equals(args[i])) {
                    maxCount=new Integer(args[++i]);
                }
                else if ("-username".equals(args[i])) {
                    username=args[++i];
                }
                else if ("-password".equals(args[i])) {
                    password=args[++i];
                }
                else if ("-loginConfig".equals(args[i])) {
                    loginConfig=args[++i];
                }
            }
            if (connFactoryJNDI==null) { 
                throw new Exception("jndi.name.connFactory not supplied");
            }
            else if (destinationJNDI==null) {
                throw new Exception("jndi.name.destination not supplied");
            }            
            else if (dlqJNDI==null) {
                throw new Exception("jndi.name.DLQ not supplied");
            }            
            else if (loginConfig==null) {
                throw new Exception("loginConfig not supplied");
            }            
            SecureWorker secureWorker = new SecureWorker(name);
            Context jndi = new InitialContext();
            secureWorker.setConnFactory(
                    (ConnectionFactory)jndi.lookup(connFactoryJNDI));
            secureWorker.setDestination((Destination)jndi.lookup(destinationJNDI));
            secureWorker.setDLQ((Destination)jndi.lookup(dlqJNDI));
            if (maxCount!=null) {
                secureWorker.setMaxCount(maxCount);
            }
            secureWorker.setLoginConfig(loginConfig);
            if (username!=null) {
                secureWorker.setUsername(username);
            }
            if (password!=null) {
                secureWorker.setPassword(password);
            }

            secureWorker.execute();
        }
        catch (Exception ex) {
            log.fatal("",ex);
            System.exit(-1);            
        }
    }


}
