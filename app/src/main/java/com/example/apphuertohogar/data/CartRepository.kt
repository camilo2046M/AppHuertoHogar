package com.example.apphuertohogar.data

import com.example.apphuertohogar.data.remote.AddToCartRequest
import com.example.apphuertohogar.data.remote.RetrofitInstance
import com.example.apphuertohogar.model.CartItemResponse
import com.example.apphuertohogar.model.OrderRequest
import com.example.apphuertohogar.model.Product

class CartRepository {
    private val api = RetrofitInstance.api


    suspend fun getCart(): Result<List<CartItemResponse>> {
        return try {
            val response = api.getCarrito()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToCart(productoId: Int, cantidad: Int): Result<Unit> {
        return try {
            api.addToCart(AddToCartRequest(productoId, cantidad))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromCart(productoId: Int): Result<Unit> {
        return try {
            api.removeFromCart(productoId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Int): Result<Product> {
        return try {
            val response = api.obtenerProductoPorId(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrder(order: OrderRequest): Result<String> {
        return try {
            val response = api.crearPedido(order)
            Result.success(response.paymentUrl) // Devolvemos solo la URL
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}