# Jackson Conversion Guide

## When This Applies

Detected when imports match `com.fasterxml.jackson.*`.

## Key Rules

1. **Annotation site targets**:
   - `@JsonProperty` on a Java field → `@field:JsonProperty` in Kotlin.
   - `@JsonProperty` on a Java getter → `@get:JsonProperty` in Kotlin.
   - When converting to Kotlin properties, apply BOTH `@field:` and `@get:` targets to
     match Java's dual annotation on field + getter.

2. **@JsonCreator**: Java's `@JsonCreator` static factory or constructor → Kotlin primary
   constructor. The `@JsonCreator` annotation is often unnecessary on Kotlin's primary
   constructor if using the Jackson Kotlin module, but preserve it for safety.

3. **@JsonIgnore**: Preserve exactly. Use `@get:JsonIgnore` or `@field:JsonIgnore`
   depending on original target.

4. **@JsonDeserialize / @JsonSerialize**: Preserve exactly with correct site targets.

5. **@JsonInclude**: Preserve on class or property level.

6. **@JsonFormat**: Preserve with `@field:JsonFormat` site target.

7. **Jackson Kotlin Module**: Note that projects using Jackson with Kotlin should add
   `jackson-module-kotlin` for proper Kotlin support (data classes, default values,
   nullable types). This is NOT something to add during conversion — just note it if
   missing.

8. **Builder pattern with @JsonPOJOBuilder**: Replace with primary constructor +
   `@JsonCreator` if converting to data class. Otherwise preserve.

---

## Example 1: DTO with Various Jackson Annotations

### Java Input

```java
package com.acme.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Data transfer object for an order summary.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderSummaryDto {

    @JsonProperty("order_id")
    private final String orderId;

    @JsonProperty("total_amount")
    private final double totalAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final String createdDate;

    @JsonIgnore
    private String internalNote;

    public OrderSummaryDto(String orderId, double totalAmount, String createdDate) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.createdDate = createdDate;
    }

    @JsonProperty("order_id")
    public String getOrderId() {
        return orderId;
    }

    @JsonProperty("total_amount")
    public double getTotalAmount() {
        return totalAmount;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public String getCreatedDate() {
        return createdDate;
    }

    @JsonIgnore
    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }
}
```

### Kotlin Output

```kotlin
package com.acme.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonFormat

/**
 * Data transfer object for an order summary.
 *
 * @property orderId unique identifier for the order, serialized as `"order_id"`
 * @property totalAmount total monetary amount, serialized as `"total_amount"`
 * @property createdDate date the order was created, formatted as `yyyy-MM-dd`
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
open class OrderSummaryDto(
    @field:JsonProperty("order_id")
    @get:JsonProperty("order_id")
    val orderId: String?,

    @field:JsonProperty("total_amount")
    @get:JsonProperty("total_amount")
    val totalAmount: Double,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val createdDate: String?
) {
    @field:JsonIgnore
    @get:JsonIgnore
    var internalNote: String? = null
}
```

**Key points:**
- `@JsonInclude` stays at class level — no site target needed.
- `@JsonProperty` gets both `@field:` and `@get:` to match the Java field + getter
  annotations.
- `@JsonFormat` also gets both `@field:` and `@get:` since Java had it on both.
- `@JsonIgnore` gets both `@field:` and `@get:` to suppress serialization fully.

---

## Example 2: Class with @JsonCreator Factory Method

### Java Input

```java
package com.acme.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable configuration entry deserialized from JSON.
 */
public class ConfigEntry {

    private final String key;
    private final String value;
    private final boolean enabled;

    @JsonCreator
    public static ConfigEntry create(
            @JsonProperty("key") String key,
            @JsonProperty("value") String value,
            @JsonProperty("enabled") boolean enabled) {
        return new ConfigEntry(key, value, enabled);
    }

    private ConfigEntry(String key, String value, boolean enabled) {
        this.key = key;
        this.value = value;
        this.enabled = enabled;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("enabled")
    public boolean isEnabled() {
        return enabled;
    }
}
```

### Kotlin Output

```kotlin
package com.acme.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Immutable configuration entry deserialized from JSON.
 *
 * @property key the configuration key
 * @property value the configuration value
 * @property enabled whether this entry is active
 */
data class ConfigEntry @JsonCreator constructor(
    @field:JsonProperty("key")
    @get:JsonProperty("key")
    val key: String?,

    @field:JsonProperty("value")
    @get:JsonProperty("value")
    val value: String?,

    @field:JsonProperty("enabled")
    @get:JsonProperty("enabled")
    val enabled: Boolean
) {
    companion object {
        /**
         * Factory method preserved for documentation; the primary constructor
         * with [JsonCreator] handles deserialization directly.
         */
        @JsonCreator
        @JvmStatic
        fun create(
            @JsonProperty("key") key: String?,
            @JsonProperty("value") value: String?,
            @JsonProperty("enabled") enabled: Boolean
        ): ConfigEntry = ConfigEntry(key, value, enabled)
    }
}
```

**Key points:**
- The Java `@JsonCreator` static factory is converted to a Kotlin primary constructor
  with `@JsonCreator`. The companion object factory is preserved for backward
  compatibility but the primary constructor handles deserialization.
- The class becomes a `data class` since it is immutable and value-oriented.
- `@JsonCreator` is kept on the primary constructor for safety, ensuring Jackson can
  deserialize even without the Jackson Kotlin module.
- String parameters remain nullable (`String?`) since Java strings are nullable by
  default and there is no `@NonNull` or `Objects.requireNonNull` evidence.
