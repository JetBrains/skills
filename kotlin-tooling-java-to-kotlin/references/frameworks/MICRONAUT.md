# Micronaut Conversion Guide

## When This Applies

This guide applies when the Java source contains imports matching `io.micronaut.*`.
This covers Micronaut HTTP, Micronaut Data, and Micronaut Security.

## Key Rules

### 1. Constructor injection is the default

Micronaut uses compile-time dependency injection via constructor injection by default.
This maps naturally to Kotlin's primary constructor. Remove `@Inject` when there is
only one constructor — Micronaut discovers it automatically.

### 2. Stereotype annotations

`@Singleton`, `@Controller`, `@Client`, `@Repository` — preserve these exactly.
No annotation site target is needed.

### 3. @Value annotation

Escape `$` in Kotlin to prevent string template interpretation:

```kotlin
@Value("\${config.key}") val configKey: String
```

### 4. @Inject field injection → constructor injection

Replace `@Inject` on fields with constructor parameters in Kotlin's primary constructor.
This eliminates `lateinit var` and makes dependencies immutable.

### 5. AOP interceptors require open classes

Classes using AOP annotations (`@Around`, `@Introduction`, `@Cacheable`) must be `open`
in Kotlin because Micronaut generates subclass proxies for them at compile time.

### 6. Bean factories

`@Factory` classes and their `@Bean`-annotated methods should be `open` so Micronaut
can manage their lifecycle through subclassing.

### 7. @ConfigurationProperties

Convert to a class with mutable properties. Use `lateinit var` for required `String`
properties and `var` with defaults for primitives. The class must be `open`.

---

## Examples

### Example 1: Micronaut Controller with Constructor Injection

**Java:**

```java
package com.acme.web;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import jakarta.inject.Inject;

@Controller("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final InventoryClient inventoryClient;

    @Inject
    public OrderController(OrderService orderService, InventoryClient inventoryClient) {
        this.orderService = orderService;
        this.inventoryClient = inventoryClient;
    }

    @Get("/{id}")
    public OrderDto getOrder(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @Get
    public List<OrderDto> listOrders() {
        return orderService.findAll();
    }
}
```

**Kotlin:**

```kotlin
package com.acme.web

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable

@Controller("/api/orders")
class OrderController(
    private val orderService: OrderService,
    private val inventoryClient: InventoryClient
) {

    @Get("/{id}")
    fun getOrder(@PathVariable id: Long): OrderDto? {
        return orderService.findById(id)
    }

    @Get
    fun listOrders(): List<OrderDto> {
        return orderService.findAll()
    }
}
```

Key changes:
- `@Inject` is removed — Micronaut auto-discovers the single constructor.
- The `jakarta.inject.Inject` import is removed because it is no longer referenced.
- Constructor parameters become `private val` in the primary constructor.
- Return type `OrderDto` becomes `OrderDto?` where the service may return null.
