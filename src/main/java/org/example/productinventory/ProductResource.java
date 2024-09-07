package org.example.productinventory;



import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    private ProductService productService;

    @GET
    public Response getAllProducts(@QueryParam("page") @DefaultValue("1") int page,
                                   @QueryParam("size") @DefaultValue("10") int size) {
        List<Product> products = productService.findAll(page, size);
        return Response.ok(products).build();
    }

    @GET
    @Path("/search")
    public Response searchProducts(@QueryParam("name") String name,
                                   @QueryParam("category") String category) {
        List<Product> products = productService.findByNameOrCategory(name, category);
        return Response.ok(products).build();
    }

    @POST
    public Response createProduct(Product product) {
        productService.create(product);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }

    @PUT
    public Response updateProduct(Product product) {
        productService.update(product);
        return Response.ok(product).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        productService.delete(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/stock")
    public Response updateStock(@PathParam("id") Long id, @QueryParam("stock") int stock) {
        productService.updateStock(id, stock);
        return Response.ok().build();
    }
}