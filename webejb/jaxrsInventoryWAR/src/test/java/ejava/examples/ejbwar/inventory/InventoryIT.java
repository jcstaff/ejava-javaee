package ejava.examples.ejbwar.inventory;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;
import ejava.examples.ejbwar.inventory.client.InventoryClient;

/**
 * This class implements a JAX-RS based integration test with the 
 * inventory application deployed to the server.
 */
public class InventoryIT {
	private static final Log log = LogFactory.getLog(InventoryIT.class);
	private InventoryClient inventoryClient;
	
	@Before
	public void setUp() throws Exception {
		InventoryTestConfig config = new InventoryTestConfig("/it.properties");
		log.info("uri=" + config.appURI());
		inventoryClient = config.inventoryClient();
		cleanup();
	}
	
	/**
	 * Remove data from previous tests
	 */
	public void cleanup() throws Exception {
		Categories categories = inventoryClient.findCategoryByName("", 0, 0);
		assertNotNull("error getting categories", categories);
		log.info(String.format("deleting %d categories", categories.getCategories().size()));
		for (Category c: categories.getCategories()) {
			inventoryClient.deleteCategory(c.getId());
		}
		assertEquals("unexpected categories after cleanup", 
				0, 
				inventoryClient.findCategoryByName("", 0, 0).getCategories().size());
		
		Products products = inventoryClient.findProductsByName("", 0, 0);
		assertNotNull("error getting products", categories);
		log.info(String.format("deleting %d products", products.getProducts().size()));
		for (Product p: products.getProducts()) {
			inventoryClient.deleteProduct(p.getId());
		}
		assertEquals("unexpected products after cleanup", 
				0, 
				inventoryClient.findProductsByName("", 0, 0).getProducts().size());
	}

	@Test
	public void testAddInventory() throws Exception {
		log.info("*** testAddInventory() ***");
		
		//create a new product
		Product product = new Product("chips");		
		product = inventoryClient.createProduct(product, "snacks");
		assertNotNull("product not created", product);
		log.info("created product:" + product);
		assertTrue("product primary ket not assigned", product.getId()>0);
		
		//get that product by ID
		Product p2 = inventoryClient.getProduct(product.getId());
		assertNotNull("product not found", p2);
		
		//get the category summary we assigned the product
		Categories categories = inventoryClient.findCategoryByName("snacks",0,0);
		assertNotNull("error getting categories", categories);
		assertEquals("category not found", 1, categories.getCategories().size());
		
		//get the full category
		Category category = inventoryClient.getCategory(categories.getCategories().get(0).getId());
		assertNotNull("unable to find category", category);
		log.info(category);
		
		//verify category has product
		assertEquals("unexpected product count", 1, category.getProductCount());
		assertEquals("unexpected products", 1, category.getProducts().size());
		Product p = category.getProducts().get(0);
		assertEquals("unexpected product name in category", product.getName(), p.getName());
	}
	
	@Test
	public void testUpdateProduct() throws Exception {
		log.info("*** testUpdateProduct ***");
		
		//create a new product
		Product product = new Product("mp3");		
		product = inventoryClient.createProduct(product, "radios");
		assertNotNull("product not created", product);
		log.info("created product:" + product);
		assertTrue("product primary ket not assigned", product.getId()>0);
		
		//update product
		product.setPrice(3.00);
		product.setQuantity(987);
		Product p2 = inventoryClient.updateProduct(product);
		assertNotNull("update failed", p2);
		assertEquals("unexpected price", product.getQuantity(), p2.getQuantity());
		assertEquals("unexpected price", product.getPrice(), p2.getPrice(), .01);
	}

}
