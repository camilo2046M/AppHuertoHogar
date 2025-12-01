package com.example.apphuertohogar.data

import com.example.apphuertohogar.data.UsuarioDao
import com.example.apphuertohogar.model.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
class TestableUsuarioRepository(private val testDao: UsuarioDao) {
    suspend fun insertarUsuario(user: Usuario): Long = testDao.insertarUsuario(user)
    suspend fun getUserByEmail(email: String): Usuario? = testDao.getUserByEmail(email)
    suspend fun getUserById(id: Int): Usuario? = testDao.getUserById(id)
    suspend fun updateUser(user: Usuario) = testDao.updateUser(user)
}

class UsuarioRepositoryTest {

    private lateinit var mockDao: UsuarioDao
    private lateinit var repo: TestableUsuarioRepository
    private lateinit var fakeUser: Usuario

    @BeforeEach
    fun setup() {
        fakeUser = Usuario(
            id = 1,
            nombre = "Juan Perez",
            email = "juan.perez@test.com",
            passHash = "hashed_password",
            direccion = "Calle Falsa 123",
            imagenUrl = "url_imagen"
        )
        mockDao = mockk<UsuarioDao>()

        repo = TestableUsuarioRepository(testDao = mockDao)
    }

    // -----------------------------------------------------
    // PRUEBAS DE INSERCIÓN
    // -----------------------------------------------------

    @Test
    fun insertarUsuario_debeRetornarElIDDeFilaYDelegarAlDAO() = runTest {
        val expectedId = 5L
        coEvery { mockDao.insertarUsuario(fakeUser) } returns expectedId

        val result = repo.insertarUsuario(fakeUser)
        assertEquals(expectedId, result)
        coVerify(exactly = 1) { mockDao.insertarUsuario(fakeUser) }
    }

    // -----------------------------------------------------
    // PRUEBAS DE CONSULTA POR EMAIL
    // -----------------------------------------------------

    @Test
    fun getUserByEmail_debeRetornarUsuarioCuandoEmailExiste() = runTest {
        coEvery { mockDao.getUserByEmail(fakeUser.email) } returns fakeUser

        val result = repo.getUserByEmail(fakeUser.email)

        assertEquals(fakeUser, result)
        coVerify(exactly = 1) { mockDao.getUserByEmail(fakeUser.email) }
    }

    @Test
    fun getUserByEmail_debeRetornarNullCuandoEmailNoExiste() = runTest {
        val nonExistentEmail = "noexiste@test.com"

        coEvery { mockDao.getUserByEmail(nonExistentEmail) } returns null

        val result = repo.getUserByEmail(nonExistentEmail)

        assertNull(result)
        coVerify(exactly = 1) { mockDao.getUserByEmail(nonExistentEmail) }
    }
}