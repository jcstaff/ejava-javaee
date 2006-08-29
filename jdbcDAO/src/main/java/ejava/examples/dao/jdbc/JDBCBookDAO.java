package ejava.examples.dao.jdbc;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.dao.domain.Book;

/**
 * This class provides a sample implementation of a DAO using straight JDBC 
 * and SQL. 
 * 
 * @author jcstaff
 * $Id:$
 */
public class JDBCBookDAO extends JDBCDAOBase {
    private static final Log log_ = LogFactory.getLog(JDBCDAOBase.class);
    public String SEQ_NAME = "DAO_BOOK_SEQ";
    public String SEQ_VALUE = "DAO_BOOK_UID";
    public String TABLE_NAME = "DAO_BOOK";
    
    /**
     * This could be used to generate unique Ids from a database sequence
     */
    public int getNextId() throws Exception {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            st.executeUpdate("UPDATE " + SEQ_VALUE + " " +
                    "SET ID=NEXT VALUE FOR " + SEQ_NAME);                        
            rs = st.executeQuery("SELECT ID FROM " + SEQ_VALUE);
            rs.next();
            return rs.getInt(1);            
        }
        finally {
            close(rs);
            close(st);
        }
    }
    
    public Book create(Book book) throws Exception {
        long id = (book.getId() == 0) ? getNextId() : book.getId();
        
        PreparedStatement st = null;
        try {
            st = getConnection().prepareStatement(
                    "INSERT INTO " + TABLE_NAME + " "
                            + "(ID, VERSION, TITLE, AUTHOR, DESCRIPTION, PAGES) "
                            + "VALUES(?, ?, ?, ?, ?, ?)");
            st.setLong(1, id);
            st.setLong(2, 0);
            st.setString(3, book.getTitle());
            st.setString(4, book.getAuthor());
            st.setString(5, book.getDescription());
            st.setInt(6, book.getPages());
            if (st.executeUpdate() != 1) {
                throw new DAOException("unable to insert Book");    
            }
            book.setVersion(0);
            if (book.getId()==0) {
                //use reflection to get private setId method of Book class
                Method setId = Book.class.getDeclaredMethod(
                        "setId", new Class[] { int.class });
                setId.setAccessible(true);
                setId.invoke(book, new Object[] { id });
            }
        } finally {
            close(st);
        }
        return book;
    }

    protected long getVersion(long id) throws Exception {
        long version = 0;
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery("SELECT VERSION FROM " + TABLE_NAME + " WHERE ID="
                    + id);
            if (!rs.next()) {
                throw new DAOException("Object not found");
            }
            version = rs.getLong(1);
        } finally {
            close(rs);
            close(st);
        }

        return version;
    }

    public Book update(Book book) throws Exception {
        if (book.getId() == 0) {
            throw new DAOException("Book does not have primary key");
        }

        PreparedStatement st = null;
        try {
            long version = getVersion(book.getId());
            st = getConnection().prepareStatement("UPDATE " + TABLE_NAME + " " +
                    "SET VERSION=?, TITLE=?, AUTHOR=?, DESCRIPTION=?, PAGES=? " +
                    "WHERE ID=?");
            st.setLong(1, ++version);
            st.setString(2, book.getTitle());
            st.setString(3, book.getAuthor());
            st.setString(4, book.getDescription());
            st.setInt(5, book.getPages());
            st.setLong(6, book.getId());
            int count = st.executeUpdate();
            if (count == 0) {
                throw new DAOException("Object not found:" + book.getId());
            }
            book.setVersion(version);
        } finally {
            close(st);
        }

        return book;
    }

    public Book get(long id) throws Exception {
        Book book = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery(
                    "SELECT VERSION, AUTHOR, TITLE, DESCRIPTION, PAGES "
                  + "FROM " + TABLE_NAME + " " + "WHERE ID=" + id);
            if (!rs.next()) {
                throw new DAOException("Object not found");
            }
            book = new Book(id);
            book.setVersion(rs.getLong(1));
            book.setAuthor(rs.getString(2));
            book.setTitle(rs.getString(3));
            book.setDescription(rs.getString(4));
            book.setPages(rs.getInt(5));
        } finally {
            close(rs);
            close(st);
        }

        return book;
    }

    public boolean remove(Book book) throws Exception {
        Statement st = null;
        try {
            st = getConnection().createStatement();
            int count = st.executeUpdate("DELETE FROM " + TABLE_NAME + " WHERE ID="
                    + book.getId());
            return count == 1;
        } finally {
            close(st);
        }
    }
}
