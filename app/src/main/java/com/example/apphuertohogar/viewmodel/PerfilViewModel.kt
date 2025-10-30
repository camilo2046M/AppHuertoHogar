package com.example.apphuertohogar.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.UsuarioDao
import com.example.apphuertohogar.model.PerfilUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.apphuertohogar.model.Usuario
import java.io.File

class PerfilViewModel(application: Application): AndroidViewModel(application) {

    private val usuarioDao: UsuarioDao = AppDatabase.getDatabase(application).usuarioDao()
    private val appContext = application.applicationContext

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    fun updateImageUri(uri: Uri?) {
        println(">>> PerfilViewModel: Updating imageUri to: $uri") // <-- ADD LOG
        _imageUri.value = uri
    }

    fun getTmpUri(): Uri {
        val cacheDir = appContext.cacheDir
        val imageDir = File(cacheDir, "images").apply { mkdirs() }
        val tmpFile = File.createTempFile("profile_pic", ".png", imageDir).apply {
            createNewFile()
            deleteOnExit()
        }
        val authority = "${appContext.packageName}.provider"
        return FileProvider.getUriForFile(appContext, authority, tmpFile)
    }


    fun loadUserProfile(usuarioId: Int?) {
        if (usuarioId == null) {
            _uiState.update { it.copy(isLoading = false, usuario = null) }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            usuarioId?.let { nonNullUserId ->
                val usuario = usuarioDao.getUserById(nonNullUserId)
                _uiState.update { state -> state.copy(isLoading = false, usuario = usuario) }

                // --- AÑADE ESTO ---
                // Carga la imagen guardada (si existe) en el StateFlow
                if (usuario != null && usuario.imagenUrl.isNotBlank()) {
                    _imageUri.update { Uri.parse(usuario.imagenUrl) }
                } else {
                    _imageUri.update { null }
                }
            }

        }
    }

    fun toggleEditMode() {
        _uiState.update { currentState ->
            val resetNombre = if (currentState.isEditing) currentState.usuario?.nombre ?: "" else currentState.editableNombre
            val resetDireccion = if (currentState.isEditing) currentState.usuario?.direccion ?: "" else currentState.editableDireccion
            val savedUri = if (currentState.usuario != null && currentState.usuario.imagenUrl.isNotBlank()) {
                Uri.parse(currentState.usuario.imagenUrl)
            } else {
                null
            }
            _imageUri.update { savedUri }
            currentState.copy(
                isEditing = !currentState.isEditing,
                editableNombre = resetNombre,
                editableDireccion = resetDireccion,
                nombreError = null,
                direccionError = null
            )
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update {
            it.copy(editableNombre = nombre, nombreError = null)
        }
    }

    fun onDireccionChange(direccion: String) {
        _uiState.update {
            it.copy(editableDireccion = direccion, direccionError = null)
        }
    }

    fun savePerfilChanges(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentState = _uiState.value
        val currentUser = currentState.usuario

        if (currentUser == null) {
            onFailure("No hay usuario para actualizar.")
            return
        }

        var isValid = true
        if (currentState.editableNombre.isBlank()) {
            _uiState.update { it.copy(nombreError = "El nombre no puede estar vacío") }
            isValid = false
        }
        if (currentState.editableDireccion.isBlank()) {
            _uiState.update { it.copy(direccionError = "La dirección no puede estar vacía") }
            isValid = false
        }

        if (!isValid) {
            onFailure("Formulario inválido.")
            return
        }

        val updatedUsuario = currentUser.copy(
            nombre = currentState.editableNombre,
            direccion = currentState.editableDireccion,
            imagenUrl = _imageUri.value?.toString() ?: currentUser.imagenUrl
        )

        viewModelScope.launch {
            try {
                usuarioDao.updateUser(updatedUsuario)
                _uiState.update {
                    it.copy(usuario = updatedUsuario, isEditing = false, nombreError = null, direccionError = null)
                }
                onSuccess()
            } catch (e: Exception) {
                onFailure("Error al guardar: ${e.message}")
            }
        }
    }
}