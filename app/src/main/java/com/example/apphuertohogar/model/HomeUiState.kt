package com.example.apphuertohogar.model

data class HomeUiState (
    val productos: List<Producto> = emptyList(),
    val isLoading: Boolean = true
)