package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.UsuarioDao
import com.example.apphuertohogar.model.RegistroUiState
import com.example.apphuertohogar.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import com.example.apphuertohogar.security.GestorPassword

class RegistroViewModel(application: Application) : AndroidViewModel(application){

    private val usuarioDao: UsuarioDao = AppDatabase.getDatabase(application).usuarioDao()
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

    fun registrarUsuario(onSuccess: (newUsuarioId: Int) -> Unit, onFailure: (String) -> Unit) {
        if (validarFormulario()) {
            viewModelScope.launch {
                try {
                    val hashedPassword = GestorPassword.hashPassword(_uiState.value.pass)

                    val newUsuario = Usuario(
                        nombre = _uiState.value.nombre,
                        email = _uiState.value.email,
                        passHash = hashedPassword
                    )
                    val newUsuarioId = usuarioDao.insertarUsuario(newUsuario)
                    onSuccess(newUsuarioId.toInt())
                } catch (e: Exception) {
                    onFailure("Error al registrar: ${e.message}")
                }
            }
        } else {
            onFailure("Formulario inválido")
        }
    }
}

