package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.UserPreferencesRepository
import com.example.apphuertohogar.model.AuthState
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel principal (Singleton) que gestiona el estado de autenticación
 * y los eventos de navegación globales.
 *
 * @param application Se usa para obtener el contexto para los Repositorios.
 */
class MainViewModel(application: Application): AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    /**
     * Un "SharedFlow" para emitir eventos de navegación de un solo uso (ej. "ir a Home").
     * La MainActivity escucha este Flow.
     */
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    /**
     * La fuente de verdad para el estado de autenticación.
     * Observa el DataStore ([userPreferencesRepository]) y emite un estado
     * [AuthState] (Loading, Authenticated, o Unauthenticated).
     * La MainActivity reacciona a este estado para decidir qué NavHost mostrar.
     */
    val authState: StateFlow<AuthState> = userPreferencesRepository.loggedInUserIdFlow
        .map { id ->
            if (id == null) {
                AuthState.Unauthenticated
            } else {
                AuthState.Authenticated(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )

    /**
     * Guarda el ID del usuario en el DataStore para persistir la sesión.
     * @param userId El ID del [Usuario] que ha iniciado sesión.
     */
    fun setLoggedInUser(userId: Int) {
        println("MainViewModel: Attempting to save logged in user ID: $userId")
        viewModelScope.launch {
            userPreferencesRepository.saveLoggedInUserId(userId)
        }
    }

    /**
     * Cierra la sesión del usuario borrando su ID del DataStore.
     * El [authState] reaccionará automáticamente y mostrará la pantalla de Login.
     */
    fun logoutUser() {
        println("MainViewModel: logoutUser called")
        viewModelScope.launch {
            userPreferencesRepository.saveLoggedInUserId(null)
        }
        // Navegamos a Login para asegurar que la pila de navegación se limpie
        navigateTo(NavigationEvent.NavigateTo(route = Screen.Login))
    }

    /**
     * Emite un evento de navegación para ir a una pantalla específica.
     * @param event El [NavigationEvent] que describe el destino y la configuración de navegación.
     */
    fun navigateTo(event: NavigationEvent.NavigateTo) {
        viewModelScope.launch {
            _navigationEvents.emit(event)
        }
    }

    /**
     * Emite un evento para retroceder en la pila de navegación.
     */
    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    /**
     * Emite un evento para subir en la pila de navegación (similar a "atrás").
     */
    fun navigateUp() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }

}

