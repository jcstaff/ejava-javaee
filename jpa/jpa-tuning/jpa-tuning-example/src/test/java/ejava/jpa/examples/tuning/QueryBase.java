package ejava.jpa.examples.tuning;

import javax.persistence.EntityManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;


public class QueryBase {
    private static Log log = LogFactory.getLog(QueryBase.class);
    private static final String PERSISTENCE_UNIT = "queryEx-test";
    protected static EntityManagerFactory emf;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        EntityManager em1 = emf.createEntityManager();
        cleanup(em1);
        populate(em1);
    }
    
    @AfterClass
    public static void tearDownClass() {
        log.trace("closing entity manager factory");
        if (emf!=null) { emf.close();  emf=null; }
    }
    
    public static void cleanup(EntityManager em) {
    	em.getTransaction().begin();
    	em.getTransaction().commit();
    }
    
    public static void populate(EntityManager em) {
    	em.getTransaction().begin();
    	new MovieFactory().setEntityManager(em).populate();
    	em.getTransaction().commit();
    }
}
