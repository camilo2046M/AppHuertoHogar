package com.example.apphuertohogar.data

import com.example.apphuertohogar.model.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para la entidad [Producto].
 * Abstrae el acceso a la fuente de datos (actualmente [ProductoDao] de Room).
 * Los ViewModels deben usar este Repositorio en lugar de acceder al DAO directamente.
 *
 * @param productoDao El DAO de Room para los productos.
 */
class ProductoRepository(private val productoDao: ProductoDao){

    /**
     * Un Flow que emite la lista completa de todos los productos, ordenados por nombre.
     */
    val todosLosProductos: Flow<List<Producto>> = productoDao.obtenerTodos()

    /**
     * Inserta un nuevo producto en la base de datos.
     */
    suspend fun insertarProducto(producto: Producto){
        productoDao.insertar(producto)
    }

    /**
     * Obtiene un Flow con la lista de productos filtrados por categoría.
     * @param categoria El string de la categoría a filtrar.
     */
    fun obtenerPorCategoria(categoria: String): Flow<List<Producto>>{
        return productoDao.obtenerPorCategoria(categoria)
    }

    /**
     * Obtiene un solo producto por su ID.
     * @param id El ID del producto a buscar.
     * @return El [Producto] si se encuentra, o `null` si no.
     */
    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return productoDao.obtenerPorId(id)
    }
}
