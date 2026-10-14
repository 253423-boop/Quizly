package com.Ivanna.proyects.myapplication.data.model



import androidx.annotation.StringRes
import com.Ivanna.proyects.myapplication.R
import kotlinx.serialization.Serializable

@Serializable
enum class Category(@StringRes val labelRes: Int) {
    JAVA(R.string.category_java),
    KOTLIN(R.string.category_kotlin)
}