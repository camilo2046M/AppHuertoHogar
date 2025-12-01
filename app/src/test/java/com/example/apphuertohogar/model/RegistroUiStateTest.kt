package com.example.apphuertohogar.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Pruebas Unitarias para RegistroUiState")
class RegistroUiStateTest {
    // --- 1. Pruebas de Valores por Defecto ---
    @Test
    @DisplayName("Debe inicializarse con todos los campos vacíos y errores nulos por defecto")
    fun `shouldInitializeWithDefaultValues`() {
        val defaultState = RegistroUiState()
        assertEquals("", defaultState.nombre)
        assertEquals("", defaultState.email)
        assertEquals("", defaultState.pass)
        assertEquals("", defaultState.confirmarPass)
        assertNull(defaultState.nombreError)
        assertNull(defaultState.emailError)
        assertNull(defaultState.passError)
        assertNull(defaultState.confirmarPassError)
    }
    // --- 2. Pruebas de Inicialización con Valores Explícitos ---
    @Test
    @DisplayName("Debe inicializarse correctamente con valores y errores pasados")
    fun `shouldInitializeWithExplicitValuesAndErrors`() {
        val testEmail = "nuevo@usuario.com"
        val errorNombre = "Nombre no puede estar vacío"
        val state = RegistroUiState(
            nombre = "Carlos",
            email = testEmail,
            pass = "pass123",
            confirmarPass = "pass123",
            nombreError = errorNombre,
            emailError = null
        )
        assertEquals("Carlos", state.nombre)
        assertEquals(testEmail, state.email)
        assertEquals(errorNombre, state.nombreError)
        assertNull(state.emailError)
    }
    // --- 3. Pruebas de Inmutabilidad y Función copy() ---
    @Test
    @DisplayName("La función copy() debe crear una nueva instancia y modificar solo el email")
    fun `copyFunctionShouldCreateNewInstanceAndModifyEmail`() {
        val originalState = RegistroUiState(nombre = "Original", email = "old@mail.com")
        val newEmail = "new@mail.com"
        val newState = originalState.copy(email = newEmail)
        assertNotEquals(originalState, newState)
        assertEquals(newEmail, newState.email)
        assertEquals(originalState.nombre, newState.nombre)
        assertEquals(originalState.pass, newState.pass)
    }

    @Test
    @DisplayName("La función copy() debe limpiar los errores correctamente al corregir el pass")
    fun `copyFunctionShouldClearPassError`() {
        val errorState = RegistroUiState(
            passError = "Contraseña demasiado corta",
            confirmarPassError = "Contraseñas no coinciden"
        )
        val fixedState = errorState.copy(passError = null)
        assertNull(fixedState.passError) // El error se limpió
        assertEquals("Contraseñas no coinciden", fixedState.confirmarPassError) // El otro error persiste
    }
}