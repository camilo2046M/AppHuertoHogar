package com.example.apphuertohogar.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // CAMBIO IMPORTANTE:
    // 10.0.2.2 es el "localhost" de tu PC visto desde el Emulador Android.
    // Asegúrate de que tu backend Spring Boot esté corriendo en el puerto 8080.
    private const val BASE_URL = "http://10.0.2.2:9090/api/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}