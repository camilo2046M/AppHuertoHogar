package com.example.apphuertohogar.viewmodel

import android.app.Application
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
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Gestiona el estado y la lógica de la pantalla de Perfil.
 * Permite al usuario ver, editar su información y cambiar su foto de perfil.
 *
 * @param application Se usa para obtener el contexto para la base de datos y el FileProvider.
 */
class PerfilViewModel(application: Application): AndroidViewModel(application) {

    private val usuarioDao: UsuarioDao = AppDatabase.getDatabase(application).usuarioDao()
    private val appContext = application.applicationContext

    private val _uiState = MutableStateFlow(PerfilUiState())
    /**
     * El estado de la UI (datos del usuario, modo de edición, errores) que [ProfileScreen] observa.
     */
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    /**
     * La URI de la imagen de perfil que se está mostrando actualmente.
     * Puede ser la URI de la BD o una URI temporal (de cámara/galería)
     * que aún no se ha guardado.
     */
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    /**
     * Actualiza la [imageUri] en el estado.
     * Esto se llama cuando el usuario selecciona una imagen de la galería o la cámara.
     */
    fun updateImageUri(uri: Uri?) {
        println(">>> PerfilViewModel: Updating imageUri to: $uri")
        _imageUri.value = uri
    }

    /**
     * Crea y devuelve una URI temporal para guardar una foto tomada con la cámara.
     * Utiliza un [FileProvider] para cumplir con las políticas de seguridad de Android.
     */
    fun getTmpUri(): Uri {
        // ... (Tu código actual de getTmpUri() está perfecto, no cambia)
        val cacheDir = appContext.cacheDir
        val imageDir = File(cacheDir, "images").apply { mkdirs() }
        val tmpFile = File.createTempFile("profile_pic", ".png", imageDir).apply {
            createNewFile()
            deleteOnExit()
        }
        val authority = "${appContext.packageName}.provider"
        return FileProvider.getUriForFile(appContext, authority, tmpFile)
    }


    /**
     * Carga el perfil del usuario desde la base de datos.
     * @param usuarioId El ID del usuario que ha iniciado sesión.
     */
    fun loadUserProfile(usuarioId: Int?) {
        if (usuarioId == null) {
            _uiState.update { it.copy(isLoading = false, usuario = null) }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val usuario = usuarioDao.getUserById(usuarioId)
            _uiState.update { state -> state.copy(isLoading = false, usuario = usuario) }

            // Carga la imagen guardada (si existe) en el StateFlow
            if (usuario != null && usuario.imagenUrl.isNotBlank()) {
                _imageUri.update { Uri.parse(usuario.imagenUrl) }
            } else {
                _imageUri.update { null }
            }
        }
    }

    /**
     * Activa o desactiva el modo de edición de perfil.
     */
    fun toggleEditMode() {
        // ... (Tu código actual de toggleEditMode() está perfecto, no cambia)
        _uiState.update { currentState ->
            val newEditingState = !currentState.isEditing
            val currentUser = currentState.usuario

            if (!newEditingState) {
                val savedUri = if (currentUser != null && currentUser.imagenUrl.isNotBlank()) {
                    Uri.parse(currentUser.imagenUrl)
                } else {
                    null
                }
                _imageUri.update { savedUri }
            }

            currentState.copy(
                isEditing = newEditingState,
                editableNombre = currentUser?.nombre ?: "",
                editableDireccion = currentUser?.direccion ?: "",
                nombreError = null,
                direccionError = null
            )
        }
    }

    /**
     * Actualiza el nombre editable en el [uiState].
     */
    fun onNombreChange(nombre: String) {
        // ... (Tu código actual de onNombreChange() está perfecto, no cambia)
        _uiState.update {
            it.copy(editableNombre = nombre, nombreError = null)
        }
    }

    /**
     * Actualiza la dirección editable en el [uiState].
     */
    fun onDireccionChange(direccion: String) {
        // ... (Tu código actual de onDireccionChange() está perfecto, no cambia)
        _uiState.update {
            it.copy(editableDireccion = direccion, direccionError = null)
        }
    }

    /**
     * Copia la imagen de una URI temporal (Galería o Caché) a un archivo
     * permanente en el almacenamiento interno de la app.
     * @return El String de la URI del *nuevo archivo permanente*, o null si falla.
     */
    private suspend fun copyImageToInternalStorage(tempUri: Uri): String? {
        // Usamos Dispatchers.IO para operaciones de archivos (entrada/salida)
        return withContext(Dispatchers.IO) {
            try {
                // 1. Abrir un "stream" para leer la imagen temporal
                val inputStream = appContext.contentResolver.openInputStream(tempUri) ?: return@withContext null

                // 2. Crear un archivo de destino permanente
                // (ej. /data/data/com.example.apphuertohogar/files/profile_pics/user_1.jpg)
                val userId = _uiState.value.usuario?.id ?: return@withContext null
                val outputDir = File(appContext.filesDir, "profile_pics").apply { mkdirs() }
                val outputFile = File(outputDir, "user_${userId}.jpg")

                // 3. Abrir un "stream" para escribir el archivo nuevo
                val outputStream = FileOutputStream(outputFile)

                // 4. Copiar los bytes de la imagen temporal al archivo permanente
                inputStream.copyTo(outputStream)

                // 5. Cerrar los streams
                inputStream.close()
                outputStream.close()

                // 6. Devolver la URI del nuevo archivo permanente
                Uri.fromFile(outputFile).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                null // Devolver null si la copia falla
            }
        }
    }

    /**
     * Guarda los cambios del perfil (nombre, dirección, imagen) en la base de datos.
     *
     * @param onSuccess Callback invocado si se guarda con éxito.
     * @param onFailure Callback invocado si hay un error de validación o de BD.
     */
    fun savePerfilChanges(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentState = _uiState.value
        val currentUser = currentState.usuario

        if (currentUser == null) {
            onFailure("No hay usuario para actualizar.")
            return
        }

        // ... (Tu código de validación de Nombre y Dirección está perfecto, no cambia)
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

        // --- LÓGICA DE GUARDADO DE IMAGEN (MODIFICADA) ---
        viewModelScope.launch {
            var finalImageUrl = currentUser.imagenUrl // Empezar con la imagen guardada
            val tempImageUri = _imageUri.value

            // Comprobar si la URI en el estado es una *nueva* imagen
            // (Es nueva si no es nula Y no es la misma que ya estaba guardada en la BD)
            if (tempImageUri != null && tempImageUri.toString() != currentUser.imagenUrl) {

                // Es una imagen nueva. Copiarla al almacenamiento permanente.
                val permanentImageUrl = copyImageToInternalStorage(tempUri = tempImageUri)

                if (permanentImageUrl != null) {
                    finalImageUrl = permanentImageUrl // Usar la nueva URL permanente
                } else {
                    // La copia falló, notificar al usuario y detener el guardado
                    onFailure("Error al guardar la imagen.")
                    return@launch
                }
            }

            // --- GUARDAR EN BASE DE DATOS ---
            val updatedUsuario = currentUser.copy(
                nombre = currentState.editableNombre,
                direccion = currentState.editableDireccion,
                imagenUrl = finalImageUrl // Guardar la URL (antigua o la nueva permanente)
            )

            try {
                usuarioDao.updateUser(updatedUsuario)
                _uiState.update {
                    it.copy(usuario = updatedUsuario, isEditing = false, nombreError = null, direccionError = null)
                }
                // Asegurarse de que el StateFlow tenga la URI permanente
                _imageUri.update { Uri.parse(finalImageUrl) }
                onSuccess()
            } catch (e: Exception) {
                onFailure("Error al guardar: ${e.message}")
            }
        }
    }
}

