package com.example.apphuertohogar.model

// Usamos solo UsuarioResponse porque es lo que viene de la API
data class CheckoutUiState(
    val usuario: UsuarioResponse? = null, // Usamos la clase nueva, pero mantenemos el nombre 'usuario'
    val isLoading: Boolean = false,       // false por defecto para que no gire al inicio si no es necesario
    val isProcessing: Boolean = false,
    val orderConfirmed: Boolean = false,
    val error: String? = null,
    val paymentUrl: String? = null
)