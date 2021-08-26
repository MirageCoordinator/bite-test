package com.example.testsapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(
    // private val repository: Repository
) : ViewModel() {

    private val repository = Repository
    private val _uiState = MutableLiveData<TestState>()
    val uiState: LiveData<TestState>
        get() = _uiState

    fun startTest(canonical: Boolean) {
        val questions: List<QuestionState> = repository.test.mapIndexed { index, question ->
            val showPrevious = index > 0
            val showDone = index == repository.test.size - 1
            QuestionState(
                question = question,
                showPrevious = showPrevious,
                showDone = showDone
            )
        }
        _uiState.value = TestState.Test(repository.testName, questions, canonical)
            .apply { currentQuestion = 0 }
    }

    fun clearTest() {
        _uiState.value = null
    }

    fun computeResult(state: TestState.Test) {
        val result: List<Result> = repository.outcomes.map { Result(it.text, it.image, 0) }
        for (question in state.questions) {
            val answerIndex = question.answered
            answerIndex?.let {
                val chosenAnswer = question.question.options[answerIndex].outcomeKey
                result[chosenAnswer].points += question.question.options[answerIndex].value
            } ?: return
        }
        val outcome = result.maxByOrNull { it.points }
        if (outcome != null) {
            _uiState.value = TestState.Result(outcome.name, outcome.imageId)
        }
    }
}