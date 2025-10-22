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
    private var bmiEz: Double = 1.0

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

    private fun wireWidgets(){
        questionText=findViewById(R.id.questions)
        option1=findViewById(R.id.optionA)
        option2=findViewById(R.id.optionB)
        option3=findViewById(R.id.optionC)
        option4=findViewById(R.id.optionD)
        restart=findViewById(R.id.startQuizButton)
        next=findViewById(R.id.buttonNext)
        textInputName=findViewById(R.id.textInputName)
        textInputHeight=findViewById(R.id.textInputHeight)
        textInputWeight=findViewById(R.id.textInputWeight)
    }//hihi idk why its not commiting

    private fun quizLoad(){
        val locale = resources.configuration.locales.get(0).language
        val quizFile =
            if(locale == "vi"){
                R.raw.quiz_vi
            }
            else{
                R.raw.quiz
            }
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
        optionsNoSee()
        restart.visibility = View.INVISIBLE
        startQuiz()
    }

    private fun optionsSee(){
        option1.visibility = View.VISIBLE
        option2.visibility = View.VISIBLE
        option3.visibility = View.VISIBLE
        option4.visibility = View.VISIBLE
    }

    private fun optionsNoSee(){
        option1.visibility = View.INVISIBLE
        option2.visibility = View.INVISIBLE
        option3.visibility = View.INVISIBLE
        option4.visibility = View.INVISIBLE
    }

    private fun startQuiz(){
        next.visibility = View.VISIBLE
        textInputName.visibility = View.VISIBLE
        textInputHeight.visibility = View.VISIBLE
        textInputWeight.visibility = View.VISIBLE
        restart.visibility = View.INVISIBLE

        next.text = getString(R.string.start_quiz)

        next.setOnClickListener{
            val nameText = textInputName.editText?.text.toString().trim()
            val heightText = textInputHeight.editText?.text.toString().trim()
            val weightText = textInputWeight.editText?.text.toString().trim()

            //all filled
            if(nameText.isEmpty() || heightText.isEmpty() || weightText.isEmpty()){
                //missing
                if(nameText.isEmpty()){
                    textInputName.error = getString(R.string.name_empty)
                }
                else{
                    textInputName.error = null
                }

                if(heightText.isEmpty()){
                    textInputHeight.error = getString(R.string.height_empty)
                }
                else{
                    textInputHeight.error = null
                }

                if(weightText.isEmpty()){
                    textInputWeight.error = getString(R.string.weight_empty)
                }
                else{
                    textInputWeight.error = null
                }
                return@setOnClickListener
            }
            //height and weight
            val heightInches = heightText.toDouble()
            val weightLbs = weightText.toDouble()

            val bmi = (weightLbs * 703) / (heightInches * heightInches)
            bmiEz = bmi * 10
            Log.d("BMI", "BMI = $bmi, multiplier = $bmiEz")
            next.visibility = View.INVISIBLE
            textInputName.visibility = View.INVISIBLE
            textInputHeight.visibility = View.INVISIBLE
            textInputWeight.visibility = View.INVISIBLE
            displayQuestion()
            buttonAnswerIdk()
        }
    }

    private fun displayQuestion(){
        questionText.visibility = View.VISIBLE
        optionsSee()
        val currentQuestion = quiz.getCurrentQuestion()

        questionText.text = "${quiz.getCurrentQuestionNumber()}/${quiz.getTotalQuestions()}\n\n${currentQuestion.question}"
        currentChoices = currentQuestion.choices.toList()

        option1.text = currentChoices[0].first
        option2.text = currentChoices[1].first
        option3.text = currentChoices[2].first
        option4.text = currentChoices[3].first
    }

    private fun buttonAnswerIdk(){
        option1.setOnClickListener{
            scoreAnswer(0)
        }
        option2.setOnClickListener{
            scoreAnswer(1)
        }
        option3.setOnClickListener{
            scoreAnswer(2)
        }
        option4.setOnClickListener{
            scoreAnswer(3)
        }
    }

    private fun scoreAnswer(choiceIndex:Int){
        //points for choice
        val points = currentChoices[choiceIndex].second
        quiz.addPoints(points)

        //more questions?
        if(quiz.hasMoreQuestions()){
            quiz.nextQuestion()
            displayQuestion()
        }
        else{
            finalScore()
        }
    }

    private fun finalScore(){
        val name = textInputName.editText?.text.toString().trim()
        val finalAdScore = (quiz.score+bmiEz)/10
        val bmiMessage = when{
            finalAdScore < 18.5 -> getString(R.string.underweight_message, name)
            finalAdScore > 25 -> getString(R.string.overweight_message, name)
            else -> getString(R.string.healthy_message, name)
        }

        questionText.text = "${getString(R.string.quiz_complete)}\n\n"+"Score: ${"%.2f".format(finalAdScore)}\n"+bmiMessage

        optionsNoSee()
        restart.visibility = View.VISIBLE
        restart.text = getString(R.string.restart_quiz)
        restart.setOnClickListener{
            quizLoad()
        }
    }

}
