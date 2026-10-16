package com.Ivanna.proyects.myapplication.quiz.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Ivanna.proyects.myapplication.R
import kotlin.math.roundToInt

@Composable
fun AddQuestionDialog(
    questionText: String,
    option1: String,
    option2: String,
    option3: String,
    option4: String,
    correctIndex: Int,
    onQuestionTextChange: (String) -> Unit,
    onOptionChange: (Int, String) -> Unit,
    onCorrectIndexChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.dialog_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = onQuestionTextChange,
                    label = { Text(stringResource(R.string.label_question_text)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = option1,
                    onValueChange = { onOptionChange(1, it) },
                    label = { Text(stringResource(R.string.label_option_1)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = option2,
                    onValueChange = { onOptionChange(2, it) },
                    label = { Text(stringResource(R.string.label_option_2)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = option3,
                    onValueChange = { onOptionChange(3, it) },
                    label = { Text(stringResource(R.string.label_option_3)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = option4,
                    onValueChange = { onOptionChange(4, it) },
                    label = { Text(stringResource(R.string.label_option_4)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = stringResource(R.string.label_correct_answer, correctIndex + 1))

                Slider(
                    value = correctIndex.toFloat(),
                    onValueChange = { onCorrectIndexChange(it.roundToInt()) },
                    valueRange = 0f..3f,
                    steps = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(text = stringResource(R.string.btn_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.btn_cancel))
            }
        }
    )
}