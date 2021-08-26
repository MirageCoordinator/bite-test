package com.example.testsapp

data class Question(
    val questionText: String,
    val options: List<Option>,
    var checked: Boolean = false,
)

data class Option(
    val text: String,
    val outcomeKey: Int,
    val value: Int = 1,
)

data class Result(
    val name: String,
    val imageId: Int,
    var points: Int,
)

data class Outcome(
    val text: String,
    val image: Int
)