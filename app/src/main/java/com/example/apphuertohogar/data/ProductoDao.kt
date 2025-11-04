package com.example.apphuertohogar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apphuertohogar.model.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [Producto].
 * Define las operaciones de base de datos para la tabla `productos`.
 */
@Dao
interface ProductoDao{

    /**
     * Inserta un producto. Si ya existe, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)

    /**
     * Obtiene un Flow de todos los productos en la BD, ordenados por nombre.
     */
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow <List<Producto>>

    /**
     * Obtiene un Flow de todos los productos filtrados por categoría.
     */
    @Query("SELECT * FROM productos WHERE categoria = :categoria ORDER BY nombre ASC")
    fun obtenerPorCategoria(categoria: String): Flow <List<Producto>>

    /**
     * Obtiene un solo producto por su ID.
     */
    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Producto?

    /**
     * Cuenta el número total de productos en la tabla.
     * Usado por [HomeViewModel] para decidir si poblar la BD.
     */
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int

}
