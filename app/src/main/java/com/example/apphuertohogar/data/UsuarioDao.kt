package com.example.apphuertohogar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.apphuertohogar.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao{

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(user: Usuario): Long
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): Usuario?

    @Update()
    suspend fun updateUser(user: Usuario)
}