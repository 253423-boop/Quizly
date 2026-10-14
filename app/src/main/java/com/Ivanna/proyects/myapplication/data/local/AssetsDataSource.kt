package com.Ivanna.proyects.myapplication.data.local

import android.content.Context
import com.Ivanna.proyects.myapplication.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class AssetsDataSource(private val context: Context) {

    private val jsonParser = Json { ignoreUnknownKeys = true }

    suspend fun loadSeedQuestions(): List<Question> = withContext(Dispatchers.IO) {
        runCatching {
            context.assets.open(QUESTIONS_FILE_NAME).bufferedReader().use { reader ->
                jsonParser.decodeFromString<List<Question>>(reader.readText())
            }
        }.getOrDefault(emptyList())
    }

    private companion object {
        const val QUESTIONS_FILE_NAME = "questions.json"
    }
}