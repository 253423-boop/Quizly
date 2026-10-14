package com.Ivanna.proyects.myapplication.data.model

import androidx.annotation.StringRes
import com.Ivanna.proyects.myapplication.R
import kotlinx.serialization.Serializable

@Serializable
enum class Difficulty(@StringRes val labelRes: Int) {
    EASY(R.string.difficulty_easy),
    MEDIUM(R.string.difficulty_medium),
    HARD(R.string.difficulty_hard)
}