package com.example.funnyaudios.model

data class Audio(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val author: String = "",
    val url: String = ""
)

enum class Category {
    OTHER
}
