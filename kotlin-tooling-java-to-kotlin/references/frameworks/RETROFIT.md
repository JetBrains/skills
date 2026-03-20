# Retrofit / OkHttp Conversion Guide

## When This Applies

Detected when imports match `retrofit2.*` or `okhttp3.*`.

## Key Rules

### 1. Interface declarations

Retrofit service interfaces convert directly — Kotlin interfaces are structurally
identical to Java interfaces for this purpose.

### 2. Call\<T\> to suspend functions

Replace `Call<T>` return types with `suspend fun` returning `T` directly. This requires
the Retrofit coroutine adapter (built-in since Retrofit 2.6.0). The `Callback<T>`
async pattern is eliminated entirely.

### 3. Annotation preservation

All Retrofit annotations transfer directly with no changes:
- HTTP method annotations: `@GET`, `@POST`, `@PUT`, `@DELETE`, `@PATCH`, `@HTTP`
- Header annotations: `@Headers`, `@Header`, `@HeaderMap`
- Parameter annotations: `@Path`, `@Query`, `@QueryMap`, `@Body`, `@Field`,
  `@FieldMap`, `@Part`, `@PartMap`
- `@FormUrlEncoded`, `@Multipart`, `@Streaming`

### 4. Response\<T\> handling

For endpoints where HTTP status codes matter, keep `Response<T>` as the return type
with `suspend fun`. For simple cases where only the body is needed, return `T` directly
and let Retrofit throw on non-2xx responses.

### 5. OkHttpClient.Builder

Java builder chains convert directly. Use `.apply {}` or `.also {}` for grouping
related configuration:

```kotlin
val client = OkHttpClient.Builder().apply {
    connectTimeout(30, TimeUnit.SECONDS)
    readTimeout(30, TimeUnit.SECONDS)
    addInterceptor(loggingInterceptor)
}.build()
```

### 6. Interceptor SAM conversion

Java `Interceptor` anonymous classes become Kotlin SAM lambdas:
`Interceptor { chain -> chain.proceed(chain.request()) }`

### 7. Request/Response body handling

`RequestBody.create(mediaType, content)` → `content.toRequestBody(mediaType)` when
using the `okhttp3-kotlin-extensions` artifact (or `okhttp-bom` with Kotlin extensions).

---

## Example: Retrofit Interface with Coroutine Support

### Java Input

```java
package com.acme.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit service interface for the Users API.
 */
public interface UserApi {

    @GET("users")
    Call<List<UserDto>> getUsers(@Query("page") int page, @Query("limit") int limit);

    @GET("users/{id}")
    Call<UserDto> getUserById(@Path("id") long id);

    @POST("users")
    @Headers("Content-Type: application/json")
    Call<UserDto> createUser(@Body CreateUserRequest request);

    @PATCH("users/{id}")
    Call<UserDto> updateUser(@Path("id") long id, @Body UpdateUserRequest request);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") long id);
}
```

### Kotlin Output

```kotlin
package com.acme.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for the Users API.
 */
interface UserApi {

    @GET("users")
    suspend fun getUsers(@Query("page") page: Int, @Query("limit") limit: Int): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDto

    @POST("users")
    @Headers("Content-Type: application/json")
    suspend fun createUser(@Body request: CreateUserRequest): UserDto

    @PATCH("users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body request: UpdateUserRequest): UserDto

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>
}
```

**Key points:**
- `Call<T>` is removed — each method becomes a `suspend fun` returning `T` directly.
  Retrofit 2.6.0+ supports this natively without an additional adapter.
- `Call<Void>` becomes `Response<Unit>`. `Unit` is Kotlin's equivalent of `Void`.
  `Response<Unit>` is used here to allow checking the HTTP status code on delete.
- `Call` and `Callback` imports are removed since they are no longer referenced.
- All HTTP method and parameter annotations (`@GET`, `@POST`, `@Path`, `@Query`,
  `@Body`, `@Headers`, etc.) are preserved exactly as-is.
- Java `int` → Kotlin `Int`, Java `long` → Kotlin `Long`.
- The `public` modifier on the interface is removed — Kotlin's default visibility
  is public.
