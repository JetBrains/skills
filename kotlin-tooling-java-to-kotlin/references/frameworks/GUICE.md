# Guice Conversion Guide

## When This Applies

This guide applies when the Java source contains imports matching `com.google.inject.*`.
This covers Google Guice core, Guice multibindings, and Guice servlet.

## Key Rules

### 1. @Inject constructor syntax

Kotlin places `@Inject` before the `constructor` keyword in the primary constructor:

```kotlin
class Foo @Inject constructor(private val bar: Bar)
```

### 2. @Provides methods in Modules

Keep `@Provides` methods as regular functions. Guice modules extend `AbstractModule`,
so override `configure()` as usual.

### 3. Module.configure() override

Override `configure()` in Kotlin. Use Guice's binding DSL with Kotlin class references:

```kotlin
bind(Foo::class.java).to(FooImpl::class.java)
```

### 4. @Named qualifier — annotation site targets

In Kotlin, `@Named` on constructor parameters needs a site target to reach the
parameter (not the field or property). Use `@param:Named` for constructor injection:

```kotlin
class Foo @Inject constructor(
    @param:Named("primary") private val dataSource: DataSource
)
```

When used on function parameters (e.g., in `@Provides` methods), no site target
is needed.

### 5. @Singleton scope

Preserve `@Singleton` exactly. It can be placed on the class declaration or in
module bindings via `.in(Singleton::class.java)`.

### 6. Provider<T>

`Provider<T>` can stay as-is for lazy or scoped injection. Where the only purpose
is deferred initialization, Kotlin's `lazy` delegation can be used as an alternative
outside of Guice-managed contexts.

---

## Examples

### Example 1: Guice Module with Bindings and an Injected Class

**Java:**

```java
package com.acme.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CacheService.class).to(RedisCacheService.class);
        bind(NotificationService.class).to(EmailNotificationService.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public HttpClient provideHttpClient(@Named("baseUrl") String baseUrl) {
        return new HttpClient(baseUrl);
    }
}
```

```java
package com.acme.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class OrderService {

    private final CacheService cacheService;
    private final HttpClient httpClient;
    private final String region;

    @Inject
    public OrderService(CacheService cacheService, HttpClient httpClient, @Named("region") String region) {
        this.cacheService = cacheService;
        this.httpClient = httpClient;
        this.region = region;
    }

    public Order findById(Long id) {
        return cacheService.getOrFetch(id, () -> httpClient.get("/orders/" + id, Order.class));
    }
}
```

**Kotlin:**

```kotlin
package com.acme.config

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.name.Named

class AppModule : AbstractModule() {

    override fun configure() {
        bind(CacheService::class.java).to(RedisCacheService::class.java)
        bind(NotificationService::class.java).to(EmailNotificationService::class.java).`in`(Singleton::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpClient(@Named("baseUrl") baseUrl: String): HttpClient {
        return HttpClient(baseUrl)
    }
}
```

```kotlin
package com.acme.service

import com.google.inject.Inject
import com.google.inject.name.Named

class OrderService @Inject constructor(
    private val cacheService: CacheService,
    private val httpClient: HttpClient,
    @param:Named("region") private val region: String
) {

    fun findById(id: Long): Order? {
        return cacheService.getOrFetch(id) { httpClient.get("/orders/$id", Order::class.java) }
    }
}
```

Key changes:
- `@Inject` moves before the `constructor` keyword in the primary constructor.
- Constructor parameters become `private val` in the primary constructor.
- `@Named("region")` uses `@param:Named` site target so the annotation reaches the
  constructor parameter rather than the Kotlin property.
- `.in(Singleton.class)` becomes `` .`in`(Singleton::class.java) `` — `in` is a
  reserved keyword in Kotlin and must be escaped with backticks.
- The lambda in `getOrFetch` uses Kotlin's trailing lambda syntax instead of an
  anonymous inner class.
- String concatenation `"/orders/" + id` becomes a string template `"/orders/$id"`.
