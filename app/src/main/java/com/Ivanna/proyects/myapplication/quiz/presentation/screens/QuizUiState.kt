package com.Ivanna.proyects.myapplication.quiz.presentation.screens

import com.Ivanna.proyects.myapplication.quiz.data.model.Question

enum class ScreenStep {
    HOME,
    DIFFICULTY,
    QUIZ,
    RESULTS
}

data class QuizUiState(
    val currentStep: ScreenStep = ScreenStep.HOME,
    val selectedCategory: String = "Java",
    val selectedDifficulty: String = "Fácil",
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val correctCount: Int = 0,
    val score: Int = 0,
    val percentage: Int = 0,
    val lastResultSummary: String = "",
    val showAddDialog: Boolean = false,
    val newQuestionText: String = "",
    val newOption1: String = "",
    val newOption2: String = "",
    val newOption3: String = "",
    val newOption4: String = "",
    val newCorrectIndex: Int = 0
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentIndex)

    val totalQuestions: Int
        get() = questions.size
}