# Hibernate / JPA Conversion Guide

## When This Applies

Detected when imports match any of:
- `javax.persistence.*`
- `jakarta.persistence.*`
- `org.hibernate.*`

## Critical Rules

1. **Do NOT use data classes for JPA entities.** Data classes generate `equals`/`hashCode`
   based on all properties, which breaks Hibernate's identity semantics and proxy creation.

2. **Keep entity classes `open`.** Hibernate creates proxies via subclassing. Kotlin classes
   are `final` by default, so you must use `open` explicitly (or use the `allopen` compiler
   plugin with JPA annotation support).

3. **Provide a no-argument constructor** if Hibernate requires one for proxy creation. Use a
   secondary constructor or default values for all primary constructor parameters.

4. **Annotation site targets matter:**
   - `@Id`, `@Column`, `@GeneratedValue` on fields → use `@field:Id`, `@field:Column`, etc.
     in Kotlin, OR place annotations on constructor parameters with `@field:` site target.
   - `@ManyToOne`, `@OneToMany`, `@JoinColumn` → same `@field:` targeting.

5. **Lazy loading considerations:** `@ManyToOne(fetch = FetchType.LAZY)` requires the entity
   class to be open for proxy creation. `@OneToMany` with lazy collections work with Kotlin's
   `MutableList`.

6. **`@Embeddable` classes**: Can be data classes (they don't need proxies).

7. **`@MappedSuperclass`**: Must be `open abstract class` in Kotlin.

## Examples

### Example 1: JPA Entity with @Id, @Column, and Relationships

**Java:**
```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    protected User() {}

    public User(String username, String email, Department department) {
        this.username = username;
        this.email = email;
        this.department = department;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}
```

**Kotlin:**
```kotlin
@Entity
@Table(name = "users")
open class User(

    @field:Column(name = "username", nullable = false, unique = true)
    open val username: String,

    @field:Column(name = "email")
    open var email: String? = null,

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "department_id")
    open var department: Department? = null

) {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    protected constructor() : this(username = "")
}
```

### Example 2: @Embeddable Value Object

**Java:**
```java
@Embeddable
public class Address {

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    protected Address() {}

    public Address(String street, String city, String zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getZipCode() { return zipCode; }
}
```

**Kotlin:**
```kotlin
@Embeddable
data class Address(

    @field:Column(name = "street")
    val street: String = "",

    @field:Column(name = "city")
    val city: String = "",

    @field:Column(name = "zip_code")
    val zipCode: String = ""
)
```

`@Embeddable` classes can safely be data classes because Hibernate does not proxy them.
Default values satisfy the no-arg constructor requirement.

### Example 3: Entity with @ManyToOne and @OneToMany

**Java:**
```java
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    protected Department() {}

    public Department(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<User> getUsers() { return users; }

    public void addUser(User user) {
        users.add(user);
        user.setDepartment(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setDepartment(null);
    }
}
```

**Kotlin:**
```kotlin
@Entity
@Table(name = "departments")
open class Department(

    @field:Column(name = "name", nullable = false)
    open val name: String = ""

) {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @field:OneToMany(mappedBy = "department", cascade = [CascadeType.ALL], orphanRemoval = true)
    open val users: MutableList<User> = mutableListOf()

    protected constructor() : this(name = "")

    fun addUser(user: User) {
        users.add(user)
        user.department = this
    }

    fun removeUser(user: User) {
        users.remove(user)
        user.department = null
    }
}
```

Key points in this example:
- `cascade` array syntax uses Kotlin's `[CascadeType.ALL]` instead of Java's `{CascadeType.ALL}`.
- The collection is typed as `MutableList` to allow Hibernate to manage the relationship.
- The class and its properties are `open` so Hibernate can create proxies.
- The no-arg constructor delegates to the primary constructor with default values.
