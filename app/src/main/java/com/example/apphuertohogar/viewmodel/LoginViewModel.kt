package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.UsuarioDao
import androidx.lifecycle.ViewModel
import com.example.apphuertohogar.model.LoginUiState
import  kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao: UsuarioDao = AppDatabase.getDatabase(application).usuarioDao()
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

    fun iniciarSesion(onSuccess: (usuarioId: Int) -> Unit, onFailure: (String) -> Unit) {
        val email = _uiState.value.email
        val pass = _uiState.value.pass

        // Basic client-side validation first
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "Correo inválido") }
            onFailure("Correo inválido")
            return
        }
        if (pass.isBlank()) {
            _uiState.update { it.copy(passError = "Contraseña no puede estar vacía") }
            onFailure("Contraseña vacía")
            return
        }

        viewModelScope.launch {
            val usuario = usuarioDao.getUserByEmail(email)
            if (usuario == null) {
                _uiState.update { it.copy(emailError = "Usuario no encontrado") }
                onFailure("Usuario no encontrado")
            } else if (usuario.passHash != pass) { // WARNING: Comparing plain text - USE HASH in real app!
                _uiState.update { it.copy(passError = "Contraseña incorrecta") }
                onFailure("Contraseña incorrecta")
            } else {
                // Login successful! Pass the user ID back.
                onSuccess(usuario.id)
            }
        }
    }
}


