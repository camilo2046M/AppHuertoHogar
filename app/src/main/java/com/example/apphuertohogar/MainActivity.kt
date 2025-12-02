package com.example.apphuertohogar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apphuertohogar.model.AuthState
import com.example.apphuertohogar.navigation.NavigationEvent
import com.example.apphuertohogar.navigation.Screen
import com.example.apphuertohogar.ui.cart.CartScreen
import com.example.apphuertohogar.ui.checkout.CheckoutScreen
import com.example.apphuertohogar.ui.detalleproducto.DetalleProductoScreen
import com.example.apphuertohogar.ui.home.HomeScreen
import com.example.apphuertohogar.ui.login.LoginScreen
import com.example.apphuertohogar.ui.perfil.ProfileScreen
import com.example.apphuertohogar.ui.registro.RegistroScreen
import com.example.apphuertohogar.ui.screens.PostScreen
import com.example.apphuertohogar.ui.theme.AppHuertoHogarTheme
import com.example.apphuertohogar.utils.TokenStore
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.LoginViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel
import com.example.apphuertohogar.viewmodel.PerfilViewModel
import com.example.apphuertohogar.viewmodel.PostViewModel
import com.example.apphuertohogar.viewmodel.RegistroViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenStore.init(applicationContext)
        setContent {
            AppHuertoHogarTheme {
                val navController = rememberNavController()

                // Configuración de la Fábrica de ViewModels
                val viewModelFactory = viewModelFactory {
                    initializer { MainViewModel(this@MainActivity.application) }
                    initializer { CartViewModel(this@MainActivity.application) }
                    initializer { PostViewModel() }
                    initializer { LoginViewModel(this@MainActivity.application) }
                    initializer { RegistroViewModel(this@MainActivity.application) }
                    initializer { PerfilViewModel(this@MainActivity.application) }

                }

                // Instanciamos los ViewModels globales
                val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)
                val cartViewModel: CartViewModel = viewModel(factory = viewModelFactory)

                // Observamos el estado de autenticación
                val authState by mainViewModel.authState.collectAsState()

                // EFECTO DE NAVEGACIÓN GLOBAL
                // Escucha los eventos que vienen de cualquier ViewModel (Login, Registro, etc)
                LaunchedEffect(Unit) {
                    mainViewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                // Reemplazo manual de argumentos en la ruta (ej: id producto)
                                val finalRoute = if (event.productoId != null) {
                                    event.route.route.replace("{productoId}", event.productoId.toString())
                                } else {
                                    event.route.route
                                }

                                navController.navigate(route = finalRoute) {
                                    // Lógica para limpiar el historial (Login -> Home)
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

                    // Decidimos la pantalla de inicio según el estado del Token
                    val startDestination = when (authState) {
                        is AuthState.Authenticated -> Screen.Home.route
                        is AuthState.Unauthenticated -> Screen.Login.route
                        is AuthState.Loading -> null
                    }

                    // GUARDIA DE AUTENTICACIÓN
                    when (authState) {
                        is AuthState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        // Si ya sabemos el estado, mostramos la App
                        is AuthState.Authenticated, is AuthState.Unauthenticated -> {
                            if (startDestination != null) {
                                NavHost(
                                    navController = navController,
                                    startDestination = startDestination,
                                    modifier = Modifier.padding(paddingValues = innerPadding)
                                ) {
                                    // Configuraciones de Animación
                                    val animSpec = tween<IntOffset>(durationMillis = 650)
                                    val slideIn = slideInHorizontally(initialOffsetX = { it }, animationSpec = animSpec)
                                    val slideOut = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = animSpec)
                                    val popSlideIn = slideInHorizontally(initialOffsetX = { -it }, animationSpec = animSpec)
                                    val popSlideOut = slideOutHorizontally(targetOffsetX = { it }, animationSpec = animSpec)

                                    // --- PANTALLAS ---

                                    composable(
                                        route = Screen.Login.route,
                                        enterTransition = { slideIn }, exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut },

                                    ) {
                                        val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory)
                                                LoginScreen(
                                                mainViewModel = mainViewModel,
                                            loginViewModel = loginViewModel // <--- Pasamos la instancia correcta
                                        )                                    }

                                    composable(
                                        route = Screen.Registro.route,
                                        enterTransition = { slideIn }, exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
                                    ) {
                                        RegistroScreen(mainViewModel = mainViewModel)
                                    }

                                    composable(
                                        route = Screen.Home.route,
                                        enterTransition = { slideIn }, exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
                                    ) {
                                        HomeScreen(mainViewModel = mainViewModel, cartViewModel = cartViewModel)
                                    }

                                    composable(
                                        route = Screen.Perfil.route,
                                        enterTransition = { slideIn }, exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
                                    ) {
                                        ProfileScreen(mainViewModel = mainViewModel)
                                    }

                                    composable(
                                        route = Screen.Carrito.route,
                                        enterTransition = { slideIn }, exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
                                    ) {
                                        CartScreen(mainViewModel = mainViewModel, cartViewModel = cartViewModel)
                                    }

                                    composable(
                                        route = Screen.DetalleProducto.route,
                                        arguments = listOf(navArgument("productoId") { type = NavType.IntType }),
                                        enterTransition = { slideIn }, exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
                                    ) { backStackEntry ->
                                        val productoId = backStackEntry.arguments?.getInt("productoId")
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

                                    composable(
                                        route = Screen.Checkout.route,
                                        enterTransition = { slideIn }, exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
                                    ) {
                                        CheckoutScreen(mainViewModel = mainViewModel, cartViewModel = cartViewModel)
                                    }

                                    composable(
                                        route = Screen.PostListApi.route,
                                        enterTransition = { slideIn }, exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }
                                    ) {
                                        // Aquí usamos la factory para mantener consistencia
                                        val postViewModel: PostViewModel = viewModel(factory = viewModelFactory)
                                        PostScreen(
                                            mainViewModel = mainViewModel,
                                            postViewModel = postViewModel
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