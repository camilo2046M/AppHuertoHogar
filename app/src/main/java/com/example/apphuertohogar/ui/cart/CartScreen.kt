package com.example.apphuertohogar.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// IMPORTANTE: Usamos el modelo que viene de la API
import com.example.apphuertohogar.model.CartItemResponse
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.ui.formatPrice
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    mainViewModel: MainViewModel,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()

    LaunchedEffect(Unit) {
        cartViewModel.fetchCart()
    }

    // CÁLCULO DEL TOTAL (Simplificado)
    // Ya no extraemos texto. Usamos la propiedad .total del modelo o multiplicamos enteros.
    val totalPrice = cartItems.sumOf { it.total }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(cartItem = item, cartViewModel = cartViewModel)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        // formatPrice ahora recibe el entero directo
                        text = "Total: ${formatPrice(totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = { mainViewModel.navigateTo(NavigationEvent.NavigateTo(route = Screen.Checkout)) }) {
                        Text("Finalizar Compra")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItemResponse, // <--- TIPO CORREGIDO
    cartViewModel: CartViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // CAMBIO: .product (inglés)
            Text(cartItem.product.nombre, style = MaterialTheme.typography.titleMedium)

            // Precio total de la fila ya calculado y formateado
            Text(
                formatPrice(cartItem.total),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                // CAMBIO: .product.id
                onClick = { cartViewModel.updateQuantity(cartItem.product.id, -1) }
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Disminuir")
            }

            // CAMBIO: .quantity (inglés)
            Text("${cartItem.quantity}", modifier = Modifier.padding(horizontal = 8.dp))

            IconButton(onClick = { cartViewModel.updateQuantity(cartItem.product.id, 1) }) {
                Icon(Icons.Default.Add, contentDescription = "Aumentar")
            }
        }

        IconButton(onClick = { cartViewModel.removeFromCart(cartItem.product.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
        }
    }
    Divider()
}