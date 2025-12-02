package com.example.apphuertohogar.model


import com.google.gson.annotations.SerializedName

data class UserUpdateRequest(
    val nombre: String,
    val direccion: String,
    @SerializedName("imagenSrc") val imagenUrl: String? = null
)