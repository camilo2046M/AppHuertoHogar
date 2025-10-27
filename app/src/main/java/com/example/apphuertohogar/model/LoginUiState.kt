package com.example.apphuertohogar.model

data class LoginUiState (
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null
)