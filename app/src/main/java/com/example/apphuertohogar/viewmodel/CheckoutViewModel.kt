package com.example.apphuertohogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.model.CheckoutUiState
import com.example.apphuertohogar.data.AuthRepository
import com.example.apphuertohogar.data.CartRepository
import com.example.apphuertohogar.model.CartItemResponse
import com.example.apphuertohogar.model.OrderItemRequest
import com.example.apphuertohogar.model.OrderRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Gestiona la lógica del Checkout usando la API.
 */
class CheckoutViewModel : ViewModel() { // Ya no necesitamos 'Application'

    // 1. Inyectamos los repositorios de red
    private val authRepository = AuthRepository()
    private val cartRepository = CartRepository()

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    /**
     * Carga los datos del usuario DESDE LA API.
     * Ya no necesitamos pasar userId, el Token en el header identifica al usuario.
     */
    fun loadUserData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.getPerfil()
                .onSuccess { usuario ->
                    _uiState.update { it.copy(isLoading = false, usuario = usuario) }
                }
                .onFailure { error ->
                    // ¡AQUÍ ESTABA EL PROBLEMA! Si fallaba, isLoading se quedaba en true.
                    // Ahora lo apagamos y mostramos el error.
                    _uiState.update {
                        it.copy(isLoading = false, error = "No se pudo cargar: ${error.message}")
                    }
                }
        }
    }

    /**
     * Procesa la orden.
     * Idealmente llamarías a un endpoint @POST /api/pedidos
     */
    fun confirmOrder(cartItems: List<CartItemResponse>) { // Recibimos los items del carrito
        val currentUser = _uiState.value.usuario ?: return

        _uiState.update { it.copy(isProcessing = true, error = null) }

        viewModelScope.launch {
            // 1. Convertimos el carrito de Android al formato que pide el Backend (DTO)
            val orderItems = cartItems.map {
                OrderItemRequest(productoId = it.product.id, cantidad = it.quantity)
            }

            val request = OrderRequest(
                usuarioId = currentUser.id,
                direccionEntrega = currentUser.direccion ?: "Sin dirección",
                items = orderItems
            )

            // 2. Enviamos al servidor
            cartRepository.createOrder(request) // Usamos el repositorio que actualizamos
                .onSuccess { url ->
                    // 3. ¡ÉXITO! Guardamos la URL para que la pantalla la vea
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            paymentUrl = url, // <--- Guardamos la URL aquí
                            orderConfirmed = true
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isProcessing = false, error = "Error: ${error.message}")
                    }
                }
        }
    }
}