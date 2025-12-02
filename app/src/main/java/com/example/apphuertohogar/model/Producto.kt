package com.example.apphuertohogar.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,

    val descripcion: String,

    // CAMBIO: Cambiamos Double a String para aceptar "$2.500 / kg"
    val precio: Int,

    val categoria: String = "General",

    @SerializedName("imagenSrc")
    val imagenUrl: String,

    val origen: String? = "Chile",
    val stock: Int = 0
)