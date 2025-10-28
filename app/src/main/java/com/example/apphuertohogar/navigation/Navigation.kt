package com.example.apphuertohogar.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apphuertohogar.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable(route = "login") { LoginScreen(navController) }
        composable(route = "home") { HomeScreen(navController) }
        composable(route = "productos") { ProductsScreen(navController) }
        composable(route = "carrito") { CartScreen(navController) }
        composable(route = "detalle/{nombreProducto}") { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombreProducto")
            DetailScreen(navController, nombreProducto = nombre ?: "")
        }
    }
}
