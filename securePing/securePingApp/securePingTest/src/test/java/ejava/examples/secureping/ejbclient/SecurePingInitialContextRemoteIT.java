package ejava.examples.secureping.ejbclient;


import static org.junit.Assert.*;
import java.util.Properties;

import javax.ejb.EJBAccessException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import ejava.examples.secureping.ejb.SecurePing;
import ejava.examples.secureping.ejb.SecurePingRemote;
import ejava.util.ejb.EJBClient;

/**
 * This class demonstrates accessing the application server and EJB using
 * a JNDI InitialContext mechanism and legacy JBoss remoting API. There is no 
 * JAAS used in this example. It assumes we have a jndi.properties file
 * in the classpath with the following contents:<b/>
 * <pre>
java.naming.factory.initial=org.jboss.naming.remote.client.InitialContextFactory
java.naming.factory.url.pkgs=
java.naming.provider.url=remote://127.0.0.1:4447
jboss.naming.client.ejb.context=true
</pre>
 */
public class SecurePingInitialContextRemoteIT {
    private static final Log log = LogFactory.getLog(SecurePingInitialContextRemoteIT.class);
    String jndiName = System.getProperty("jndi.name.secureping",
    	EJBClient.getRemoteLookupName("securePingEAR", "securePingEJB", 
   			"SecurePingEJB", SecurePingRemote.class.getName()));
    String knownUser = System.getProperty("known.username", "known");
    String knownPassword = System.getProperty("known.password", "password");
    String userUser = System.getProperty("user.username","user1");
    String userPassword = System.getProperty("user.password","password");
    String adminUser = System.getProperty("admin.username","admin1");
    String adminPassword = System.getProperty("admin.password","password");
    
    SecurePing ejb;
    
    /**
     * Sets up the proxy reference to the remote interface of the EJB.
     */
    @Before
    public void setUp() throws Exception {
    	log.info("== setUp() ==");
        log.debug("jndi name:" + jndiName);
        
        	//server is secured -- must have a known user to get InitialContext
        Context jndi = runAs(knownUser, knownPassword);
        ejb = (SecurePing)jndi.lookup(jndiName);
        
        log.debug("ejb=" + ejb);
        jndi.close(); //logout of this user
    }
    
    /**
     * Changes the security context for the connection to the server 
     * by getting a new InitialContext. Note that the caller must close()
     * the returned context for future calls to this method to change the 
     * identity of the caller.
     * @param username
     * @param password
     * @return
     * @throws NamingException
     */
	private Context runAs(String username, String password) throws NamingException {
        Properties env = new Properties();
        if (username != null) {
	        env.put(Context.SECURITY_PRINCIPAL, username);
	        env.put(Context.SECURITY_CREDENTIALS, password);
        }
        log.debug(String.format("%s env=%s", username==null?"anonymous":username, env));
        return new InitialContext(env);
    }
    
    /**
     * Tests if the server is allowing anonymous users establish an 
     * InitialContext.
     * @throws NamingException
     */
    @Test
    public void testAnonymousInitialContext() throws NamingException {
    	log.info("*** testAnonymousInitialContext ***");
    	Context jndi=null;
    	try {
    		jndi=runAs(null, null);
    		fail("did not detect anonymous InitialContext");
    	}
        catch (NamingException ex) {
            log.info("expected error for anonymous InitialContext:" + ex);
        }
        catch (Exception ex) {
            log.error("unexpected exception for anonymous", ex); 
            fail("unexpected exception for anonymous:" + ex); 
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
    }

    /**
     * Logs in as each user type an calls a query method of the EJB that 
     * will determine if the server side believes we are in the provided
     * role.
     * @throws Exception
     */
    @Test
    public void testIsCallerInRole() throws Exception {
        log.info("*** testIsCallerInRole ***");

        Context jndi=null;
        try {        	
        	jndi=runAs(null, null);
	        assertFalse("anonomous in admin role",
	                ejb.isCallerInRole("admin"));
	        assertFalse("anonomous in user role",
	                ejb.isCallerInRole("user"));
	        assertFalse("anonomous in internalRole role",
	                ejb.isCallerInRole("internalRole"));
        }
        catch (Exception ex) {
            log.info("anonymous calls to isCallerinRole failed:"+ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {        	
        	jndi=runAs(knownUser, knownPassword);
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
        	jndi=runAs(userUser, userPassword);
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
	    	jndi=runAs(adminUser, adminPassword);
	        assertTrue("admin not in admin role",
                ejb.isCallerInRole("admin"));
	        assertTrue("admin not in user role",
	        	ejb.isCallerInRole("user"));
	        assertTrue("user in internalRole role",
	        	ejb.isCallerInRole("internalRole"));
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
    }
    
    /**
     * Logs in as each use and calls the pingAll() method which permits 
     * all users to call it. Nobody should get rejected with the exception
     * of the anonymous user if the server requires an authenticated user 
     * identity to connect.
     * @throws Exception
     */
    @Test
    public void testPingAll() throws Exception {
        log.info("*** testPingAll ***");
        Context jndi=null;
        try {
            String result = ejb.pingAll();
            log.info(result);
            fail("anonymous user not detected"); 
        }
        catch (IllegalStateException ex) {
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
        	jndi = runAs("known", "password");
            String result = ejb.pingAll();
            log.info(result);
            assertEquals("unexpected result for known",
        		"called pingAll, principal=known, isUser=false, isAdmin=false, isInternalRole=false",
        		result);
        }
        catch (Exception ex) {
            log.info("error calling pingAll for known", ex);
            fail("error calling pingAll for known:" +ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
        
        try {
        	jndi = runAs(userUser, userPassword);
            String result = ejb.pingAll();
            log.info(result);
            assertEquals("unexpected result for admin",
        		String.format("called pingAll, principal=%s, isUser=true, isAdmin=false, isInternalRole=false", userUser),
        		result);
        }
        catch (Exception ex) {
            log.info("error calling pingAll for user", ex);
            fail("error calling pingAll for user:" +ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi = runAs(adminUser, adminPassword);
            String result=ejb.pingAll();
            log.info(result);
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
    
    /**
     * Logs in as each user and attempts to invoke a method that requires
     * the caller to have the user role. This should fail for all but the 
     * user and admin.
     * @throws Exception
     */
    @Test
    public void testPingUser() throws Exception {    	
        log.info("*** testPingUser ***");
        Context jndi=null;
        try {
            log.info(ejb.pingUser());
            fail("didn't detect anonymous user");
        }
        catch (IllegalStateException ex) {
            log.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
        	jndi=runAs(knownUser, knownPassword);
            log.info(ejb.pingUser());
            fail("didn't detect known, but non-user");
        }
        catch (EJBAccessException ex) {
            log.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
        
        try {
            jndi = runAs(userUser, userPassword);
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
            jndi = runAs(adminUser, adminPassword);
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
     * Logs in as each of the users and attempts to invoke a method that
     * has been restricted to just admins. All but the admin login should
     * fail.
     * @throws Exception
     */
    @Test
    public void testPingAdmin() throws Exception {
        log.info("*** testPingAdmin ***");
        Context jndi=null;
        try {
            log.info(ejb.pingAdmin());
            fail("didn't detect anonymous user");
        }
        catch (IllegalStateException ex) {
            log.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
            jndi=runAs(knownUser, knownPassword);
            log.info(ejb.pingAdmin());
            fail("didn't detect known, but non-admin user");
        }
        catch (EJBAccessException ex) {
            log.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
        
        try {
            jndi = runAs(userUser, userPassword);
            log.info(ejb.pingAdmin());
            fail("didn't detect non-admin user");
        }
        catch (EJBAccessException ex) {
            log.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
            jndi = runAs(adminUser, adminPassword);
            log.info(ejb.pingAdmin());
        }
        catch (Exception ex) {
            log.info("error calling pingAdmin:" + ex, ex);
            fail("error calling pingAdmin:" +ex);
        }
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
    }

    /**
     * Logs in as each of the users and attempts to invoke a method that 
     * has been excluded by all users to call it. All should fail.
     * @throws Exception
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
            log.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
            jndi=runAs(knownUser, knownPassword);
            log.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (EJBAccessException ex) {
            log.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
        
        try {
            jndi = runAs(userUser, userPassword);
            log.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (EJBAccessException ex) {
            log.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }        
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }

        try {
            jndi = runAs(adminUser, adminPassword);
            log.info(ejb.pingExcluded());
            fail("didn't detect excluded");
        }
        catch (EJBAccessException ex) {
            log.info("expected exception thrown:" + ex);
        }
        catch (Exception ex) {
        	fail("unexpected exception type:" + ex);
        }                
        finally {
        	if (jndi != null) { jndi.close(); jndi=null; }
        }
    } 
}
