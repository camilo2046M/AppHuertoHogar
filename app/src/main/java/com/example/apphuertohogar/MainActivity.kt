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
class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            AppHuertoHogarTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = viewModel()
                val cartViewModel: CartViewModel = viewModel()


                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collectLatest { event ->
                        when (event){
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(route = event.route.route){
                                    event.popUpToRoute?.let {
                                        popUpTo(route=it.route){
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
                    NavHost(
                        navController=navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(paddingValues = innerPadding)
                    ){
                        composable(route= Screen.Login.route){
                            com.example.apphuertohogar.ui.login.LoginScreen(mainViewModel = viewModel)
                        }
                        composable(route= Screen.Registro.route){
                            com.example.apphuertohogar.ui.registro.RegistroScreen(mainViewModel= viewModel)
                        }
                        composable(route= Screen.Home.route){
                            com.example.apphuertohogar.ui.home.HomeScreen(mainViewModel = viewModel, cartViewModel=cartViewModel)
                        }
                        composable(route= Screen.Perfil.route){
                            PlaceholderScreen(name="Perfil",viewModel=viewModel)
                        }
                        composable(route=Screen.Carrito.route){
                            PlaceholderScreen(name="Carrito", viewModel=viewModel)
                        }
                        composable(route= Screen.Checkout.route){
                            PlaceholderScreen(name="Checkout",viewModel=viewModel)
                        }



                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name:String,viewModel: MainViewModel){
    Box(modifier = Modifier.padding(16.dp)){
        Text(text = "Estás en la pantalla: $name")
    }
}