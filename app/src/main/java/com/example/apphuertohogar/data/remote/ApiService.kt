package com.example.apphuertohogar.data.remote

import com.example.apphuertohogar.model.Post
import com.example.apphuertohogar.model.ProductResponse // Importar el nuevo modelo
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.example.apphuertohogar.model.*
import com.google.gson.annotations.SerializedName

data class AddToCartRequest(
    @SerializedName("productoId") val productoId: Int,
    @SerializedName("cantidad") val cantidad: Int
)
interface ApiService {



    @GET("api/carrito")
    suspend fun getCarrito(): List<CartItemResponse> // Definimos esto abajo

    @POST("api/carrito/agregar")
    suspend fun addToCart(@Body request: AddToCartRequest): Any // 'Any' porque solo nos importa que sea 200 OK

    @DELETE("api/carrito/{id}")
    suspend fun removeFromCart(@Path("id") productoId: Int): Any
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // --- SECCIÓN DE PRODUCTOS (Tu Backend Spring Boot) ---

    /**
     * Obtiene la lista de productos desde tu backend.
     * El backend devuelve un Page<Producto>, que mapeamos a ProductResponse.
     */
    @GET("api/productos")
    suspend fun obtenerProductos(): ProductResponse

    @GET("api/productos/{id}")
    suspend fun obtenerProductoPorId(@Path("id") id: Int): Product

    // ApiService.kt
    @GET("api/auth/perfil")
    suspend fun getPerfil(): UsuarioResponse

    @PUT("api/auth/perfil")
    suspend fun updatePerfil(@Body request: UserUpdateRequest): Any // O un response específico

    // --- SECCIÓN DE PEDIDOS //

    @POST("api/pedidos") // Ajusta la ruta según tu controlador
    suspend fun crearPedido(@Body order: OrderRequest): OrderResponse


    // --- SECCIÓN DE POSTS (Placeholder - Mantener por ahora) ---
    @GET("posts") // Nota: Quité el "/" inicial para ser consistente con la Base URL
    suspend fun getPosts(): List<Post>

    @POST("posts")
    suspend fun createPost(@Body post: Post): Post

    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Post

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Unit
}