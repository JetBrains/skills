# Lombok Conversion Guide

## When This Applies

Detected when imports match `lombok.*`.

## Core Rule

**Remove ALL Lombok annotations entirely.** Do not convert Lombok to Lombok — convert
to idiomatic Kotlin equivalents. Lombok has no place in Kotlin code.

## Annotation Conversion Table

| Lombok Annotation | Kotlin Equivalent |
|---|---|
| `@Getter` / `@Setter` | Kotlin properties (val/var) — automatic |
| `@Data` | `data class` with primary constructor properties |
| `@Value` (Lombok) | `data class` with `val` properties (immutable) |
| `@Builder` | Default parameter values, or named arguments. For complex builders, use Kotlin builder DSL |
| `@NoArgsConstructor` | Secondary no-arg constructor, or default values for all params |
| `@AllArgsConstructor` | Primary constructor (Kotlin default) |
| `@RequiredArgsConstructor` | Primary constructor with only required (non-default) params |
| `@ToString` | `data class` auto-generates toString, or manual `override fun toString()` |
| `@EqualsAndHashCode` | `data class` auto-generates, or manual `override fun equals/hashCode` |
| `@Slf4j` / `@Log` / `@Log4j2` | Companion object with logger (see example below) |
| `@Cleanup` | Kotlin's `.use {}` extension function |
| `@SneakyThrows` | Kotlin has no checked exceptions — just remove it |
| `@Synchronized` | Kotlin's `@Synchronized` annotation |
| `@With` | `data class` `.copy()` method |
| `@Accessors(chain = true)` | Kotlin's `apply {}` block |

## Key Rules

1. **@Slf4j** — Convert to a companion object with an explicit logger:
```kotlin
companion object {
    private val log = LoggerFactory.getLogger(MyClass::class.java)
}
```

2. **@Data with JPA entities** — Do NOT use `data class` for JPA entities. Use regular
   `open class` with properties instead. Data classes break Hibernate proxies.

3. **@Builder** — Prefer default parameter values. Only create an explicit builder
   pattern if the Java code has complex builder logic beyond simple setters.

4. **Lombok `val`** — Replace with Kotlin's `val` (they serve the same purpose).

---

## Example 1: @Data Class with @Builder

### Java Input

```java
package com.acme.model;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a customer order with shipping details.
 */
@Data
@Builder
public class Order {
    private String orderId;
    private String customerName;
    private int quantity;
    private boolean expedited;
}
```

### Kotlin Output

```kotlin
package com.acme.model

/**
 * Represents a customer order with shipping details.
 */
data class Order(
    val orderId: String?,
    val customerName: String?,
    val quantity: Int = 0,
    val expedited: Boolean = false
)
```

**What changed:**
- `@Data` → `data class` with primary constructor properties.
- `@Builder` → default parameter values. Callers use named arguments:
  `Order(orderId = "123", customerName = "Alice", quantity = 2)`.
- All Lombok imports removed.
- Fields become `val` properties (immutable by default; use `var` only if mutation is
  required by the original code).
- Reference types are nullable (`String?`) because Java fields default to `null` unless
  proven otherwise.

---

## Example 2: @Slf4j Annotated Service Class

### Java Input

```java
package com.acme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service that processes incoming payment requests.
 */
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway gateway;
    private final NotificationSender notifier;

    /**
     * Processes a payment for the given amount.
     *
     * @param amount the payment amount in cents
     * @return true if the payment succeeded
     */
    public boolean processPayment(long amount) {
        log.info("Processing payment of {} cents", amount);
        try {
            gateway.charge(amount);
            notifier.sendConfirmation(amount);
            log.info("Payment of {} cents succeeded", amount);
            return true;
        } catch (Exception e) {
            log.error("Payment failed for amount {}", amount, e);
            return false;
        }
    }
}
```

### Kotlin Output

```kotlin
package com.acme.service

import org.slf4j.LoggerFactory

/**
 * Service that processes incoming payment requests.
 */
open class PaymentService(
    private val gateway: PaymentGateway,
    private val notifier: NotificationSender
) {

    companion object {
        private val log = LoggerFactory.getLogger(PaymentService::class.java)
    }

    /**
     * Processes a payment for the given amount.
     *
     * @param amount the payment amount in cents
     * @return true if the payment succeeded
     */
    fun processPayment(amount: Long): Boolean {
        log.info("Processing payment of {} cents", amount)
        return try {
            gateway.charge(amount)
            notifier.sendConfirmation(amount)
            log.info("Payment of {} cents succeeded", amount)
            true
        } catch (e: Exception) {
            log.error("Payment failed for amount {}", amount, e)
            false
        }
    }
}
```

**What changed:**
- `@Slf4j` → companion object with `LoggerFactory.getLogger(...)`.
- `@RequiredArgsConstructor` → primary constructor with `val` parameters.
- Lombok imports replaced with `org.slf4j.LoggerFactory`.
- `try/catch` used as an expression (idiomatic Kotlin).
- Class is `open` because Java classes are implicitly open.

---

## Example 3: @Value (Lombok) Immutable Class

### Java Input

```java
package com.acme.config;

import lombok.Value;

/**
 * Immutable configuration for connecting to a database.
 */
@Value
public class DatabaseConfig {
    String host;
    int port;
    String databaseName;
    boolean useSsl;
}
```

### Kotlin Output

```kotlin
package com.acme.config

/**
 * Immutable configuration for connecting to a database.
 */
data class DatabaseConfig(
    val host: String?,
    val port: Int,
    val databaseName: String?,
    val useSsl: Boolean
)
```

**What changed:**
- `@Value` → `data class` with `val` properties (all immutable).
- Lombok's `@Value` makes the class final, and Kotlin `data class` is also final by
  default — so the semantics match.
- All Lombok imports removed.
- Auto-generated `equals()`, `hashCode()`, `toString()`, and `copy()` come from
  `data class` for free.
- Reference types are nullable (`String?`) since the original Java fields have no
  nullability annotations.
