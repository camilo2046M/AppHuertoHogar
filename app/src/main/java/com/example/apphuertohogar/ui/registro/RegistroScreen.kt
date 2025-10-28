package com.example.apphuertohogar.ui.registro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.viewmodel.MainViewModel
import com.example.apphuertohogar.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    mainViewModel: MainViewModel,
    registroViewModel: RegistroViewModel = viewModel()
) {
    val uiState by registroViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = { registroViewModel.onNombreChange(it) },
            label = { Text("Nombre Completo") },
            isError = uiState.nombreError != null,
            supportingText = { if (uiState.nombreError != null) Text(uiState.nombreError!!) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { registroViewModel.onEmailChange(it) },
            label = { Text("Correo Electrónico") },
            isError = uiState.emailError != null,
            supportingText = { if (uiState.emailError != null) Text(uiState.emailError!!) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.pass,
            onValueChange = { registroViewModel.onPassChange(it) },
            label = { Text("Contraseña") },
            isError = uiState.passError != null,
            visualTransformation = PasswordVisualTransformation(),
            supportingText = { if (uiState.passError != null) Text(uiState.passError!!) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.confirmarPass,
            onValueChange = { registroViewModel.onConfirmarPassChange(it) },
            label = { Text("Confirmar Contraseña") },
            isError = uiState.confirmarPassError != null,
            visualTransformation = PasswordVisualTransformation(),
            supportingText = { if (uiState.confirmarPassError != null) Text(uiState.confirmarPassError!!) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                registroViewModel.registrarUsuario(
                    onSuccess = { newUsuarioId -> //
                        mainViewModel.setLoggedInUser(newUsuarioId)
                        mainViewModel.navigateTo(Screen.Home)
                    },
                    onFailure = { errorMessage ->
                        println("Error de registro: $errorMessage")
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarme")
        }

        TextButton(
            onClick = {
                mainViewModel.navigateBack()
            }
        ) {
            Text("Ya tengo cuenta")
        }
    }
}