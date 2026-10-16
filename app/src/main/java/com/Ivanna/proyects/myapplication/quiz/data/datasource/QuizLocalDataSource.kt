package com.Ivanna.proyects.myapplication.quiz.data.datasource

import android.content.Context
import com.Ivanna.proyects.myapplication.quiz.data.model.Question
import org.json.JSONArray
import org.json.JSONObject

class QuizLocalDataSource(private val context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getQuestions(category: String, difficulty: String): List<Question> {
        val allQuestions = loadSeedQuestions() + loadCustomQuestions()
        return allQuestions
            .filter { it.category == category && it.difficulty == difficulty }
            .shuffled()
            .take(QUESTIONS_PER_GAME)
    }

    fun addCustomQuestion(question: Question) {
        val currentList = loadCustomQuestions().toMutableList()
        currentList.add(question)

        val jsonArray = JSONArray()
        currentList.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("category", item.category)
                put("difficulty", item.difficulty)
                put("text", item.text)
                put("options", JSONArray(item.options))
                put("correctAnswerIndex", item.correctAnswerIndex)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_CUSTOM_QUESTIONS, jsonArray.toString()).apply()
    }

    fun saveLastResult(summary: String) {
        prefs.edit().putString(KEY_LAST_RESULT, summary).apply()
    }

    fun getLastResult(): String {
        return prefs.getString(KEY_LAST_RESULT, "") ?: ""
    }

    private fun loadSeedQuestions(): List<Question> {
        val jsonString = context.assets.open("questions.json")
            .bufferedReader()
            .use { it.readText() }
        return parseQuestionsJson(jsonString)
    }

    private fun loadCustomQuestions(): List<Question> {
        val jsonString = prefs.getString(KEY_CUSTOM_QUESTIONS, "[]") ?: "[]"
        return parseQuestionsJson(jsonString)
    }

    private fun parseQuestionsJson(jsonString: String): List<Question> {
        val list = mutableListOf<Question>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val optionsArray = obj.getJSONArray("options")
            val optionsList = mutableListOf<String>()
            for (j in 0 until optionsArray.length()) {
                optionsList.add(optionsArray.getString(j))
            }
            list.add(
                Question(
                    id = obj.getInt("id"),
                    category = obj.getString("category"),
                    difficulty = obj.getString("difficulty"),
                    text = obj.getString("text"),
                    options = optionsList,
                    correctAnswerIndex = obj.getInt("correctAnswerIndex")
                )
            )
        }
        return list
    }

    private companion object {
        const val PREFS_NAME = "quizly_local_prefs"
        const val KEY_CUSTOM_QUESTIONS = "custom_questions"
        const val KEY_LAST_RESULT = "last_result"
        const val QUESTIONS_PER_GAME = 4
    }
}