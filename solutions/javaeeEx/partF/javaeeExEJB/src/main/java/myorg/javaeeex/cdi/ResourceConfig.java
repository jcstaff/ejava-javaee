package myorg.javaeeex.cdi;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
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
}
