package com.example.quizapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.PracticingJsonActivity.Companion.TAG
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var questions:TextView
    private lateinit var option1: Button
    private lateinit var option2: Button
    private lateinit var option3: Button
    private lateinit var option4: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        wireWidgets()
        displayQuestion()
        buttonAnswerIdk()


        val gson = Gson()
        val inputStream = resources.openRawResource(R.raw.quiz)
        val jsonString = inputStream.bufferedReader().use {
            it.readText()
        }
        val type = object : TypeToken<List<Question>>() { }.type
        val questions = gson.fromJson<List<Question>>(jsonString, type)
        //get name, height,weight --> calc bmi
        Log.d(TAG, "onCreate: $questions")
        var quiz = Quiz(questions)


        // listener
        // assign points based on what they chose
        // are there more questions?
        // if so, get the next one and set up the buttons
        // if not, game over screen
    }

    private fun wireWidgets(){
        questions=findViewById(R.id.questions)
        option1=findViewById(R.id.optionA)
        option2=findViewById(R.id.optionB)
        option3=findViewById(R.id.optionC)
        option4=findViewById(R.id.optionD)
    }

    private fun displayQuestion() {
        val currentQuestion = quiz.getCurrentQuestion()
        
        questionText.text = "${quiz.getCurrentQuestionNumber()} / ${quiz.getTotalQuestions()}\n\n${currentQuestion.question}"
        currentChoices = currentQuestion.choices.toList()
        
        option1.text = currentChoices[0].first
        option2.text = currentChoices[1].first
        option3.text = currentChoices[2].first
        option4.text = currentChoices[3].first
    }

    private fun buttonAnswerIdk() {
        option1.setOnClickListener { scoreAnswer(0) }
        option2.setOnClickListener { scoreAnswer(1) }
        option3.setOnClickListener { scoreAnswer(2) }
        option4.setOnClickListener { scoreAnswer(3) }
    }

    private fun scoreAnswer(choiceIndex: Int) {
        //points for choice
        val points = currentChoices[choiceIndex].second
        quiz.addPoints(points)
        
        //more questions?
        if (quiz.hasMoreQuestions()) {
            quiz.nextQuestion()
            displayQuestion()
        } else {
            finalScore()
        }
    }

    private fun finalScore() {
        questionText.text = "${getString(R.string.quiz_complete)}\n\n${getString(R.string.your_score)} ${quiz.score}"
        
        option1.visibility = View.GONE
        option2.visibility = View.GONE
        option3.visibility = View.GONE
        option4.visibility = View.GONE
    }
    
}
