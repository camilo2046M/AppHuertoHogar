package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.ProductoRepository
import com.example.apphuertohogar.model.HomeUiState
import com.example.apphuertohogar.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductoRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val productoDao = AppDatabase.getDatabase(application).productoDao()
        repository = ProductoRepository(productoDao)

        viewModelScope.launch {
            poblarBaseDeDatos()
        }

        viewModelScope.launch {
            repository.todosLosProductos
                .distinctUntilChanged()
                .collect { listaDeProductos ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            productos = listaDeProductos,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private suspend fun poblarBaseDeDatos() {
        val productos = listOf(
            Producto(nombre = "Manzanas Fuji", descripcion = "Manzanas rojas y dulces.", precio = 1500.0, categoria = "Fruta", imagenUrl = "url_manzana"),
            Producto(nombre = "Lechuga Costina", descripcion = "Fresca y crujiente.", precio = 800.0, categoria = "Verdura", imagenUrl = "url_lechuga"),
            Producto(nombre = "Huevos de Campo", descripcion = "Docena de huevos de gallina feliz.", precio = 3500.0, categoria = "Despensa", imagenUrl = "url_huevos"),
            Producto(nombre = "Miel de Quillay", descripcion = "Miel pura de 500g.", precio = 4500.0, categoria = "Despensa", imagenUrl = "url_miel")
        )

        productos.forEach {
            repository.insertarProducto(it)
        }
    }
}