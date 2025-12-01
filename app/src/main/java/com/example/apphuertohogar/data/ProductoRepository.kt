package com.example.apphuertohogar.data

import com.example.apphuertohogar.data.remote.RetrofitInstance
import com.example.apphuertohogar.model.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para la entidad [Producto].
 * Gestiona la sincronización entre la base de datos local (Room) y el backend remoto.
 */
class ProductoRepository(private val productoDao: ProductoDao){

    // La "Fuente de la Verdad" sigue siendo la base de datos local.
    // La UI observa esto, así que cuando guardemos datos de la API aquí,
    // la pantalla se actualizará automáticamente.
    val todosLosProductos: Flow<List<Producto>> = productoDao.obtenerTodos()

    /**
     * Llama a la API de Spring Boot, obtiene los productos reales
     * y los guarda en Room.
     */
    suspend fun refrescarProductos() {
        try {
            println(">>> Iniciando refresco de productos desde API...")

            // 1. Llamada a la red (al backend local 10.0.2.2)
            // Retrofit nos devuelve un ProductResponse (con la lista en 'content')
            val response = RetrofitInstance.api.obtenerProductos()

            // 2. Extraer la lista real de productos
            val productosRed = response.content

            println(">>> Productos recibidos del backend: ${productosRed.size}")

            // 3. Guardar en BD local (Room actúa como caché)
            // Esto insertará o actualizará los productos existentes si tienen el mismo ID.
            productosRed.forEach {
                productoDao.insertar(it)
            }
            println(">>> Productos guardados en Room exitosamente.")

        } catch (e: Exception) {
            println(">>> Error al refrescar productos: ${e.message}")
            e.printStackTrace()
            // Aquí podrías lanzar una excepción o manejar el error de conexión
        }
    }

    suspend fun insertarProducto(producto: Producto){
        productoDao.insertar(producto)
    }

    fun obtenerPorCategoria(categoria: String): Flow<List<Producto>>{
        return productoDao.obtenerPorCategoria(categoria)
    }

    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return productoDao.obtenerPorId(id)
    }
}