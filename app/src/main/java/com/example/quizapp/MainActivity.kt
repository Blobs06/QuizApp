package com.example.quizapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.activity_main),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })

        val gson = Gson()
        val inputStream = resources.openRawResource(R.raw.quiz)
        val jsonString = inputStream.bufferedReader().use {
            //kotlin use lambda makes "it" the object use is called on
            //will close that resource when the block finishes
            it.readText()
        }
        val test = gson.fromJson(jsonString, Quiz::class.java)
        //get name, height,weight --> calc bmi
    }
}