package com.example.apphuertohogar.data

import com.example.apphuertohogar.model.Producto
import io.mockk.MockKAnnotations // Clave para la inicialización
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockProductoDao: ProductoDao

    private lateinit var repository: ProductoRepository

    private val producto1 = Producto(1, "Tomate", "Rojo y jugoso", 2500.0, "Verdura", "url1")
    private val producto2 = Producto(2, "Manzana", "Fresca y dulce", 1500.0, "Fruta", "url2")
    private val productoList = listOf(producto1, producto2)
    private val categoriaFiltro = "Verdura"

    @BeforeEach
    fun setup() {

        MockKAnnotations.init(this)

        every { mockProductoDao.obtenerTodos() } returns emptyFlow()

        repository = ProductoRepository(mockProductoDao)
    }

    // --- TESTS DE FUNCIONES SUSPENDIDAS ---

    @Test
    fun insertarProducto_debeLlamarInsertarEnElDAO() = runTest {
        coEvery { mockProductoDao.insertar(eq(producto1)) } returns Unit

        repository.insertarProducto(producto1)

        coVerify(exactly = 1) { mockProductoDao.insertar(eq(producto1)) }
    }

    @Test
    fun obtenerProductoPorId_conIdExistente_debeRetornarProducto() = runTest {
        val idBusqueda = 1
        coEvery { mockProductoDao.obtenerPorId(eq(idBusqueda)) } returns producto1

        val result = repository.obtenerProductoPorId(idBusqueda)

        assertEquals(producto1, result)
        coVerify(exactly = 1) { mockProductoDao.obtenerPorId(eq(idBusqueda)) }
    }

    @Test
    fun obtenerProductoPorId_conIdInexistente_debeRetornarNull() = runTest {
        val idBusqueda = 99
        coEvery { mockProductoDao.obtenerPorId(eq(idBusqueda)) } returns null

        val result = repository.obtenerProductoPorId(idBusqueda)

        assertNull(result)
        coVerify(exactly = 1) { mockProductoDao.obtenerPorId(eq(idBusqueda)) }
    }

    // --- TESTS DE FUNCIONES CON ARGUMENTOS (FLOW) ---

    @Test
    fun obtenerPorCategoria_debeLlamarDAOconCategoriaCorrecta_yRetornarFlow() = runTest {
        val productosFiltrados = listOf(producto1)
        every { mockProductoDao.obtenerPorCategoria(eq(categoriaFiltro)) } returns flowOf(productosFiltrados)
        val resultFlow: Flow<List<Producto>> = repository.obtenerPorCategoria(categoriaFiltro)
        val resultList = resultFlow.first()
        assertEquals(1, resultList.size)
        assertEquals(productosFiltrados, resultList)
        verify(exactly = 1) { mockProductoDao.obtenerPorCategoria(eq(categoriaFiltro)) }
    }
}