package com.example.apphuertohogar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.apphuertohogar.model.CarritoItem
import com.example.apphuertohogar.model.Producto
import com.example.apphuertohogar.model.Usuario

// IMPORTANTE:
// 1. entities: Debe tener las 3 clases: [Producto::class, Usuario::class, CarritoItem::class]
// 2. version: Debe ser 5 (o superior si ya la subiste antes)
@Database(entities = [Producto::class, Usuario::class, CarritoItem::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun carritoDao(): CarritoDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun getDatabase(contexto: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "huertohogar_database_v5" // Cambiamos el nombre para forzar una limpia 100% nueva
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}