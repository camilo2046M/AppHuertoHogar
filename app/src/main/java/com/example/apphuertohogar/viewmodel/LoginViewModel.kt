package com.example.apphuertohogar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.apphuertohogar.model.LoginUiState
import  kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel: ViewModel() {

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

        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
}