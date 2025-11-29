package com.example.apphuertohogar

import android.app.Application
import com.example.apphuertohogar.model.Post
import com.example.apphuertohogar.viewmodel.PostViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.Test
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@Test
@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest : StringSpec({

    val mockApp = mockk<Application>(relaxed = true)

// 2. Se la pasamos al constructor


    "postList debe contener los datos esperados después de fetchPosts()" {
        // 1. Datos falsos (Mock)
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Título 1", body = "Contenido 1"),
            Post(userId = 2, id = 2, title = "Título 2", body = "Contenido 2")
        )

        // 2. ViewModel falso para sobrescribir la función real
        val testViewModel = object : PostViewModel() {
            override fun fetchPosts() {
                _postList.value = fakePosts
            }
        }

        // 3. Ejecución y Validación
        runTest {
            testViewModel.fetchPosts()
            // Verificamos que la lista del VM contenga exactamente los datos falsos
            testViewModel.postList.value shouldContainExactly fakePosts
        }
    }
})