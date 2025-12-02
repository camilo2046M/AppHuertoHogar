package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.model.CartItemResponse
import com.example.apphuertohogar.model.Product // <--- CAMBIO: Usar Product (API), no Producto (Room)
import com.example.apphuertohogar.data.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CartRepository()

    // Usamos CartItemResponse que mapea la respuesta del JSON del Backend
    private val _cartItems = MutableStateFlow<List<CartItemResponse>>(emptyList())
    val cartItems: StateFlow<List<CartItemResponse>> = _cartItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    /**
     * Descarga el carrito actualizado desde el servidor (AWS).
     * Esto asegura que la persistencia funcione entre sesiones.
     */
    fun fetchCart() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCart()
                .onSuccess { items ->
                    _cartItems.value = items
                }
                .onFailure {
                    println("Error sincronizando carrito: ${it.message}")
                }
            _isLoading.value = false
        }
    }

    /**
     * Agrega un producto al carrito en el servidor.
     */
    fun addToCart(product: Product) { // <--- CAMBIO: Recibe Product
        viewModelScope.launch {
            // Enviamos 1 unidad positiva
            repository.addToCart(product.id, 1)
                .onSuccess {
                    fetchCart() // Recargamos para ver el cambio reflejado
                }
                .onFailure {
                    // Manejar error (ej. Toast)
                }
        }
    }

    /**
     * Elimina un producto completamente del carrito.
     */
    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
                .onSuccess { fetchCart() }
        }
    }

    /**
     * Actualiza la cantidad.
     * @param change Puede ser +1 (aumentar) o -1 (disminuir).
     */
    fun updateQuantity(productId: Int, change: Int) {
        viewModelScope.launch {
            // Tu Backend suma lo que le envíes.
            // Si envías -1, el backend hará: cantidad_actual + (-1)
            repository.addToCart(productId, change)
                .onSuccess { fetchCart() }
        }
    }

    fun calculateTotal(): Int {
        return _cartItems.value.sumOf { it.total }
    }
}