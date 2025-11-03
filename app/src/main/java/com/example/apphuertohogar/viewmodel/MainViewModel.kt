package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.UserPreferencesRepository
import com.example.apphuertohogar.model.AuthState
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

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
            started = SharingStarted.WhileSubscribed(5000), // 5 segundos
            initialValue = AuthState.Loading // <-- Inicia como Loading
        )

    fun setLoggedInUser(userId: Int) {
        println("MainViewModel: Attempting to save logged in user ID: $userId")
        viewModelScope.launch {
            userPreferencesRepository.saveLoggedInUserId(userId)
        }
    }

    fun logoutUser() {
        println("MainViewModel: logoutUser called")
        viewModelScope.launch {
            userPreferencesRepository.saveLoggedInUserId(null)
        }
        navigateTo(NavigationEvent.NavigateTo(route = Screen.Login))
    }

    // --- CORRECCIÓN: Eliminamos (Dispatchers.Main) ---
    fun navigateTo(event: NavigationEvent.NavigateTo) {
        viewModelScope.launch { // <-- Dispatchers.Main eliminado
            _navigationEvents.emit(event)
        }
    }

    fun navigateBack() {
        viewModelScope.launch { // <-- Dispatchers.Main eliminado
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }


    fun navigateUp() {
        viewModelScope.launch { // <-- DispatchTers.Main eliminado
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }

}