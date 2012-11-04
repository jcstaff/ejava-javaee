package ejava.examples.ejbwar.inventory.rs;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.ejb.InventoryMgmtEJB;

@Path("/products")
public class ProductsResource {
	private static final Log log = LogFactory.getLog(ProductsResource.class);
	@Inject
	private InventoryMgmtEJB ejb;
	@Context
	private Request request;
	@Context 
	private UriInfo uriInfo;

	/**
	 * Creates a product
	 * @param product
	 * @param category
	 * @return
	 */
	@POST @Path("")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_XML)
	public Response createProduct(
			@FormParam("name") String name,
			@FormParam("quantity") Integer quantity,
			@FormParam("price") Double price,
			@FormParam("category") String category) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));

		try {
			Product product = new Product(name, quantity, price);
			Product p = ejb.addProduct(product, category);
			return Response.ok(p)
				.tag("" + p.getVersion())
				.build();
		} catch (Exception ex) {
			return ResourceHelper.serverError(log, "creating product", ex).build();
		}
	}
	
	/**
	 * Returns a specific product
	 * @param id
	 * @return
	 */
	@GET @Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getProduct(@PathParam("id")int id) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));

		try {
			Product product = ejb.getProduct(id);
			if (product != null) {
				return Response.ok(product)
						.tag("" + product.getVersion())
						.build();
			}
			else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity(String.format("unable to locate product %d", id))
						.type(MediaType.TEXT_PLAIN)
						.build();
			}
		} catch (Exception ex) {
			return ResourceHelper.serverError(log, "getting product", ex).build();
		}
	}

	@PUT @Path("{id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response updateProduct(@PathParam("id") int id, Product product) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));

		try {
			Product p = ejb.updateProduct(product);
			return Response.ok(p)
					.tag("" + p.getVersion())
					.build();
		} catch (Exception ex) {
			return ResourceHelper.serverError(log, "update product", ex).build();
		}
	}

	/**
	 * Deletes the product identified.
	 * @param id
	 * @return
	 */
	@DELETE @Path("{id}")
	public Response deleteProduct(@PathParam("id")int id) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));

		try {
			Product product = ejb.getProduct(id);
			if (product != null) {
				try {
					ejb.deleteProduct(product);
					return Response.noContent().build();
				} catch (Exception ex) {
					return ResourceHelper.serverError(log, "deleting product", ex).build();
				}
			}
			else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity(String.format("unable to locate product %d", id))
						.type(MediaType.TEXT_PLAIN)
						.build();
			}
		} catch (Exception ex) {
			return ResourceHelper.serverError(log, "getting product", ex).build();
		}
	}

	/**
	 * Returns a simple list of products that match provided name
	 * @param name
	 * @return
	 */
	@GET @Path("")
	@Produces(MediaType.APPLICATION_XML)
	public Response findProductsByName(@QueryParam("name")String name) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));

		try {
			List<Product> products = ejb.findProductByName(name);
			if (products.size() > 0) {
				return Response.ok(new GenericEntity<List<Product>>(products){})
						.build();
			}
			else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity(String.format("unable to locate product %s", name))
						.type(MediaType.TEXT_PLAIN)
						.build();
			}
		} catch (Exception ex) {
			return ResourceHelper.serverError(log, "getting product", ex).build();
		}
	}
	
}
