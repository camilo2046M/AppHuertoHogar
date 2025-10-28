package com.example.apphuertohogar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.apphuertohogar.model.Producto
import com.example.apphuertohogar.model.Usuario

@Database(entities= [Producto::class, Usuario::class], version = 2 , exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao


    companion object{
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun getDatabase(contexto: Context): AppDatabase{
            return INSTANCIA ?: synchronized(this){
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "huertohogar_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }

}