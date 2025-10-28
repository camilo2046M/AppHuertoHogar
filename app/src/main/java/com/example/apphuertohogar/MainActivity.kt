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
import com.example.apphuertohogar.navigation.AppNavigation


class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppHuertoHogarTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = viewModel()
                AppNavigation()

            }

            @Composable
            fun PlaceholderScreen(name: String, viewModel: MainViewModel) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Estás en la pantalla: $name")
                }
            }
        }
    }
}