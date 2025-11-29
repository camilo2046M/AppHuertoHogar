package com.example.apphuertohogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphuertohogar.data.PostRepository
import com.example.apphuertohogar.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


open class PostViewModel: ViewModel() {

    private val repository = PostRepository()
    open val _postList = MutableStateFlow<List<Post>>(value = emptyList())
    open val postList: StateFlow<List<Post>> = _postList


    init {
        fetchPosts()
    }


    open fun fetchPosts() {
        viewModelScope.launch {
            try {
                _postList.value = repository.getPosts()
            } catch (e: Exception) {
                println("Error al obtener datos: ${e.localizedMessage}")
            }
        }
    }


    fun createPost() {
        viewModelScope.launch {
            try {
                val newPost = Post(
                    userId = 1,
                    id = 0,
                    title = "Post de Prueba (AppHuertoHogar)",
                    body = "Este post fue creado desde nuestra app de Android."
                )
                val createdPost = repository.createPost(newPost)
                _postList.update { currentList ->
                    listOf(createdPost) + currentList
                }
            } catch (e: Exception) {
                println("Error al crear el post: ${e.localizedMessage}")
            }
        }
    }


    fun updatePost(post: Post) {
        viewModelScope.launch {
            try {
                val updatedPostData = post.copy(
                    title = post.title + " [EDITADO]"
                )

                val updatedPostFromApi = repository.updatePost(post.id, updatedPostData)

                _postList.update { currentList ->
                    currentList.map {
                        if (it.id == updatedPostFromApi.id) {
                            updatedPostFromApi
                        } else {
                            it
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error al actualizar el post: ${e.localizedMessage}")
            }
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            try {
                repository.deletePost(post.id)


                _postList.update { currentList ->
                    currentList.filter { it.id != post.id }
                }

            } catch (e: Exception) {
                println("Error al eliminar el post: ${e.localizedMessage}")
            }
        }
    }
}