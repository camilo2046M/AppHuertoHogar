package com.example.apphuertohogar.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Pruebas Unitarias para PerfilUiState")
class PerfilUiStateTest {
    private fun createTestUsuario(): Usuario {
        return Usuario(
            id = 1,
            nombre = "Camilo Pérez",
            email = "camilo@test.com",
            direccion = "Calle Falsa 123"
        )
    }
    // --- 1. Pruebas de Valores por Defecto ---
    @Test
    @DisplayName("Debe inicializarse en estado de carga (isLoading=true) y sin datos")
    fun `shouldInitializeWithDefaultLoadingState`() {
        val defaultState = PerfilUiState()
        assertNull(defaultState.usuario) // Usuario nulo al inicio
        assertTrue(defaultState.isLoading) // Debe estar cargando
        assertFalse(defaultState.isEditing) // No debe estar editando
        assertEquals("", defaultState.editableNombre)
        assertEquals("", defaultState.editableDireccion)
        assertNull(defaultState.nombreError)
    }
    // --- 2. Pruebas de Carga de Datos (Estado de Éxito) ---
    @Test
    @DisplayName("El estado debe reflejar los datos del usuario después de la carga exitosa")
    fun `shouldReflectUserDataAfterSuccessfulLoad`() {
        val testUsuario = createTestUsuario()
        val loadedState = PerfilUiState(
            usuario = testUsuario,
            isLoading = false,
            editableNombre = testUsuario.nombre,
            editableDireccion = testUsuario.direccion ?: ""
        )
        assertEquals(testUsuario, loadedState.usuario)
        assertFalse(loadedState.isLoading)
        assertEquals("Camilo Pérez", loadedState.editableNombre)
        assertEquals("Calle Falsa 123", loadedState.editableDireccion)
    }
    // --- 3. Pruebas de Modo de Edición y Errores ---
    @Test
    @DisplayName("La función copy() debe activar el modo de edición y simular un error")
    fun `copyFunctionShouldActivateEditingModeAndSetError`() {
        val originalState = PerfilUiState(
            isLoading = false,
            isEditing = false
        )
        val newNombre = "Carlos"
        val errorState = originalState.copy(
            isEditing = true,
            editableNombre = newNombre,
            nombreError = "Nombre demasiado corto"
        )
        assertTrue(errorState.isEditing)
        assertEquals(newNombre, errorState.editableNombre)
        assertEquals("Nombre demasiado corto", errorState.nombreError)
        assertNull(errorState.direccionError) // Este error debe seguir nulo
    }

    @Test
    @DisplayName("La función copy() debe desactivar el modo de edición y limpiar los errores")
    fun `copyFunctionShouldDeactivateEditingAndClearErrors`() {
        val errorState = PerfilUiState(
            isEditing = true,
            nombreError = "Error anterior"
        )
        val savedState = errorState.copy(
            isEditing = false,
            nombreError = null,
            usuario = createTestUsuario()
        )
        assertFalse(savedState.isEditing)
        assertNull(savedState.nombreError)
        assertEquals(createTestUsuario(), savedState.usuario)
        assertNotEquals(errorState, savedState)
    }
}