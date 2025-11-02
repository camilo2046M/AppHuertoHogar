package com.example.apphuertohogar.model

data class CheckoutUiState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = true,
    val isProcessing: Boolean = false,
    val orderConfirmed: Boolean = false,
    val error: String? = null
)