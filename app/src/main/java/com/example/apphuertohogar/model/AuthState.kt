package com.example.apphuertohogar.model

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    // Ya no es obligatorio pasar el ID aquí, el token lo tiene todo.
    // Pero si quieres mantenerlo, puedes dejarlo o ponerle un valor dummy por ahora.
    data class Authenticated(val userId: Int? = null) : AuthState()
}