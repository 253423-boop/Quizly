package com.Ivanna.proyects.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.Ivanna.proyects.myapplication.quiz.presentation.screens.QuizPage
import com.Ivanna.proyects.myapplication.ui.theme.QuizlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizlyTheme {
                QuizPage()
            }
        }
    }
}