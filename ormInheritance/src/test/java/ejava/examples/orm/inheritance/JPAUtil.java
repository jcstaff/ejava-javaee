package ejava.examples.orm.inheritance;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to create and hold open EntityManagerFactory objects 
 * when not running within a Java EE environment.
 *
 * @author jcstaff
 */
public class JPAUtil {
    private static final Log log = LogFactory.getLog(JPAUtil.class);
    private static final Map<String, EntityManagerFactory> factories = 
        new HashMap<String, EntityManagerFactory>();
    
    /**
     * This method will create or return the EntityManagerFactory for 
     * the specified persistence unit.
     * 
     * @param puName matches value in persistence.xml
     * @return
     */
    public static EntityManagerFactory getEntityManagerFactory(String puName) {
        EntityManagerFactory emf = factories.get(puName);
        if (emf == null) {
            synchronized(factories) {
                emf = factories.get(puName);
                if (emf == null) {
                    log.debug("creating EntityManagerFactory(" + puName + ")");
                    emf = Persistence.createEntityManagerFactory(puName);
                    factories.put(puName, emf);
                }
            }
        }
        return emf;
    }
    
    /**
     * This method closes all EntityManagerFactory objects and clears them
     * from the cache.
     * 
     */
    public static void close() {
        log.debug("closing " + factories.size() + " EntityManagerFactories");
        synchronized(factories) {
            for(String puName : factories.keySet()) {
                factories.get(puName).close();
                log.debug(puName + " closed");
            }
            factories.clear();
        }
    }
}
