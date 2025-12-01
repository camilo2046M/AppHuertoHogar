package com.example.apphuertohogar.viewmodel

import android.app.Application
import com.example.apphuertohogar.data.CarritoDao
import com.example.apphuertohogar.data.UserPreferencesRepository
import com.example.apphuertohogar.model.CartItem
import com.example.apphuertohogar.model.CarritoItem
import com.example.apphuertohogar.model.Producto
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    @MockK
    private lateinit var mockCarritoDao: CarritoDao
    @MockK
    private lateinit var mockUserPrefs: UserPreferencesRepository

    private val userIdFlow = MutableStateFlow<Int?>(null)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CartViewModel
    private val loggedInUserId = 1
    private val testProduct = Producto(10, "Manzana", "...", 1000.0, "Fruta", "url")
    private val initialCartItem = CarritoItem(loggedInUserId, testProduct.id, 2)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        every { mockUserPrefs.loggedInUserIdFlow } returns userIdFlow
        val mockApplication: Application = mockk(relaxed = true)
        viewModel = CartViewModel(
            application = mockApplication,
            carritoDao = mockCarritoDao,
            userPreferencesRepository = mockUserPrefs
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun cartItems_cuandoUsuarioCierraSesion_debeEstarVacio() = runTest {
        advanceUntilIdle()
        assertEquals(emptyList<CartItem>(), viewModel.cartItems.value)
    }

    @Test
    fun cartItems_cuandoUsuarioIniciaSesion_debeCargarDatosDelDAO() = runTest {
        val fakeCartItems = listOf(CartItem(testProduct, 1))
        every { mockCarritoDao.obtenerItemsParaUI(loggedInUserId) } returns flowOf(fakeCartItems)
        val job = launch {
            viewModel.cartItems.collect()
        }
        userIdFlow.value = loggedInUserId
        advanceUntilIdle()
        assertEquals(fakeCartItems, viewModel.cartItems.value)
        job.cancel()
    }
    @Test
    fun addToCart_productoNuevo_debeInsertarConCantidadUno() = runTest {
        userIdFlow.value = loggedInUserId
        coEvery { mockCarritoDao.obtenerItemCrudo(loggedInUserId, testProduct.id) } returns null
        coEvery { mockCarritoDao.insertarOActualizar(any()) } returns Unit
        viewModel.addToCart(testProduct)
        advanceUntilIdle()
        val expectedCarritoItem = CarritoItem(loggedInUserId, testProduct.id, 1)
        coVerify(exactly = 1) { mockCarritoDao.insertarOActualizar(expectedCarritoItem) }
    }

    @Test
    fun addToCart_productoExistente_debeIncrementarCantidad() = runTest {
        userIdFlow.value = loggedInUserId
        coEvery { mockCarritoDao.obtenerItemCrudo(loggedInUserId, testProduct.id) } returns initialCartItem
        coEvery { mockCarritoDao.insertarOActualizar(any()) } returns Unit
        viewModel.addToCart(testProduct)
        advanceUntilIdle()
        val expectedCarritoItem = initialCartItem.copy(cantidad = 3) // 2 + 1 = 3
        coVerify(exactly = 1) { mockCarritoDao.insertarOActualizar(expectedCarritoItem) }
    }

    @Test
    fun addToCart_sinUsuarioLogueado_noDebeHacerNada() = runTest {
        viewModel.addToCart(testProduct)
        advanceUntilIdle()
        coVerify(exactly = 0) { mockCarritoDao.obtenerItemCrudo(any(), any()) }
        coVerify(exactly = 0) { mockCarritoDao.insertarOActualizar(any()) }
    }
    @Test
    fun updateQuantity_incrementar_debeActualizarCantidad() = runTest {
        userIdFlow.value = loggedInUserId
        coEvery { mockCarritoDao.obtenerItemCrudo(loggedInUserId, testProduct.id) } returns initialCartItem
        coEvery { mockCarritoDao.insertarOActualizar(any()) } returns Unit
        viewModel.updateQuantity(testProduct.id, 1)
        advanceUntilIdle()
        val expectedUpdatedItem = initialCartItem.copy(cantidad = 3) // 2 + 1 = 3
        coVerify(exactly = 1) { mockCarritoDao.insertarOActualizar(expectedUpdatedItem) }
        coVerify(exactly = 0) { mockCarritoDao.eliminarProductoDelCarrito(any(), any()) }
    }
    @Test
    fun updateQuantity_disminuirACero_debeEliminarItem() = runTest {
        userIdFlow.value = loggedInUserId
        val itemToDecrease = initialCartItem.copy(cantidad = 1)
        coEvery { mockCarritoDao.obtenerItemCrudo(loggedInUserId, testProduct.id) } returns itemToDecrease
        coEvery { mockCarritoDao.eliminarProductoDelCarrito(any(), any()) } returns Unit
        viewModel.updateQuantity(testProduct.id, -1) // 1 - 1 = 0. Debe eliminarse.
        advanceUntilIdle()
        coVerify(exactly = 1) { mockCarritoDao.eliminarProductoDelCarrito(loggedInUserId, testProduct.id) }
        coVerify(exactly = 0) { mockCarritoDao.insertarOActualizar(any()) }
    }

    @Test
    fun updateQuantity_sinUsuarioLogueado_noDebeHacerNada() = runTest {
        viewModel.updateQuantity(testProduct.id, 1)
        advanceUntilIdle()
        coVerify(exactly = 0) { mockCarritoDao.obtenerItemCrudo(any(), any()) }
    }
    @Test
    fun removeFromCart_conUsuarioLogueado_debeLlamarAEliminar() = runTest {
        userIdFlow.value = loggedInUserId
        coEvery { mockCarritoDao.eliminarProductoDelCarrito(any(), any()) } returns Unit
        viewModel.removeFromCart(testProduct.id)
        advanceUntilIdle()
        coVerify(exactly = 1) { mockCarritoDao.eliminarProductoDelCarrito(loggedInUserId, testProduct.id) }
    }

    @Test
    fun removeFromCart_sinUsuarioLogueado_noDebeHacerNada() = runTest {
        viewModel.removeFromCart(testProduct.id)
        advanceUntilIdle()
        coVerify(exactly = 0) { mockCarritoDao.eliminarProductoDelCarrito(any(), any()) }
    }
}