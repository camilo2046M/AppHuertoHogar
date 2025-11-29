package com.example.apphuertohogar.data.remote

import com.example.apphuertohogar.model.Post
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET(value = "/posts")
    suspend fun getPosts(): List<Post>

    @POST(value = "/posts")
    suspend fun createPost(@Body post: Post): Post

    @PUT(value = "/posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Post

    @DELETE(value = "/posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Unit
}


