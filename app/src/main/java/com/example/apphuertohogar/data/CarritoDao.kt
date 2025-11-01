package com.example.apphuertohogar.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apphuertohogar.model.CartItem // <-- El modelo de UI
import com.example.apphuertohogar.model.CarritoItem // <-- La Entidad
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    /**
     * Esta es la query principal para la UI.
     * Utiliza un JOIN para combinar la tabla de productos y la de carrito,
     * devolviendo la lista de objetos CartItem (Producto + cantidad)
     * para un usuario específico.
     */
    @Query("""
        SELECT 
            P.*,  -- Todos los campos de la tabla Producto
            CI.cantidad  -- El campo 'cantidad' de la tabla CarritoItem
        FROM productos P 
        INNER JOIN carrito_items CI ON P.id = CI.productoId 
        WHERE CI.usuarioId = :usuarioId
    """)
    fun obtenerItemsParaUI(usuarioId: Int): Flow<List<CartItem>> // Devuelve el modelo de UI

    /**
     * Obtiene un item crudo (solo IDs y cantidad) de la BD.
     * Útil para la lógica interna del ViewModel.
     */
    @Query("SELECT * FROM carrito_items WHERE usuarioId = :usuarioId AND productoId = :productoId LIMIT 1")
    suspend fun obtenerItemCrudo(usuarioId: Int, productoId: Int): CarritoItem?

    /**
     * Inserta un nuevo item en el carrito o actualiza su cantidad
     * si ya existe (gracias a OnConflictStrategy.REPLACE).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOActualizar(item: CarritoItem)

    /**
     * Elimina un producto específico del carrito de un usuario.
     */
    @Query("DELETE FROM carrito_items WHERE usuarioId = :usuarioId AND productoId = :productoId")
    suspend fun eliminarProductoDelCarrito(usuarioId: Int, productoId: Int)

    /**
     * Limpia todo el carrito de un usuario (ej. después de un checkout).
     */
    @Query("DELETE FROM carrito_items WHERE usuarioId = :usuarioId")
    suspend fun limpiarCarrito(usuarioId: Int)
}