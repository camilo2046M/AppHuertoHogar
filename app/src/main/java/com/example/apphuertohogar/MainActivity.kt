package com.example.apphuertohogar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.ui.theme.AppHuertoHogarTheme
import com.example.apphuertohogar.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.unit.dp
import com.example.apphuertohogar.ui.registro.RegistroScreen
import com.example.apphuertohogar.ui.home.HomeScreen
import com.example.apphuertohogar.viewmodel.CartViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.first
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.navigation.navArgument
import com.example.apphuertohogar.ui.detalleproducto.DetalleProductoScreen
import androidx.navigation.NavType
import com.example.apphuertohogar.model.AuthState
import com.example.apphuertohogar.ui.cart.CartScreen
import com.example.apphuertohogar.ui.login.LoginScreen
import com.example.apphuertohogar.ui.perfil.ProfileScreen


class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            AppHuertoHogarTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()
                val cartViewModel: CartViewModel = viewModel()

                val authState by mainViewModel.authState.collectAsState()

                // Tu LaunchedEffect para manejar los eventos de navegación
                // (Este bloque de código está perfecto, no lo cambies)
                LaunchedEffect(Unit) {
                    mainViewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                val finalRoute: String
                                if (event.productoId != null) {
                                    finalRoute = event.route.route.replace(
                                        "{productoId}",
                                        event.productoId.toString()
                                    )
                                } else {
                                    finalRoute = event.route.route
                                }
                                navController.navigate(route = finalRoute) {
                                    event.popUpToRoute?.let { popUpScreen ->
                                        popUpTo(popUpScreen.route) {
                                            inclusive = event.inclusive
                                        }
                                    }
                                    launchSingleTop = event.singleTop
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                        }
                    }
                }
                Scaffold { innerPadding ->
                    val startDestination = when (authState) {
                        is AuthState.Authenticated -> Screen.Home.route
                        is AuthState.Unauthenticated -> Screen.Login.route
                        is AuthState.Loading -> null // Se maneja abajo
                    }

                    // 2. Usamos el 'when' solo para mostrar "Cargando" o el NavHost
                    when (authState) {
                        is AuthState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is AuthState.Authenticated, is AuthState.Unauthenticated -> {
                            // Solo mostramos el NavHost si ya decidimos la ruta
                            if (startDestination != null) {

                                // 3. ¡UN SOLO NavHost con TODAS las rutas!
                                NavHost(
                                    navController = navController,
                                    startDestination = startDestination, // <-- Aquí se decide dónde empezar
                                    modifier = Modifier.padding(paddingValues = innerPadding)
                                ) {
                                    // Pon TODAS tus pantallas aquí
                                    composable(route = Screen.Login.route) {
                                        LoginScreen(mainViewModel = mainViewModel)
                                    }
                                    composable(route = Screen.Registro.route) {
                                        RegistroScreen(mainViewModel = mainViewModel)
                                    }
                                    composable(route = Screen.Home.route) {
                                        HomeScreen(
                                            mainViewModel = mainViewModel,
                                            cartViewModel = cartViewModel
                                        )
                                    }
                                    composable(route = Screen.Perfil.route) {
                                        ProfileScreen(mainViewModel = mainViewModel)
                                    }
                                    composable(route = Screen.Carrito.route) {
                                        CartScreen(
                                            mainViewModel = mainViewModel,
                                            cartViewModel = cartViewModel
                                        )
                                    }
                                    composable(
                                        route = Screen.DetalleProducto.route,
                                        arguments = listOf(navArgument("productoId") {
                                            type = NavType.IntType
                                        })
                                    ) { backStackEntry ->
                                        val productoId =
                                            backStackEntry.arguments?.getInt("productoId")
                                        if (productoId == null) {
                                            navController.popBackStack()
                                        } else {
                                            DetalleProductoScreen(
                                                mainViewModel = mainViewModel,
                                                cartViewModel = cartViewModel,
                                                productoId = productoId
                                            )
                                        }
                                    }
                                    composable(route = Screen.Checkout.route) {
                                        PlaceholderScreen(
                                            name = "Checkout",
                                            viewModel = mainViewModel
                                        )
                                    }
                                }
                            }
                        }
                    }
                 // --- FIN DE LA CORRECCIÓN ---
                } // Fin Scaffold
            } // Fin Theme
        } // Fin setContent
    } // Fin onCreate
} // Fin Activity

@Composable
fun PlaceholderScreen(name:String,viewModel: MainViewModel){
    Box(modifier = Modifier.padding(16.dp)){
        Text(text = "Estás en la pantalla: $name")
        }
    }
