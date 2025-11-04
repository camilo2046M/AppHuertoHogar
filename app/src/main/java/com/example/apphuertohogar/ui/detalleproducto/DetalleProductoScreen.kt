package com.example.apphuertohogar.ui.detalleproducto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.apphuertohogar.ui.formatPrice
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.DetalleProductoViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    mainViewModel: MainViewModel,
    cartViewModel: CartViewModel,
    detalleViewModel: DetalleProductoViewModel = viewModel(),
    productoId: Int
) {
    val uiState by detalleViewModel.uiState.collectAsState()

    LaunchedEffect(productoId) {
        detalleViewModel.cargarProducto(productoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.producto?.nombre ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.producto != null -> {
                    val producto = uiState.producto!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = producto.imagenUrl,
                            contentDescription = producto.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                producto.nombre,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                formatPrice(producto.precio),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                producto.descripcion,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(32.dp))
                            Button(
                                onClick = { cartViewModel.addToCart(producto) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text("Agregar al Carrito")
                            }
                        }
                    }
                }
                else -> {
                    Text("Producto no encontrado", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}