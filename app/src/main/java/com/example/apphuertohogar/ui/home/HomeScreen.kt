package com.example.apphuertohogar.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphuertohogar.model.Producto
import com.example.apphuertohogar.viewmodel.HomeViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.apphuertohogar.viewmodel.CartViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import com.example.apphuertohogar.navigation.Screen
import androidx.compose.material.icons.filled.Person
import com.example.apphuertohogar.navigation.NavigationEvent
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import com.example.apphuertohogar.ui.formatPrice

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalItemsInCart = cartItems.sumOf { it.cantidad }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HuertoHogar") },
                colors = TopAppBarDefaults.topAppBarColors( /* ... */ ),
                actions = {IconButton(onClick = { mainViewModel.navigateTo(NavigationEvent.NavigateTo(route = Screen.Carrito)) }) {

                    BadgedBox(
                        badge = {
                            if (totalItemsInCart > 0) {
                                Badge {
                                    Text(text = "$totalItemsInCart")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Carrito de Compras"
                        )
                    }
                }
                    IconButton(onClick = { mainViewModel.navigateTo(NavigationEvent.NavigateTo(route = Screen.Perfil)) }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Mi Perfil"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {

            // --- REEMPLAZA TU if (uiState.isLoading) ... else ... CON ESTO: ---

            // 1. El indicador de carga aparecerá y desaparecerá con un fundido
            AnimatedVisibility(
                visible = uiState.isLoading,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                CircularProgressIndicator()
            }

            AnimatedVisibility(
                visible = !uiState.isLoading,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 300))
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "Nuestros Productos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            cartViewModel = cartViewModel,
                            onCardClick = {
                                mainViewModel.navigateTo(
                                    NavigationEvent.NavigateTo(
                                        route = Screen.DetalleProducto,
                                        productoId = producto.id
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ProductoCard(
    producto: Producto,
    cartViewModel: CartViewModel,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

            // --- Text Content ---
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleLarge)
                Text(producto.descripcion, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = formatPrice(producto.precio),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(onClick = {cartViewModel.addToCart(producto)}) {
                Text("Agregar")
            }
        }
    }
}