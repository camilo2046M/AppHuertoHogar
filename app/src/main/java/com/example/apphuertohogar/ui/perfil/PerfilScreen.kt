package com.example.apphuertohogar.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apphuertohogar.viewmodel.MainViewModel
import com.example.apphuertohogar.viewmodel.PerfilViewModel
import com.example.apphuertohogar.model.PerfilUiState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Cancel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val loggedInUserId by mainViewModel.loggedInUserId.collectAsState()

    val uiState: PerfilUiState by perfilViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loggedInUserId) {
        println("ProfileScreen: LaunchedEffect triggered. User ID: $loggedInUserId")
        perfilViewModel.loadUserProfile(loggedInUserId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (uiState.usuario != null) {
                    if (uiState.isEditing) {
                        IconButton(onClick = {
                            perfilViewModel.savePerfilChanges(
                                onSuccess = { scope.launch { snackbarHostState.showSnackbar("Perfil guardado") } },
                                onFailure = { errorMsg -> scope.launch { snackbarHostState.showSnackbar("Error: $errorMsg") } }
                            )
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Guardar Cambios")
                        }
                        IconButton(onClick = { perfilViewModel.toggleEditMode() }) {
                            Icon(Icons.Default.Cancel, contentDescription = "Cancelar Edición")
                        }
                    } else {
                        IconButton(onClick = { perfilViewModel.toggleEditMode() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Perfil")
                        }
                    }
                }
                    IconButton(onClick = { mainViewModel.logoutUser() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
                }
                uiState.usuario != null && loggedInUserId != null -> {
                    if (uiState.isEditing) {
                        EditProfileForm(uiState = uiState, perfilViewModel = perfilViewModel)
                    } else {
                        DisplayProfileInfo(uiState = uiState)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Mis Pedidos (Próximamente)", style = MaterialTheme.typography.titleMedium)
                    // TODO: Add Order History Section
                }
                else -> {
                    Text(
                        "No se pudo cargar el perfil o no has iniciado sesión.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayProfileInfo(uiState: PerfilUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Bienvenido,", style = MaterialTheme.typography.headlineSmall)
        Text(uiState.usuario!!.nombre, style = MaterialTheme.typography.headlineMedium)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Email: ${uiState.usuario!!.email}", style = MaterialTheme.typography.bodyLarge)
        Text("Dirección: ${uiState.usuario!!.direccion.ifBlank { "No especificada" }}", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun EditProfileForm(uiState: PerfilUiState, perfilViewModel: PerfilViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Editar Perfil", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = uiState.editableNombre,
            onValueChange = { perfilViewModel.onNombreChange(it) },
            label = { Text("Nombre Completo") },
            isError = uiState.nombreError != null,
            supportingText = { if (uiState.nombreError != null) Text(uiState.nombreError!!) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.editableDireccion,
            onValueChange = { perfilViewModel.onDireccionChange(it) },
            label = { Text("Dirección") },
            isError = uiState.direccionError != null,
            supportingText = { if (uiState.direccionError != null) Text(uiState.direccionError!!) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}