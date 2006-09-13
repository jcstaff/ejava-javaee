package myorg.jdbctutorial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class CoffeeTest extends TestCase {
    private static final Log log = LogFactory.getLog(CoffeeTest.class);
    private static final String dbDriver = System.getProperty("jdbc.driver"); 
    private static final String dbUrl = System.getProperty("jdbc.url"); 
    private static final String dbUser = System.getProperty("jdbc.user"); 
    private static final String dbPassword = System.getProperty("jdbc.password"); 
    private Connection connection;

    public void setUp() throws ClassNotFoundException, SQLException {
        log.debug("loading driver:" + dbDriver);
        Class.forName(dbDriver);
        log.debug("getting connection: url=" + dbUrl + 
                ", user=" + dbUser + ", password=" + dbPassword);
        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
    
    public void tearDown() throws SQLException {
        log.debug("closing connection"); 
        connection.close();
    }
    
    public void testDemoSelect() throws Exception {
        log.debug("testDemoSelect");
        Coffee dao = new Coffee();
        dao.demoSelect(connection);
    }    
}
