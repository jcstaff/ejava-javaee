package ejava.examples.daoex.jpa;

import javax.persistence.EntityManager;

public class JPADAOBase {
    private static ThreadLocal<EntityManager> em = 
        new ThreadLocal<EntityManager>();
    
    public static void setEntityManager(EntityManager mgr) {
        em.set(mgr);
    }
    
    protected EntityManager getEntityManager() {
        return em.get();
    }
}
