package com.example.apphuertohogar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apphuertohogar.model.AuthState
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.ui.cart.CartScreen
import com.example.apphuertohogar.ui.checkout.CheckoutScreen // Importar la nueva pantalla
import com.example.apphuertohogar.ui.detalleproducto.DetalleProductoScreen
import com.example.apphuertohogar.ui.home.HomeScreen
import com.example.apphuertohogar.ui.login.LoginScreen
import com.example.apphuertohogar.ui.perfil.ProfileScreen
import com.example.apphuertohogar.ui.registro.RegistroScreen
import com.example.apphuertohogar.ui.theme.AppHuertoHogarTheme
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.DetalleProductoViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest


class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            AppHuertoHogarTheme {
                // Obtener ViewModels
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()
                val cartViewModel: CartViewModel = viewModel()

                val authState by mainViewModel.authState.collectAsState()

                // Tu LaunchedEffect para manejar los eventos de navegación
                LaunchedEffect(Unit) {
                    mainViewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                val finalRoute: String = if (event.productoId != null) {
                                    // Construye la ruta para DetalleProducto con el ID
                                    event.route.route.replace(
                                        "{productoId}",
                                        event.productoId.toString()
                                    )
                                } else {
                                    event.route.route
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
                        is AuthState.Loading -> null
                    }

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
                            if (startDestination != null) {

                                NavHost(
                                    navController = navController,
                                    startDestination = startDestination,
                                    modifier = Modifier.padding(paddingValues = innerPadding)
                                ) {
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
                                    // 🚀 RUTA DE CHECKOUT (FINALIZAR COMPRA)
                                    composable(route = Screen.Checkout.route) {
                                        // Asegura la autenticación (aunque el flujo ya lo gestiona)
                                        val currentAuthState by mainViewModel.authState.collectAsState()
                                        if (currentAuthState is AuthState.Authenticated) {
                                            CheckoutScreen(
                                                mainViewModel = mainViewModel,
                                                cartViewModel = cartViewModel
                                            )
                                        } else {
                                            // Fallback: Redirigir a Login si no está autenticado
                                            LaunchedEffect(Unit) {
                                                mainViewModel.navigateTo(NavigationEvent.NavigateTo(Screen.Login))
                                            }
                                        }
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
// Función PlaceholderScreen eliminada, ya no es necesaria en el NavHost.
@Composable
fun PlaceholderScreen(name:String,viewModel: MainViewModel){
    Box(modifier = Modifier.padding(16.dp)){
        Text(text = "Estás en la pantalla: $name")
    }
}