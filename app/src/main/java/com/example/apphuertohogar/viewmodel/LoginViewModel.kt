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
import com.example.apphuertohogar.security.GestorPassword
import android.util.Patterns


class LoginViewModel(
    application: Application,
    usuarioDao: UsuarioDao? = null,
    private val emailValidator: (String) -> Boolean = { email ->
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
) : AndroidViewModel(application) {

    private val usuarioDaoImpl: UsuarioDao = usuarioDao ?: AppDatabase.getDatabase(application).usuarioDao()
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

    fun iniciarSesion(onSuccess: (usuarioId: Int) -> Unit, onFailure: (String) -> Unit) {
        val email = _uiState.value.email
        val pass = _uiState.value.pass

        if (email.isBlank() || !emailValidator(email)) {
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
            val usuario = usuarioDaoImpl.getUserByEmail(email)
            if (usuario == null) {
                _uiState.update { it.copy(emailError = "Usuario no encontrado") }
                onFailure("Usuario no encontrado")

            } else if (!GestorPassword.checkPassword(pass, usuario.passHash)) {
                _uiState.update { it.copy(passError = "Contraseña incorrecta") }
                onFailure("Contraseña incorrecta")
            } else {
                onSuccess(usuario.id)
            }
        }
    }
}