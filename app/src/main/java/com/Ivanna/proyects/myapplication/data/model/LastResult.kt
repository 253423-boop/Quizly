package com.Ivanna.proyects.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LastResult(
    val category: Category,
    val difficulty: Difficulty,
    val scorePoints: Int,
    val percentage: Int,
    val correctAnswers: Int
)