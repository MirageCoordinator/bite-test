package com.example.testsapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class TestState {
    data class Test(
        val testName: String,
        val questions: List<QuestionState>,
        val useCanonical: Boolean = false
    ) : TestState() {
        var currentQuestion by mutableStateOf(0)
    }

    data class Result(
        val outcomeText: String,
        val outcomeImage: Int
    ) : TestState()

}

class QuestionState(
    val question: Question,
    val showPrevious: Boolean,
    val showDone: Boolean
) {
    var answered: Int? by mutableStateOf(null)
}