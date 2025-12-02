package com.example.apphuertohogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.ProductRepository
import com.example.apphuertohogar.model.DetalleProductoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Ya no necesitamos 'Application' ni 'AndroidViewModel'
class DetalleProductoViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow(DetalleProductoUiState())
    val uiState: StateFlow<DetalleProductoUiState> = _uiState.asStateFlow()

    fun cargarProducto(id: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // Usamos la función getProductById del nuevo repositorio
            repository.getProductById(id)
                .onSuccess { productoNuevo ->
                    _uiState.update {
                        it.copy(producto = productoNuevo, isLoading = false)
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(isLoading = false, error = "Error al cargar")
                    }
                }
        }
    }
}