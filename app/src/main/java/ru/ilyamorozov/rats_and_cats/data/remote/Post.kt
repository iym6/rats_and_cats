package ru.ilyamorozov.rats_and_cats.data.remote

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)