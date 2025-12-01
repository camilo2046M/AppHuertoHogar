package com.example.apphuertohogar.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.apphuertohogar.data.UsuarioDao
import com.example.apphuertohogar.model.Usuario
import com.example.apphuertohogar.security.GestorPassword
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockUsuarioDao: UsuarioDao

    private lateinit var viewModel: LoginViewModel

    // Datos de prueba
    private val testEmail = "usuario@ejemplo.com"
    private val testPassword = "passwordSeguro123"
    private val shortPassword = "short"
    private val nonMatchingEmail = "non@match.com"
    private val testUserId = 5
    private val testUser = Usuario(
        id = testUserId,
        nombre = "Test",
        email = testEmail,
        passHash = "hash_mock_seguro"
    )

    private val testApplication: Application = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()


    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)

        val mockEmailValidator: (String) -> Boolean = { email ->
            email == testEmail
        }

        viewModel = LoginViewModel(
            application = testApplication,
            usuarioDao = mockUsuarioDao,
            emailValidator = mockEmailValidator
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- TESTS DE CAMBIO DE INPUT Y ESTADO ---

    @Test
    fun onEmailChange_debeActualizarEstadoYEliminarError() {
        viewModel.onEmailChange(nonMatchingEmail)
        viewModel.validarFormulario()

        viewModel.onEmailChange(testEmail)

        val state = viewModel.uiState.value
        assertEquals(testEmail, state.email)
        assertNull(state.emailError)
    }

    @Test
    fun onPasswordChange_debeActualizarEstadoYEliminarError() {
        viewModel.onPasswordChange(shortPassword)
        viewModel.validarFormulario()

        viewModel.onPasswordChange(testPassword)

        val state = viewModel.uiState.value
        assertEquals(testPassword, state.pass)
        assertNull(state.passError)
    }

    // --- TESTS DE VALIDACIÓN LOCAL (validarFormulario) ---

    @Test
    fun validarFormulario_conDatosValidos_debeRetornarTrue() {
        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange(testPassword)

        val esValido = viewModel.validarFormulario()

        assertTrue(esValido)
        assertNull(viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.passError)
    }

    @Test
    fun validarFormulario_conEmailInvalido_debeRetornarFalseYMostrarError() {
        viewModel.onEmailChange(nonMatchingEmail)
        viewModel.onPasswordChange(testPassword)

        val esValido = viewModel.validarFormulario()

        assertFalse(esValido)
        assertEquals("Email Inválido", viewModel.uiState.value.emailError)
    }

    @Test
    fun validarFormulario_conPasswordCorta_debeRetornarFalseYMostrarError() {
        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange(shortPassword)

        val esValido = viewModel.validarFormulario()

        assertFalse(esValido)
        assertEquals("La contraseña debe tener al menos 8 caracteres", viewModel.uiState.value.passError)
    }

    // --- TESTS DE INICIO DE SESIÓN (iniciarSesion) ---
    @Test
    fun iniciarSesion_conUsuarioNoEncontrado_debeLlamarOnFailureYMostrarError() = runTest {
        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange(testPassword)

        coEvery { mockUsuarioDao.getUserByEmail(testEmail) } returns null

        var failureMessage: String? = null

        viewModel.iniciarSesion(
            onSuccess = { fail("No se esperaba éxito") },
            onFailure = { msg -> failureMessage = msg }
        )

        advanceUntilIdle()

        assertEquals("Usuario no encontrado", failureMessage)
        assertEquals("Usuario no encontrado", viewModel.uiState.value.emailError)
    }


}