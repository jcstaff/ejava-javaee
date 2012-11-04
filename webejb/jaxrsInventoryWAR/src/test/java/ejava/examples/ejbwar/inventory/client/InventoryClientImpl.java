package ejava.examples.ejbwar.inventory.client;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.InventoryRepresentation;
import ejava.examples.ejbwar.inventory.rs.Application;
import ejava.examples.ejbwar.inventory.rs.CategoriesResource;

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
	
	public void init() {
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Category> getCategories(int offset, int limit) throws Exception {
		URI uri = UriBuilder.fromUri(appURI)
			.path("rest")
			.path(CategoriesResource.class)
			.path(CategoriesResource.class,"getCategories")
			.build(offset, limit);
		
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return (List<Category>)InventoryRepresentation.unmarshall(Category.class,
					response.getEntity().getContent());
		}
		return null;
	}
}
