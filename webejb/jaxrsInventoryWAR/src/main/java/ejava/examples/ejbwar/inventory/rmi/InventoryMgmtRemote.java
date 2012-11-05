package ejava.examples.ejbwar.inventory.rmi;

import javax.ejb.Remote;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;

@Remote
public interface InventoryMgmtRemote {
	Categories findCategoryByName(String name, int offset, int limit);

	void deleteCategory(int id);

	Products findProductByName(String name, int offset, int limit);

	void deleteProduct(int id);

	Product createProduct(Product product, String category);

	Product getProduct(int id);

	Category getCategory(int id);

	Product updateProduct(Product product);
}
