package com.example.apphuertohogar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CartScreen(navController: NavController) {
    var total by remember { mutableStateOf(0) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text("Carrito de Compras 🛒", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Aún no hay productos en el carrito")
            Spacer(modifier = Modifier.height(24.dp))
            Text("Total: $$total")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Volver al Inicio")
            }
        }
    }
}
