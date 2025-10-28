package com.example.apphuertohogar.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.viewmodel.LoginViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    mainViewModel: MainViewModel,
    loginViewModel: LoginViewModel = viewModel()
) {
    val uiState by loginViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { loginViewModel.onEmailChange(it) },
            label = { Text("Correo Electrónico") },
            isError = uiState.emailError != null,
            supportingText = {
                if (uiState.emailError != null) {
                    Text(uiState.emailError!!)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.pass,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            isError = uiState.passError != null,
            visualTransformation = PasswordVisualTransformation(),
            supportingText = {
                if (uiState.passError != null) {
                    Text(uiState.passError!!)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                loginViewModel.iniciarSesion(
                    onSuccess = { usuarioId ->
                        println("Inicio de sesión exitoso!!")
                        mainViewModel.setLoggedInUser(usuarioId)
                        mainViewModel.navigateTo(
                            NavigationEvent.NavigateTo(
                                route = Screen.Home,
                                popUpToRoute = Screen.Login, // Pop back up to Login screen
                                inclusive = true,            // Remove Login screen itself
                                singleTop = true             // Avoid multiple Home instances
                            )
                        )
                    },
                    onFailure = { errorMessage ->
                        println("Error de login: $errorMessage")
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        TextButton(
            onClick = {
                mainViewModel.navigateTo(
                    NavigationEvent.NavigateTo(route = Screen.Registro)
                )
            }
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }


}