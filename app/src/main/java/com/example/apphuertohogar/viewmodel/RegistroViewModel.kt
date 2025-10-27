package com.example.apphuertohogar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.apphuertohogar.model.RegistroUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel : ViewModel(){

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow <RegistroUiState> = _uiState.asStateFlow()

    fun onNombreChange(nombre:String){
        _uiState.update { it.copy(nombre=nombre, nombreError = null) }
    }

    fun onEmailChange(email:String){
        _uiState.update { it.copy(email=email, emailError = null) }
    }

    fun onPassChange(pass:String){
        _uiState.update{it.copy(pass=pass, passError = null)}
    }

    fun onConfirmarPassChange(pass: String){
        _uiState.update { it.copy(confirmarPass = pass, confirmarPassError = null) }
    }


    fun validarFormulario() : Boolean {
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

        if (estado.pass.isBlank()|| estado.pass.length < 8) {
            _uiState.update { it.copy(passError = "Debe tener al menos 8 carácteres") }
            esValido = false

        }

        if (estado.confirmarPass != estado.pass){
            _uiState.update { it.copy(confirmarPassError = "Las contraseñas no coinciden") }
            esValido = false
        }

        return esValido
    }
}