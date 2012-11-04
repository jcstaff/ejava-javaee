package ejava.examples.ejbwar.inventory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.client.InventoryClient;
import ejava.examples.ejbwar.inventory.client.InventoryClientImpl;
import ejava.examples.ejbwar.inventory.rs.ProductsResource;

public class InventoryTestConfig {
	private HttpClient httpClient;
	private URI appURI;
	private InventoryClient inventoryClient;
	
	private Properties props = new Properties(); 
	public InventoryTestConfig(String resource) throws IOException {
		InputStream is = getClass().getResourceAsStream(resource);
		if (is!=null) {
			try {
				props.load(is);
			} finally {
				is.close();
			}
		}
	}
	
	public HttpClient httpClient() {
		if (httpClient==null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

	/**
	 * Return the base URI to the application
	 * @return
	 */
	public URI appURI() {
		if (appURI==null) {
			try {
				String host=props.getProperty("host", "localhost");
				int port=Integer.parseInt(props.getProperty("port", "8080"));
				String path=props.getProperty("servletContext", "/");
				URL url=new URL("http", host, port, path);
				appURI = url.toURI();
			} catch (MalformedURLException ex) {
				throw new RuntimeException("error creating URL:" + ex, ex);
			} catch (URISyntaxException ex) {
				throw new RuntimeException("error creating URI:" + ex, ex);
			} finally {}
		}
		return appURI;
	}
	
	public InventoryClient inventoryClient() {
		if (inventoryClient==null) {
			InventoryClient client = new InventoryClientImpl();
			((InventoryClientImpl)client).setHttpClient(httpClient());
			((InventoryClientImpl)client).setAppURI(appURI());
			inventoryClient = client;
		}
		return inventoryClient;
	}
}
