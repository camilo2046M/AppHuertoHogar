package com.example.apphuertohogar.viewmodel

import android.app.Application
// Se han eliminado las reglas de Junit 4 para evitar conflictos con Junit 5
// Se asume que el ViewModel de producción fue refactorizado para aceptar inyección de dependencias

// IMPORTS CRUCIALES (Asegúrate que estas rutas son correctas)
import com.example.apphuertohogar.data.CarritoDao // <-- Clase mencionada
import com.example.apphuertohogar.data.UserPreferencesRepository // <-- Clase mencionada
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

// ANOTACIONES DE JUNIT 5
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
// No se usa org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    // 2. DEPENDENCIAS MOCK
    @MockK
    private lateinit var mockCarritoDao: CarritoDao
    @MockK
    private lateinit var mockUserPrefs: UserPreferencesRepository

    private val userIdFlow = MutableStateFlow<Int?>(null)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CartViewModel

    // DATOS DE PRUEBA
    private val loggedInUserId = 1
    private val testProduct = Producto(10, "Manzana", "...", 1000.0, "Fruta", "url")
    private val initialCartItem = CarritoItem(loggedInUserId, testProduct.id, 2)

    @BeforeEach // Anotación de setup de JUNIT 5
    fun setup() {
        // Inicializa los @MockK
        MockKAnnotations.init(this)

        // Establece el dispatcher para todas las coroutines
        Dispatchers.setMain(testDispatcher)

        // Configura el Mock del Repositorio de Preferencias
        every { mockUserPrefs.loggedInUserIdFlow } returns userIdFlow

        val mockApplication: Application = mockk(relaxed = true)

        // Inyectar los mocks en el constructor refactorizado
        viewModel = CartViewModel(
            application = mockApplication,
            carritoDao = mockCarritoDao,
            userPreferencesRepository = mockUserPrefs
        )
    }

    @AfterEach // Anotación de teardown de JUNIT 5
    fun tearDown() {
        Dispatchers.resetMain()
    }


    // --- PRUEBAS DE ESTADO Y FLUJO (Flow) ---

    @Test
    fun cartItems_cuandoUsuarioCierraSesion_debeEstarVacio() = runTest {
        advanceUntilIdle()
        assertEquals(emptyList<CartItem>(), viewModel.cartItems.value)
    }

    @Test
    fun cartItems_cuandoUsuarioIniciaSesion_debeCargarDatosDelDAO() = runTest {
        // ARRANGE
        val fakeCartItems = listOf(CartItem(testProduct, 1))
        every { mockCarritoDao.obtenerItemsParaUI(loggedInUserId) } returns flowOf(fakeCartItems)

        // ACT
        userIdFlow.value = loggedInUserId
        advanceUntilIdle()

        // ASSERT
        assertEquals(fakeCartItems, viewModel.cartItems.value)
    }

    // --- PRUEBAS DE ACCIONES (addToCart) ---

    @Test
    fun addToCart_productoNuevo_debeInsertarConCantidadUno() = runTest {
        // ARRANGE
        userIdFlow.value = loggedInUserId
        coEvery { mockCarritoDao.obtenerItemCrudo(loggedInUserId, testProduct.id) } returns null
        coEvery { mockCarritoDao.insertarOActualizar(any()) } returns Unit

        // ACT
        viewModel.addToCart(testProduct)
        advanceUntilIdle()

        // ASSERT
        val expectedCarritoItem = CarritoItem(loggedInUserId, testProduct.id, 1)
        coVerify(exactly = 1) { mockCarritoDao.insertarOActualizar(expectedCarritoItem) }
    }

    @Test
    fun addToCart_productoExistente_debeIncrementarCantidad() = runTest {
        // ARRANGE
        userIdFlow.value = loggedInUserId
        coEvery { mockCarritoDao.obtenerItemCrudo(loggedInUserId, testProduct.id) } returns initialCartItem
        coEvery { mockCarritoDao.insertarOActualizar(any()) } returns Unit

        // ACT
        viewModel.addToCart(testProduct)
        advanceUntilIdle()

        // ASSERT
        val expectedCarritoItem = initialCartItem.copy(cantidad = 3) // 2 + 1 = 3
        coVerify(exactly = 1) { mockCarritoDao.insertarOActualizar(expectedCarritoItem) }
    }

    @Test
    fun addToCart_sinUsuarioLogueado_noDebeHacerNada() = runTest {
        // ARRANGE: userIdFlow ya está en null

        // ACT
        viewModel.addToCart(testProduct)
        advanceUntilIdle()

        // ASSERT
        coVerify(exactly = 0) { mockCarritoDao.obtenerItemCrudo(any(), any()) }
        coVerify(exactly = 0) { mockCarritoDao.insertarOActualizar(any()) }
    }


    // --- PRUEBAS DE ACCIONES (updateQuantity) ---

    @Test
    fun updateQuantity_incrementar_debeActualizarCantidad() = runTest {
        // ARRANGE
        userIdFlow.value = loggedInUserId
        coEvery { mockCarritoDao.obtenerItemCrudo(loggedInUserId, testProduct.id) } returns initialCartItem
        coEvery { mockCarritoDao.insertarOActualizar(any()) } returns Unit

        // ACT
        viewModel.updateQuantity(testProduct.id, 1)
        advanceUntilIdle()

        // ASSERT
        val expectedUpdatedItem = initialCartItem.copy(cantidad = 3) // 2 + 1 = 3
        coVerify(exactly = 1) { mockCarritoDao.insertarOActualizar(expectedUpdatedItem) }
        coVerify(exactly = 0) { mockCarritoDao.eliminarProductoDelCarrito(any(), any()) }
    }

    @Test
    fun updateQuantity_disminuirACero_debeEliminarItem() = runTest {
        // ARRANGE
        userIdFlow.value = loggedInUserId
        val itemToDecrease = initialCartItem.copy(cantidad = 1)
        coEvery { mockCarritoDao.obtenerItemCrudo(loggedInUserId, testProduct.id) } returns itemToDecrease
        coEvery { mockCarritoDao.eliminarProductoDelCarrito(any(), any()) } returns Unit

        // ACT
        viewModel.updateQuantity(testProduct.id, -1) // 1 - 1 = 0. Debe eliminarse.
        advanceUntilIdle()

        // ASSERT
        coVerify(exactly = 1) { mockCarritoDao.eliminarProductoDelCarrito(loggedInUserId, testProduct.id) }
        coVerify(exactly = 0) { mockCarritoDao.insertarOActualizar(any()) }
    }

    @Test
    fun updateQuantity_sinUsuarioLogueado_noDebeHacerNada() = runTest {
        // ARRANGE: userIdFlow ya está en null

        // ACT
        viewModel.updateQuantity(testProduct.id, 1)
        advanceUntilIdle()

        // ASSERT
        coVerify(exactly = 0) { mockCarritoDao.obtenerItemCrudo(any(), any()) }
    }


    // --- PRUEBAS DE ACCIONES (removeFromCart) ---

    @Test
    fun removeFromCart_conUsuarioLogueado_debeLlamarAEliminar() = runTest {
        // ARRANGE
        userIdFlow.value = loggedInUserId
        coEvery { mockCarritoDao.eliminarProductoDelCarrito(any(), any()) } returns Unit

        // ACT
        viewModel.removeFromCart(testProduct.id)
        advanceUntilIdle()

        // ASSERT
        coVerify(exactly = 1) { mockCarritoDao.eliminarProductoDelCarrito(loggedInUserId, testProduct.id) }
    }

    @Test
    fun removeFromCart_sinUsuarioLogueado_noDebeHacerNada() = runTest {
        // ARRANGE: userIdFlow ya está en null

        // ACT
        viewModel.removeFromCart(testProduct.id)
        advanceUntilIdle()

        // ASSERT
        coVerify(exactly = 0) { mockCarritoDao.eliminarProductoDelCarrito(any(), any()) }
    }
}