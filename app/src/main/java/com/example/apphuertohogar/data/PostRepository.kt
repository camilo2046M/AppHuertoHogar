package com.example.apphuertohogar.data

import com.example.apphuertohogar.data.remote.RetrofitInstance
import com.example.apphuertohogar.model.Post


open class PostRepository {


    open suspend fun getPosts(): List<Post> {
        return RetrofitInstance.api.getPosts()
    }


    suspend fun createPost(post: Post): Post {
        return RetrofitInstance.api.createPost(post)
    }


    suspend fun updatePost(id: Int, post: Post): Post {
        return RetrofitInstance.api.updatePost(id, post)
    }


    suspend fun deletePost(id: Int): Unit {
        return RetrofitInstance.api.deletePost(id)
    }
}