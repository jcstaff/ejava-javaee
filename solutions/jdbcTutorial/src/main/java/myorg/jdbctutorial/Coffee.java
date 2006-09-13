package myorg.jdbctutorial;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains the data access methods to access the COFFEEES
 * database. 
 *
 * @author jcstaff
 */
public class Coffee {
    private static final Log log = LogFactory.getLog(Coffee.class);
    
    /**
     * This method performs a simple select to show a template framework
     * for adding other methods to complete the tutorial.
     */
    public void demoSelect(Connection connection) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = connection.createStatement();
            rs = st.executeQuery("SELECT * FROM COFFEES");
            while (rs.next()) {
                log.info("COF_NAME=" + rs.getString("COF_NAME") +
                        ", SUP_ID=" + rs.getInt("SUP_ID") +
                        ", PRICE=" + rs.getString("PRICE") +
                        ", SALES" + rs.getFloat("PRICE") +
                        ", TOTAL=" + rs.getInt("TOTAL")
                        );            
            }                                
        }
        finally {
            try { rs.close(); } catch (Throwable ignored) {}
            try { st.close(); } catch (Throwable ignored) {}
        }
    }
}
