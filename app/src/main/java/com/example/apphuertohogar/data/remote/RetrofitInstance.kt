package com.example.apphuertohogar.data.remote

import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // CAMBIO IMPORTANTE:
    // 10.0.2.2 es el "localhost" de tu PC visto desde el Emulador Android.
    // Asegúrate de que tu backend Spring Boot esté corriendo en el puerto 8080.
    private const val BASE_URL = "http://52.44.157.216:9090/"
    const val IMAGES_BASE_URL = "http://52.44.157.216:9090"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Muestra URL, Headers y Datos enviados
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}