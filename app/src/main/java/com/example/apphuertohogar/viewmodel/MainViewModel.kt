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

// IMPORTANTE: Debe recibir (application: Application) y extender AndroidViewModel
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
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )

    fun setLoggedInUser(userId: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveLoggedInUserId(userId)
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            userPreferencesRepository.saveLoggedInUserId(null)
        }
        navigateTo(NavigationEvent.NavigateTo(route = Screen.Login))
    }

    fun navigateTo(event: NavigationEvent.NavigateTo) {
        viewModelScope.launch {
            _navigationEvents.emit(event)
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    fun navigateUp() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }
}