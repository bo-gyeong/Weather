package com.example.weather.Retrofit

data class Message(
    val response: RESPONSE
)

data class RESPONSE(
    val header: HEADER,
    val body: BODY
)

data class HEADER(
    val resultCode : Int,
    val resultMsg : String
)

data class BODY(
    val dataType : String,
    val items : ITEMS,
)

data class ITEMS(
    val item : List<ITEM>
)

data class ITEM(
    val category : String,
    val fcstDate : Int,
    val fcstTime : Int,
    val fcstValue : String,
    val nx : String,
    val ny : String
)