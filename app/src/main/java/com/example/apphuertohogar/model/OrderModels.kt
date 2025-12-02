package com.example.apphuertohogar.model


import com.google.gson.annotations.SerializedName

// LO QUE ENVIAMOS (Debe coincidir con PedidoRequestDto de Java)
data class OrderRequest(
    val usuarioId: Int,
    val direccionEntrega: String,
    val telefonoEntrega: String = "", // Opcional
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val productoId: Int,
    val cantidad: Int
)

// LO QUE RECIBIMOS (La URL de Stripe)
data class OrderResponse(
    @SerializedName("paymentUrl") val paymentUrl: String
)