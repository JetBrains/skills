# Quarkus Conversion Guide

## When This Applies

This guide applies when the Java source contains imports matching `io.quarkus.*`,
`javax.enterprise.*`, or `jakarta.enterprise.*`. This covers Quarkus REST, Quarkus CDI,
and Panache ORM.

## Key Rules

### 1. CDI beans need a no-arg constructor

The CDI specification requires beans to have a no-arg constructor (package-private or
public). In Kotlin, satisfy this by giving all constructor parameters default values,
or by adding a secondary no-arg constructor.

### 2. Scope annotations

`@ApplicationScoped`, `@RequestScoped`, `@Dependent` — preserve these exactly.
Beans with these scopes must have a no-arg constructor accessible to CDI.

### 3. @Inject field injection → constructor injection

Replace `@Inject` on fields with an `@Inject`-annotated primary constructor in Kotlin.
CDI requires the `@Inject` annotation on the constructor when multiple constructors
exist. With a single constructor, Quarkus discovers it automatically.

### 4. REST endpoint annotations

`@Path`, `@GET`, `@POST`, `@PUT`, `@DELETE`, `@Produces`, `@Consumes` — preserve
these exactly. No annotation site target is needed.

### 5. Panache entities

Panache entities must remain `open` — do NOT use `data class`. Extend `PanacheEntity`
(auto-generated Long ID) or `PanacheEntityBase` (custom ID type). Keep fields as
`open` mutable properties because Panache enhances field access at build time.

### 6. @ConfigProperty

Use on constructor parameters with a default value to satisfy CDI's no-arg
constructor requirement:

```kotlin
@ConfigProperty(name = "app.greeting") val greeting: String = ""
```

---

## Examples

### Example 1: REST Resource with CDI Injection

**Java:**

```java
package com.acme.web;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/products")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @Inject
    PricingService pricingService;

    @GET
    public List<ProductDto> listProducts() {
        return productService.findAll();
    }

    @GET
    @Path("/{id}")
    public ProductDto getProduct(@PathParam("id") Long id) {
        return productService.findById(id);
    }
}
```

**Kotlin:**

```kotlin
package com.acme.web

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/api/products")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
class ProductResource @Inject constructor(
    private val productService: ProductService,
    private val pricingService: PricingService
) {

    // No-arg constructor required by CDI — default values satisfy this
    constructor() : this(
        productService = ProductService(),
        pricingService = PricingService()
    )

    @GET
    fun listProducts(): List<ProductDto> {
        return productService.findAll()
    }

    @GET
    @Path("/{id}")
    fun getProduct(@PathParam("id") id: Long): ProductDto? {
        return productService.findById(id)
    }
}
```

Key changes:
- `@Inject` field injection is replaced by an `@Inject`-annotated primary constructor.
- A secondary no-arg constructor is added to satisfy the CDI specification. In practice,
  CDI will use the `@Inject` constructor — the no-arg constructor exists only to pass
  validation.
- Constructor parameters become `private val` in the primary constructor.
- Return type `ProductDto` becomes `ProductDto?` where the service may return null.
