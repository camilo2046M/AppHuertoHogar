package com.example.apphuertohogar.data.remote

import com.example.apphuertohogar.model.Post
import com.example.apphuertohogar.model.ProductResponse // Importar el nuevo modelo
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // --- SECCIÓN DE PRODUCTOS (Tu Backend Spring Boot) ---

    /**
     * Obtiene la lista de productos desde tu backend.
     * El backend devuelve un Page<Producto>, que mapeamos a ProductResponse.
     */
    @GET("productos")
    suspend fun obtenerProductos(): ProductResponse


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