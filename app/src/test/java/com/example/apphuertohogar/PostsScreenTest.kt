package com.example.apphuertohogar

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.apphuertohogar.model.Post
import com.example.apphuertohogar.ui.screens.PostScreen // Asegúrate de importar tu pantalla real
import com.example.apphuertohogar.viewmodel.MainViewModel
import com.example.apphuertohogar.viewmodel.PostViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import android.app.Application

class PostScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_titulo_de_post_debe_aparecer_en_pantalla() {
        // 1. Datos simulados
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Titulo 1", body = "Contenido 1"),
            Post(userId = 2, id = 2, title = "Titulo 2", body = "Contenido 2")
        )

        val context = ApplicationProvider.getApplicationContext<Application>()

// 2. Se lo pasamos al constructor
        val fakePostViewModel = object : PostViewModel() {
            override val postList = MutableStateFlow(fakePosts)
        }

        // 2. NUEVO: ViewModel falso para Main (Para rellenar el requisito)
        // Usamos 'open' en MainViewModel si es necesario, igual que antes.
        // Si MainViewModel no tiene lógica abstracta, esto basta:
        val fakeMainViewModel = object : MainViewModel() {}

        // 3. Renderizamos usando los nombres EXACTOS que pide tu error
        composeRule.setContent {
            PostScreen(
                mainViewModel = fakeMainViewModel,  // Agregamos este parámetro que faltaba
                postViewModel = fakePostViewModel   // Cambiamos 'viewModel' por 'postViewModel'
            )
        }


        // 4. Validar que los textos existen en la UI
        composeRule.onNodeWithText("Titulo: Titulo 1").assertIsDisplayed()
        composeRule.onNodeWithText("Titulo: Titulo 2").assertIsDisplayed()
    }
}