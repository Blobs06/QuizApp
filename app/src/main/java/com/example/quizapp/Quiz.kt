package com.example.quizapp

class Quiz (
    val question: String,
    val choices: List<Choices>
)
data class Choices(
    val a: Int
)