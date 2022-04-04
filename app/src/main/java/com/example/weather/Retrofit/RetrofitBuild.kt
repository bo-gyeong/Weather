package com.example.weather.Retrofit

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

var num_of_rows = 13
val page_no = 1
val data_type = "JSON"
@SuppressLint("SimpleDateFormat")
var base_time = SimpleDateFormat("HHmm").format(System.currentTimeMillis())
@RequiresApi(Build.VERSION_CODES.O)
var base_date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
val nx = "55"
val ny = "127"

object RetrofitBuild {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/") // 마지막 / 반드시 들어가야 함
        .addConverterFactory(GsonConverterFactory.create()) // converter 지정
        .build() // retrofit 객체 생성

    val retrofitService = retrofit.create(WeatherInterface::class.java)
}