package com.example.apphuertohogar.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test

class GestorPasswordTest {

    private val passwordNormal = "MiContraseñaSegura123"
    private val passwordIncorrecta = "OtraContraseña123"

    @Test
    fun hashPassword_generaUnHashValido() {
        val hash = GestorPassword.hashPassword(passwordNormal)
        assertNotNull(hash)
        assertFalse(hash == passwordNormal)
        assertTrue(hash.startsWith("\$2a$") || hash.startsWith("\$2b$") || hash.startsWith("\$2y$"))
    }

    @Test
    fun checkPassword_contrasenaCorrecta_debeRetornarTrue() {
        val hashGenerado = GestorPassword.hashPassword(passwordNormal)
        assertTrue(GestorPassword.checkPassword(passwordNormal, hashGenerado))
    }

    @Test
    fun checkPassword_contrasenaIncorrecta_debeRetornarFalse() {
        val hashGenerado = GestorPassword.hashPassword(passwordNormal)
        assertFalse(GestorPassword.checkPassword(passwordIncorrecta, hashGenerado))
    }

    @Test
    fun checkPassword_hashInvalido_debeRetornarFalse() {
        val hashInvalido = "estoNoEsUnHashBCryptValido"
        assertFalse(GestorPassword.checkPassword(passwordNormal, hashInvalido))
    }
}
