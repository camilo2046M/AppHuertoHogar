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
import com.example.apphuertohogar.ui.detalleproducto.DetalleProductoScreen
import com.example.apphuertohogar.ui.home.HomeScreen
import com.example.apphuertohogar.ui.login.LoginScreen
import com.example.apphuertohogar.ui.perfil.ProfileScreen
import com.example.apphuertohogar.ui.registro.RegistroScreen
import com.example.apphuertohogar.ui.theme.AppHuertoHogarTheme
import com.example.apphuertohogar.viewmodel.CartViewModel
import com.example.apphuertohogar.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.IntOffset
import com.example.apphuertohogar.ui.checkout.CheckoutScreen // <-- 1. IMPORTAMOS LA NUEVA PANTALLA


class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            AppHuertoHogarTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()
                val cartViewModel: CartViewModel = viewModel()

                val authState by mainViewModel.authState.collectAsState()

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
                                    val animSpec = tween<IntOffset>(durationMillis = 300)
                                    val slideIn = slideInHorizontally(initialOffsetX = { it }, animationSpec = animSpec)
                                    val slideOut = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = animSpec)
                                    val popSlideIn = slideInHorizontally(initialOffsetX = { -it }, animationSpec = animSpec)
                                    val popSlideOut = slideOutHorizontally(targetOffsetX = { it }, animationSpec = animSpec)

                                    composable(
                                        route= Screen.Login.route,
                                        enterTransition = { slideIn },
                                        exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn },
                                        popExitTransition = { popSlideOut }
                                    ){
                                        LoginScreen(mainViewModel = mainViewModel)
                                    }
                                    composable(
                                        route= Screen.Registro.route,
                                        enterTransition = { slideIn },
                                        exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn },
                                        popExitTransition = { popSlideOut }
                                    ){
                                        RegistroScreen(mainViewModel = mainViewModel)
                                    }
                                    composable(
                                        route= Screen.Home.route,
                                        enterTransition = { slideIn },
                                        exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn },
                                        popExitTransition = { popSlideOut }
                                    ){
                                        HomeScreen(mainViewModel = mainViewModel, cartViewModel=cartViewModel)
                                    }
                                    composable(
                                        route = Screen.Perfil.route,
                                        enterTransition = { slideIn },
                                        exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn },
                                        popExitTransition = { popSlideOut }
                                    ) {
                                        ProfileScreen(mainViewModel = mainViewModel)
                                    }
                                    composable(
                                        route=Screen.Carrito.route,
                                        enterTransition = { slideIn },
                                        exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn },
                                        popExitTransition = { popSlideOut }
                                    ){
                                        CartScreen(
                                            mainViewModel = mainViewModel,
                                            cartViewModel = cartViewModel
                                        )
                                    }
                                    composable(
                                        route = Screen.DetalleProducto.route,
                                        arguments = listOf(navArgument("productoId") { type = NavType.IntType }),
                                        enterTransition = { slideIn },
                                        exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn },
                                        popExitTransition = { popSlideOut }
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
                                        route= Screen.Checkout.route,
                                        enterTransition = { slideIn },
                                        exitTransition = { slideOut },
                                        popEnterTransition = { popSlideIn },
                                        popExitTransition = { popSlideOut }
                                    ){
                                        CheckoutScreen(
                                            mainViewModel = mainViewModel,
                                            cartViewModel = cartViewModel
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

