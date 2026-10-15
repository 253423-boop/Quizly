package com.Ivanna.proyects.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.Ivanna.proyects.myapplication.data.model.LastResult
import com.Ivanna.proyects.myapplication.data.model.Question
import com.Ivanna.proyects.myapplication.data.repository.QuizRepository
import com.Ivanna.proyects.myapplication.ui.state.NewQuestionFormState
import com.Ivanna.proyects.myapplication.ui.state.QuizScreenStep
import com.Ivanna.proyects.myapplication.ui.state.QuizUiEvent
import com.Ivanna.proyects.myapplication.ui.state.QuizUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    // Patrón Backing Property estricto para proteger la mutabilidad del estado
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        observeRepositoryStreams()
    }

    private fun observeRepositoryStreams() {
        viewModelScope.launch {
            repository.lastResultFlow.collect { lastResult ->
                _uiState.update { it.copy(lastSavedResult = lastResult) }
            }
        }
        viewModelScope.launch {
            repository.observeTotalQuestionsCount().collect { (total, custom) ->
                _uiState.update {
                    it.copy(
                        totalAvailableQuestions = total,
                        customQuestionsCount = custom
                    )
                }
            }
        }
    }

    fun onEvent(event: QuizUiEvent) {
        when (event) {
            is QuizUiEvent.SelectCategory -> {
                _uiState.update { it.copy(selectedCategory = event.category) }
            }
            is QuizUiEvent.SelectDifficulty -> {
                _uiState.update { it.copy(selectedDifficulty = event.difficulty) }
            }
            QuizUiEvent.StartQuiz -> startNewMatch()
            is QuizUiEvent.SelectOption -> {
                _uiState.update { it.copy(selectedAnswerIndex = event.index) }
            }
            QuizUiEvent.SubmitAnswerAndAdvance -> processAnswerAndAdvance()
            QuizUiEvent.PlayAgain -> startNewMatch()
            QuizUiEvent.BackToHome -> {
                _uiState.update { it.copy(currentStep = QuizScreenStep.HOME, selectedAnswerIndex = null) }
            }
            is QuizUiEvent.ToggleAddQuestionDialog -> {
                _uiState.update {
                    it.copy(
                        isAddDialogOpen = event.isOpen,
                        formState = NewQuestionFormState(
                            category = it.selectedCategory,
                            difficulty = it.selectedDifficulty
                        )
                    )
                }
            }
            is QuizUiEvent.UpdateFormStatement -> {
                _uiState.update {
                    it.copy(formState = it.formState.copy(statement = event.value, showValidationError = false))
                }
            }
            is QuizUiEvent.UpdateFormOption -> {
                _uiState.update { state ->
                    val updatedForm = when (event.optionNumber) {
                        1 -> state.formState.copy(option1 = event.value, showValidationError = false)
                        2 -> state.formState.copy(option2 = event.value, showValidationError = false)
                        3 -> state.formState.copy(option3 = event.value, showValidationError = false)
                        else -> state.formState.copy(option4 = event.value, showValidationError = false)
                    }
                    state.copy(formState = updatedForm)
                }
            }
            is QuizUiEvent.UpdateFormCategory -> {
                _uiState.update { it.copy(formState = it.formState.copy(category = event.category)) }
            }
            is QuizUiEvent.UpdateFormDifficulty -> {
                _uiState.update { it.copy(formState = it.formState.copy(difficulty = event.difficulty)) }
            }
            is QuizUiEvent.UpdateFormCorrectIndex -> {
                _uiState.update { it.copy(formState = it.formState.copy(correctOptionIndex = event.index)) }
            }
            QuizUiEvent.SaveCustomQuestion -> saveNewCustomQuestion()
        }
    }

    private fun startNewMatch() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val selectedQuestions = repository.getQuestionsForMatch(
                category = currentState.selectedCategory,
                difficulty = currentState.selectedDifficulty,
                questionsPerMatch = QUESTIONS_PER_MATCH
            )
            _uiState.update {
                it.copy(
                    currentStep = QuizScreenStep.QUIZ,
                    matchQuestions = selectedQuestions,
                    currentQuestionIndex = 0,
                    selectedAnswerIndex = null,
                    correctAnswersCount = 0,
                    finalScorePoints = 0,
                    finalPercentage = 0
                )
            }
        }
    }

    private fun processAnswerAndAdvance() {
        val state = _uiState.value
        val currentQuestion = state.currentQuestion ?: return
        val chosenIndex = state.selectedAnswerIndex ?: return

        val isCorrect = chosenIndex == currentQuestion.correctIndex
        val updatedCorrectCount = if (isCorrect) state.correctAnswersCount + 1 else state.correctAnswersCount

        if (state.isLastQuestion) {
            val totalPoints = updatedCorrectCount * POINTS_PER_CORRECT_ANSWER
            val percentage = (updatedCorrectCount * 100) / state.matchQuestions.size.coerceAtLeast(1)

            val result = LastResult(
                category = state.selectedCategory,
                difficulty = state.selectedDifficulty,
                scorePoints = totalPoints,
                percentage = percentage,
                correctAnswers = updatedCorrectCount
            )

            viewModelScope.launch {
                repository.saveMatchResult(result)
            }

            _uiState.update {
                it.copy(
                    currentStep = QuizScreenStep.RESULTS,
                    correctAnswersCount = updatedCorrectCount,
                    finalScorePoints = totalPoints,
                    finalPercentage = percentage,
                    selectedAnswerIndex = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    correctAnswersCount = updatedCorrectCount,
                    selectedAnswerIndex = null
                )
            }
        }
    }

    private fun saveNewCustomQuestion() {
        val form = _uiState.value.formState
        val options = listOf(form.option1.trim(), form.option2.trim(), form.option3.trim(), form.option4.trim())

        if (form.statement.isBlank() || options.any { it.isBlank() }) {
            _uiState.update { it.copy(formState = form.copy(showValidationError = true)) }
            return
        }

        val customQuestion = Question(
            id = "custom_${UUID.randomUUID()}",
            category = form.category,
            difficulty = form.difficulty,
            statement = form.statement.trim(),
            options = options,
            correctIndex = form.correctOptionIndex,
            isCustom = true
        )

        viewModelScope.launch {
            repository.addCustomQuestion(customQuestion)
            _uiState.update {
                it.copy(
                    isAddDialogOpen = false,
                    formState = NewQuestionFormState()
                )
            }
        }
    }

    companion object {
        private const val QUESTIONS_PER_MATCH = 4
        private const val POINTS_PER_CORRECT_ANSWER = 25

        fun provideFactory(repository: QuizRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return QuizViewModel(repository) as T
                }
            }
    }
}