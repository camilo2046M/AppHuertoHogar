package com.example.apphuertohogar.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import com.example.apphuertohogar.viewmodel.LoginViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    mainViewModel: MainViewModel,
    loginViewModel: LoginViewModel
) {
    val context = LocalContext.current
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

        // Campo Email
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { loginViewModel.onEmailChange(it) },
            label = { Text("Correo Electrónico") },
            isError = uiState.emailError != null,
            supportingText = {
                if (uiState.emailError != null) Text(uiState.emailError!!)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = uiState.pass,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            isError = uiState.passError != null,
            visualTransformation = PasswordVisualTransformation(),
            supportingText = {
                if (uiState.passError != null) Text(uiState.passError!!)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Botón Ingresar
        Button(
            onClick = {
                loginViewModel.iniciarSesion(
                    onSuccess = {
                        mainViewModel.checkAuthStatus()
                        // CORRECCIÓN AQUÍ: Usamos los parámetros de tu Data Class
                        mainViewModel.navigateTo(
                            NavigationEvent.NavigateTo(
                                route = Screen.Home,
                                popUpToRoute = Screen.Login, // Le decimos qué pantalla borrar
                                inclusive = true             // Le decimos que la borre inclusive
                            )
                        )
                    },
                    onFailure = { mensajeError ->
                        Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Registro (Dentro de la columna)
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