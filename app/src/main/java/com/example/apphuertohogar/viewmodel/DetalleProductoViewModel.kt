package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.ProductoRepository
import com.example.apphuertohogar.model.DetalleProductoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetalleProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductoRepository
    private val _uiState = MutableStateFlow(DetalleProductoUiState())
    val uiState: StateFlow<DetalleProductoUiState> = _uiState.asStateFlow()

    init {
        val productoDao = AppDatabase.getDatabase(application).productoDao()
        repository = ProductoRepository(productoDao)
    }

    fun cargarProducto(id: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val producto = repository.obtenerProductoPorId(id)
            _uiState.update {
                it.copy(producto = producto, isLoading = false)
            }
        }
    }
}