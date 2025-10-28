package com.example.apphuertohogar.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Products : Screen("productos")
    object Cart : Screen("carrito")
    object Detail : Screen("detalle/{nombreProducto}")

}
