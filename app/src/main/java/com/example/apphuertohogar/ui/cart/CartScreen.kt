package com.example.apphuertohogar.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.apphuertohogar.model.CartItem
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    mainViewModel: MainViewModel,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()

    val totalPrice = cartItems.sumOf { it.producto.precio * it.cantidad }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = { IconButton(onClick = { mainViewModel.navigateBack() }) { Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
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
            // If cart is empty, show a message
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                // If cart has items, show the list and total
                LazyColumn(
                    modifier = Modifier.weight(1f), // Takes available space
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
                        text = "Total: $${totalPrice}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = { mainViewModel.navigateTo(Screen.Checkout) }) {
                        Text("Finalizar Compra")
                    }
                }
            }
        }
    }
}

// Simple Composable to display one row in the cart
@Composable
fun CartItemRow(
    cartItem: CartItem,
    cartViewModel: CartViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
        // horizontalArrangement = Arrangement.SpaceBetween (Removed to use weights/spacers)
    ) {
        // Product Info (Takes available space)
        Column(modifier = Modifier.weight(1f)) {
            Text(cartItem.producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                "$${cartItem.producto.precio * cartItem.cantidad}", // Price per item * quantity
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Quantity Controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { cartViewModel.updateQuantity(cartItem.producto.id, -1) },
                enabled = cartItem.cantidad > 1
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
            }

            Text("${cartItem.cantidad}", modifier = Modifier.padding(horizontal = 8.dp)) // Show quantity

            IconButton(onClick = { cartViewModel.updateQuantity(cartItem.producto.id, 1) }) {
                Icon(Icons.Default.Add, contentDescription = "Increase quantity")
            }
        }

        IconButton(onClick = { cartViewModel.removeFromCart(cartItem.producto.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Remove item", tint = MaterialTheme.colorScheme.error)
        }
    }
    Divider()
}