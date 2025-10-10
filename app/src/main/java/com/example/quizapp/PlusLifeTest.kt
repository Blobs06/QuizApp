package com.example.quizapp

data class PlusLifeTest(
    val targetTemp: Double,
    val testData: TestData,
    //val testResult: List<Results>
)
data class TestData (
    val temperatureSamples: List<TempSample>,
    val samples: List<SampleInfo>
)
data class SampleInfo (
    val startingChannel: Int,
    val firstChannelResult: Double
//    val numberOfChannels: Int,
//    val samplingTime: Int,
//    val samplingTemperature: Int,
//    val sampleType: Int,
//    val sampleStreamNumber: Int,
//    val currentDataIndex: Int,
//    val totalNumberOfSamples: Int,
)
data class TempSample (
    val time: String,
    val temp: Double
)

