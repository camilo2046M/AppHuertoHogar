package com.example.apphuertohogar.data

import com.example.apphuertohogar.model.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao){

    val todosLosProductos: Flow<List<Producto>> = productoDao.obtenerTodos()

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