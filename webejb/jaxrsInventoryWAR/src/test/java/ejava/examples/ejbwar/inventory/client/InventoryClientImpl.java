package ejava.examples.ejbwar.inventory.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.InventoryRepresentation;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;
import ejava.examples.ejbwar.inventory.rs.CategoriesResource;
import ejava.examples.ejbwar.inventory.rs.ProductsResource;

/**
 * This class implements an HTTP Client interface to the inventory 
 * web application. All commands are through HTTP POST, GET, PUT, and DELETE
 * methods to specific resource URIs for products and categories.
 */
public class InventoryClientImpl implements InventoryClient {
	private static final Log log = LogFactory.getLog(InventoryClientImpl.class);
	private HttpClient client;
	private URI appURI;

	public void setHttpClient(HttpClient client) {
		this.client = client;
	}
	public void setAppURI(URI appURI) {
		this.appURI = appURI;
	}	
	
	@Override
	public Categories findCategoryByName(String name, int offset, int limit) throws Exception {
		URI uri = UriBuilder.fromUri(appURI)
				.path("rest")
				.path(CategoriesResource.class)
				.path(CategoriesResource.class,"findCategoriesByName")
				.build(name, offset, limit);
			
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Categories.class,
					response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public Category getCategory(int id) throws Exception {
		URI uri = UriBuilder.fromUri(appURI)
				.path("rest")
				.path(CategoriesResource.class)
				.path(CategoriesResource.class,"getCategory")
				.build(id);
			
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Category.class,
					response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public boolean deleteCategory(int id) throws Exception {
		URI uri = UriBuilder.fromUri(appURI)
				.path("rest")
				.path(CategoriesResource.class)
				.path(CategoriesResource.class,"deleteCategory")
				.build(id);
			
		HttpDelete delete = new HttpDelete(uri);
		HttpResponse response = client.execute(delete);
		log.info(String.format("%s %s", delete.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return true;
		}
		return false;
	}
	
	@Override
	public Product createProduct(Product product, String categoryName) 
		throws Exception {
		URI uri = UriBuilder.fromUri(appURI)
				.path("rest")
				.path(ProductsResource.class)
				.path(ProductsResource.class,"createProduct")
				.build();

		//build the form data with the request parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", product.getName()));
		params.add(new BasicNameValuePair("category", categoryName));
		if (product.getQuantity()!=null) {
			params.add(new BasicNameValuePair("quantity", product.getQuantity().toString()));
		}
		if (product.getPrice() != null) {
			params.add(new BasicNameValuePair("price", product.getPrice().toString()));
		}

		//create the request
		HttpPost post = new HttpPost(uri);
		post.addHeader(HttpHeaders.CONTENT_ENCODING, MediaType.APPLICATION_FORM_URLENCODED);
		post.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		post.setEntity(new UrlEncodedFormEntity(params));
			
		//issue the request and check the response
		HttpResponse response = client.execute(post);
		log.info(String.format("%s %s", post.getURI(), response));
		if (Response.Status.CREATED.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Product.class,response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public Products findProductByName(String name, int offset, int limit) throws Exception {
		URI uri = UriBuilder.fromUri(appURI)
				.path("rest")
				.path(ProductsResource.class)
				.path(ProductsResource.class,"findProductsByName")
				.build(name, offset, limit);
			
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Products.class,
					response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public Product getProduct(int id) throws Exception {
		URI uri = UriBuilder.fromUri(appURI)
				.path("rest")
				.path(ProductsResource.class)
				.path(ProductsResource.class,"getProduct")
				.build(id);
			
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);		
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Product.class,
					response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public Product updateProduct(Product product) throws Exception {
		URI uri = UriBuilder.fromUri(appURI)
				.path("rest")
				.path(ProductsResource.class)
				.path(ProductsResource.class,"updateProduct")
				.build(product.getId());
			
		HttpPut put = new HttpPut(uri);
		put.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
		put.setEntity(new StringEntity(product.toString(), "UTF-8"));
		HttpResponse response = client.execute(put);
		log.info(String.format("%s %s", put.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Product.class,
					response.getEntity().getContent());
		}
		return null;
	}

	@Override
	public boolean deleteProduct(int id) throws Exception {
		URI uri = UriBuilder.fromUri(appURI)
				.path("rest")
				.path(ProductsResource.class)
				.path(ProductsResource.class,"deleteProduct")
				.build(id);
			
		HttpDelete delete = new HttpDelete(uri);
		HttpResponse response = client.execute(delete);
		log.info(String.format("%s %s", delete.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return true;
		}
		return false;
	}
}
