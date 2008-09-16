package ejava.examples.dao.jpa;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ejava.examples.dao.domain.Author;
import junit.framework.TestCase;

/**
 * This class provides an exploded view of the DAO operations for clarify
 * of the Java Persistence API, but at the risk of obscuring the use of a 
 * DAO. DAOs are still useful and necessary when using JPA. This unit test
 * is just an early demo of the mechanics of the API, NOT the architecture 
 * that should surround the use of the API.
 * 
 * @author jcstaff
 */
public class JPANoDAODemo extends TestCase {
    private static Log log_ = LogFactory.getLog(JPANoDAODemo.class);
    private EntityManager em;    
        
    public void setUp() throws Exception {
        log_.debug("setUp() started, em=" + em);
        EntityManagerFactory emf = 
            JPAUtil.getEntityManagerFactory("jpaDemo");
        em = emf.createEntityManager();
    }
    
    public void tearDown() throws Exception {
        try {
            log_.debug("tearDown() started, em=" + em);
            em.getTransaction().begin();
            em.flush();
            em.getTransaction().commit();
            em.close();
            log_.debug("tearDown() complete, em=" + em);
        }
        catch (Exception ex) {
            log_.fatal("tearDown failed", ex);
            throw ex;
        }
    }
    
    
    /**
     * This test verifies we can persist an entity.
     */
    public void testCreate() throws Exception {
        log_.info("testCreate()");
        Author author = new Author();
        author.setFirstName("dr");
        author.setLastName("seuss");
        author.setSubject("children");
        author.setPublishDate(new Date());
        log_.info("creating author:" + author);

        //entity managers with extended persistence contexts can be called
        //outside of a transaction
        em.persist(author);
        log_.info("created author:" + author);        
    }
    

    /**
     * This test verifies the ability of the DAO to get an object from the 
     * database.
     * @throws Exception
     */
    public void testGet() throws Exception {
        log_.info("testGet()");
        Author author = new Author();
        author.setFirstName("thing");
        author.setLastName("one");
        author.setSubject("children");
        author.setPublishDate(new Date());
        
        log_.info("creating author:" + author);
        em.persist(author);
        log_.info("created author:" + author);        

        Author author2=null;
        author2 = em.find(Author.class, author.getId());
        log_.info("got author author:" + author2);

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
        
        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("Query");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        
        log_.info("creating author:" + author);
        em.persist(author);
        
        //need to associate em with Tx to allow query to see entity in DB
        try {
            em.getTransaction().begin();
            //note that the persist does not have to be within the tx
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            log_.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }

      
        Author author2 = null;
        try {
            Query query = em.createQuery(
                    "from jpaAuthor where id=" + author.getId());
            author2 = (Author)query.getSingleResult();
            log_.info("got author:" + author2);
        }
        catch (Exception ex) {
            log_.fatal(ex);
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
        
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setSubject(subject);
        author.setPublishDate(published);        
        em.persist(author);                
        assertTrue("author is not managed", em.contains(author));
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        //make sure we have our author in the DB but not currently 
        //managed by the entity manager
        em.clear();
        assertFalse("author is still managed", em.contains(author));        
        
        //now make some changes to our baseline object
        author.setFirstName("updated " + firstName);
        author.setLastName("updated " + lastName);
        author.setSubject("updated " + subject);
        author.setPublishDate(new Date(published.getTime()+ 1000));
        
        //show that the DB has not been updated
        Author dbAuthor = em.find(Author.class,author.getId());
        assertFalse("unexpected first name", 
                author.getFirstName().equals(dbAuthor.getFirstName()));
        assertFalse("unexpected last name", 
                author.getLastName().equals(dbAuthor.getLastName()));
        assertFalse("unexpected subject", 
                author.getFirstName().equals(dbAuthor.getSubject()));
        assertFalse("unexpected publish date", 
                author.getFirstName().equals(dbAuthor.getPublishDate()));        
        
        try {
            //example of using setter methods to make changes in DB
            dbAuthor = em.find(Author.class, author.getId());

            dbAuthor.setFirstName(author.getFirstName());
            dbAuthor.setLastName(author.getLastName());
            dbAuthor.setSubject(author.getSubject());
            dbAuthor.setPublishDate(author.getPublishDate());
            
            em.getTransaction().begin();
            em.getTransaction().commit();
            log_.info("updated author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            fail("" + ex);
        }
        
        //now verify changes were made to DB
        Author author2 = null;
        try {
            author2 = em.find(Author.class, author.getId());
            log_.info("got author:" + author2);
        }
        catch (Exception ex) {
            log_.fatal(ex);
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
        
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setSubject(subject);
        author.setPublishDate(published);        
        try {
            em.getTransaction().begin();
            em.persist(author);
            em.flush();
            em.getTransaction().commit();
            em.clear();
            log_.info("created author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        //create a new object with the same primary key as the one in the DB
        Author author2 = new Author(author.getId());
        author2.setFirstName("updated " + author.getFirstName());
        author2.setLastName("updated " + author.getLastName());
        author2.setSubject("updated " + author.getSubject());
        author2.setPublishDate(new Date(published.getTime()+ 1000));
        try {
            log_.info("merging with author:" + author2);
            Author tmp = em.merge(author2);
            em.getTransaction().begin();
            em.getTransaction().commit();
            log_.info("merged author:" + tmp);
            assertFalse("author2 is managed", em.contains(author2));
            assertTrue("tmp Author is not managed", em.contains(tmp));
        }
        catch (Exception ex) {
            log_.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        //verify our changes were made to the DB
        Author author3 = null;
        try {
            author3 = em.find(Author.class, author.getId());
            log_.info("got author:" + author3);
        }
        catch (Exception ex) {
            log_.fatal(ex);
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

        Author author = new Author();
        author.setFirstName("test");
        author.setLastName("Remove");
        author.setSubject("testing");
        author.setPublishDate(new Date());
        try {
            em.getTransaction().begin();
            em.persist(author);
            em.getTransaction().commit();
            log_.info("created author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }
        
        try {
            em.remove(author); //remove doesn't happen until tx
            em.getTransaction().begin();
            em.getTransaction().commit();
            log_.info("removed author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            em.getTransaction().rollback();
            fail("" + ex);
        }

        Author author2=null;
        try {
            author2 = em.find(Author.class, author.getId());
            log_.info("removed author:" + author);
        }
        catch (Exception ex) {
            log_.fatal(ex);
            fail("" + ex);
        }
        if (author2 != null) {
            fail("object not deleted");
        }        
    }

}
