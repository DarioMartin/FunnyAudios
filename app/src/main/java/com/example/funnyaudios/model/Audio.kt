package com.example.funnyaudios.model

data class Audio(
    val id: String = "",
    val name: String = "",
    val author: String = "",
    val url: String = "",
    val duration: String = ""
)

enum class Category {
    OTHER
}
