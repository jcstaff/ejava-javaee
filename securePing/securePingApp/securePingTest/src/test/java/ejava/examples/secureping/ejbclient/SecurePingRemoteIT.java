package ejava.examples.secureping.ejbclient;


import static org.junit.Assert.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ejava.examples.secureping.ejb.SecurePing;

public class SecurePingRemoteIT extends SecurePingTestBase {
    private static final Log log = LogFactory.getLog(SecurePingRemoteIT.class);
    String jmxUser = System.getProperty("jmx.username","admin");
    String jmxPassword = System.getProperty("jmx.password","password");
    
    Map<String,CallbackHandler> logins = new HashMap<String, CallbackHandler>();
    CallbackHandler knownLogin;
    CallbackHandler userLogin;
    CallbackHandler adminLogin;
    CallbackHandler jmxLogin;
    String skipFlush = System.getProperty("skip.flush");
    
    @Before
    public void setUp() throws Exception {
        log.debug("jndi name:" + jndiName);
        
        //create different types of logins
        knownLogin = new BasicCallbackHandler();
        ((BasicCallbackHandler)knownLogin).setName("known");
        ((BasicCallbackHandler)knownLogin).setPassword("password");
        
        userLogin = new BasicCallbackHandler();
        log.debug("using user username=" + userUser);
        ((BasicCallbackHandler)userLogin).setName(userUser);
        ((BasicCallbackHandler)userLogin).setPassword(userPassword);

        adminLogin = new BasicCallbackHandler();
        log.debug("using admin username=" + adminUser);
        ((BasicCallbackHandler)adminLogin).setName(adminUser);
        ((BasicCallbackHandler)adminLogin).setPassword(adminPassword);
        
        jmxLogin = new BasicCallbackHandler();
        log.debug("using jmx username=" + jmxUser);
        ((BasicCallbackHandler)jmxLogin).setName(jmxUser);
        ((BasicCallbackHandler)jmxLogin).setPassword(jmxPassword);

        //account for how maven and Ant will expand string
        /*
        if (skipFlush == null || 
            "${skip.flush}".equals(skipFlush) ||
            "false".equalsIgnoreCase(skipFlush)) {
        	LoginContext lc = new LoginContext("securePingTest", jmxLogin);
        	lc.login();
            //new ResetAuthenticationCache().execute();
            lc.logout();
        }
            */
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private Context runAs(String username, String password) throws NamingException {
        Hashtable env = new Hashtable();
        if (username != null) {
	        env.put(Context.SECURITY_PRINCIPAL, username);
	        env.put(Context.SECURITY_CREDENTIALS, password);
        }
        log.debug(String.format("%s env=%s", username, env));
        return new InitialContext(env);
    }
    
    @Test @Ignore
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

    /*
    @Test
    public void testIsCallerInRole() throws Exception {
        log.info("*** testIsCallerInRole ***");
        
        try {        	
	        assertFalse("anonomous in admin role",
	                     securePing.isCallerInRole("admin"));
	        assertFalse("anonomous in user role",
	                securePing.isCallerInRole("user"));
	        assertFalse("anonomous in internalRole role",
	                securePing.isCallerInRole("internalRole"));
        }
        catch (Exception ex) {
            log.info("anonymous calls to isCallerinRole failed:"+ex);
        }

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
	*/
    
    @Test @Ignore
    public void testPingAll() throws Exception {
    	SecurePing ejb=null;
    	
        log.info("*** testPingAll ***");
        Context jndi=null;
        try {
        	jndi=runAs(null,  null);
        	ejb = (SecurePing)jndi.lookup(jndiName); 
            log.info(ejb.pingAll());
            fail("anonymous user not detected"); 
        }
        catch (NamingException ex) {
            log.info("expected error calling pingAll:" + ex);
        }
        catch (Exception ex) {
            log.error("unexpected exception for anonymous", ex); 
            fail("unexpected exception for anonymous:" + ex); 
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
            LoginContext lc = new LoginContext("securePingTest", knownLogin);
            lc.login();
            
        	//jndi = runAs("known", "password");
        	jndi = runAs(null, null);
        	ejb = (SecurePing)jndi.lookup(jndiName);
            String result = ejb.pingAll();
            log.info(result);
            assertEquals("unexpected result for known",
        		"called pingAll, principal=known, isUser=false, isAdmin=false, isInternalRole=false",
        		result);
            lc.logout();
        }
        catch (Exception ex) {
            log.info("error calling pingAll for known", ex);
            fail("error calling pingAll for known:" +ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
        
        try {
            LoginContext lc = new LoginContext("securePingTest", userLogin);
            lc.login();
        	jndi = runAs(userUser, userPassword);
        	ejb = (SecurePing)jndi.lookup(jndiName);
            String result = ejb.pingAll();
            log.info(result);
            assertEquals("unexpected result for admin",
        		String.format("called pingAll, principal=%s, isUser=true, isAdmin=false, isInternalRole=false", userUser),
        		result);
            lc.logout();
        }
        catch (Exception ex) {
            log.info("error calling pingAll for user", ex);
            fail("error calling pingAll for user:" +ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
//            LoginContext lc = new LoginContext("securePingTest", adminLogin);
//            lc.login();
        	jndi = runAs(adminUser, adminPassword);
        	ejb = (SecurePing)jndi.lookup(jndiName);
            String result=ejb.pingAll();
            log.info(result);
//            lc.logout();
            assertEquals("unexpected result for admin",
        		String.format("called pingAll, principal=%s, isUser=true, isAdmin=true, isInternalRole=true", adminUser),
        		result);
        }
        catch (Exception ex) {
            log.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

    }
    
    /*
    
    @Test
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

    @Test
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

    @Test
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
    */     
}
