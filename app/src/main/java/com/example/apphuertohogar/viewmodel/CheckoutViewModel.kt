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

/**
 * Gestiona la lógica de la pantalla de finalización de compra.
 * Carga los datos del usuario (dirección) y procesa la orden.
 *
 * @param application Se usa para obtener el contexto para la base de datos.
 */
class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao: UsuarioDao = AppDatabase.getDatabase(application).usuarioDao()
    private val carritoDao: CarritoDao = AppDatabase.getDatabase(application).carritoDao()

    private val _uiState = MutableStateFlow(CheckoutUiState())
    /**
     * El estado de la UI (datos de usuario, estado de carga/procesando) que [CheckoutScreen] observa.
     */
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    /**
     * Carga los detalles del usuario actual (especialmente la dirección)
     * desde la base de datos.
     *
     * @param userId El ID del usuario que está haciendo el checkout.
     */
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

    /**
     * Procesa la orden del usuario.
     * En una app real, esto contactaría una pasarela de pagos (Stripe, etc.).
     *
     * Como simulación, simplemente limpia el carrito del usuario.
     *
     * @param userId El ID del usuario que confirma la orden.
     * @param onSuccess Callback invocado si la "orden" es exitosa.
     * @param onFailure Callback invocado si ocurre un error.
     */
    fun confirmOrder(
        userId: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        _uiState.update { it.copy(isProcessing = true, error = null) }

        viewModelScope.launch {
            try {
                // TODO: Integrar una pasarela de pago real aquí.
                // Esta sección es solo una simulación.

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
