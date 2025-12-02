package com.example.apphuertohogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.model.HomeUiState
import com.example.apphuertohogar.data.ProductRepository // <--- EL NUEVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Gestiona el estado de la pantalla Home.
 * Se conecta al backend real (AWS) para obtener los productos.
 */
class HomeViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarProductosDeRed()
    }

    // Cambiamos a 'public' si quieres llamarlo desde el Pull-to-Refresh
    fun cargarProductosDeRed() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getProducts()
                .onSuccess { listaDeProductos ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            productos = listaDeProductos,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    println("Error cargando productos: ${error.message}")
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }
}