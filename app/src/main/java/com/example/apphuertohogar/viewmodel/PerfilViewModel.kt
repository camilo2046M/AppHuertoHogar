package com.example.apphuertohogar.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.model.PerfilUiState
import com.example.apphuertohogar.model.Usuario
import com.example.apphuertohogar.data.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    // CAMBIO 1: Reemplazamos DAO por Repository
    private val repository = AuthRepository()
    private val appContext = application.applicationContext

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    // --- CARGA DE DATOS ---

    /**
     * Carga el perfil desde la API usando el Token guardado.
     * Ya no necesitamos pasar el ID como parámetro.
     *
     *
     */

    private suspend fun copyImageToInternalStorage(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Leemos la imagen original
                val inputStream =
                    appContext.contentResolver.openInputStream(uri) ?: return@withContext null

                // 2. Preparamos el archivo destino en nuestra carpeta privada
                val fileName = "profile_${System.currentTimeMillis()}.jpg"
                val file = File(appContext.filesDir, fileName)
                val outputStream = FileOutputStream(file)

                // 3. Copiamos los datos
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()

                // 4. Devolvemos la ruta absoluta del archivo nuevo
                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun loadUserProfile() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getPerfil()
                .onSuccess { usuarioResponse ->
                    // CORRECCIÓN: Ahora sí asignamos los datos que vienen del servidor.
                    // Usamos el operador elvis (?:) por si el backend manda null.
                    val usuarioUi = Usuario(
                        id = usuarioResponse.id,
                        nombre = usuarioResponse.nombre,
                        email = usuarioResponse.email,
                        direccion = usuarioResponse.direccion ?: "", // <--- AQUÍ ESTÁ EL CAMBIO
                        imagenUrl = usuarioResponse.imagenUrl ?: ""  // <--- AQUÍ ESTÁ EL CAMBIO
                    )

                    _uiState.update {
                        it.copy(isLoading = false, usuario = usuarioUi)
                    }
                }
                .onFailure { error ->
                    // CORRECCIÓN: Apagamos el loading para que no se quede pegado
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // Asegúrate de que PerfilUiState tenga un campo 'error' o 'direccionError' donde mostrar esto
                            // Si no tienes un campo de error genérico, puedes usar un Toast en la UI observando un evento
                        )
                    }
                    println("Error cargando perfil: ${error.message}")
                }
        }
    }

    // --- CÁMARA E IMÁGENES (Lógica Local) ---
    // Mantenemos esto para que la UI de la cámara funcione,
    // aunque la subida al servidor queda pendiente.

    fun updateImageUri(uri: Uri?) {
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

    // --- EDICIÓN ---

    fun toggleEditMode() {
        _uiState.update { currentState ->
            val isEditing = !currentState.isEditing
            val currentUser = currentState.usuario

            currentState.copy(
                isEditing = isEditing,
                editableNombre = currentUser?.nombre ?: "",
                editableDireccion = currentUser?.direccion ?: ""
            )
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(editableNombre = nombre, nombreError = null) }
    }

    fun onDireccionChange(direccion: String) {
        _uiState.update { it.copy(editableDireccion = direccion, direccionError = null) }
    }

    fun savePerfilChanges(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentState = _uiState.value
        val currentUri = _imageUri.value

        if (currentState.editableNombre.isBlank()) {
            _uiState.update { it.copy(nombreError = "Nombre requerido") }
            return
        }
        if (currentState.editableDireccion.isBlank()) {
            _uiState.update { it.copy(direccionError = "Dirección requerida") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            var finalImagePath = currentState.usuario?.imagenUrl // Mantener la anterior por defecto

            // Si hay una URI seleccionada y es distinta a la que ya teníamos guardada...
            if (currentUri != null && currentUri.toString() != finalImagePath) {
                // ... intentamos copiarla al almacenamiento interno
                val localPath = copyImageToInternalStorage(currentUri)
                if (localPath != null) {
                    finalImagePath = localPath // ¡Éxito! Usaremos la ruta local segura
                }
            }

            // Enviamos la ruta (sea la vieja o la nueva local) al servidor
            val imageUrlToSend = finalImagePath ?: ""

            repository.updateProfile(
                nombre = currentState.editableNombre,
                direccion = currentState.editableDireccion,
                imagenUrl = imageUrlToSend
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEditing = false,
                        usuario = it.usuario?.copy(
                            nombre = currentState.editableNombre,
                            direccion = currentState.editableDireccion,
                            imagenUrl = imageUrlToSend
                        )
                    )
                }
                onSuccess()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                onFailure("Error: ${error.message}")
            }
        }
    }
}