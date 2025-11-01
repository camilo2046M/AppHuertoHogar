package com.example.apphuertohogar.model

sealed interface AuthState {
    object Loading : AuthState
    object Unauthenticated : AuthState
    data class Authenticated(val userId: Int) : AuthState
}