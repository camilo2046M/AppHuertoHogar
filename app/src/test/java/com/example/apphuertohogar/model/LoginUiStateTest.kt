package com.example.apphuertohogar.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Pruebas Unitarias para LoginUiState")
class LoginUiStateTest {
    // --- 1. Pruebas de Valores por Defecto ---
    @Test
    @DisplayName("Debe inicializarse con campos vacíos y errores nulos por defecto")
    fun `shouldInitializeWithDefaultValues`() {
        val defaultState = LoginUiState()
        assertEquals("", defaultState.email)
        assertEquals("", defaultState.pass)
        assertNull(defaultState.emailError)
        assertNull(defaultState.passError)
    }
    // --- 2. Pruebas de Inicialización con Valores Explícitos --
    @Test
    @DisplayName("Debe inicializarse correctamente con valores pasados")
    fun `shouldInitializeWithExplicitValues`() {
        val testEmail = "test@correo.com"
        val testPass = "contrasena123"
        val error = "Email inválido"
        val state = LoginUiState(
            email = testEmail,
            pass = testPass,
            emailError = error,
            passError = null
        )
        assertEquals(testEmail, state.email)
        assertEquals(testPass, state.pass)
        assertEquals(error, state.emailError)
        assertNull(state.passError)
    }
    // --- 3. Pruebas de Inmutabilidad y Función copy() ---
    @Test
    @DisplayName("La función copy() debe crear una nueva instancia inmutable y modificar solo el campo deseado")
    fun `copyFunctionShouldCreateNewInstanceAndModifyField`() {
        // Arrange
        val originalState = LoginUiState(
            email = "original@test.com",
            pass = "12345",
            emailError = "Error original"
        )
        val newPass = "nuevaContrasenaSegura"
        val newState = originalState.copy(pass = newPass)
        assertNotEquals(originalState, newState)
        assertEquals(newPass, newState.pass)
        assertEquals(originalState.email, newState.email)
        assertEquals(originalState.emailError, newState.emailError)
    }
    @Test
    @DisplayName("La función copy() debe limpiar los errores correctamente")
    fun `copyFunctionShouldClearErrorsCorrectly`() {
        val errorState = LoginUiState(
            emailError = "Required",
            passError = "Too short"
        )
        val fixedState = errorState.copy(passError = null)
        assertNull(fixedState.passError)
        assertEquals("Required", fixedState.emailError)
    }
}