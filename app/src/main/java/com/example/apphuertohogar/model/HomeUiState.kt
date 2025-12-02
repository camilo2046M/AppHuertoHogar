package com.example.apphuertohogar.model

data class HomeUiState(
    // ANTES: val productos: List<Producto> = emptyList(),
    // AHORA:
    val productos: List<Product> = emptyList(),
    val isLoading: Boolean = false
)