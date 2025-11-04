package com.example.apphuertohogar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.apphuertohogar.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [Usuario].
 * Define las operaciones de base de datos para la tabla `usuarios`.
 */
@Dao
interface UsuarioDao{

    /**
     * Inserta un nuevo usuario. Falla si el email ya existe (OnConflictStrategy.ABORT).
     * @return El ID (rowId) del usuario recién insertado.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(user: Usuario): Long

    /**
     * Busca un usuario por su email.
     * Usado por [LoginViewModel].
     */
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): Usuario?

    /**
     * Busca un usuario por su ID.
     * Usado por [PerfilViewModel] y [CheckoutViewModel].
     */
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): Usuario?

    /**
     * Actualiza los datos de un usuario existente.
     * Usado por [PerfilViewModel] para guardar cambios.
     */
    @Update()
    suspend fun updateUser(user: Usuario)
}
