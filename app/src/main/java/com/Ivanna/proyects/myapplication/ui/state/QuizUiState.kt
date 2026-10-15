package com.Ivanna.proyects.myapplication.ui.state

import com.Ivanna.proyects.myapplication.data.model.Category
import com.Ivanna.proyects.myapplication.data.model.Difficulty
import com.Ivanna.proyects.myapplication.data.model.LastResult
import com.Ivanna.proyects.myapplication.data.model.Question

enum class QuizScreenStep {
    HOME,
    QUIZ,
    RESULTS
}

data class NewQuestionFormState(
    val statement: String = "",
    val option1: String = "",
    val option2: String = "",
    val option3: String = "",
    val option4: String = "",
    val category: Category = Category.KOTLIN,
    val difficulty: Difficulty = Difficulty.EASY,
    val correctOptionIndex: Int = 0,
    val showValidationError: Boolean = false
)

data class QuizUiState(
    val currentStep: QuizScreenStep = QuizScreenStep.HOME,
    val selectedCategory: Category = Category.JAVA,
    val selectedDifficulty: Difficulty = Difficulty.EASY,
    val matchQuestions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val correctAnswersCount: Int = 0,
    val finalScorePoints: Int = 0,
    val finalPercentage: Int = 0,
    val lastSavedResult: LastResult? = null,
    val totalAvailableQuestions: Int = 24,
    val customQuestionsCount: Int = 0,
    val isAddDialogOpen: Boolean = false,
    val formState: NewQuestionFormState = NewQuestionFormState()
) {
    val currentQuestion: Question?
        get() = matchQuestions.getOrNull(currentQuestionIndex)

    val progressFraction: Float
        get() = if (matchQuestions.isEmpty()) 0f else (currentQuestionIndex + 1).toFloat() / matchQuestions.size

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == matchQuestions.lastIndex
}

sealed interface QuizUiEvent {
    data class SelectCategory(val category: Category) : QuizUiEvent
    data class SelectDifficulty(val difficulty: Difficulty) : QuizUiEvent
    data object StartQuiz : QuizUiEvent
    data class SelectOption(val index: Int) : QuizUiEvent
    data object SubmitAnswerAndAdvance : QuizUiEvent
    data object PlayAgain : QuizUiEvent
    data object BackToHome : QuizUiEvent
    data class ToggleAddQuestionDialog(val isOpen: Boolean) : QuizUiEvent
    data class UpdateFormStatement(val value: String) : QuizUiEvent
    data class UpdateFormOption(val optionNumber: Int, val value: String) : QuizUiEvent
    data class UpdateFormCategory(val category: Category) : QuizUiEvent
    data class UpdateFormDifficulty(val difficulty: Difficulty) : QuizUiEvent
    data class UpdateFormCorrectIndex(val index: Int) : QuizUiEvent
    data object SaveCustomQuestion : QuizUiEvent
}