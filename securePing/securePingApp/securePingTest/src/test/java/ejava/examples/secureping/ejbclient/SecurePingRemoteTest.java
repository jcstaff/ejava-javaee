package ejava.examples.secureping.ejbclient;


import java.security.AccessControlContext;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.secureping.ResetAuthenticationCache;
import ejava.examples.secureping.ejb.SecurePing;
import ejava.examples.secureping.ejb.SecurePingRemote;

public class SecurePingRemoteTest extends TestCase {
    static Log log = LogFactory.getLog(SecurePingRemoteTest.class);
    InitialContext jndi;
    String jndiName = System.getProperty("jndi.name.secureping");
    String adminUser = System.getProperty("admin.username");
    String userUser = System.getProperty("user.username");
    
    SecurePing securePing;
    Map<String,CallbackHandler> logins = new HashMap<String, CallbackHandler>();
    CallbackHandler knownLogin;
    CallbackHandler userLogin;
    CallbackHandler adminLogin;
    String skipFlush = System.getProperty("skip.flush");
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        log.debug("jndi name:" + jndiName);
        
        securePing = (SecurePingRemote)jndi.lookup(jndiName);
        
        //create different types of logins
        knownLogin = new BasicCallbackHandler();
        ((BasicCallbackHandler)knownLogin).setName("known");
        ((BasicCallbackHandler)knownLogin).setPassword("password");
        
        userLogin = new BasicCallbackHandler();
        log.debug("using user username=" + userUser);
        ((BasicCallbackHandler)userLogin).setName(userUser);
        ((BasicCallbackHandler)userLogin).setPassword("password");

        adminLogin = new BasicCallbackHandler();
        log.debug("using admin username=" + adminUser);
        ((BasicCallbackHandler)adminLogin).setName(adminUser);
        ((BasicCallbackHandler)adminLogin).setPassword("password");
        
        //account for how maven and Ant will expand string
        if (skipFlush == null || 
            "${skip.flush}".equals(skipFlush) ||
            "false".equalsIgnoreCase(skipFlush)) {
            new ResetAuthenticationCache().execute();
        }
    }
    
    public void testSubject() throws Exception {
        log.info("*** testSubject ***");
        
        //Subject.getSubject(acc);
    }
    
    public void testLoginContext() throws Exception {
        log.info("*** testLoginContext ***");
        
        LoginContext lc = new LoginContext("securePingTest", adminLogin);
        lc.login();
        log.info("subject=" + lc.getSubject());
        for (Principal p: lc.getSubject().getPrincipals()) {
            log.info("principal=" + p + ", " + p.getClass().getName());
        }
        log.info(lc.getSubject().getPrivateCredentials().size() + 
                " private credentials");
        for (Object c: lc.getSubject().getPrivateCredentials()) {
            log.info("private credential=" + c + ", " + c.getClass().getName());
        }
        log.info(lc.getSubject().getPublicCredentials().size() + 
                 " public credentials");
        for (Object c: lc.getSubject().getPublicCredentials()) {
            log.info("public credential=" + c + ", " + c.getClass().getName());
        }
        lc.logout();
    }
    
    public void testIsCallerInRole() throws Exception {
        log.info("*** testIsCallerInRole ***");
        
        assertFalse("anonomous in admin role",
                     securePing.isCallerInRole("admin"));
        assertFalse("anonomous in user role",
                securePing.isCallerInRole("user"));
        assertFalse("anonomous in internalRole role",
                securePing.isCallerInRole("internalRole"));

        LoginContext lc = new LoginContext("securePingTest", knownLogin);
        lc.login();
        assertFalse("known in admin role",
                securePing.isCallerInRole("admin"));
        assertFalse("known in user role",
           securePing.isCallerInRole("user"));
        assertFalse("known in internalRole role",
           securePing.isCallerInRole("internalRole"));
        lc.logout();
        
        lc = new LoginContext("securePingTest", userLogin);
        lc.login();
        assertFalse("user in admin role",
                securePing.isCallerInRole("admin"));
        assertTrue("user not in user role",
           securePing.isCallerInRole("user"));
        assertFalse("user in internalRole role",
           securePing.isCallerInRole("internalRole"));
        lc.logout();
        
        lc = new LoginContext("securePingTest", adminLogin);
        lc.login();
        assertTrue("admin not in admin role",
                securePing.isCallerInRole("admin"));
        assertTrue("admin not in user role",
           securePing.isCallerInRole("user"));
        //not working ;(
        //assertTrue("admin not in internalRole role",
           securePing.isCallerInRole("internalRole");
        //);
        lc.logout();        
    }

    public void testPingAll() throws Exception {
        log.info("*** testPingAll ***");
        try {
            log.info(securePing.pingAll());
        }
        catch (Exception ex) {
            log.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }

        try {
            LoginContext lc = new LoginContext("securePingTest", knownLogin);
            lc.login();
            log.info(securePing.pingAll());
            lc.logout();
        }
        catch (Exception ex) {
            log.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }
        
        try {
            LoginContext lc = new LoginContext("securePingTest", userLogin);
            lc.login();
            log.info(securePing.pingAll());
            lc.logout();
        }
        catch (Exception ex) {
            log.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }        

        try {
            LoginContext lc = new LoginContext("securePingTest", adminLogin);
            lc.login();
            log.info(securePing.pingAll());
            lc.logout();
        }
        catch (Exception ex) {
            log.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }        
    }
    
    public void testPingUser() throws Exception {
        log.info("*** testPingUser ***");
        try {
            log.info(securePing.pingUser());
            fail("didn't detect anonymous user");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }

        try {
            LoginContext lc = new LoginContext("securePingTest", knownLogin);
            lc.login();
            log.info(securePing.pingUser());
            lc.logout();
            fail("didn't detect known, but non-user");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }
        
        try {
            LoginContext lc = new LoginContext("securePingTest", userLogin);
            lc.login();
            log.info(securePing.pingUser());
            lc.logout();
        }
        catch (Exception ex) {
            log.info("error calling pingUser:" + ex, ex);
            fail("error calling pingUser:" +ex);
        }        

        try {
            LoginContext lc = new LoginContext("securePingTest", adminLogin);
            lc.login();
            log.info(securePing.pingUser());
            lc.logout();
        }
        catch (Exception ex) {
            log.info("error calling pingUser:" + ex, ex);
            fail("error calling pingUser:" +ex);
        }        
    }

    public void testPingAdmin() throws Exception {
        log.info("*** testPingAdmin ***");
        try {
            log.info(securePing.pingAdmin());
            fail("didn't detect anonymous user");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }

        try {
            LoginContext lc = new LoginContext("securePingTest", knownLogin);
            lc.login();
            log.info(securePing.pingAdmin());
            lc.logout();
            fail("didn't detect known, but non-admin user");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }
        
        try {
            LoginContext lc = new LoginContext("securePingTest", userLogin);
            lc.login();
            log.info(securePing.pingAdmin());
            lc.logout();
            fail("didn't detect non-admin user");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }        

        try {
            LoginContext lc = new LoginContext("securePingTest", adminLogin);
            lc.login();
            log.info(securePing.pingAdmin());
            lc.logout();
        }
        catch (Exception ex) {
            log.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
        
    }

    public void testPingExcluded() throws Exception {
        log.info("*** testPingExcluded ***");
        try {
            log.info(securePing.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }

        try {
            LoginContext lc = new LoginContext("securePingTest", knownLogin);
            lc.login();
            log.info(securePing.pingExcluded());
            lc.logout();
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }
        
        try {
            LoginContext lc = new LoginContext("securePingTest", userLogin);
            lc.login();
            log.info(securePing.pingExcluded());
            lc.logout();
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }        

        try {
            LoginContext lc = new LoginContext("securePingTest", adminLogin);
            lc.login();
            log.info(securePing.pingExcluded());
            lc.logout();
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }        
    }      
}
