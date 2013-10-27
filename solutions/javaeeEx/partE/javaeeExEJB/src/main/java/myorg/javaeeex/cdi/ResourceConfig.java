package myorg.javaeeex.cdi;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This class will be used to define a mapping between the dependency 
 * injection solutions and their source within the container.
 */
public class ResourceConfig {

    @Produces @JavaeeEx
    @PersistenceContext(unitName="javaeeEx")
    public EntityManager em;

    //this is a second option for an EntityManager to produce a bean that 
    //requires inputs and manipulation
    @Produces //@JavaeeEx2
    public EntityManager getEntityManager(@JavaeeEx EntityManager em2) {
    	em2.setProperty("foo", "bar");
    	return em2;
    }
}
