package com.example.apphuertohogar.model

data class PerfilUiState(

    val usuario: Usuario? = null,
    val isLoading: Boolean = true,

    val editableNombre: String = "",
    val editableDireccion: String = "",

    val nombreError: String? = null,
    val direccionError: String? = null,
    val isEditing: Boolean = false

)