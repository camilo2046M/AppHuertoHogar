package com.example.apphuertohogar.model

import com.example.apphuertohogar.model.Product

data class DetalleProductoUiState(
    // ANTES: val producto: Producto? = null,
    // AHORA:
    val producto: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)