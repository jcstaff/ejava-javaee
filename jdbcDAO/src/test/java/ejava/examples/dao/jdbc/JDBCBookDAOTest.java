package ejava.examples.dao.jdbc;

import static org.junit.Assert.*;

import java.sql.Connection;

import java.sql.DriverManager;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ejava.examples.dao.BookDAO;
import ejava.examples.dao.DAOException;
import ejava.examples.dao.domain.Book;


/**
 * This class provides a demonstration of using a DAO implemented with JDBC. 
 * The client must obtain database connections and manage the scope of the
 * transactions. In this case, we are only using a local database transaction
 * since there are no JTA transactions available outside of the JavaEE
 * container.
 * 
 * @author jcstaff
 * $Id:$
 */
public class JDBCBookDAOTest {
    public static final boolean USE_GENERATED_ID = false;
    static Log log_ = org.apache.commons.logging.LogFactory
            .getLog(JDBCBookDAOTest.class);

	private static String dbDriver = 
		System.getProperty("jdbc.driver", "org.hsqldb.jdbcDriver");
	private static String dbUrl = 
		System.getProperty("jdbc.url", "jdbc:hsqldb:hsql://localhost:9001");
	private static String dbUser = 
		System.getProperty("jdbc.user", "sa");
	private static String dbPassword = 
		System.getProperty("jdbc.password", "");
    private static long id=2001;    
    private static long nextId() { 
        return (USE_GENERATED_ID) ? 0 : id++; 
    }
    private Connection connection = null;
    private BookDAO dao = null;

    /**
     * This method uses the DriverManager and provided system properties
     * to obtain a new connection to the database.
     */
    private static Connection getConnection() throws Exception {
        log_.info("getConnection(" + dbUrl + ", " + dbUser + ", "
                + dbPassword + ")");
        Thread.currentThread().getContextClassLoader().loadClass(
                        dbDriver).newInstance();
        return DriverManager.getConnection(dbUrl, dbUser,
                        dbPassword);
    }

    /**
     * This is a convenience method to close and null out the connection 
     * to the database. Ideally, we would cache connections, but ... 
     */
    private void closeConnection() throws Exception {
        log_.info("closeConnection()=" + connection);
        if (connection != null) {
           connection.close();
           connection = null;
        }
    }

    /**
     * Before each test is run, we need to make sure we have a connection
     * to the database and the DAOs within this thread have a reference 
     * to it.
     */
    @Before
    public void setUp() throws Exception {
        connection = getConnection();
        connection.setAutoCommit(false);
        JDBCBookDAO.setConnection(connection);
        dao = new JDBCBookDAO();
    }

    /**
     * Close (or return to cache) connections between each test so we 
     * don't leak valuable resources.
     */
    @After
    public void tearDown() throws Exception {
        closeConnection();
    }    

    /**
     * This test verifies the ability of the DAO to create an object.
     */
    @Test
    public void testCreate() throws Exception {
        log_.info("testCreate()");

        Book book = new Book(nextId());
        book.setTitle("a");
        book.setAuthor("b");
        book.setDescription("testCreate");
        book.setPages(20);

        try {
            log_.info("creating book=" + book);
            Book book2 = dao.create(book);
            connection.commit();
            assertNotNull(book2);
            log_.info("created book=" + book2);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            connection.rollback();
            fail("" + ex);
        }
    }

    /**
     * This test verifies the ability of the DAO to update the database.
     */
    @Test
    public void testUpdate() throws Exception {
        log_.info("testUpdate()");

        Book book = new Book(nextId());
        dao.create(book);

        Book book2 = dao.get(book.getId());
        book2.setTitle("a");
        book2.setAuthor("b");
        book2.setDescription("testUpdate");
        book2.setPages(20);
        try {
            dao.update(book2);
            connection.commit();
        }
        catch (Exception ex) {
            log_.fatal(ex);
            connection.rollback();
            fail("" + ex);
        }
        
        Book book3 = dao.get(book.getId());
        log_.info("book1=" + book);
        log_.info("book2=" + book2);
        log_.info("book3=" + book3);

        assertEquals(book.getId(), book2.getId());

        assertEquals(book2.getId(), book3.getId());
        assertTrue("authors not match", book2.getAuthor().equals(
                book3.getAuthor()));
        assertTrue("titles not match", book2.getTitle()
                .equals(book3.getTitle()));
        assertTrue("descriptions not match ", book2.getDescription().equals(
                book3.getDescription()));
        assertTrue("pages not match", book2.getPages() == book3.getPages());
    }

    /**
     * This test verifies the ability of the DAO to get an object from the
     * database.
     */
    @Test
    public void testGet() throws Exception {
        log_.info("testGet()");

        Book book = new Book(nextId());
        book.setTitle("a");
        book.setAuthor("b");
        book.setDescription("testGet");
        book.setPages(20);
        try {
            dao.create(book);
            connection.commit();
        }
        catch (Exception ex) {
            log_.fatal(ex);
            connection.rollback();
            fail("" + ex);
        }

        Book book2 = dao.get(book.getId());
        assertEquals(book.getId(), book2.getId());
        assertEquals(book.getAuthor(), book2.getAuthor());
        assertEquals(book.getTitle(), book2.getTitle());
        assertEquals(book.getDescription(), book2.getDescription());
        assertEquals(book.getPages(), book2.getPages());
    }

    /**
     * This test verifies the ability of the DAO to remove an object from
     * the database.
     */
    @Test
    public void testRemove() throws Exception {
        log_.info("testRemove()");
        Book book = new Book(nextId());
        book.setDescription("testRemove");
        try {
            dao.create(book);
            connection.commit();
        }
        catch (Exception ex) {
            log_.fatal(ex);
            connection.rollback();
            fail("" + ex);
        }

        assertNotNull(dao.get(book.getId()));
        dao.remove(book);

        boolean errorReported = false;
        try {
            dao.get(book.getId()); // this should throw not found exception
        } catch (DAOException ex) {
            errorReported = true;
        }
        assertTrue("get error not reported", errorReported);
    }
    
    @Test
    public void testFind() throws Exception {
        log_.info("testFind()");
        try {
            for(int i=0; i<100; i++) {
                Book book = new Book(nextId());
                book.setDescription("testFind:" + i);
                dao.create(book);
            }
            connection.commit();
        }
        catch (Exception ex) {
            log_.fatal(ex);
            connection.rollback();
            fail("" + ex);
        }

        Collection<Book> books = dao.findAll(20, 25);
        assertTrue("unexpected number of books:" + books.size(), 
            books.size()==25);
    }

    /**
     * This test verifies that the framework can create a connection and
     * make it available to the DAO. A test class is used to inspect the 
     * non-public access to that connection.
     */
    @Test
    public void testGetConnection() throws Exception {
        log_.info("testGetConnection()");
        Connection connection = JDBCBookDAOTest.getConnection();
        JDBCBookDAO.setConnection(connection);

        TestClass dao = new TestClass();

        assertEquals(connection, dao.getConnection());
        JDBCBookDAO.setConnection(null);
    }
    private class TestClass extends JDBCDAOBase {
        public Connection getConnection() {
            return super.getConnection();
        }
    }
}
