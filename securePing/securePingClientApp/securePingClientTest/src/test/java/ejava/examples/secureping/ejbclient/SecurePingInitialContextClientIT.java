package ejava.examples.secureping.ejbclient;


import static org.junit.Assert.*;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ejava.examples.secureping.ejb.SecurePingClient;
import ejava.examples.secureping.ejb.SecurePingClientRemote;
import ejava.util.ejb.EJBClient;

/**
 * This class peforms a check of the SecurePingClientEJB using RMI Calls.
 * The SecurePingClientEJB lets anyone in and then performs the same
 * action on the SecurePingEJB using a run-as admin1 identity and admin role.
 * 
 * This specific class uses the JNDI InitialContext class to authenticate the
 * current user with the server.
 */
public class SecurePingInitialContextClientIT {
    static final Log log = LogFactory.getLog(SecurePingInitialContextClientIT.class);
    private InitialContext jndi;
    
    //jndi name for SecurePingEJB
    private String jndiName = System.getProperty("jndi.name.secureping",
    	EJBClient.getRemoteLookupName("securePingClientEAR", "securePingClientEJB", 
			"SecurePingClientEJB", 
			ejava.examples.secureping.ejb.SecurePingClientRemote.class.getName()));
    
    //username to use for admin login
    private String adminUser = System.getProperty("admin.username", "admin1");
    
    //username to use for user login
    private String userUser = System.getProperty("user.username", "user1");

    //username for known user with no roles
	private String knownUser = System.getProperty("known.username", "known");

    
    //reference to remote interface for SecurePingEJB
    SecurePingClient ejb;
    
    @Before
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        log.debug("jndi name:" + jndiName);
        
        for (int i=0; i<5; i++) {
        	try { ejb = (SecurePingClientRemote)jndi.lookup(jndiName); }
        	catch (NamingException ignored){}
        	if (ejb != null){ break; }
        	Thread.sleep(1000);
        }
        //if the above wait did not work -- do it again and throw the reported exception
        if (ejb == null) {
        	ejb = (SecurePingClientRemote)jndi.lookup(jndiName);
        }
        jndi.close();
    }
    
    /**
     * This method will add the caller credentials to the credentials 
     * supplied in the jndi.properties file. If there are default credentials
     * already there -- these should take precendence.
     * @param username
     * @return
     * @throws NamingException
     */
    protected Context runAs(String username) throws NamingException {
    	Properties env = new Properties();
    	if (username != null) {
    		env.put(Context.SECURITY_PRINCIPAL, username);
    		env.put(Context.SECURITY_CREDENTIALS, "password");
    	}
    	Context context = new InitialContext(env);
    	return context;
    }

    /**
     * This method walks through each login and checks whether they are in 
     * expected roles. Inspect the log to see the value SecurePingClientEJB
     * thinks we are in and then what SecurePingEJB things after doing a
     * run-as.
     */
    @Test
    public void testIsCallerInRole() throws Exception {
        log.info("*** testIsCallerInRole ***");
        Context jndi=null;
        
        try {
            assertFalse("anonomous in admin role",
                 ejb.isCallerInRole("admin"));
            assertFalse("anonomous in user role",
                 ejb.isCallerInRole("user"));
            assertFalse("anonomous in internalRole role",
                 ejb.isCallerInRole("internalRole"));
        fail("failed to prevent calls as anonymous user");
        } catch (IllegalStateException ex) {
            log.info("caught expected exception for anonymous caller:" +ex);
        } catch (Exception ex) {
        	fail("unexpected exception for anonymous caller:" + ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
        
        try {
        	jndi=runAs(knownUser);
	        assertFalse("known in admin role",
                ejb.isCallerInRole("admin"));
	        assertFalse("known in user role",
	           ejb.isCallerInRole("user"));
	        assertFalse("known in internalRole role",
	           ejb.isCallerInRole("internalRole"));
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
        
        try {
        	jndi = runAs(userUser);
	        assertFalse("user in admin role",
               ejb.isCallerInRole("admin"));
	        assertTrue("user not in user role",
	           ejb.isCallerInRole("user"));
	        assertFalse("user in internalRole role",
	           ejb.isCallerInRole("internalRole"));
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi = runAs(adminUser);
	        assertTrue("admin not in admin role",
               ejb.isCallerInRole("admin"));
	        assertTrue("admin not in user role",
  	           ejb.isCallerInRole("user"));
	        //securePingClientEJB does not have this role mapped to admin
	        assertFalse("admin not in internalRole role",
	           ejb.isCallerInRole("internalRole"));
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
    }

    /**
     * This will invoke the SecurePingClientEJB using several different
     * logins. The SecurePingClientEJB will then pass the call off to 
     * SecurePingEJB. Both will report security information to the log as 
     * to what the container thought of the caller.
     */
    @Test
    public void testPingAll() throws Exception {
        log.info("*** testPingAll ***");
        Context jndi=null;
        
        try {
            log.info(ejb.pingAll());
            fail("no error detected for anonymous");
        }
        catch (IllegalStateException ex) {
            log.info("caught expected exception for anonymous:" + ex);
        }
        catch (Exception ex) {
            fail("unexpected expected exception for anonymous:" + ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi = runAs(knownUser);
        	String result=ejb.pingAll();
            log.info(result);
            String expected = String.format(
"securePingClient called pingAll, principal=%s, isUser=false, isAdmin=false, isInternalRole=false:\n"+
"securePing=called pingAll, principal=admin1, isUser=false, isAdmin=true, isInternalRole=true", knownUser);
            assertEquals("", expected, result);
        }
        catch (Exception ex) {
            log.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
        
        try {
        	jndi = runAs(userUser);
        	String result=ejb.pingAll();
            log.info(result);
            String expected = String.format(
"securePingClient called pingAll, principal=%s, isUser=true, isAdmin=false, isInternalRole=false:\n"+
"securePing=called pingAll, principal=admin1, isUser=false, isAdmin=true, isInternalRole=true", userUser);
            assertEquals("", expected, result);
        }
        catch (Exception ex) {
            log.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi = runAs(adminUser);
        	String result=ejb.pingAll();
            log.info(result);
            String expected = String.format(
"securePingClient called pingAll, principal=%s, isUser=true, isAdmin=true, isInternalRole=false:\n"+
"securePing=called pingAll, principal=admin1, isUser=false, isAdmin=true, isInternalRole=true", adminUser);
            assertEquals("", expected, result);
        }
        catch (Exception ex) {
            log.info("error calling pingAll:" + ex, ex);
            fail("error calling pingAll:" +ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
    }
    
    /**
     * run-as is only allowing a single role to be specified -- so when the 
     * caller goes to invoke securePingEJB in the admin role, the user role
     * is lost. We have to turn off this test.
     * @throws Exception
     */
    @Test @Ignore
    public void testPingUser() throws Exception {
        log.info("*** testPingUser ***");
        Context jndi = null;
        try {
            log.info(ejb.pingUser());
            fail("no error detected for anonymous");
        }
        catch (IllegalStateException ex) {
            log.info("caught expected exception for anonymous:" + ex);
        }
        catch (Exception ex) {
            fail("unexpected exception for anonymous:" +ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi = runAs(knownUser);
            log.info(ejb.pingUser());
        }
        catch (Exception ex) {
            log.info("error calling pingUser:" + ex, ex);
            fail("error calling pingUser:" +ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
        
        try {
        	jndi = runAs(userUser);
            log.info(ejb.pingUser());
        }
        catch (Exception ex) {
            log.info("error calling pingUser:" + ex, ex);
            fail("error calling pingUser:" +ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi = runAs(adminUser);
            log.info(ejb.pingUser());
        }
        catch (Exception ex) {
            log.info("error calling pingUser:" + ex, ex);
            fail("error calling pingUser:" +ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
    }

    /**
     * This will invoke the SecurePingClientEJB using several different
     * logins. The SecurePingClientEJB will then pass the call off to 
     * SecurePingEJB. Both will report security information to the log as 
     * to what the container thought of the caller.
     */
    @Test
    public void testPingAdmin() throws Exception {    	
        log.info("*** testPingAdmin ***");
        Context jndi=null;
        try {
            log.info(ejb.pingAdmin());
            fail("no error detected for anonymous");
        }
        catch (IllegalStateException ex) {
            log.info("caught expected exception for anonymous:" + ex);
        }
        catch (Exception ex) {
            fail("unexpected exception for anonymous:" + ex);
        }
        finally {
        	if (jndi!=null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi = runAs(adminUser);
            log.info(ejb.pingAdmin());
        }
        catch (Exception ex) {
            log.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
        finally {
        	if (jndi!=null) { jndi.close(); jndi=null; }
        }
        
        try {
        	jndi = runAs(knownUser);
            log.info(ejb.pingAdmin());
        }
        catch (Exception ex) {
            log.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
        finally {
        	if (jndi!=null) { jndi.close(); jndi=null; }
        }
        
        try {
        	jndi = runAs(userUser);        	
            log.info(ejb.pingAdmin());
        }
        catch (Exception ex) {
            log.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }        
        finally {
        	if (jndi!=null) { jndi.close(); jndi=null; }
        }
    }

    /**
     * This will invoke the SecurePingClientEJB using several different
     * logins. The SecurePingClientEJB will then pass the call off to 
     * SecurePingEJB. Both will report security information to the log as 
     * to what the container thought of the caller.
     */
    @Test
    public void testPingExcluded() throws Exception {
        log.info("*** testPingExcluded ***");
        Context jndi=null;
        try {
            log.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (IllegalStateException ex) {
            log.info("expected exception thrown for anonymous:" + ex);
        }
        catch (Exception ex) {
            fail("unexpected exception thrown for anonymous:" + ex);
        }
        finally {
        	if (jndi!=null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi = runAs(knownUser);
            log.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }
        finally {
        	if (jndi!=null) { jndi.close(); jndi=null; }
        }
        
        try {
        	jndi = runAs(userUser);
            log.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }        
        finally {
        	if (jndi!=null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi = runAs(adminUser);
            log.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (Exception ex) {
            log.info("expected exception thrown:" + ex);
        }        
        finally {
        	if (jndi!=null) { jndi.close(); jndi=null; }
        }
    }      
}
