package ru.ilyamorozov.rats_and_cats.util

fun String.formatDate(): String {
    val parts = split("-")
    if (parts.size != 3) return this
    return "${parts[2]}.${parts[1]}.${parts[0]}"
}