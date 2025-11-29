package com.example.apphuertohogar

import com.example.apphuertohogar.model.Post
import com.example.apphuertohogar.data.remote.ApiService
import com.example.apphuertohogar.data.PostRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

// Clase auxiliar para inyectar el servicio API manualmente (según la guía)
class TestablePostRepository(private val testApi: ApiService) : PostRepository() {
    // Asegúrate de que tu repositorio original permita esta lógica o sobrescribe getPosts
    override suspend fun getPosts(): List<Post> {
        return testApi.getPosts()
    }
}

class PostRepositoryTest : StringSpec({
    "getPosts() debe retornar una lista de posts simulada" {
        // 1. Simular datos
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Título 1", body = "Cuerpo 1"),
            Post(userId = 2, id = 2, title = "Título 2", body = "Cuerpo 2")
        )

        // 2. Crear Mock de la API
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getPosts() } returns fakePosts

        // 3. Instanciar el repo inyectando el mock
        val repo = TestablePostRepository(testApi = mockApi)

        // 4. Testear
        runTest {
            val result = repo.getPosts()
            result shouldContainExactly fakePosts
        }
    }
})


