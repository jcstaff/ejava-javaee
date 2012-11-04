package ejava.examples.ejbwar.inventory.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.dao.InventoryDAO;

@Stateless
public class InventoryMgmtEJB {
	@Inject
	private InventoryDAO dao;
	
	/**
	 * Returns the name and identity of a category without the associated 
	 * products.
	 * @param offset
	 * @param limit
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Category> getCategories(int offset, int limit) {
		List<Category> categories = dao.findCategoryByName("%", offset, limit);
		for (Category category : categories) {
			category.setProductCount(categories.size());
			dao.detachCategory(category); //detach before manipulating collection 
			category.setProducts(null);
		}
		return categories;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Product addProduct(Product product, String categoryName) {
		dao.addProduct(product);
		Category category = createOrGetCategory(categoryName);
		category.getProducts().add(product);
		return product;
	}
	
	public Product updateProduct(Product product) {
		return dao.updateProduct(product);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Category createOrGetCategory(String name) {
		List<Category> categories = dao.findCategoryByName(name, 0, 1); 
		if (categories.size()==0) {
			Category category=new Category(name);
			dao.createCategory(category);
			return category;
		}
		else {
			return categories.get(0);
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Category getCategory(int id) {
		return dao.getCategory(id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Product getProduct(int id) {
		return dao.getProduct(id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Product> findProductByName(String name) {
		return dao.findProductsByName(name, 0, 5);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteProduct(Product product) {
		dao.deleteProduct(product);
	}
}
