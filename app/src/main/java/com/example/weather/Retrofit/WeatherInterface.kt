package com.example.weather.Retrofit

import com.example.weather.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {
    @GET("getVilageFcst?serviceKey=${BuildConfig.WEATHER_API_KEY}")
    fun GetWeather(  // @Query:사용시 파라미터를 url 뒤에 붙여서 전달하기 때문에 유저들에게 쉽게 노출된다.
        @Query("dataType") dataType : String,
        @Query("numOfRows") numOfRows : Int,
        @Query("pageNo") pageNo : Int,
        @Query("base_date") baseDate : Int,
        @Query("base_time") baseTime : String,
        @Query("nx") nx : String,
        @Query("ny") ny : String
    ): retrofit2.Call<Message>
}