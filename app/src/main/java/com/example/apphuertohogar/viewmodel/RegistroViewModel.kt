package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.UsuarioDao
import com.example.apphuertohogar.model.RegistroUiState
import com.example.apphuertohogar.model.Usuario
import com.example.apphuertohogar.data.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, nombreError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPassChange(pass: String) {
        _uiState.update { it.copy(pass = pass, passError = null) }
    }

    fun onConfirmarPassChange(pass: String) {
        _uiState.update { it.copy(confirmarPass = pass, confirmarPassError = null) }
    }


    fun validarFormulario(): Boolean {
        val estado = _uiState.value
        var esValido = true

        if (estado.nombre.isBlank()) {
            _uiState.update { it.copy(nombreError = "El nombre no puede estar vacío") }
            esValido = false
        }

        if (estado.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(estado.email)
                .matches()
        ) {
            _uiState.update { it.copy(emailError = "Correo Inválido") }
            esValido = false
        }

        if (estado.pass.isBlank() || estado.pass.length < 8) {
            _uiState.update { it.copy(passError = "Debe tener al menos 8 carácteres") }
            esValido = false

        }

        if (estado.confirmarPass != estado.pass) {
            _uiState.update { it.copy(confirmarPassError = "Las contraseñas no coinciden") }
            esValido = false
        }

        return esValido
    }

    fun registrarUsuario(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (validarFormulario()) {
            _uiState.update { it.copy(isLoading = true) } // Sería bueno agregar un estado de carga

            viewModelScope.launch {
                repository.register(
                    nombre = _uiState.value.nombre,
                    email = _uiState.value.email,
                    pass = _uiState.value.pass
                )
                    .onSuccess { response ->
                        // ÉXITO: El backend respondió 200 OK.
                        // NO intentamos guardar token porque no viene.
                        _uiState.update { it.copy(isLoading = false) }
                        delay(2500)// Apaga carga
                        onSuccess() // La pantalla navegará al Login
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoading = false) } // Apaga carga
                        onFailure("Error: ${error.message}")
                    }
            }
        }
    }
}