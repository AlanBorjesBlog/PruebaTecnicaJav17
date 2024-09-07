# Product Inventory API

Esta es una API para gestionar el inventario de productos.

## Requerimientos

1. Jakarta EE 9
2. Java 17
3. Servidor de aplicaciones (Payara, WildFly, etc.)
4. Base de datos compatible con JDBC

## Configuración

### Conexión a la Base de Datos

La configuración de la conexión a la base de datos se realiza en el archivo `persistence.xml` ubicado en `src/main/resources/META-INF/`. Asegúrate de configurar los detalles de la base de datos correctamente.

#### Ejemplo de `persistence.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence" version="2.0">
    <persistence-unit name="ProductPU">
        <class>org.example.productinventory.Product</class>
        <properties>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:yourdatabaseurl"/>
            <property name="jakarta.persistence.jdbc.user" value="yourusername"/>
            <property name="jakarta.persistence.jdbc.password" value="yourpassword"/>
            <property name="jakarta.persistence.schema-generation.database.action" value="create"/>
        </properties>
    </persistence-unit>
</persistence>
```

### Empaquetar y Desplegar

1. Empaqueta la aplicación:
    ```sh
    mvn clean package
    ```

2. Despliega el archivo `.war` generado en tu servidor de aplicaciones.

3. Accede a los end-points en `http://localhost:8080/api/products`.

## End-points

- **GET /products:** Obtener todos los productos.
- **GET /products/search:** Buscar productos por nombre o categoría.
- **POST /products:** Crear un nuevo producto.
- **PUT /products:** Actualizar un producto.
- **DELETE /products/{id}:** Eliminar un producto.
- **PUT /products/{id}/stock:** Actualizar el stock de un producto.

## Patrones de Diseño Utilizados

El proyecto sigue varios patrones de diseño bien establecidos para asegurar la modularidad, mantenibilidad y robustez del código. Aquí se describen algunos de ellos:

### 1. Patrón de Inyección de Dependencias (Dependency Injection)

Este patrón se utiliza para inyectar dependencias en un objeto, en lugar de depender de que el objeto mismo cree las dependencias. En `ProductResource`, usamos `@Inject` para inyectar `ProductService`.

#### Ejemplo en `ProductResource.java`

```java
@Inject
private ProductService productService;
```

### 2. Patrón de Fachada (Facade)

La clase `ProductService` actúa como una fachada que encapsula la lógica compleja de interacción con la base de datos usando JPA, simplificando el acceso a la base de datos para otras capas de la aplicación.

#### Ejemplo en `ProductService.java`

```java
@Stateless
public class ProductService {
    @PersistenceContext
    private EntityManager em;

    // Métodos CRUD y de búsqueda
}
```

### 3. Patrón de Transferencia de Datos (DTO - Data Transfer Object)

La clase `Product` actúa como un DTO, representando los datos transferidos entre las capas de la aplicación.

#### Ejemplo en `Product.java`

```java
@Entity
@Table(name = "products")
public class Product {
    // Propiedades y métodos getters y setters
}
```

### 4. Patrón de Gestión Transaccional (Transactional Management)

El uso de la anotación `@Transactional` en los métodos del servicio asegura que las operaciones de base de datos se manejen de manera transaccional. Esto asegura que las operaciones sean atómicas y consistentes.

#### Ejemplo en `ProductService.java`

```java
@Transactional
public void create(Product product) {
    em.persist(product);
}

@Transactional
public void update(Product product) {
    em.merge(product);
}

@Transactional
public void delete(Long id) {
    Product product = em.find(Product.class, id);
    if (product != null) {
        em.remove(product);
    }
}

@Transactional
public void updateStock(Long id, int stock) {
    Product product = em.find(Product.class, id);
    if (product != null) {
        product.setStock(stock);
        em.merge(product);
    }
}
```

### 5. Patrón de Restful Resource

La clase `ProductResource` sigue el patrón RESTful Resource, utilizando anotaciones de Jakarta RESTful Web Services (`@Path`, `@GET`, `@POST`, `@PUT`, `@DELETE`) para definir los recursos y sus operaciones.

#### Ejemplo en `ProductResource.java`

```java
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
```

## Conclusión

El proyecto está alineado con varios patrones de diseño bien establecidos, asegurando que la arquitectura sea modular, extensible y mantenible. La combinación de estos patrones contribuye a la robustez y calidad de la aplicación.

Para cualquier duda o problema, por favor contacta al equipo de desarrollo.