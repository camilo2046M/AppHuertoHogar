package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.UsuarioDao
import androidx.lifecycle.ViewModel
import com.example.apphuertohogar.data.AuthRepository
import com.example.apphuertohogar.model.LoginUiState
import  kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Patterns
import com.example.apphuertohogar.utils.TokenStore


class LoginViewModel(
    application: Application,
    usuarioDao: UsuarioDao? = null,
    private val emailValidator: (String) -> Boolean = { email ->
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                email = email,
                emailError = null
            )
        }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { currentState ->
            currentState.copy(
                pass = pass,
                passError = null
            )
        }
    }

    fun validarFormulario(): Boolean {
        val email = _uiState.value.email
        val pass = _uiState.value.pass
        var esValido = true

        if (email.isBlank() || !emailValidator(email)) {
            _uiState.update {
                it.copy(emailError = "Email Inválido")
            }
            esValido = false
        }

        if (pass.isBlank() || pass.length < 8) {
            _uiState.update {
                it.copy(passError = "La contraseña debe tener al menos 8 caracteres")
            }
            esValido = false
        }
        return esValido

    }

    fun iniciarSesion(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val email = _uiState.value.email
        val pass = _uiState.value.pass

        // Validaciones básicas antes de enviar
        if (email.isBlank()) { onFailure("Correo vacío"); return }
        if (pass.isBlank()) { onFailure("Contraseña vacía"); return }

        viewModelScope.launch {
            // Llamamos a la API. Pasamos la contraseña PLANA.
            val result = repository.login(email, pass)

            result.onSuccess { response ->
                viewModelScope.launch {
                    // Guardamos en DataStore
                    TokenStore.saveToken(response.token)
                    // Navegamos
                    onSuccess()
                }

            }.onFailure { error ->
                // FALLO: El backend rechazó la contraseña o el usuario no existe (o error 403/401)
                _uiState.update { it.copy(emailError = "Credenciales incorrectas o error de red") }
                onFailure("Error: ${error.message}")
            }
        }
    }
}