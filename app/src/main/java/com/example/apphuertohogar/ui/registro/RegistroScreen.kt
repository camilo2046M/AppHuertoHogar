package com.example.apphuertohogar.ui.registro

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.viewmodel.MainViewModel
import com.example.apphuertohogar.viewmodel.RegistroViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    mainViewModel: MainViewModel,
    viewModel: RegistroViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // --- ESTAS SON LAS VARIABLES QUE TE FALTABAN ---
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    // -----------------------------------------------

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Regístrate", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Nombre
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo") },
                isError = uiState.nombreError != null,
                supportingText = { if (uiState.nombreError != null) Text(uiState.nombreError!!) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Email
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo Electrónico") },
                isError = uiState.emailError != null,
                supportingText = { if (uiState.emailError != null) Text(uiState.emailError!!) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = uiState.pass,
                onValueChange = { viewModel.onPassChange(it) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.passError != null,
                supportingText = { if (uiState.passError != null) Text(uiState.passError!!) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Confirmar Contraseña
            OutlinedTextField(
                value = uiState.confirmarPass,
                onValueChange = { viewModel.onConfirmarPassChange(it) },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.confirmarPassError != null,
                supportingText = { if (uiState.confirmarPassError != null) Text(uiState.confirmarPassError!!) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Registro
            Button(
                onClick = {
                    viewModel.registrarUsuario(
                        onSuccess = {
                            // Mostrar mensaje y navegar
                            scope.launch {
                                snackbarHostState.showSnackbar("¡Cuenta creada! Inicia sesión.")
                            }
                            // Navegar al Login
                            mainViewModel.navigateTo(
                                NavigationEvent.NavigateTo(
                                    route = Screen.Login,
                                    popUpToRoute = Screen.Registro,
                                    inclusive = true
                                )
                            )
                        },
                        onFailure = { errorMsg ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Error: $errorMsg")
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    mainViewModel.navigateTo(NavigationEvent.NavigateTo(Screen.Login))
                }
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}