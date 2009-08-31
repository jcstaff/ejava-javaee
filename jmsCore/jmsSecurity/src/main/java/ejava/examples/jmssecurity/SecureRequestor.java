package ejava.examples.jmssecurity;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.Session;
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

import ejava.examples.jmsscheduler.Requestor;

/**
 * This class is used to perform a login before handing control to the 
 * Requestor implementation. This allows the Requestor to be used in an
 * environment where a valid username/password is required to access a 
 * JMS destination. 
 *
 * @author jcstaff
 */
public class SecureRequestor extends Requestor 
    implements Runnable, MessageListener, CallbackHandler {
    private static final Log log = LogFactory.getLog(SecureRequestor.class);
    
    private String loginConfig;
    protected String username;
    protected String password;
    protected Destination replyTo;


    public SecureRequestor(String name) {
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
    public void setReplyTo(Destination replyTo) {
        this.replyTo = replyTo;
    }

    public void execute() throws Exception {
        //LoginContext lc = new LoginContext(loginConfig, this);
        //lc.login();
        //log.info("login complete: user=" + username + 
        //        ", password=" + new String(password));
        super.execute();
        //lc.logout();
    }
    
    /**
     * override the default, no-args, createConnection to supply username/
     * password authentication directly to JMS provider.
     */
    protected Connection createConnection(ConnectionFactory connFactory) 
        throws Exception {
        log.debug("creating JMS connection with account=" + username + 
                "/" + password);
        return connFactory.createConnection(username, password); 
    }
    
    /**
     * override the temporaryQueue implementation in the base for a queue
     * that can have its security configuration fully defined.
     */
    protected Destination getReplyTo(Session session) throws Exception {
        log.debug("using responseQueue=" + replyTo); 
        return replyTo;
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
                log.info("setting password=" + new String(passwd));
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
            String requestQueueJNDI=null;
            String responseQueueJNDI=null;
            String name="";
            Long sleepTime=null;
            Integer maxCount=null;
            String username=null;
            String password=null;
            String loginConfig=null;
             for (int i=0; i<args.length; i++) {
                if ("-jndi.name.connFactory".equals(args[i])) {
                    connFactoryJNDI = args[++i];
                }
                else if ("-jndi.name.requestQueue".equals(args[i])) {
                    requestQueueJNDI=args[++i];
                }
                else if ("-jndi.name.responseQueue".equals(args[i])) {
                    responseQueueJNDI=args[++i];
                }
                else if ("-name".equals(args[i])) {
                    name=args[++i];
                }
                else if ("-sleep".equals(args[i])) {
                    sleepTime=new Long(args[++i]);
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
            else if (requestQueueJNDI==null) {
                throw new Exception("jndi.name.requestQueue not supplied");
            }            
            else if (responseQueueJNDI==null) {
                throw new Exception("jndi.name.responseQueue not supplied");
            }            
            else if (loginConfig==null) {
                throw new Exception("loginConfig not supplied");
            }            
            SecureRequestor secureRequestor = new SecureRequestor(name);
            Context jndi = new InitialContext();
            secureRequestor.setConnFactory(
                    (ConnectionFactory)jndi.lookup(connFactoryJNDI));
            secureRequestor.setRequestQueue(
                    (Destination)jndi.lookup(requestQueueJNDI));
            secureRequestor.setReplyTo(
                    (Destination)jndi.lookup(responseQueueJNDI));
            if (maxCount!=null) {
                secureRequestor.setMaxCount(maxCount);
            }
            if (sleepTime!=null) {
                secureRequestor.setSleepTime(sleepTime);
            }
            secureRequestor.setLoginConfig(loginConfig);
            if (username!=null) {
                secureRequestor.setUsername(username);
            }
            if (password!=null) {
                secureRequestor.setPassword(password);
            }
            secureRequestor.execute();
        }
        catch (Exception ex) {
            log.fatal("",ex);
            System.exit(-1);            
        }
    }
}
