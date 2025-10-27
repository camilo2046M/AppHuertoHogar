package com.example.apphuertohogar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.apphuertohogar.model.Producto

@Database(entities= [Producto::class], version = 1 , exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun productoDao(): ProductoDao

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