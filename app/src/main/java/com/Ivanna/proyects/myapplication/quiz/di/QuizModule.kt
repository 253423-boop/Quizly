package com.Ivanna.proyects.myapplication.quiz.di

import android.content.Context
import com.Ivanna.proyects.myapplication.quiz.data.datasource.QuizLocalDataSource
import com.Ivanna.proyects.myapplication.quiz.data.repository.QuizRepository
import com.Ivanna.proyects.myapplication.quiz.data.repository.QuizRepositoryImpl
import com.Ivanna.proyects.myapplication.quiz.presentation.viewmodels.QuizViewModelFactory

object QuizModule {

    fun provideQuizViewModelFactory(context: Context): QuizViewModelFactory {
        val dataSource = QuizLocalDataSource(context.applicationContext)
        val repository: QuizRepository = QuizRepositoryImpl(dataSource)
        return QuizViewModelFactory(repository)
    }
}