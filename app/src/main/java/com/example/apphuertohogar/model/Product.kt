package com.example.apphuertohogar.model

import com.google.gson.annotations.SerializedName

// Esta es la clase NUEVA para la API
data class Product(
    val id: Int,
    val nombre: String,
    val precio: Int, // Int, porque ya lo arreglaste en el Backend
    val descripcion: String,

    // Mapeamos el campo JSON "imagenUrl" (o como se llame en tu backend)
    @SerializedName("imagenSrc")
    val imagenUrl: String
)