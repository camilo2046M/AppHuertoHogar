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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.apphuertohogar.model.Product // <--- USAR EL NUEVO (Product)
import com.example.apphuertohogar.ui.buildImageUrl
import com.example.apphuertohogar.ui.formatPrice
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import com.example.apphuertohogar.data.CartRepository // O inyectarlo en un ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    mainViewModel: MainViewModel,
    cartViewModel: CartViewModel,
    productoId: Int
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado local para el producto (lo ideal sería un DetalleViewModel, pero esto funciona rápido)
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Repositorio temporal para cargar el dato (mejor mover a ViewModel si puedes)
    val repository = remember { CartRepository() }

    LaunchedEffect(productoId) {
        isLoading = true
        repository.getProductById(productoId)
            .onSuccess {
                product = it
                isLoading = false
            }
            .onFailure {
                isLoading = false
                scope.launch { snackbarHostState.showSnackbar("Error al cargar producto") }
            }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(product?.nombre ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            product?.let { item ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = buildImageUrl(item.imagenUrl),
                        contentDescription = item.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = item.nombre, style = MaterialTheme.typography.headlineMedium)

                    // PRECIO: Ya es Int, usamos formatPrice directo
                    Text(
                        text = formatPrice(item.precio),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = item.descripcion, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            // AQUÍ ESTABA EL ERROR: Ahora pasamos 'item' que es de tipo 'Product'
                            cartViewModel.addToCart(item)
                            scope.launch { snackbarHostState.showSnackbar("Agregado al carrito") }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Agregar al Carrito")
                    }
                }
            }
        }
    }
}