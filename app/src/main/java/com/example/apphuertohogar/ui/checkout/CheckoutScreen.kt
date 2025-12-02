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
import com.example.apphuertohogar.model.AuthState
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.ui.formatPrice
// import com.example.apphuertohogar.ui.extractPriceValue // YA NO LO NECESITAS, BÓRRALO
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.CheckoutViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    mainViewModel: MainViewModel,
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel = viewModel()

) {
    // Obtenemos los ítems del servidor (CartItemResponse)
    val cartItems by cartViewModel.cartItems.collectAsState()
    val authState by mainViewModel.authState.collectAsState()
    val checkoutUiState by checkoutViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        checkoutViewModel.loadUserData()
    }
    // CORRECCIÓN 1: Cálculo del total usando la propiedad .total del modelo nuevo
    // (O multiplicando product.precio * quantity directamente)
    val totalPrice = cartItems.sumOf { it.total }

    // CORRECCIÓN 2: Ya no dependemos del ID manual, sino del estado de autenticación general
    val isAuthenticated = authState is AuthState.Authenticated


    LaunchedEffect(checkoutUiState.paymentUrl) {
        checkoutUiState.paymentUrl?.let { url ->
            // Abrimos Chrome/Navegador con la URL de Stripe
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)

            // Opcional: Navegar al Home después de lanzar el navegador
            mainViewModel.navigateTo(NavigationEvent.NavigateTo(Screen.Home))
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
                // Mostrar carga si está cargando O si no está autenticado aún
                checkoutUiState.isLoading || !isAuthenticated -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                cartItems.isEmpty() -> {
                    Text("Tu carrito está vacío.", Modifier.padding(top = 32.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { mainViewModel.navigateTo(NavigationEvent.NavigateTo(Screen.Home)) }) {
                        Text("Ir a la tienda")
                    }
                }
                else -> {
                    Text("Resumen del Pedido", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    cartItems.forEach { item ->
                        // CORRECCIÓN 4: Usamos 'item.product' y 'item.quantity'
                        // Y usamos el precio directo porque ya es Int
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.product.nombre} (x${item.quantity})", style = MaterialTheme.typography.bodyLarge)
                            Text(formatPrice(item.total), style = MaterialTheme.typography.bodyLarge)
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

                    // CORRECCIÓN 5: Manejo seguro de nulos en la dirección
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
                            // CORRECCIÓN: Llamada limpia.
                            // El ViewModel se encarga de actualizar el estado (loading, error, paymentUrl).
                            checkoutViewModel.confirmOrder(cartItems)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        // Deshabilitamos si está procesando o si no hay dirección
                        enabled = !checkoutUiState.isProcessing && !direccion.isNullOrBlank()
                    ) {
                        if (checkoutUiState.isProcessing) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Confirmar y Pagar ${formatPrice(totalPrice)}")
                        }
                    }
                }
            }
        }
    }
}