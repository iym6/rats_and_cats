package ru.ilyamorozov.rats_and_cats.data.remote

import retrofit2.http.GET
import ru.ilyamorozov.rats_and_cats.data.remote.Post

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}