package ejava.examples.ejbwar.inventory.client;

import java.util.List;

import ejava.examples.ejbwar.inventory.bo.Category;

public interface InventoryClient {
	List<Category> getCategories(int offset, int limit) throws Exception;
}
