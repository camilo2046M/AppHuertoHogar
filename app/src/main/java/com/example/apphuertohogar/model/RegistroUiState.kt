package com.example.apphuertohogar.model

data class RegistroUiState (
    val nombre : String = "",
    val email : String = "",
    val pass : String = "",
    val confirmarPass: String = "",

    val nombreError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmarPassError: String?= null

)