package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.ProductoRepository
import com.example.apphuertohogar.model.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.apphuertohogar.data.ProductoDao

/**
 * Gestiona el estado de la pantalla Home.
 * Se conecta al backend real para obtener los productos y maneja el estado de carga.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val productoDao: ProductoDao
    private val repository: ProductoRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        productoDao = AppDatabase.getDatabase(application).productoDao()
        repository = ProductoRepository(productoDao)

        // 1. OBSERVAR LA BASE DE DATOS LOCAL (Fuente de la verdad)
        // Esto asegura que si hay datos en caché, se muestren INMEDIATAMENTE
        viewModelScope.launch {
            repository.todosLosProductos
                .distinctUntilChanged()
                .collect { listaDeProductos ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            productos = listaDeProductos
                        )
                    }
                }
        }

        // 2. INICIAR CARGA DE DATOS REALES (Sincronización)
        cargarProductosDeRed()
    }

    private fun cargarProductosDeRed() {
        viewModelScope.launch {
            // Indicamos que estamos cargando
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Llamamos al repositorio para que traiga datos de la API
                repository.refrescarProductos()
            } catch (e: Exception) {
                // Si falla, podríamos mostrar un error (por ahora solo log)
                println("Error en ViewModel al cargar productos: ${e.message}")
            } finally {
                // PASE LO QUE PASE (éxito o error), terminamos de cargar
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}