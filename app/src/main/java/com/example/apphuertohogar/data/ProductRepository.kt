package com.example.apphuertohogar.data

import com.example.apphuertohogar.data.remote.RetrofitInstance
import com.example.apphuertohogar.model.Product

class ProductRepository {
    private val api = RetrofitInstance.api

    suspend fun getProducts(): Result<List<Product>> { // El repositorio sigue prometiendo una Lista limpia
        return try {
            val response = api.obtenerProductos() // Recibimos el objeto complejo

            // EXTRAEMOS LA LISTA (.content) y la devolvemos
            Result.success(response.content)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- AGREGA ESTA FUNCIÓN QUE FALTABA ---
    suspend fun getProductById(id: Int): Result<Product> {
        return try {
            // Asegúrate de tener este endpoint en ApiService también*
            val response = api.obtenerProductoPorId(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}