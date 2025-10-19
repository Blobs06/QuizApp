package com.example.quizapp

class Quiz (val questions: List<Question>) {
    var currentQuestionIndex = 0
    var score = 0

    // these are the questions that we need methods to answer
    // are there more questions?
    fun hasMoreQuestions() : Boolean {
        return currentQuestionIndex < questions.size - 1
        // since you are deleting the question from the list as we go, you can just see if it's empty or not
        // also we need to make the list Mutable
        return true;
    }
    
    fun getCurrentQuestion(): Question {
        return questions[currentQuestionIndex]
    }

    fun getCurrentQuestionNumber(): Int {
        return currentQuestionIndex + 1
    }

    // what is the current question?
    // is the answer they selected correct?

    fun getTotalQuestions(): Int {
        return questions.size
    }
    
    fun addPoints(points: Int) {
        score += points
    }
    
    fun nextQuestion() {
        if (hasMoreQuestions()) {
            currentQuestionIndex++
        }
    }

}
