package com.example.apphuertohogar.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.CheckoutViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import com.example.apphuertohogar.model.AuthState
import com.example.apphuertohogar.ui.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    mainViewModel: MainViewModel,
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val authState by mainViewModel.authState.collectAsState()
    val checkoutUiState by checkoutViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val totalPrice = cartItems.sumOf { it.producto.precio * it.cantidad }

    val userId = (authState as? AuthState.Authenticated)?.userId

    LaunchedEffect(userId) {
        if (userId != null) {
            checkoutViewModel.loadUserData(userId)
        }
    }

    LaunchedEffect(checkoutUiState.orderConfirmed) {
        if (checkoutUiState.orderConfirmed) {
            mainViewModel.navigateTo(
                NavigationEvent.NavigateTo(
                    route = Screen.Home,
                    popUpToRoute = Screen.Carrito,
                    inclusive = true,
                    singleTop = true
                )
            )
            scope.launch { snackbarHostState.showSnackbar("¡Pedido confirmado con éxito!") }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Compra") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al carrito")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            when {
                // Estado de carga inicial
                checkoutUiState.isLoading || userId == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                cartItems.isEmpty() -> {
                    Text("Tu carrito está vacío. No puedes finalizar la compra.", Modifier.padding(top = 32.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { mainViewModel.navigateTo(NavigationEvent.NavigateTo(Screen.Home)) }) {
                        Text("Ir a la tienda")
                    }
                }
                else -> {

                    Text("Resumen del Pedido", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.producto.nombre} (x${item.cantidad})", style = MaterialTheme.typography.bodyLarge)
                            Text(formatPrice(item.producto.precio * item.cantidad), style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Divider(Modifier.padding(vertical = 16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total a Pagar:", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(formatPrice(totalPrice), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(Modifier.height(32.dp))
                    Text("Dirección de Envío", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    val direccion = checkoutUiState.usuario?.direccion
                    val direccionText = if (direccion.isNullOrBlank()) {
                        "No has especificado una dirección. Por favor, edita tu perfil."
                    } else {
                        direccion
                    }

                    Text(direccionText, style = MaterialTheme.typography.bodyLarge)

                    if (direccion.isNullOrBlank()) {
                        TextButton(onClick = { mainViewModel.navigateTo(NavigationEvent.NavigateTo(Screen.Perfil)) }) {
                            Text("Ir a Perfil para actualizar")
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    if (checkoutUiState.error != null) {
                        Text("Error: ${checkoutUiState.error}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            if (userId != null) {
                                checkoutViewModel.confirmOrder(
                                    userId = userId,
                                    onSuccess = {},
                                    onFailure = { errorMsg ->
                                        scope.launch { snackbarHostState.showSnackbar(errorMsg) }
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !checkoutUiState.isProcessing && !direccion.isNullOrBlank()
                    ) {
                        if (checkoutUiState.isProcessing) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Confirmar y Pagar ${formatPrice(totalPrice)}")
                        }
                    }

                    if (direccion.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "🚨 Por favor, especifica una dirección en tu perfil para continuar.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
