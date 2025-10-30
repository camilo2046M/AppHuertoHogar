package com.example.apphuertohogar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apphuertohogar.model.Producto
import kotlinx.coroutines.flow.Flow



@Dao
interface ProductoDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow <List<Producto>>

    @Query("SELECT * FROM productos WHERE categoria = :categoria ORDER BY nombre ASC")
    fun obtenerPorCategoria(categoria: String): Flow <List<Producto>>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Producto?

}