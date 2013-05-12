package myorg.queryex;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;


public class QueryBase {
    private static Log log = LogFactory.getLog(QueryBase.class);
    private static final String PERSISTENCE_UNIT = "queryEx-test";
    private static EntityManagerFactory emf;
    protected EntityManager em;    

    @BeforeClass
    public static void setUpClass() {
        log.debug("creating entity manager factory");
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUp() throws Exception {
        log.debug("creating entity manager");
        em = emf.createEntityManager();
        cleanup();
        populate();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        try {
            log.debug("tearDown() started, em=" + em);
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                em.getTransaction().commit();            
            } else if (!em.getTransaction().getRollbackOnly()) {
                em.getTransaction().commit();                        	
            } else {
            	em.getTransaction().rollback();
            }
            em.close();
            log.debug("tearDown() complete, em=" + em);
        }
        catch (Exception ex) {
            log.fatal("tearDown failed", ex);
            throw ex;
        }
     }
    
    @AfterClass
    public static void tearDownClass() {
        log.debug("closing entity manager factory");
        if (emf!=null) { emf.close(); }
    }
    
    public void cleanup() {
    }
    
    public void populate() {
    	em.getTransaction().begin();
    	new MovieFactory().setEntityManager(em).populate();
    	em.getTransaction().commit();
    }
}
