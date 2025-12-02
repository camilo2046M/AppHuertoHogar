package com.example.apphuertohogar.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// Extensión para crear el DataStore
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object TokenStore {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private var appContext: Context? = null

    // Debemos inicializar esto en el MainActivity
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Guardar Token (Suspendida para usar en Corrutinas)
    suspend fun saveToken(token: String) {
        appContext?.dataStore?.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    // Borrar Token (Logout)
    suspend fun clearToken() {
        appContext?.dataStore?.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    // Obtener Token (Bloqueante para el Interceptor de Retrofit)
    // Usamos runBlocking porque el Interceptor no es suspendido
    fun getTokenSync(): String? {
        val context = appContext ?: return null
        return runBlocking {
            context.dataStore.data.first()[TOKEN_KEY]
        }
    }
}