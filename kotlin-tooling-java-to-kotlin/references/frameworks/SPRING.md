# Spring Framework Conversion Guide

## When This Applies

This guide applies when the Java source contains imports matching `org.springframework.*`.
This covers Spring Boot, Spring MVC, Spring Data, and Spring Security.

## Key Rules

### 1. SpringApplication.run — spread CLI args

In Kotlin, `String[]` varargs must be spread with the `*` operator.

- Java: `SpringApplication.run(App.class, args);`
- Kotlin: `SpringApplication.run(App::class.java, *args)`

### 2. Constructor injection over @Autowired

Kotlin's primary constructor makes constructor injection natural. When a class has a
single constructor, Spring auto-discovers it — remove `@Autowired`.

### 3. Stereotype annotations

`@Component`, `@Service`, `@RestController`, and `@Repository` target the class.
Preserve these annotations exactly. No annotation site target is needed.

### 4. @Value annotation

Use `@Value` on constructor parameters. Escape `$` in SpEL expressions to prevent
Kotlin string template interpretation:

```kotlin
@Value("\${app.name}") val appName: String
```

### 5. @ConfigurationProperties

Convert to a `data class` only if the properties are immutable. For mutable
configuration, use a regular class with `lateinit var`.

### 6. Spring Data repositories

Interface declarations convert directly. Replace `Optional<T>` return types with
nullable `T?` in Kotlin for idiomatic usage.

### 7. @RequestMapping / @GetMapping / @PostMapping etc.

Preserve exactly. Where Java uses array initializer syntax for annotation parameters,
use `arrayOf()` in Kotlin.

### 8. @Transactional

Preserve exactly. The class must remain `open` because Spring creates proxies via
subclassing. Do not make `@Transactional` classes `final`.

### 9. @Bean methods in @Configuration classes

`@Bean` methods must be `open` so that Spring can override them in CGLIB proxies.
Alternatively, apply the `allopen` compiler plugin with a Spring preset, which makes
annotated classes and their members open automatically.

---

## Examples

### Example 1: Spring Boot Application Main Class

**Java:**

```java
package com.acme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Kotlin:**

```kotlin
package com.acme

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
```

Key changes:
- `main` becomes a top-level function (no companion object needed).
- `runApplication<T>` is a Spring Boot Kotlin extension that replaces
  `SpringApplication.run(T::class.java, *args)`.
- The `*args` spread operator is required for the varargs parameter.

---

### Example 2: REST Controller with Constructor Injection

**Java:**

```java
package com.acme.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }
}
```

**Kotlin:**

```kotlin
package com.acme.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserDto? {
        return userService.findById(id)
    }

    @GetMapping
    fun getAllUsers(): List<UserDto> {
        return userService.findAll()
    }
}
```

Key changes:
- `@Autowired` is removed — Spring auto-discovers the single constructor.
- The `Autowired` import is removed because it is no longer referenced.
- Constructor parameter becomes a `private val` in the primary constructor.
- Return type `UserDto` becomes `UserDto?` where the service may return null.

---

### Example 3: @ConfigurationProperties Class

**Java:**

```java
package com.acme.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.mail")
public class MailProperties {

    private String host;
    private int port = 587;
    private String username;
    private String password;

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
```

**Kotlin (mutable config with lateinit var):**

```kotlin
package com.acme.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.mail")
open class MailProperties {
    lateinit var host: String
    var port: Int = 587
    lateinit var username: String
    lateinit var password: String
}
```

Key changes:
- Getters and setters are replaced by Kotlin properties.
- `lateinit var` is used for required `String` properties that Spring populates
  after construction.
- `port` keeps its default value and uses a regular `var` (`lateinit` does not
  support primitive types).
- The class is `open` so that Spring can create a CGLIB proxy for it.
