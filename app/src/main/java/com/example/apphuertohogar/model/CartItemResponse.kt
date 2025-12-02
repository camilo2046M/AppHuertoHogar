package com.example.apphuertohogar.model

import com.google.gson.annotations.SerializedName

data class CartItemResponse(
    val id: Int, // ID de la fila del carrito
    @SerializedName("producto") val product: Product, // El objeto producto anidado
    @SerializedName("cantidad") val quantity: Int
) {
    // Helper para la UI (calcula total)
    val total: Int
        get() = product.precio * quantity
}