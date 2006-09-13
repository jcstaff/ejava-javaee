package ejava.examples.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectDemo {
    private static final Log log = LogFactory.getLog(ConnectDemo.class);
    private static final String dbDriver = System.getProperty("jdbc.driver");
    private static final String dbUrl = System.getProperty("jdbc.url");
    private static final String dbUser = System.getProperty("jdbc.user");
    private static String dbPassword = System.getProperty("jdbc.password");
    static {
        if (dbPassword.equals("\"\"")) { dbPassword = ""; }
    }

    
    private static void loadDriver() throws ClassNotFoundException {
        log.debug("loading driver " + dbDriver);
        Class.forName(dbDriver);
    }
    
    private static Connection getConnection() throws SQLException {
        log.debug("getting connection for " + dbUrl + 
                ", user=" + dbUser + ", password=" + dbPassword);
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
    
    private static void accessDatabase() throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select * from IMAGE_IMAGE");
            while (rs.next()) {
                String imageId = rs.getString("IMAGE_ID");
                String imageType = rs.getString("IMAGE_TYPE");
                String fileName = rs.getString("FILENAME");
                String url = rs.getString("URL");
                log.info(imageId + 
                        ", " + imageType + 
                        ", " + fileName +
                        ", " + url);
            }
        }
        finally {
            log.debug("closing resources");
            try { rs.close(); } catch (Throwable ignored) {}
            try { st.close(); } catch (Throwable ignored) {}
            try { conn.close(); } catch (Throwable ignored) {}
            log.debug("resources closed");
        }
    }
    
    public static void main(String args[]) {
        try {
            loadDriver();
            accessDatabase();
        }
        catch (Exception ex) {
            log.fatal("error running Connect demo", ex);
            System.exit(-1);
        }
    }

}
