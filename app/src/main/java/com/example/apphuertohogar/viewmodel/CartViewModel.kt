package com.example.apphuertohogar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.apphuertohogar.model.CartItem
import com.example.apphuertohogar.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _cartItems= MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems : StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(producto: Producto){
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.producto.id == producto.id}
            if(existingItem != null){
                currentItems.map{
                    if(it.producto.id == producto.id) {
                        it.copy(cantidad = it.cantidad + 1)
                    }else{
                        it
                    }
                }
            }else{
                currentItems + CartItem(producto = producto)

            }
        }
    }

    fun removeFromCart(productoId: Int) {
        _cartItems.update { currentItems ->
            currentItems.filterNot { it.producto.id == productoId }
        }
        // TODO: Update persistent storage
    }

    fun updateQuantity(productoId: Int, change: Int) {
        _cartItems.update { currentItems ->
            currentItems.mapNotNull { item ->
                if (item.producto.id == productoId) {
                    val newQuantity = item.cantidad + change
                    if (newQuantity > 0) {
                        item.copy(cantidad = newQuantity)
                    } else {
                        null
                    }
                } else {
                    item
                }
            }
        }
        // TODO: Update persistent storage
    }
}

