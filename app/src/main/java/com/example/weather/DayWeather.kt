package com.example.weather

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.Retrofit.Message
import com.example.weather.Retrofit.RetrofitBuild
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
var base_date_day = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()

class DayWeather {
    var num_of_rows = 290
    val page_no = 1
    val data_type = "JSON"
    var base_time = "2359"
    val nx = "55"
    val ny = "127"

    fun main(context : Context){
        val call = RetrofitBuild.retrofitService.GetWeather(data_type, num_of_rows, page_no, base_date_day, base_time, nx, ny)
        call.enqueue(object : retrofit2.Callback<Message>{
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if (response.isSuccessful){
                    // 방문날짜 기록
                    val pref = context.getSharedPreferences("visited", AppCompatActivity.MODE_PRIVATE)
                    val editor= pref.edit()
                    editor.putInt("today", base_date_day)
                    editor.apply()

                    // 오늘 날짜의 tmp(기온), sky(하늘상태), pty(강수형태)를 받아서 저장
                    val tmp = response.body()!!.response.body.items.item
                    val prefTmp = context.getSharedPreferences("dayTmp", AppCompatActivity.MODE_PRIVATE)
                    val editorTmp = prefTmp.edit()
                    val prefSky = context.getSharedPreferences("daySky", AppCompatActivity.MODE_PRIVATE)
                    val editorSky = prefSky.edit()
                    val prefPty = context.getSharedPreferences("dayPty", AppCompatActivity.MODE_PRIVATE)
                    val editorPty = prefPty.edit()

                    var tmpCnt = 0
                    var skyCnt = 5
                    var ptyCnt = 6
                    var k = 0
                    for(i in 0 until (num_of_rows-2) step(12)){
                        val time = tmp[tmpCnt].fcstTime.toString()

                        editorTmp.putString(k.toString(), tmp[tmpCnt].fcstValue)
                        editorTmp.apply()
                        editorSky.putString(k.toString(), tmp[skyCnt].fcstValue)
                        editorSky.apply()
                        editorPty.putString(k.toString(), tmp[ptyCnt].fcstValue)
                        editorPty.apply()

                        val prefMN = context.getSharedPreferences("MNTmp", AppCompatActivity.MODE_PRIVATE)
                        val editorMN = prefMN.edit()
                        if (time.equals("600")){
                            editorMN.putString("TMN", tmp[tmpCnt+12].fcstValue)
                            editorMN.apply()

                            tmpCnt++
                            skyCnt++
                            ptyCnt++
                        }
                        else if (time.equals("1500")){
                            editorMN.putString("TMX", tmp[tmpCnt+12].fcstValue)
                            editorMN.apply()

                            tmpCnt++
                            skyCnt++
                            ptyCnt++
                        }
                        tmpCnt += 12
                        skyCnt += 12
                        ptyCnt += 12
                        k++
                    }
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                println("api fail : " + t.message)
            }
        })
    }
}