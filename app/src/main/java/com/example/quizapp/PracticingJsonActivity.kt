package com.example.quizapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import kotlin.math.min

class PracticingJsonActivity : AppCompatActivity() {

    companion object {
        val TAG = "Practicing Json"
    }

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_practicing_json)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.main_practingJson),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })

        // load the file from json and convert into the data class we created using Gson
        val gson = Gson()
        val inputStream = resources.openRawResource(R.raw.pluslife)
        val jsonString = inputStream.bufferedReader().use {
            //kotlin use lambda makes "it" the object use is called on
            //will close that resource when the block finishes
            it.readText()
        }
        //use gson to convert the jsonString into the data class we want
        val test = gson.fromJson(jsonString, PlusLifeTest::class.java)
        //if you wrote the data classes correctly, we'll get to this line of code
        //you'll see in the logcat the data
        //some potential crashes will look like..:
        //      expected object(data class) but found array (in the JSON) at line x
        //      expected array but found object at Line x
        Log.d(TAG, "onCreate: $test")
        // examples of for loops in java vs kotlin
        // assume that we have List<Template> called testSample
        // in Java, an enhanced for loop would look like
        //for(TempSample sample : tempSamples)
        //in kotlin(it looks like python) --> for(sample in tempSample)

        //java traditional for loop --> for(int i =0; i<tempSamples.size(); i++)

//        val tempSamples = listOf(1234, 1233, 11, 3) //demo purposes
//        for(i in 0 until tempSamples.size){
//            tempSamples[i]
//        }
//        for(i in 0 .. (tempSamples.size-1)){}
//        for(i in tempSamples.indices){}

        //max temp
        val testTemp = test.testData.temperatureSamples
        var maxTemp = 0.0
        var indexofMax = 0
        for(i in 0 until testTemp.size){
            if(testTemp[i].temp > maxTemp){
                maxTemp = testTemp[i].temp
                indexofMax = i
            }
        }
        Log.d(TAG, "onCreate: max : $maxTemp")

        //min temp
        var indexofMin =0
        var minTemp = maxTemp
        for(i in 0 until testTemp.size){
            if(testTemp[i].temp < minTemp){
                minTemp = testTemp[i].temp
                indexofMin = i
            }
        }
        Log.d(TAG, "onCreate: min : $minTemp")

        //average temp
        var avgTemp = 0.0
        for(i in 0 until testTemp.size){
            avgTemp += testTemp[i].temp
        }
        avgTemp/=testTemp.size
        Log.d(TAG, "onCreate: avg : $avgTemp")

        //largest difference from target temperature (- value means below)
        var largestDiffTemp = 0.0
        if(test.targetTemp - minTemp > Math.abs(test.targetTemp - maxTemp)){
            largestDiffTemp = test.targetTemp - minTemp
        }
        else{
            largestDiffTemp = Math.abs(test.targetTemp - maxTemp)
        }
        Log.d(TAG, "onCreate: Largest Difference: $largestDiffTemp")

        /*val diffs = testTemp.map{Math.abs(it.temp-test.targetTemp)}
        val maxIndex = diffs.indexOf
        */

        //location of the largest difference (the index in the temperature samples list)
        Log.d(TAG, "onCreate: Max Diff Index: $indexofMax")

        //number of temperatures that were more than .5 degrees from the target temp
        var tempsAboveTarget = 0
        for(i in 0 until testTemp.size){
            if(Math.abs(testTemp[i].temp-test.targetTemp)>=.5){
                tempsAboveTarget++
            }
        }
        Log.d(TAG, "onCreate: Total Diffs Above Threshold: $tempsAboveTarget")

        //1. Create a list that only contains the firstChannelResult of every sample in the control channel startingChannel = 3.
        var channelList = mutableListOf<Int>()
        for(i in 0 until  test.testData.samples.size){
            if( test.testData.samples[i].startingChannel == 3){
                channelList.add(test.testData.samples[i].firstChannelResult.toInt())
            }
        }

        //val odds = nums6.filter{it%2==1}
        // sample object has a firstChannelResult and startingChannel --> do a filter to get samples where startinChannel = 3
        val samples = test.testData.samples
        val firstChannelResult = samples.filter { it.startingChannel==3 }
                                        .map { it.firstChannelResult }

        //2. Does it increase the whole time? We will consider no change between adjacent items acceptable. So [1, 3, 3, 5] would be considered increasing the whole time but [1, 3, 2, 5] would not.
        var increasing = true
        for(i in 0 until channelList.size-1){
            if(channelList[i] <= channelList[i+1]){
                increasing = true
            }
            else{
                increasing = false
            }
        }
        Log.d(TAG, "onCreate: Is it increasing?: $increasing")

        //3. Create a list of the differences between consecutive data points. What are the largest 3 differences?
        var differenceList = mutableListOf<Int>()
        var helpmepls = mutableListOf<Int>()

        for(i in 0 until channelList.size-1){
            helpmepls.add(Math.abs(channelList[i] - channelList[i+1]))
        }

        helpmepls.sortDescending()
        for(i in 0 until 3){
            differenceList.add(helpmepls[i])
        }

        Log.d(TAG, "onCreate: Top 3 diffs: $differenceList")
    }


}

