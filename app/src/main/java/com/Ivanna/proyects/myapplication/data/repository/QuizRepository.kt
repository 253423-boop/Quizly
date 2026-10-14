package com.Ivanna.proyects.myapplication.data.repository

import com.Ivanna.proyects.myapplication.data.local.AssetsDataSource
import com.Ivanna.proyects.myapplication.data.local.PreferencesDataSource
import com.Ivanna.proyects.myapplication.data.model.Category
import com.Ivanna.proyects.myapplication.data.model.Difficulty
import com.Ivanna.proyects.myapplication.data.model.LastResult
import com.Ivanna.proyects.myapplication.data.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class QuizRepository(
    private val assetsDataSource: AssetsDataSource,
    private val preferencesDataSource: PreferencesDataSource
) {
    private var cachedSeedQuestions: List<Question>? = null

    val lastResultFlow: Flow<LastResult?> = preferencesDataSource.lastResultFlow
    val customQuestionsFlow: Flow<List<Question>> = preferencesDataSource.customQuestionsFlow

    private suspend fun getSeedQuestions(): List<Question> {
        return cachedSeedQuestions ?: assetsDataSource.loadSeedQuestions().also {
            cachedSeedQuestions = it
        }
    }

    fun observeTotalQuestionsCount(): Flow<Pair<Int, Int>> = customQuestionsFlow.map { customList ->
        val seedCount = getSeedQuestions().size
        Pair(seedCount + customList.size, customList.size)
    }

    suspend fun getQuestionsForMatch(
        category: Category,
        difficulty: Difficulty,
        questionsPerMatch: Int = 4
    ): List<Question> {
        val seed = getSeedQuestions()
        val custom = preferencesDataSource.customQuestionsFlow.first()
        return (seed + custom)
            .filter { it.category == category && it.difficulty == difficulty }
            .shuffled()
            .take(questionsPerMatch)
    }

    suspend fun addCustomQuestion(question: Question) {
        preferencesDataSource.saveCustomQuestion(question)
    }

    suspend fun saveMatchResult(result: LastResult) {
        preferencesDataSource.saveLastResult(result)
    }
}