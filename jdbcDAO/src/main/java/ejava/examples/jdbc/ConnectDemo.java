package ejava.examples.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides a very simplistic technical demonstration of how a 
 * Java class can access the DB thru straight JDBC and no DAO.
 */
public class ConnectDemo {
    private static final Log log = LogFactory.getLog(ConnectDemo.class);
    /**
     * Since this class is in the src/main tree -- i.e., "production" --
     * the DB info should come from external sources. It would be error
     * prone to place defaults here that are valid for test and not
     * production. It would be a security issue if operational credentials
     * were placed in the source code.
     */
    private static final String dbDriver = System.getProperty("jdbc.driver");
    private static final String dbUrl = System.getProperty("jdbc.url");
    private static final String dbUser = System.getProperty("jdbc.user");
    private static String dbPassword = System.getProperty("jdbc.password");
    static {
        if (dbPassword.equals("\"\"")) { dbPassword = ""; }
    }

    /**
     * This helper function encapsulates the duty of loading the DB driver
     * provided in the jdbc.driver system property. 
     * @throws ClassNotFoundException
     */
    private static void loadDriver() throws ClassNotFoundException {
        log.debug(String.format("loading driver %s", dbDriver));
        Class.forName(dbDriver);
    }
    
    /**
     * This helper function demonstrates how to obtain a physical DB connection
     * based on the provided jdbc.url system property and authenticate using 
     * the values in the jdbc.user/jdbc/password properties. 
     * @return
     * @throws SQLException
     */
    private static Connection getConnection() throws SQLException {
        log.debug(String.format("getting connection for %s, user=%s, password=%s", 
        		dbUrl, dbUser, dbPassword));
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
    
    /**
     * This method demonstrates a sample access of the DB.
     * @param connection provided by transaction/integration layer
     * @throws SQLException
     */
    private static void accessDatabase(Connection conn) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select * from IMAGE_IMAGE");
            while (rs.next()) {
                String imageId = rs.getString("IMAGE_ID");
                String imageType = rs.getString("IMAGE_TYPE");
                String fileName = rs.getString("FILENAME");
                String url = rs.getString("URL");
                log.info(String.format("%s, %s %s, %s",
                		imageId, imageType, fileName, url));
            }
        }
        finally {
            log.debug("closing resources");
            if (rs != null) { rs.close(); }
            if (st != null) { st.close(); }
            log.debug("resources closed");
        }
    }
    
    public static void main(String args[]) {
        Connection connection = null;
        try {
            loadDriver();
            connection = getConnection();
            accessDatabase(connection);
        }
        catch (Exception ex) {
            log.fatal("error running Connect demo", ex);
            System.exit(-1);
        }
        finally {
        	try {
        		if (connection != null) { connection.close(); }
        	} catch (SQLException ex) {
				log.warn("unexpected SQL exception closing connection", ex);
	            System.exit(-1);
			}
        }
    }

}
