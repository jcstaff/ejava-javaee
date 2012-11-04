package ejava.examples.ejbwar.inventory.ejb;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This class is used to provide resources required by the application
 */
public class InventoryResources {
	@Produces @Named("inventory")
	@PersistenceContext(unitName="webejb-inventory")
	public EntityManager inventoryEM;
}
