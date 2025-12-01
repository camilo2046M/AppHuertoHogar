package com.example.apphuertohogar.model

import com.google.gson.annotations.SerializedName

/**
 * Mapea la respuesta de paginación de Spring Boot (Page<Producto>).
 * Retrofit usará esto para desenvolver la lista de productos.
 */
data class ProductResponse(
    @SerializedName("content")
    val content: List<Producto>,

    // Metadatos útiles para el futuro (scroll infinito)
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int
)