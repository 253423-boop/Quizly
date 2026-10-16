package com.Ivanna.proyects.myapplication.quiz.data.model

data class Question(
    val id: Int,
    val category: String,
    val difficulty: String,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)