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
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var questionText:TextView
    private lateinit var option1: Button
    private lateinit var option2: Button
    private lateinit var option3: Button
    private lateinit var option4: Button
    private lateinit var quiz: Quiz
    private lateinit var currentChoices: List<Pair<String, Double>>
    private lateinit var restart: Button
    private lateinit var next: Button
    private lateinit var textInputName: TextInputLayout
    private lateinit var textInputHeight: TextInputLayout
    private lateinit var textInputWeight: TextInputLayout
    private var bmiMultiplier: Double = 1.0


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
        quizLoad()


        // listener
        // assign points based on what they chose
        // are there more questions?
        // if so, get the next one and set up the buttons
        // if not, game over screen
    }

    private fun quizLoad(){
        val quizFile =
            if (resources.configuration.locales.get(0).language == "vi")
                R.raw.quiz_vi
            else
                R.raw.quiz
        val gson = Gson()
        val inputStream = resources.openRawResource(quizFile)
        val jsonString = inputStream.bufferedReader().use {
            it.readText()
        }
        val type = object : TypeToken<List<Question>>() { }.type
        val questions = gson.fromJson<List<Question>>(jsonString, type)
        //get name, height,weight --> calc bmi
        Log.d(TAG, "onCreate: $questions")
        quiz = Quiz(questions)

        questionText.visibility = View.INVISIBLE
        option1.visibility = View.INVISIBLE
        option2.visibility = View.INVISIBLE
        option3.visibility = View.INVISIBLE
        option4.visibility = View.INVISIBLE
        restart.visibility = View.INVISIBLE
        startQuiz()
    }

    private fun startQuiz() {
        next.visibility = View.VISIBLE
        textInputName.visibility = View.VISIBLE
        textInputHeight.visibility = View.VISIBLE
        textInputWeight.visibility = View.VISIBLE
        restart.visibility = View.INVISIBLE

        next.setOnClickListener{
            val nameText = textInputName.editText?.text.toString().trim()
            val heightText = textInputHeight.editText?.text.toString().trim()
            val weightText = textInputWeight.editText?.text.toString().trim()

            //all filled
            if (nameText.isEmpty() || heightText.isEmpty() || weightText.isEmpty()) {
                //missing
                if (nameText.isEmpty()) textInputName.error = "Please enter your name"
                if (heightText.isEmpty()) textInputHeight.error = "Please enter your height"
                if (weightText.isEmpty()) textInputWeight.error = "Please enter your weight"
                return@setOnClickListener
            }

            textInputName.error = null
            textInputHeight.error = null
            textInputWeight.error = null

            //height and weight
            val heightInches = heightText.toDouble()
            val weightLbs = weightText.toDouble()

            val bmi = (weightLbs * 703) / (heightInches * heightInches)
            bmiMultiplier = bmi * 10
            Log.d("BMI", "BMI = $bmi, multiplier = $bmiMultiplier")
            next.visibility = View.INVISIBLE
            textInputName.visibility = View.INVISIBLE
            textInputHeight.visibility = View.INVISIBLE
            textInputWeight.visibility = View.INVISIBLE
            displayQuestion()
            buttonAnswerIdk()
        }


    }

    private fun wireWidgets(){
        questionText=findViewById(R.id.questions)
        option1=findViewById(R.id.optionA)
        option2=findViewById(R.id.optionB)
        option3=findViewById(R.id.optionC)
        option4=findViewById(R.id.optionD)
        restart=findViewById(R.id.startQuizButton)
        next=findViewById(R.id.buttonNext)
        textInputName = findViewById(R.id.textInputName)
        textInputHeight = findViewById(R.id.textInputHeight)
        textInputWeight = findViewById(R.id.textInputWeight)
    }

    private fun displayQuestion() {
        questionText.visibility = View.VISIBLE
        option1.visibility = View.VISIBLE
        option2.visibility = View.VISIBLE
        option3.visibility = View.VISIBLE
        option4.visibility = View.VISIBLE
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
        val name = textInputName.editText?.text.toString().trim()
        val bmi = bmiMultiplier
        val finalAdjustedScore = (quiz.score + (bmi) / 10)
        val bmiMessage = when {
            finalAdjustedScore < 18.5 -> getString(R.string.underweight_message, name)
            finalAdjustedScore > 25 -> getString(R.string.overweight_message, name)
            else -> getString(R.string.healthy_message, name)
        }

        questionText.text = "${getString(R.string.quiz_complete)}\n\n" +"Score: ${"%.2f".format(finalAdjustedScore)}\n" + bmiMessage

        option1.visibility = View.GONE
        option2.visibility = View.GONE
        option3.visibility = View.GONE
        option4.visibility = View.GONE
        restart.visibility = View.VISIBLE
        restart.text = "Restart Quiz"
        restart.setOnClickListener {
            quizLoad()
        }
    }

}
