package com.example.apphuertohogar.navigation

sealed class Screen(val route:String) {

    data object Login: Screen(route= "login_screen")
    data object Registro: Screen(route= "registro_screen")

    data object Home: Screen(route="home_screen")
    data object Carrito: Screen(route="carrito_screen")
    data object Checkout: Screen(route="checkout_screen")
    data object Perfil: Screen(route="perfil_screen")

    // CORRECCIÓN: Restauramos la definición completa con la función 'conId'
    data object DetalleProducto: Screen(route="detalle_producto/{productoId}") {
        // Función helper para construir la ruta con un ID
        fun conId(id: Int): String {
            return "detalle_producto/$id"
        }
    }

    data object PostListApi: Screen(route="post_list_api")
}