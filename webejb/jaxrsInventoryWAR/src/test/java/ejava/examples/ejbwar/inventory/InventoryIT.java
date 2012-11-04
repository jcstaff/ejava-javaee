package ejava.examples.ejbwar.inventory;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import ejava.examples.ejbwar.inventory.client.InventoryClient;


public class InventoryIT {
	private static final Log log = LogFactory.getLog(InventoryIT.class);
	private InventoryClient inventoryClient;
	
	@Before
	public void setUp() throws IOException {
		InventoryTestConfig config = new InventoryTestConfig("/it.properties");
		log.info("uri=" + config.appURI());
		inventoryClient = config.inventoryClient();
	}

	@Test
	public void testAddInventory() throws Exception {
		log.info("*** testAddInventory() ***");
		
		int startCount=inventoryClient.getCategories(0, 0).size();
		log.info("starting category count="+ startCount);
	}

}
