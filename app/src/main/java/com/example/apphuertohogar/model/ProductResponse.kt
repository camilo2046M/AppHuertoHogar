package com.example.apphuertohogar.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    // Spring Boot pone la lista dentro de una variable llamada "content"
    @SerializedName("content") val content: List<Product>
)