package com.example.apphuertohogar.data

import com.example.apphuertohogar.data.remote.RetrofitInstance
import com.example.apphuertohogar.model.AuthResponse
import com.example.apphuertohogar.model.LoginRequest
import com.example.apphuertohogar.model.RegisterRequest
import com.example.apphuertohogar.model.UserUpdateRequest
import com.example.apphuertohogar.model.UsuarioResponse

class AuthRepository {
    private val api = RetrofitInstance.api


    suspend fun login(email: String, pass: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, pass))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(nombre: String, email: String, pass: String): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(nombre, email, pass))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // AuthRepository.kt
    suspend fun getPerfil(): Result<UsuarioResponse> {
        return try {
            val response = api.getPerfil()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(nombre: String, direccion: String, imagenUrl: String?): Result<Unit> {
        return try {
            api.updatePerfil(UserUpdateRequest(nombre, direccion, imagenUrl))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}