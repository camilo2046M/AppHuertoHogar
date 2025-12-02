package com.example.apphuertohogar.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person // <--- Importante para el ícono de perfil
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.apphuertohogar.model.Product
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.ui.buildImageUrl
import com.example.apphuertohogar.ui.formatPrice
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    cartViewModel: CartViewModel
) {
    val products by mainViewModel.products.collectAsState()
    val cartItemsState by cartViewModel.cartItems.collectAsState()

    // Calculamos la cantidad total de ítems para el "globo" rojo (Badge)
    val cartItemCount = cartItemsState.sumOf { it.quantity }

    // CORRECCIÓN 1: Cargar el carrito apenas se abre el Home (Persistencia visual)
    LaunchedEffect(Unit) {
        cartViewModel.fetchCart()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Huerto Hogar") },
                actions = {
                    // CORRECCIÓN 2: Agregamos el botón de Perfil de vuelta
                    IconButton(onClick = {
                        mainViewModel.navigateTo(NavigationEvent.NavigateTo(Screen.Perfil))
                    }) {
                        Icon(Icons.Default.Person, contentDescription = "Mi Perfil")
                    }

                    // Botón de Carrito con Badge
                    IconButton(onClick = {
                        mainViewModel.navigateTo(NavigationEvent.NavigateTo(Screen.Carrito))
                    }) {
                        BadgedBox(badge = {
                            if (cartItemCount > 0) {
                                Badge { Text(cartItemCount.toString()) }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onProductClick = {
                        mainViewModel.navigateTo(
                            NavigationEvent.NavigateTo(
                                route = Screen.DetalleProducto,
                                productoId = product.id
                            )
                        )
                    },
                    onAddToCart = {
                        cartViewModel.addToCart(product)
                        // Opcional: Podrías mostrar un Snackbar aquí si pasas el hostState
                    }
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onProductClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = buildImageUrl(product.imagenUrl),
                contentDescription = product.nombre,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = formatPrice(product.precio),
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = onAddToCart, modifier = Modifier.align(Alignment.End)) {
                    Text("Agregar")
                }
            }
        }
    }
}