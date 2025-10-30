package com.example.apphuertohogar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository (private val context: Context){

    private val LOGGED_IN_USER_ID = intPreferencesKey("logged_in_user_id")

    val loggedInUserIdFlow: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[LOGGED_IN_USER_ID]
        }
    suspend fun saveLoggedInUserId(userId: Int?) {
        context.dataStore.edit { preferences ->
            if (userId == null) {
                preferences.remove(LOGGED_IN_USER_ID)
            } else {
                preferences[LOGGED_IN_USER_ID] = userId
            }
        }
    }
}

