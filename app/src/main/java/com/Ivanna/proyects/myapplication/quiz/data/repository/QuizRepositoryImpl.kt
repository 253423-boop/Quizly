package com.Ivanna.proyects.myapplication.quiz.data.repository

import com.Ivanna.proyects.myapplication.quiz.data.datasource.QuizLocalDataSource
import com.Ivanna.proyects.myapplication.quiz.data.model.Question

class QuizRepositoryImpl(
    private val localDataSource: QuizLocalDataSource
) : QuizRepository {

    override fun getQuestions(category: String, difficulty: String): List<Question> {
        return localDataSource.getQuestions(category, difficulty)
    }

    override fun addCustomQuestion(question: Question) {
        localDataSource.addCustomQuestion(question)
    }

    override fun saveLastResult(summary: String) {
        localDataSource.saveLastResult(summary)
    }

    override fun getLastResult(): String {
        return localDataSource.getLastResult()
    }
}