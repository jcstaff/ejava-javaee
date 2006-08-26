package ejava.examples.dao.jpa;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JPADAOBase {
    private static Log log_ = LogFactory.getLog(JPADAOBase.class);    
    private static ThreadLocal<EntityManager> em = new ThreadLocal<EntityManager>();
    
    public static void setEntityManager(EntityManager mgr) {
        em.set(mgr);
    }
    
    protected EntityManager getEntityManager() {
        return em.get();
    }
}
