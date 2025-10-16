package com.example.quizapp

class Quiz (val questions: List<Question>) {
    var currentQuestionIndex = 0
    var score = 0

    // these are the questions that we need methods to answer
    // are there more questions?
    fun hasMoreQuestions() : Boolean {
        // since you are deleting the question from the list as we go, you can just see if it's empty or not
        // also we need to make the list Mutable
    }
    // what is the current question?
    // is the answer they selected correct?

    fun questionTracker(){
        //after 1st go ++
        //keep going till no ++
    }

}