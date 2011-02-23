package ejava.projects.edmv.jdbc;

import static org.junit.Assert.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.dao.PersonDAO;
import ejava.projects.edmv.jdbc.JDBCPersonDAO;

/**
 * This test case provides an example of one might test the JDBC DAO. It 
 * provides most of the basics, knowing the DAO only implemented one method.
 * As more tests are added, the implementation show here would benefit from
 * re-usable techniques to check the values within the database.
 * 
 * @author jcstaff
 *
 */
public class JDBCPersonDAOTest {
	private static Log log = LogFactory.getLog(JDBCPersonDAOTest.class);
	private static String jdbcDriver = 
		System.getProperty("jdbc.driver", "org.hsqldb.jdbcDriver");
	private static String jdbcURL = 
		System.getProperty("jdbc.url", "jdbc:hsqldb:hsql://localhost:9001");
	private static String jdbcUser = 
		System.getProperty("jdbc.user", "sa");
	private static String jdbcPassword = 
		System.getProperty("jdbc.password", "");
	
	private Connection connection;
	private PersonDAO dao;
	
	@Before
	public void setUp() throws Exception {		
		log.debug("loading JDBC driver:" + jdbcDriver);
		Thread.currentThread()
		      .getContextClassLoader()
		      .loadClass(jdbcDriver)
		      .newInstance();
		
		log.debug("getting connection(" + jdbcURL +
				", user=" + jdbcUser + ", password=" + jdbcPassword + ")");
		connection = 
			DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
		
	    dao = new JDBCPersonDAO();
	    ((JDBCPersonDAO)dao).setConnection(connection);
		
		connection.setAutoCommit(false);
		cleanup();
	}

	@After
	public void tearDown() throws Exception {
		if (connection != null) {
			connection.commit();
		    ((JDBCPersonDAO)dao).setConnection(null);
			connection.close();
		}
	}
	
	private void cleanup() throws Exception {
		Statement statement=null;
		try {
			statement = connection.createStatement();
            statement.execute("DELETE FROM EDMV_VREG_OWNER_LINK");
			statement.execute("DELETE FROM EDMV_PERSON");
			statement.execute("DELETE FROM EDMV_VREG");
		}
		finally {
            if (statement != null) { statement.close(); }			
		}
	}

	/**
	 * This method tests a single create into the database using the DAO. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJDBCCreate() throws Exception {
		log.info("*** testJDBCCreate ***");
		
    	String lastName = "foo";
    	
    	Person person = new Person();
    	person.setLastName(lastName);
    	
    	log.debug("instantiated Person:" + person);
    	assertEquals("unexpected initial ID", 0, person.getId());
    	
    	dao.createPerson(person);
    	log.debug("dao created Person:" + person);
        assertFalse("unexpected generated PK", 0 == person.getId());
    	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JDBC resources.
    	
    	connection.commit();
    	Statement statement = null;
    	ResultSet rs = null;
    	try {
    	    statement = connection.createStatement();
    	    
        	log.debug("getting core person info...");
    	    rs = statement.executeQuery(
    	    		"SELECT LAST_NAME FROM EDMV_PERSON WHERE ID='" 
    	    		+ person.getId() + "'");
    	    if (rs.next()) {
    	    	String name = rs.getString("LAST_NAME");
    	    	assertEquals("unexpected last name", person.getLastName(), name);
    	    }
    	    else {
    	    	fail("no person found");
    	    }
    	    
        	log.debug("JDBC person looks good");
    	}
    	finally {
    		if (rs != null) { rs.close(); }
    		if (statement != null) { statement.close(); }
    	}
	}
}
