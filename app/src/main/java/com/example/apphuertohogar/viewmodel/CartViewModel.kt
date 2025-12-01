package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.CarritoDao
import com.example.apphuertohogar.data.UserPreferencesRepository
import com.example.apphuertohogar.model.CartItem
import com.example.apphuertohogar.model.CarritoItem
import com.example.apphuertohogar.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Gestión del estado del carrito.
 * Ha sido refactorizado para la Inyección de Dependencias.
 */
class CartViewModel(
    application: Application,
    // [MODIFICACIÓN CLAVE] Parámetros opcionales para TESTING
    carritoDao: CarritoDao? = null,
    userPreferencesRepository: UserPreferencesRepository? = null
) : AndroidViewModel(application) {

    // Variables internas que contienen el mock o la implementación real.
    private val carritoDaoImpl: CarritoDao
    private val userPreferencesRepositoryImpl: UserPreferencesRepository

    private val userIdFlow: StateFlow<Int?>
    val cartItems: StateFlow<List<CartItem>>

    init {
        // Inicialización: si se pasó un mock (en test), úsalo. Si no, usa el real.
        carritoDaoImpl = carritoDao ?: AppDatabase.getDatabase(application).carritoDao()
        userPreferencesRepositoryImpl = userPreferencesRepository ?: UserPreferencesRepository(application)


        // Usar las implementaciones
        userIdFlow = userPreferencesRepositoryImpl.loggedInUserIdFlow
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

        // Usar las implementaciones
        cartItems = userIdFlow.flatMapLatest { userId ->
            if (userId == null) {
                emptyFlow()
            } else {
                carritoDaoImpl.obtenerItemsParaUI(userId)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // --- ACCIONES DEL USUARIO (todas las llamadas usan el Impl) ---

    fun addToCart(producto: Producto) {
        viewModelScope.launch {
            val userId = userIdFlow.value ?: return@launch

            val existingItem = carritoDaoImpl.obtenerItemCrudo(userId, producto.id)

            val newQuantity: Int
            if (existingItem != null) {
                newQuantity = existingItem.cantidad + 1
            } else {
                newQuantity = 1
            }

            val newItem = CarritoItem(
                usuarioId = userId,
                productoId = producto.id,
                cantidad = newQuantity
            )

            carritoDaoImpl.insertarOActualizar(newItem)
        }
    }

    fun removeFromCart(productoId: Int) {
        viewModelScope.launch {
            val userId = userIdFlow.value ?: return@launch
            carritoDaoImpl.eliminarProductoDelCarrito(userId, productoId)
        }
    }

    fun updateQuantity(productoId: Int, change: Int) {
        viewModelScope.launch {
            val userId = userIdFlow.value ?: return@launch
            val existingItem = carritoDaoImpl.obtenerItemCrudo(userId, productoId) ?: return@launch

            val newQuantity = existingItem.cantidad + change

            if (newQuantity > 0) {
                val updatedItem = existingItem.copy(cantidad = newQuantity)
                carritoDaoImpl.insertarOActualizar(updatedItem)
            } else {
                carritoDaoImpl.eliminarProductoDelCarrito(userId, productoId)
            }
        }
    }
}
