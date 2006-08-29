package ejava.examples.dao.jdbc;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

public class JDBCDAOBase {
    static ThreadLocal<Connection> connection = new ThreadLocal<Connection>();

    /**
     * sets the connection for the current thread.
     * @param conn
     */
    public static void setConnection(Connection conn) {
        connection.set(conn);
    }
    
    /**
     * returns the connection assigned to the current thread.
     * @return
     * @throws IllegalStateException if setConnection has not been called
     *     for current thread
     */
    protected Connection getConnection() throws IllegalStateException {
        Connection conn = connection.get();
        if (conn == null) {
            throw new IllegalStateException("Connection has not been set");
        }
        return conn;
    }

    /**
     * Closes the connection associated with the thread.
     * @throws SQLException
     */
    protected static void closeConnection() throws SQLException {
        Connection conn = connection.get();
        if (conn != null) {
            connection.set(null);
            conn.close();
        }
    }
    
    /**
     * Closes the provided statement.
     * @param st
     */
    protected void close(Statement st) {
        try { st.close();}
        catch (Throwable ignored) {}
    }

    /**
     * Closes the provided ResultSet
     * @param rs
     */
    protected void close(ResultSet rs) {
        try { rs.close();}
        catch (Throwable ignored) {}
    }
}
