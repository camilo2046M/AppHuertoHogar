package com.example.apphuertohogar.security

import org.mindrot.jbcrypt.BCrypt

object GestorPassword {

    /**
     * Genera un hash seguro de una contraseña usando BCrypt.
     * @param password La contraseña en texto plano.
     * @return Un string con el hash.
     */
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    /**
     * Comprueba si una contraseña en texto plano coincide con un hash.
     * @param password La contraseña en texto plano.
     * @param hash El hash guardado en la base de datos.
     * @return true si coinciden, false si no.
     */
    fun checkPassword(password: String, hash: String): Boolean {
        return try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            false
        }
    }
}