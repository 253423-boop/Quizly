package com.Ivanna.proyects.myapplication.quiz.data.repository

import com.Ivanna.proyects.myapplication.quiz.data.model.Question

interface QuizRepository {
    fun getQuestions(category: String, difficulty: String): List<Question>
    fun addCustomQuestion(question: Question)
    fun saveLastResult(summary: String)
    fun getLastResult(): String
}