package com.Ivanna.proyects.myapplication.data.local



import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.Ivanna.proyects.myapplication.data.model.LastResult
import com.Ivanna.proyects.myapplication.data.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quizly_prefs")

class PreferencesDataSource(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    val customQuestionsFlow: Flow<List<Question>> = context.dataStore.data.map { prefs ->
        val rawJson = prefs[CUSTOM_QUESTIONS_KEY] ?: return@map emptyList()
        runCatching { json.decodeFromString<List<Question>>(rawJson) }.getOrDefault(emptyList())
    }

    val lastResultFlow: Flow<LastResult?> = context.dataStore.data.map { prefs ->
        val rawJson = prefs[LAST_RESULT_KEY] ?: return@map null
        runCatching { json.decodeFromString<LastResult>(rawJson) }.getOrNull()
    }

    suspend fun saveCustomQuestion(newQuestion: Question) {
        context.dataStore.edit { prefs ->
            val currentList = prefs[CUSTOM_QUESTIONS_KEY]?.let {
                runCatching { json.decodeFromString<List<Question>>(it) }.getOrDefault(emptyList())
            } ?: emptyList()
            val updatedList = currentList + newQuestion
            prefs[CUSTOM_QUESTIONS_KEY] = json.encodeToString(updatedList)
        }
    }

    suspend fun saveLastResult(result: LastResult) {
        context.dataStore.edit { prefs ->
            prefs[LAST_RESULT_KEY] = json.encodeToString(result)
        }
    }

    private companion object {
        val CUSTOM_QUESTIONS_KEY = stringPreferencesKey("custom_questions_json")
        val LAST_RESULT_KEY = stringPreferencesKey("last_result_json")
    }
}