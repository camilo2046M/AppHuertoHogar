package com.example.apphuertohogar.model

import androidx.room.Embedded

data class CartItem(
    @Embedded
    val producto: Producto,
    var cantidad: Int = 1
)