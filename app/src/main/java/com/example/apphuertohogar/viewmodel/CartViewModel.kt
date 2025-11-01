package com.example.apphuertohogar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.apphuertohogar.model.CartItem
import com.example.apphuertohogar.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.CarritoDao
import com.example.apphuertohogar.data.UserPreferencesRepository
import com.example.apphuertohogar.model.CarritoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val carritoDao: CarritoDao
    private val userPreferencesRepository: UserPreferencesRepository

    // Un Flow que nos dice quién es el usuario actual
    private val userIdFlow: StateFlow<Int?>

    // El StateFlow público que la UI observará
    val cartItems: StateFlow<List<CartItem>>

    init {
        // Obtenemos el DAO de la base de datos
        carritoDao = AppDatabase.getDatabase(application).carritoDao()
        // Obtenemos el repositorio de preferencias del usuario
        userPreferencesRepository = UserPreferencesRepository(application)

        // Obtenemos el ID del usuario logueado desde las preferencias
        userIdFlow = userPreferencesRepository.loggedInUserIdFlow
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

        // Esta es la parte "mágica":
        // 'flatMapLatest' escucha los cambios en userIdFlow.
        // Si el userId cambia (de null a 1, o de 1 a null),
        // cancela la observación de base de datos anterior y crea una nueva.
        cartItems = userIdFlow.flatMapLatest { userId ->
            if (userId == null) {
                // Si no hay usuario, devuelve un Flow vacío
                emptyFlow()
            } else {
                // Si hay un usuario, observa la base de datos
                // y obtén los items del carrito para ESE usuario
                carritoDao.obtenerItemsParaUI(userId)
            }
        }.stateIn( // Convierte el Flow en un StateFlow para que la UI lo consuma
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // El valor inicial es una lista vacía
        )
    }

    // --- LÓGICA DE ACCIONES (AHORA ESCRIBE EN LA BD) ---

    fun addToCart(producto: Producto) {
        viewModelScope.launch {
            // 1. Obtener el ID del usuario actual
            val userId = userIdFlow.value ?: return@launch // No hacer nada si no hay usuario

            // 2. Comprobar si el producto ya existe en la BD
            val existingItem = carritoDao.obtenerItemCrudo(userId, producto.id)

            val newQuantity: Int
            if (existingItem != null) {
                // Si existe, incrementa la cantidad
                newQuantity = existingItem.cantidad + 1
            } else {
                // Si no existe, la cantidad es 1
                newQuantity = 1
            }

            // 3. Crear la entidad CarritoItem para guardar/actualizar
            val newItem = CarritoItem(
                usuarioId = userId,
                productoId = producto.id,
                cantidad = newQuantity
            )

            // 4. Insertar o Reemplazar en la base de datos
            carritoDao.insertarOActualizar(newItem)
        }
    }

    fun removeFromCart(productoId: Int) {
        viewModelScope.launch {
            val userId = userIdFlow.value ?: return@launch
            // Simplemente borra la fila de la base de datos
            carritoDao.eliminarProductoDelCarrito(userId, productoId)
        }
    }

    fun updateQuantity(productoId: Int, change: Int) {
        viewModelScope.launch {
            val userId = userIdFlow.value ?: return@launch
            val existingItem = carritoDao.obtenerItemCrudo(userId, productoId) ?: return@launch

            val newQuantity = existingItem.cantidad + change

            if (newQuantity > 0) {
                // Si la nueva cantidad es positiva, actualiza
                val updatedItem = existingItem.copy(cantidad = newQuantity)
                carritoDao.insertarOActualizar(updatedItem)
            } else {
                // Si es 0 o menos, elimina el item
                carritoDao.eliminarProductoDelCarrito(userId, productoId)
            }
        }
    }
}

