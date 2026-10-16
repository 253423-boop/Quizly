package com.Ivanna.proyects.myapplication.quiz.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Ivanna.proyects.myapplication.R
import com.Ivanna.proyects.myapplication.quiz.di.QuizModule
import com.Ivanna.proyects.myapplication.quiz.presentation.components.AddQuestionDialog
import com.Ivanna.proyects.myapplication.quiz.presentation.components.DifficultyContent
import com.Ivanna.proyects.myapplication.quiz.presentation.components.HomeContent
import com.Ivanna.proyects.myapplication.quiz.presentation.components.QuestionContent
import com.Ivanna.proyects.myapplication.quiz.presentation.components.ResultContent
import com.Ivanna.proyects.myapplication.quiz.presentation.viewmodels.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPage(
    viewModel: QuizViewModel = viewModel(
        factory = QuizModule.provideQuizViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            R.string.top_bar_title,
                            uiState.selectedCategory
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        when (uiState.currentStep) {
            ScreenStep.HOME -> {
                HomeContent(
                    lastResultSummary = uiState.lastResultSummary,
                    onCategorySelected = viewModel::onCategorySelected,
                    onOpenAddDialog = { viewModel.onToggleAddDialog(true) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            ScreenStep.DIFFICULTY -> {
                DifficultyContent(
                    selectedCategory = uiState.selectedCategory,
                    selectedDifficulty = uiState.selectedDifficulty,
                    onDifficultySelected = viewModel::onDifficultySelected,
                    onStartQuiz = viewModel::onStartQuiz,
                    onBack = viewModel::onBackToHome,
                    onOpenAddDialog = { viewModel.onToggleAddDialog(true) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            ScreenStep.QUIZ -> {
                val question = uiState.currentQuestion
                if (question != null) {
                    QuestionContent(
                        question = question,
                        currentIndex = uiState.currentIndex,
                        totalQuestions = uiState.totalQuestions,
                        selectedOptionIndex = uiState.selectedOptionIndex,
                        onOptionSelected = viewModel::onOptionSelected,
                        onNextClicked = viewModel::onNextClicked,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

            ScreenStep.RESULTS -> {
                ResultContent(
                    score = uiState.score,
                    percentage = uiState.percentage,
                    correctCount = uiState.correctCount,
                    totalQuestions = uiState.totalQuestions,
                    onRetry = viewModel::onRestartQuiz,
                    onBackToHome = viewModel::onBackToHome,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        if (uiState.showAddDialog) {
            AddQuestionDialog(
                questionText = uiState.newQuestionText,
                option1 = uiState.newOption1,
                option2 = uiState.newOption2,
                option3 = uiState.newOption3,
                option4 = uiState.newOption4,
                correctIndex = uiState.newCorrectIndex,
                onQuestionTextChange = viewModel::onNewQuestionTextChange,
                onOptionChange = viewModel::onNewOptionChange,
                onCorrectIndexChange = viewModel::onNewCorrectIndexChange,
                onDismiss = { viewModel.onToggleAddDialog(false) },
                onSave = viewModel::onSaveCustomQuestion
            )
        }
    }
}