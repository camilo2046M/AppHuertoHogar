package com.example.apphuertohogar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear la instancia de DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Repositorio para gestionar las preferencias del usuario (ej. sesión).
 * Utiliza Jetpack DataStore para persistir datos simples de clave-valor.
 *
 * @param context El contexto de la aplicación.
 */
class UserPreferencesRepository (private val context: Context){

    // Clave privada para almacenar el ID del usuario en DataStore
    private val LOGGED_IN_USER_ID = intPreferencesKey("logged_in_user_id")

    /**
     * Un Flow que emite el ID del usuario logueado.
     * Emite `null` si no hay ningún usuario logueado.
     * El [MainViewModel] observa este Flow para determinar el [AuthState].
     */
    val loggedInUserIdFlow: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[LOGGED_IN_USER_ID]
        }

    /**
     * Guarda el ID del usuario en DataStore para persistir la sesión.
     * @param userId El ID del usuario a guardar, o `null` para cerrar sesión.
     */
    suspend fun saveLoggedInUserId(userId: Int?) {
        context.dataStore.edit { preferences ->
            if (userId == null) {
                // Si el ID es nulo, elimina la clave (cierra sesión)
                preferences.remove(LOGGED_IN_USER_ID)
            } else {
                // Si el ID existe, lo guarda
                preferences[LOGGED_IN_USER_ID] = userId
            }
        }
    }
}
