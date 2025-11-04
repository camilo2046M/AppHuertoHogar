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
import com.example.apphuertohogar.data.ProductoDao

/**
 * Gestiona el estado de la pantalla Home, cargando la lista de productos.
 *
 * @param application Se usa para obtener el contexto para la base de datos.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val productoDao: ProductoDao
    private val repository: ProductoRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    /**
     * El estado de la UI (lista de productos, estado de carga) que la [HomeScreen] observa.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        productoDao = AppDatabase.getDatabase(application).productoDao()
        repository = ProductoRepository(productoDao)

        viewModelScope.launch {
            // Se asegura de que haya datos de ejemplo si la BD está vacía
            poblarBaseDeDatosSiNecesario()
        }

        viewModelScope.launch {
            // Observa la base de datos y actualiza la UI
            repository.todosLosProductos
                .distinctUntilChanged() // Solo emite si la lista cambia
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

    /**
     * Función de desarrollo.
     * Rellena la base de datos local con productos de ejemplo si está vacía.
     * En una app real, esto sería reemplazado por una llamada a una API.
     */
    private suspend fun poblarBaseDeDatosSiNecesario() {
        if (productoDao.contarProductos() == 0) {
            val productos = listOf(
                Producto(nombre = "Manzanas Fuji", descripcion = "Manzanas rojas y dulces.", precio = 1500.0, categoria = "Fruta", imagenUrl = "https://d26z5keclpxl8.cloudfront.net/web-dist/fotos/productos/60/galeria/2020/jpg/manzana_fuji_1_kg_9557_600x600.jpg"),
                Producto(nombre = "Lechuga Costina", descripcion = "Fresca y crujiente.", precio = 800.0, categoria = "Verdura", imagenUrl = "https://d26z5keclpxl8.cloudfront.net/web-dist/fotos/productos/28/galeria/1742/jpg/lechuga_costina_un_9526_600x600.jpg"),
                Producto(nombre = "Huevos de Campo", descripcion = "Docena de huevos de gallina feliz.", precio = 3500.0, categoria = "Despensa", imagenUrl = "https://feriasrurales.cl/wp/wp-content/uploads/2020/03/huevos_de_campo.png"),
                Producto(nombre = "Miel de Quillay", descripcion = "Miel pura de 500g.", precio = 4500.0, categoria = "Despensa", imagenUrl = "https://chilebefree.com/cdn/shop/products/B00001520_2_2048x.png?v=1621195711")
            )
            productos.forEach {
                repository.insertarProducto(it)
            }
        }
    }
}
