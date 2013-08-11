package ejava.examples.daoex.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.daoex.bo.Book;
import ejava.examples.daoex.dao.BookDAO;
import ejava.examples.daoex.dao.DAOException;

/**
 * This class provides a sample implementation of a DAO using straight JDBC 
 * and SQL. 
 * 
 */
public class JDBCBookDAO implements BookDAO {
	private static final Log log = LogFactory.getLog(JDBCBookDAO.class);
    public static final String SEQ_NAME = "DAO_BOOK_SEQ";
    public static final String SEQ_VALUE = "DAO_BOOK_UID";
    public static final String TABLE_NAME = "DAO_BOOK";
    
    protected Connection conn;
    
    /**
     * This method must be called to inject a JDBC connection prior
     * to using this DAO.
     * @param conn
     */
	public void setConnection(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * This helper method is used to close result set and statements.
	 * @param rs
	 * @param st
	 */
	protected void close(Statement st, ResultSet rs) {
		try { if (rs != null) { rs.close(); }
		} catch (SQLException ex) {
			log.warn("SQLException closing result set:" + ex, ex);
		}
		try { if (st != null) { st.close(); }
		} catch (SQLException ex) {
			log.warn("SQLException closing statement:" + ex, ex);
		}
	}

	/**
	 * This helper method will generate an ID based on a DB sequence.
	 * @return
	 * @throws DAOException
	 */
    protected int getNextId() throws DAOException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeUpdate(String.format("UPDATE %s SET ID=NEXT VALUE FOR %s",
                    SEQ_VALUE, SEQ_NAME));                        
            rs = st.executeQuery(String.format("SELECT ID FROM %s", SEQ_VALUE));
            rs.next();
            return rs.getInt(1);            
        }
        catch (SQLException ex) {
            throw new DAOException(ex);
        }
        finally {
            close(st, rs);
        }
    }
    
	@Override
    public Book create(Book book) throws DAOException {
        long id = (book.getId() == 0) ? getNextId() : book.getId();
        
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(String.format("INSERT INTO %s " +
            		"(ID, VERSION, TITLE, AUTHOR, DESCRIPTION, PAGES) " +
            		"VALUES(?, ?, ?, ?, ?, ?)", 
            		TABLE_NAME));
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
            
            return book;
        } catch (Exception ex) {
            throw new DAOException(ex);
        } finally {
            close(st, null);
        }
    }

	/**
	 * This helper method will return the current version of the row with
	 * the 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
    protected long getVersion(long id) throws SQLException, DAOException {
        long version = 0;
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(String.format("SELECT VERSION FROM %s WHERE ID=%d",
            		TABLE_NAME, id));
            if (!rs.next()) {
                throw new DAOException("Object not found");
            }
            version = rs.getLong(1);
        } finally {
            close(st, rs);
        }

        return version;
    }

	@Override
    public Book update(Book book) throws DAOException {
        if (book.getId() == 0) {
            throw new DAOException("Book does not have primary key");
        }

        PreparedStatement st = null;
        try {
            long version = getVersion(book.getId());
            st = conn.prepareStatement(String.format("UPDATE %s " +
            	"SET VERSION=?, TITLE=?, AUTHOR=?, DESCRIPTION=?, PAGES=? WHERE ID=?",
                TABLE_NAME));
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
            
            return book;
        } catch (SQLException ex) {
            throw new DAOException(ex);                
        } finally {
            close(st, null);
        }

    }

	@Override
    public Book get(long id) throws DAOException {
        Book book = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(String.format(
            	"SELECT VERSION, AUTHOR, TITLE, DESCRIPTION, PAGES " +
                "FROM %s WHERE ID=%d",
                	TABLE_NAME, 
                	id));
            if (!rs.next()) {
                throw new DAOException("Object not found");
            }
            book = new Book(id);
            book.setVersion(rs.getLong(1));
            book.setAuthor(rs.getString(2));
            book.setTitle(rs.getString(3));
            book.setDescription(rs.getString(4));
            book.setPages(rs.getInt(5));
            
            return book;
        } catch (SQLException ex) {
            throw new DAOException(ex);
        } finally {
            close(st, rs);
        }
    }

	@Override
    public boolean remove(Book book) throws DAOException {
        Statement st = null;
        try {
            st = conn.createStatement();
            int count = st.executeUpdate(String.format("DELETE FROM %s WHERE ID=%d",
            		TABLE_NAME,
                    book.getId()));
            return count == 1;
        } catch (SQLException ex) {
            throw new DAOException(ex);
        } finally {
            close(st, null);
        }
    }

	@Override
    public Collection<Book> findAll(int start, int count) throws DAOException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery(String.format("SELECT * FROM %s", TABLE_NAME));
            Collection<Book> collection = new ArrayList<Book>();
            if (start > 0) {
            	rs.absolute(start);
            }
            int i=0;
            while (rs.next() && (count<=0 || i++<count)) {
                Book b = new Book(rs.getLong("ID"));
                b.setAuthor(rs.getString("AUTHOR"));
                b.setDescription(rs.getString("DESCRIPTION"));
                b.setPages(rs.getInt("PAGES"));
                b.setTitle(rs.getString("TITLE"));
                b.setVersion(rs.getLong("VERSION"));
                collection.add(b);
            }
            
            return collection;
        } catch (SQLException ex) {
            throw new DAOException(ex);                
        } finally {
            close(st, rs);
        }
    }
}
