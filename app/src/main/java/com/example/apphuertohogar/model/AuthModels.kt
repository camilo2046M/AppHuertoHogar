package com.example.apphuertohogar.model

import com.google.gson.annotations.SerializedName

// Lo que enviamos al registrar (JSON)
data class RegisterRequest(
    val nombre: String,
    @SerializedName("correo")
    val email: String,
    val password: String // Se envía limpia, Spring la encripta
)

// Lo que enviamos al loguear (JSON)
data class LoginRequest(
    @SerializedName("correo")
    val email: String,
    val password: String
)

// Lo que responde Spring Boot (Token + info usuario)
data class AuthResponse(
    @SerializedName("token") val token: String, // Ajusta "token" si tu backend usa "accessToken"
    val usuario: UsuarioResponse? // Datos del usuario si el backend los devuelve
)

// Una versión simplificada de Usuario que viene del servidor (sin anotaciones de Room)
data class UsuarioResponse(
    val id: Int,
    val nombre: String,

    // Mapeamos "correo" del JSON a "email" en Kotlin
    @SerializedName("correo")
    val email: String,

    val role: String,

    // Estos campos pueden venir nulos si el usuario es nuevo
    val direccion: String? = null,

    // Si tu backend envía "imagenUrl", úsalo. Si envía "imagen", cambia el SerializedName.
    @SerializedName("imagenSrc")
    val imagenUrl: String? = null
)