package com.example.quizapp

data class Question(
    var question: String,
    var choices: Map<String, Int>
)
