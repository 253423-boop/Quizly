package com.Ivanna.proyects.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String,
    val category: Category,
    val difficulty: Difficulty,
    val statement: String,
    val options: List<String>,
    val correctIndex: Int,
    val isCustom: Boolean = false
)