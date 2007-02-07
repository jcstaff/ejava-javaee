package ejava.projects.esales.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.esales.bo.Account;
import ejava.projects.esales.bo.Address;
import ejava.projects.esales.dao.AccountDAO;

import junit.framework.TestCase;

/**
 * This test case provides an example of one might test the JDBC DAO. It 
 * provides most of the basics, knowing the DAO only implemented one method.
 * As more tests are added, the implementation show here would benefit from
 * re-usable techniques to check the values within the database.
 * 
 * @author jcstaff
 *
 */
public class JDBCAccountDAOTest extends TestCase {
	private static Log log = LogFactory.getLog(JDBCAccountDAO.class);
	private static String jdbcDriver = System.getProperty("jdbc.driver");
	private static String jdbcURL = System.getProperty("jdbc.url");
	private static String jdbcUser = System.getProperty("jdbc.user");
	private static String jdbcPassword = System.getProperty("jdbc.password");
	
	private Connection connection;
	private AccountDAO dao;
	
	public void setUp() throws Exception {
		assertNotNull("jdbc.driver not supplied", jdbcDriver);
		assertNotNull("jdbc.url not supplied", jdbcURL);
		assertNotNull("jdbc.user not supplied", jdbcUser);
		assertNotNull("jdbc.password not supplied", jdbcPassword);
		
		log.debug("loading JDBC driver:" + jdbcDriver);
		Thread.currentThread()
		      .getContextClassLoader()
		      .loadClass(jdbcDriver)
		      .newInstance();
		
		log.debug("getting connection(" + jdbcURL +
				", user=" + jdbcUser + ", password=" + jdbcPassword + ")");
		connection = 
			DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
		
	    dao = new JDBCAccountDAO();
	    ((JDBCAccountDAO)dao).setConnection(connection);
		
		connection.setAutoCommit(false);
		cleanup();
	}

	protected void tearDown() throws Exception {
		if (connection != null) {
			connection.commit();
		    ((JDBCAccountDAO)dao).setConnection(null);
			connection.close();
		}
	}
	
	private void cleanup() throws Exception {
		Statement statement=null;
		try {
			statement = connection.createStatement();
			statement.execute("DELETE FROM ESALES_ACCT_ADDRESS_LINK");
			statement.execute("DELETE FROM ESALES_ADDRESS");
			statement.execute("DELETE FROM ESALES_ACCT");
		}
		finally {
            if (statement != null) { statement.close(); }			
		}
	}

	/**
	 * This method tests a single create into the database using the DAO. 
	 * This tests some core functionality, but clearly more types of inserts
	 * should also be tested. For example, what happens when the same userId 
	 * is added a second time.
	 * 
	 * @throws Exception
	 */
	public void testJDBCCreate() throws Exception {
		log.info("*** testJDBCCreate ***");
		
    	String userId = "foo";
    	String firstName = "bar";
    	
    	Account account = new Account(userId);
    	account.setFirstName(firstName);
    	account.getAddresses().add(new Address(0, "Shipping", "Laurel"));
    	account.getAddresses().add(new Address(0, "Billing", "Columbia"));
    	
    	log.debug("instantiated Account:" + account);
    	
    	dao.createAccount(account);
    	log.debug("dao created Account:" + account);
    	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JDBC resources.
    	
    	connection.commit();
    	Statement statement = null;
    	ResultSet rs = null;
    	try {
    	    statement = connection.createStatement();
    	    
        	log.debug("getting core account info...");
    	    rs = statement.executeQuery(
    	    		"SELECT FIRST_NAME FROM ESALES_ACCT WHERE USER_ID='" 
    	    		+ userId + "'");
    	    if (rs.next()) {
    	    	String fName = rs.getString("FIRST_NAME");
    	    	assertEquals("unexpected first name", firstName, fName);
    	    }
    	    else {
    	    	fail("no account found");
    	    }
    	    
        	log.debug("core account good, checking addresses...");
    	    rs = statement.executeQuery(
    	    	"SELECT NAME, CITY " +
    	    	"FROM ESALES_ACCT, ESALES_ADDRESS, ESALES_ACCT_ADDRESS_LINK " +
    	    	"WHERE ESALES_ACCT_ADDRESS_LINK.USER_ID='" + userId + "' " +
    	    		"AND ESALES_ACCT_ADDRESS_LINK.ADDRESS_ID = " +
    	    		"ESALES_ADDRESS.ID");
    	    int count=0;
    	    while (rs.next()) {
    	    	count += 1;
    	    	String name = rs.getString("NAME");
    	    	String city = rs.getString("CITY");
        		if ("Shipping".equals(name)) {
        			assertEquals("unexpected city", "Laurel", city);
        		}
        		else if ("Billing".equals(name)) {
        			assertEquals("unexpected city", "Columbia", city);
        		}
        		else {
        			fail("unexpected address:" + name);
        		}
    	    }
    	    assertEquals("unexpected number of addresses", 
    	    		account.getAddresses().size(), count);
    	    
        	log.debug("address info good too");
    	}
    	finally {
    		if (rs != null) { rs.close(); }
    		if (statement != null) { statement.close(); }
    	}
	}
}
