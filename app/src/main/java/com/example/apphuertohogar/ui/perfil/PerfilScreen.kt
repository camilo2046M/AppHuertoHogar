package com.example.apphuertohogar.ui.perfil

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.apphuertohogar.R
import com.example.apphuertohogar.model.PerfilUiState
import com.example.apphuertohogar.viewmodel.MainViewModel
import com.example.apphuertohogar.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val loggedInUserId by mainViewModel.loggedInUserId.collectAsState()
    val context = LocalContext.current
    val uiState: PerfilUiState by perfilViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val imageUri by perfilViewModel.imageUri.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var uriForCameraCapture: Uri? by remember { mutableStateOf(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            uriForCameraCapture?.let { savedUri ->
                perfilViewModel.updateImageUri(savedUri)
                println(">>> takePictureLauncher Success: ViewModel updated with $savedUri")
            } ?: run {
                println(">>> takePictureLauncher Success, but temp URI was null?")
            }
        } else {
            println(">>> takePictureLauncher Failed or Cancelled")

            perfilViewModel.updateImageUri(null)
            scope.launch { snackbarHostState.showSnackbar("Captura cancelada o fallida.") }
        }
        showDialog = false
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        perfilViewModel.updateImageUri(uri)
        showDialog = false
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uriForCamera = perfilViewModel.getTmpUri()
            uriForCamera?.let { uri ->
                perfilViewModel.updateImageUri(uri)
                takePictureLauncher.launch(uri)
            } ?: run {
                scope.launch { snackbarHostState.showSnackbar("Error al crear archivo temporal.") }
                showDialog = false
            }
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado.") }
            showDialog = false
        }
    }



    fun checkAndRequestCameraPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                val generatedUri = perfilViewModel.getTmpUri()
                generatedUri?.let { uri ->
                    uriForCameraCapture = uri
                    takePictureLauncher.launch(uri)
                } ?: run { }
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    LaunchedEffect(loggedInUserId) {
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
                            }) { Icon(Icons.Default.Save, contentDescription = "Guardar Cambios") }
                            IconButton(onClick = { perfilViewModel.toggleEditMode() }) {
                                Icon(Icons.Default.Cancel, contentDescription = "Cancelar Edición")
                            }
                        } else {
                            IconButton(onClick = { perfilViewModel.toggleEditMode() }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar Perfil")
                            }
                        }
                    }
                    // Logout button
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
                    // Profile Picture
                    AsyncImage(
                        model = imageUri ?: R.drawable.iconapphuertohogar,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable { showDialog = true },
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.iconapphuertohogar),
                        error = painterResource(id = R.drawable.iconapphuertohogar)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Toca la imagen para cambiarla",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable { showDialog = true }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

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
        } // End Column

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Cambiar foto de perfil") },
                text = { Text("Elige una opción:") },
                confirmButton = {
                    Column {
                        TextButton(onClick = {
                            checkAndRequestCameraPermission()
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                Text("Tomar Foto")
                            }
                        }
                        // "Elegir de Galería" Button - Launches gallery picker
                        TextButton(onClick = {
                            pickImageLauncher.launch("image/*")
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                Text("Elegir de Galería")
                            }
                        }
                    }
                },
                dismissButton = {
                    // "Cancelar" Button
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
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
        Text("Email: ${uiState.usuario.email}", style = MaterialTheme.typography.bodyLarge)
        Text("Dirección: ${uiState.usuario.direccion.ifBlank { "No especificada" }}", style = MaterialTheme.typography.bodyLarge)
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