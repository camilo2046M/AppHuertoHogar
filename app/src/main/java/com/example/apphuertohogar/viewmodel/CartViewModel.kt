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
 * Gestiona el estado del carrito de compras.
 *
 * Este ViewModel es responsable de:
 * 1. Observar el usuario logueado desde [UserPreferencesRepository].
 * 2. Observar los items del carrito de ese usuario desde [CarritoDao].
 * 3. Proveer funciones para añadir, actualizar y eliminar items del carrito.
 * 4. Asegurar que el carrito sea persistente (guardado en Room).
 */
class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val carritoDao: CarritoDao
    private val userPreferencesRepository: UserPreferencesRepository

    // Un Flow que nos dice quién es el usuario actual.
    // Se inicializa 'Eagerly' para que esté disponible de inmediato para cartItems.
    private val userIdFlow: StateFlow<Int?>

    /**
     * El StateFlow público que la UI (CartScreen, HomeScreen) observará.
     * Contiene la lista de items del carrito del usuario actual.
     *
     * Utiliza `flatMapLatest` para "reaccionar" a los cambios en [userIdFlow].
     * Si el usuario cierra sesión (userId se vuelve null), el carrito se vacía.
     * Si el usuario inicia sesión, se suscribe al Flow del nuevo userId.
     */
    val cartItems: StateFlow<List<CartItem>>

    init {
        carritoDao = AppDatabase.getDatabase(application).carritoDao()
        userPreferencesRepository = UserPreferencesRepository(application)

        userIdFlow = userPreferencesRepository.loggedInUserIdFlow
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

        // Esta es la lógica reactiva principal
        cartItems = userIdFlow.flatMapLatest { userId ->
            if (userId == null) {
                // Si no hay usuario, devuelve un Flow de lista vacía
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

    // --- ACCIONES DEL USUARIO ---

    /**
     * Añade un producto al carrito del usuario actual.
     * Si el producto ya existe, incrementa su cantidad en 1.
     *
     * @param producto El [Producto] que se va a añadir.
     */
    fun addToCart(producto: Producto) {
        viewModelScope.launch {
            // No hacer nada si no hay un usuario logueado
            val userId = userIdFlow.value ?: return@launch

            // 1. Comprobar si el producto ya existe en la BD
            val existingItem = carritoDao.obtenerItemCrudo(userId, producto.id)

            val newQuantity: Int
            if (existingItem != null) {
                // Si existe, incrementa la cantidad
                newQuantity = existingItem.cantidad + 1
            } else {
                // Si no existe, la cantidad es 1
                newQuantity = 1
            }

            // 2. Crear la entidad CarritoItem para guardar/actualizar
            val newItem = CarritoItem(
                usuarioId = userId,
                productoId = producto.id,
                cantidad = newQuantity
            )

            // 3. Insertar o Reemplazar en Room.
            // OnConflictStrategy.REPLACE (definido en el DAO) se encarga de la lógica.
            carritoDao.insertarOActualizar(newItem)
        }
    }

    /**
     * Elimina un producto (y todas sus cantidades) del carrito.
     *
     * @param productoId El ID del producto a eliminar.
     */
    fun removeFromCart(productoId: Int) {
        viewModelScope.launch {
            val userId = userIdFlow.value ?: return@launch
            carritoDao.eliminarProductoDelCarrito(userId, productoId)
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito.
     *
     * @param productoId El ID del producto a actualizar.
     * @param change La cantidad a añadir (ej. 1) o restar (ej. -1).
     */
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
