package com.example.funnyaudios.model

data class Audio(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val authors: List<String> = listOf()
)

enum class Category {
    OTHER
}
