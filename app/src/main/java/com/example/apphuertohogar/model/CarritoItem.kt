package com.example.apphuertohogar.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// Creamos una entidad nueva.
// Usamos 'primaryKeys' para asegurar que solo haya UNA entrada
// por cada par de usuario/producto.
@Entity(
    tableName = "carrito_items",
    primaryKeys = ["usuarioId", "productoId"],
    indices = [Index(value = ["productoId"])],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE // Si se borra el usuario, se borra su carrito
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE // Si se borra el producto, se quita del carrito
        )
    ]
)
data class CarritoItem(
    val usuarioId: Int,
    val productoId: Int,
    val cantidad: Int
)