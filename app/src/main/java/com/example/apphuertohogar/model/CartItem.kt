package com.example.apphuertohogar.model

data class CartItem(
    val producto: Producto,
    var cantidad: Int = 1
)