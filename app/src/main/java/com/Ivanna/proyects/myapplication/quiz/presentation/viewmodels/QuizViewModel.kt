package com.Ivanna.proyects.myapplication.quiz.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.Ivanna.proyects.myapplication.quiz.data.model.Question
import com.Ivanna.proyects.myapplication.quiz.data.repository.QuizRepository
import com.Ivanna.proyects.myapplication.quiz.presentation.screens.QuizUiState
import com.Ivanna.proyects.myapplication.quiz.presentation.screens.ScreenStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QuizViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { current ->
            current.copy(lastResultSummary = repository.getLastResult())
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                currentStep = ScreenStep.DIFFICULTY
            )
        }
    }

    fun onDifficultySelected(difficulty: String) {
        _uiState.update { current ->
            current.copy(selectedDifficulty = difficulty)
        }
    }

    fun onStartQuiz() {
        val state = _uiState.value
        val matchQuestions = repository.getQuestions(
            category = state.selectedCategory,
            difficulty = state.selectedDifficulty
        )
        _uiState.update { current ->
            current.copy(
                questions = matchQuestions,
                currentIndex = 0,
                selectedOptionIndex = null,
                correctCount = 0,
                score = 0,
                percentage = 0,
                currentStep = ScreenStep.QUIZ
            )
        }
    }

    fun onOptionSelected(optionIndex: Int) {
        _uiState.update { current ->
            current.copy(selectedOptionIndex = optionIndex)
        }
    }

    fun onNextClicked() {
        val state = _uiState.value
        val selected = state.selectedOptionIndex ?: return
        val currentQuestion = state.currentQuestion ?: return

        val isCorrect = selected == currentQuestion.correctAnswerIndex
        val newCorrectCount = if (isCorrect) state.correctCount + 1 else state.correctCount
        val nextIndex = state.currentIndex + 1

        if (nextIndex >= state.totalQuestions) {
            val finalPoints = newCorrectCount * 25
            val finalPercentage = (newCorrectCount * 100) / state.totalQuestions.coerceAtLeast(1)
            val summary = "${state.selectedCategory} (${state.selectedDifficulty}): $finalPoints pts ($finalPercentage%)"

            repository.saveLastResult(summary)

            _uiState.update { current ->
                current.copy(
                    correctCount = newCorrectCount,
                    score = finalPoints,
                    percentage = finalPercentage,
                    lastResultSummary = summary,
                    selectedOptionIndex = null,
                    currentStep = ScreenStep.RESULTS
                )
            }
        } else {
            _uiState.update { current ->
                current.copy(
                    currentIndex = nextIndex,
                    correctCount = newCorrectCount,
                    selectedOptionIndex = null
                )
            }
        }
    }

    fun onRestartQuiz() {
        onStartQuiz()
    }

    fun onBackToHome() {
        _uiState.update { current ->
            current.copy(
                currentStep = ScreenStep.HOME,
                selectedOptionIndex = null
            )
        }
    }

    fun onToggleAddDialog(show: Boolean) {
        _uiState.update { current ->
            current.copy(
                showAddDialog = show,
                newQuestionText = "",
                newOption1 = "",
                newOption2 = "",
                newOption3 = "",
                newOption4 = "",
                newCorrectIndex = 0
            )
        }
    }

    fun onNewQuestionTextChange(value: String) {
        _uiState.update { current -> current.copy(newQuestionText = value) }
    }

    fun onNewOptionChange(optionNumber: Int, value: String) {
        _uiState.update { current ->
            when (optionNumber) {
                1 -> current.copy(newOption1 = value)
                2 -> current.copy(newOption2 = value)
                3 -> current.copy(newOption3 = value)
                else -> current.copy(newOption4 = value)
            }
        }
    }

    fun onNewCorrectIndexChange(index: Int) {
        _uiState.update { current -> current.copy(newCorrectIndex = index) }
    }

    fun onSaveCustomQuestion() {
        val state = _uiState.value
        if (state.newQuestionText.isBlank() || state.newOption1.isBlank() ||
            state.newOption2.isBlank() || state.newOption3.isBlank() || state.newOption4.isBlank()
        ) {
            return
        }

        val customQuestion = Question(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            category = state.selectedCategory,
            difficulty = state.selectedDifficulty,
            text = state.newQuestionText.trim(),
            options = listOf(
                state.newOption1.trim(),
                state.newOption2.trim(),
                state.newOption3.trim(),
                state.newOption4.trim()
            ),
            correctAnswerIndex = state.newCorrectIndex
        )

        repository.addCustomQuestion(customQuestion)
        onToggleAddDialog(false)
    }
}