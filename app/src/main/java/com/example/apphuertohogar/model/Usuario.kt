package com.example.apphuertohogar.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "usuarios", indices = [Index(value=["email"], unique = true)])
data class Usuario (
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val nombre: String="",
    val email: String ="",
    val passHash: String ="",
    val direccion: String= ""
)
