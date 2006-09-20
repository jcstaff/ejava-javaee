package ejava.examples.dao.jpa;

import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.dao.AuthorDAO;
import ejava.examples.dao.domain.Author;

import junit.framework.TestCase;

/**
 * This class provides a demo of the Java Persistence API Entity Manager by 
 * testing a DAO built using a javax.persistence.EntityManager.
 * 
 * EntityManagers operate in two types of scope; TRANSACTION and EXTENDED. 
 * JavaEE environments primarily use Tx-scoped and, as the name implies,
 * must always be invoked within the scope of a JTA transaction. These are 
 * commonly used in injection, but can also be looked up in JNDI. 
 * Extended-scope EntityManagers are typically independent of JTA and provided
 * in non-JavaEE environments. One uses the static method within the 
 * PersistenceManagerFactory class to obtain an instance versus JNDI.
 * 
 * With that said, the JBoss embbedable container offers both extended and 
 * transaction scoped EntityManagers. The test below highlights some of the
 * differences.
 * 
 * @author jcstaff
 * $Id:$
 */
public class JPAAuthorDAODemo extends TestCase {
    private static final boolean USE_INJECTED_EM = false;
    private static final boolean USE_JTA = false;  //used only when INJ_EM=false    
    //when true @GeneratedValue must be added to Author
    private static final boolean USE_GENERATED_ID = true;
    
    private static long id = 1001;
    private static Log log_ = LogFactory.getLog(JPAAuthorDAODemo.class);
    private EntityManager em;
    private AuthorDAO dao;    
    
    public void setUp() throws Exception {
        log_.debug("setUp() started, em=" + em);
        em = getEntityManager();
        JPADAOBase.setEntityManager(em);
        dao = new JPAAuthorDAO();
        log_.debug("setUp() complete, em=" + em);
    }
    public void tearDown() throws Exception {
        try {
            log_.debug("tearDown() started, em=" + em);
            beginTx();
            em.flush();
            if (!USE_INJECTED_EM) {
                em.close();
            }
            commitTx();
            log_.debug("tearDown() complete, em=" + em);
        }
        catch (Exception ex) {
            log_.fatal("tearDown failed", ex);
            throw ex;
        }
    }
    
    public static EntityManager getEntityManager() throws Exception {
        EntityManager em; 
        if (USE_INJECTED_EM) {
            log_.info("** using Injected EntityManager **");
            InitialContext jndi = new InitialContext();
            em = (EntityManager)jndi.lookup("java:/EntityManagers/ejava");
        }
        else {
            log_.info("** using EntityManagerFactory **");
            EntityManagerFactory emf = 
                JPAUtil.getEntityManagerFactory("jpaDemo");
            em = emf.createEntityManager();
            if (USE_JTA) {
                log_.info("** joining EntityManager with JTA **");
                em.joinTransaction();
            }
        }            
        return em;
    }
    
    public void beginTx() throws Exception {
        if (USE_INJECTED_EM || USE_JTA) {
            log_.debug("begin JTA Tx");
            getInjectedTransactionManager().begin();
        }
        else {
            log_.debug("begin Persistence Tx");
            em.getTransaction().begin();
        }
    }
    public void commitTx() throws Exception {
        if (USE_INJECTED_EM || USE_JTA) {
            log_.debug("commit JTA Tx");
            getInjectedTransactionManager().commit();
        }
        else {
            log_.debug("commit Persistence Tx");
            em.getTransaction().commit();
        }
    }
    public void rollbackTx() throws Exception {
        if (USE_INJECTED_EM || USE_JTA) {
            log_.debug("rollback JTA Tx");
            getInjectedTransactionManager().rollback();
        }
        else {
            log_.debug("rollback Persistence Tx");
            em.getTransaction().rollback();
        }
    }

    public static TransactionManager getInjectedTransactionManager() throws Exception {
        log_.debug("getting injected TransactionManager");
        InitialContext jndi = new InitialContext();
        return (TransactionManager)jndi.lookup("java:/TransactionManager");
    }
    
    private long nextId() { return (USE_GENERATED_ID) ? 0 : id++; }
    
    /**
     * This test verifies that the required properties required for 
     * InitialContext can be located. They are intended to be supplied in 
     * a jndi.properties file located in src/test/resources.
     */
    public void testInitialContext() throws Exception {
        log_.info("testInitialContext()");
        Context jndi = new InitialContext();
        assertNotNull(jndi);
        log_.info("jndi properties used:" + jndi.getEnvironment());
    }
    
    /**
     * This test verifies that the EntityManager can be located in the 
     * JNDI tree.
     */
    public void testGetEntityManager() throws Exception {
        log_.info("testGetEntityManager()");
        EntityManager em = getEntityManager();
        assertNotNull(em);
    }

    /**
     * This test verifies that the TransactionManager can be located in the 
     * JNDI tree.
     */
    public void testGetTransactionManager() throws Exception {
        log_.info("testGetTransactionManager()");
        TransactionManager tx = getInjectedTransactionManager();
        assertNotNull(tx);
    }
    
    /**
     * This test verifies we can persist an entity.
     */
    public void testCreate() throws Exception {
        log_.info("testCreate()");
        Author author = new Author(nextId());
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        log_.info("creating author:" + author);
        
        //try creating outside of a transaction - bang        
        boolean threwException = false;
        try {
            dao.create(author);
            log_.info("created author:" + author);
        }
        catch (Exception ex) { log_.debug(ex); threwException = true; }
        
        if (USE_INJECTED_EM) {
            assertTrue(threwException);
            try {
                beginTx();
                dao.create(author);
                commitTx();
                log_.info("created author:" + author);
            }
            catch (Exception ex) {
                log_.fatal(ex);
                rollbackTx();
                fail("" + ex);
            }
        }
        else { //the non-injected EM uses an EXTENDED persistence context
            assertFalse(threwException);
            /* when the DAO, using an EXTENDED persistence context,
             * calls em.persist() outside of the context
             * of a transaction, the insert is queued until the context is
             * again associated with a transaction. 
             */
        }
    }
    

    /**
     * This test verifies the ability of the DAO to get an object from the 
     * database.
     * @throws Exception
     */
    public void testGet() throws Exception {
        log_.info("testGet()");
        Author author = new Author(nextId());
        author.setFirstName("thing");
        author.setLastName("one");
        author.setSubject("children");
        author.setPublishDate(new Date());
        
        log_.info("creating author:" + author);
        try {
            beginTx();
            dao.create(author);
            commitTx();
            log_.info("created author:" + author);        
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }

        Author author2=null;
        //try getting outside of a transaction - bang
        boolean threwException = false;
        try {
            author2 = dao.get(author.getId());
            log_.info("got author author:" + author2);
        }
        catch (Exception ex) { threwException = true; }
        
        if (USE_INJECTED_EM) {
            assertTrue(threwException);
            try {
                beginTx();
                author2 = dao.get(author.getId());
                commitTx();
                log_.info("got author author:" + author2);
            }
            catch (Exception ex) {
                log_.fatal(ex);
                rollbackTx();
                fail("" + ex);
            }
        }
        else {
            assertFalse(threwException);            
        }

        assertEquals(author.getFirstName(), author2.getFirstName());
        assertEquals(author.getLastName(), author2.getLastName());
        assertEquals(author.getSubject(), author2.getSubject());
        assertEquals(author.getPublishDate(), author2.getPublishDate());
    }

    /**
     * This test verifies the functionality of a query method that simply 
     * queries by the primary key value.
     */
    public void testQuery() throws Exception {
        log_.info("testQuery()");
        
        Author author = new Author(nextId());
        author.setFirstName("test");
        author.setLastName("Query");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        
        log_.info("creating author:" + author);
        try {
            beginTx();
            dao.create(author);
            commitTx();
            log_.info("created author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        
        Author author2 = null;
        try {
            beginTx();
            author2 = dao.getByQuery(author.getId());
            commitTx();
            log_.info("got author author:" + author2);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        
        assertNotNull(author2);
        assertEquals(author.getFirstName(), author2.getFirstName());
        assertEquals(author.getLastName(), author2.getLastName());
        assertEquals(author.getSubject(), author2.getSubject());
        assertEquals(author.getPublishDate(), author2.getPublishDate());
    }
    

    /**
     * This tests the ability to update on object.
     */
    public void testUpdate() throws Exception {
        log_.info("testUpdate");
        
        String firstName="test";
        String lastName="Update";
        String subject="testing";
        Date published = new Date();
        
        Author author = new Author(nextId());
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setSubject(subject);
        author.setPublishDate(published);        
        try {
            beginTx();
            dao.create(author);
            commitTx();
            log_.info("created author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        
        author.setFirstName("updated " + firstName);
        author.setLastName("updated " + lastName);
        author.setSubject("updated " + subject);
        author.setPublishDate(new Date(published.getTime()+ 1000));
        try {
            beginTx();
            dao.update(author);
            commitTx();
            log_.info("updated author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        
        Author author2 = null;
        try {
            beginTx();
            author2 = dao.get(author.getId());
            commitTx();
            log_.info("got author:" + author2);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        
        assertNotNull(author2);
        assertEquals("updated " + firstName, author2.getFirstName());
        assertEquals("updated " + lastName, author2.getLastName());
        assertEquals("updated " + subject, author2.getSubject());
        assertEquals(new Date(published.getTime()+1000), author2.getPublishDate());
    }

    /**
     * This tests the ability to merge an object.
     */
    public void testMerge() throws Exception {
        log_.info("testMerge");
        
        String firstName="test";
        String lastName="Merge";
        String subject="testing";
        Date published = new Date();
        
        Author author = new Author(nextId());
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setSubject(subject);
        author.setPublishDate(published);        
        try {
            beginTx();
            dao.create(author);
            em.flush();
            em.clear();
            commitTx();
            log_.info("created author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        
        Author author2 = new Author(author.getId());
        author2.setFirstName("updated " + author.getFirstName());
        author2.setLastName("updated " + author.getLastName());
        author2.setSubject("updated " + author.getSubject());
        author2.setPublishDate(new Date(published.getTime()+ 1000));
        try {
            log_.info("merging with author:" + author2);
            beginTx();
            Author tmp = dao.updateByMerge(author2);
            commitTx();
            log_.info("merged author:" + tmp);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        
        Author author3 = null;
        try {
            beginTx();
            author3 = dao.get(author.getId());
            commitTx();
            log_.info("got author:" + author3);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        
        assertNotNull(author3);
        assertEquals("updated " + firstName, author3.getFirstName());
        assertEquals("updated " + lastName, author3.getLastName());
        assertEquals("updated " + subject, author3.getSubject());
        assertEquals(new Date(published.getTime()+1000), author3.getPublishDate());
    }
    
    public void testRemove() throws Exception {
        log_.info("testRemove()");

        Author author = new Author(nextId());
        author.setFirstName("test");
        author.setLastName("Remove");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        try {
            beginTx();
            dao.create(author);
            commitTx();
            log_.info("created author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        
        try {
            beginTx();
            dao.remove(author);
            commitTx();
            log_.info("removed author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }

        Author author2=null;
        try {
            beginTx();
            author2 = dao.get(author.getId());
            commitTx();
            log_.info("removed author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            rollbackTx();
            fail("" + ex);
        }
        if (author2 != null) {
            fail("object not deleted");
        }

        
    }

}
