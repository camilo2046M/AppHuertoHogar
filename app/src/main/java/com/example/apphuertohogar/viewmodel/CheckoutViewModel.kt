package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.AppDatabase
import com.example.apphuertohogar.data.CarritoDao
import com.example.apphuertohogar.data.UsuarioDao
import com.example.apphuertohogar.model.CheckoutUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao: UsuarioDao = AppDatabase.getDatabase(application).usuarioDao()
    private val carritoDao: CarritoDao = AppDatabase.getDatabase(application).carritoDao()

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    // Carga los detalles del usuario, especialmente la dirección de envío.
    fun loadUserData(userId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val usuario = usuarioDao.getUserById(userId)
                _uiState.update {
                    it.copy(
                        usuario = usuario,
                        isLoading = false,
                        error = if (usuario == null) "Usuario no encontrado." else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar datos del usuario: ${e.message}"
                    )
                }
            }
        }
    }

    // Simula la confirmación de la orden y limpia el carrito.
    fun confirmOrder(
        userId: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        _uiState.update { it.copy(isProcessing = true, error = null) }

        viewModelScope.launch {
            try {
                // Aquí iría la lógica real de pago y creación de pedido.
                // Simulación: Limpiar el carrito después de la "compra"
                carritoDao.limpiarCarrito(userId)

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        orderConfirmed = true,
                    )
                }
                onSuccess()

            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessing = false, error = "Error al procesar el pedido.") }
                onFailure("Error al procesar el pedido: ${e.message}")
            }
        }
    }
}