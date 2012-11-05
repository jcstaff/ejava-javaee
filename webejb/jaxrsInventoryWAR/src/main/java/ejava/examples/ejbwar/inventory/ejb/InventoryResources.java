package ejava.examples.ejbwar.inventory.ejb;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This class is used to provide resources required by the application.
 * We must have a Web-INF/beans.xml file in place in the WAR to enable CDI.
 */
public class InventoryResources {
	@Produces @Named("inventory")
	@PersistenceContext(unitName="webejb-inventory")
	public EntityManager inventoryEM;
}
