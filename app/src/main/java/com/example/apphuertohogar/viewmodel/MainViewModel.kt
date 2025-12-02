package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.model.AuthState
import com.example.apphuertohogar.model.Product // <--- USAR EL NUEVO
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.data.ProductRepository
import com.example.apphuertohogar.utils.TokenStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    // Repositorio para traer productos del Home
    private val productRepository = ProductRepository()

    // --- NAVEGACIÓN ---
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // --- PRODUCTOS (Para HomeScreen) ---
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoadingProducts = MutableStateFlow(false)
    val isLoadingProducts = _isLoadingProducts.asStateFlow()

    // --- AUTENTICACIÓN ---
    // Verificamos si hay token en memoria (o DataStore si lo implementaste)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
        fetchProducts()
    }

    /**
     * Verifica si tenemos un token válido guardado.
     */
    fun checkAuthStatus() {
        val token = TokenStore.getTokenSync() // Leemos del disco
        if (!token.isNullOrBlank()) {
            _authState.value = AuthState.Authenticated(null)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            TokenStore.clearToken() // Borramos del disco
            _authState.value = AuthState.Unauthenticated
            // ... navegación al login ...
        }
    }

    /**
     * Carga los productos de la API para el Home.
     */
    fun fetchProducts() {
        viewModelScope.launch {
            _isLoadingProducts.value = true
            productRepository.getProducts()
                .onSuccess { lista ->
                    _products.value = lista
                }
                .onFailure {
                    println("Error cargando productos: ${it.message}")
                    // Aquí podrías manejar un estado de error visual
                }
            _isLoadingProducts.value = false
        }
    }

    /**
     * Cierra sesión: Borra token y navega al Login.
     */

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