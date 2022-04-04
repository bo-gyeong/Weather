package com.example.weather

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.Retrofit.*
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

/***** https://hydroponicglass.tistory.com/198 ******/
class MainActivity : AppCompatActivity() {
    var isNight = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        changeTime()

        thread(start = true){
            val call = RetrofitBuild.retrofitService.GetWeather(data_type, num_of_rows, page_no, base_date, base_time, nx, ny)
            call.enqueue(object : retrofit2.Callback<Message>{
                override fun onResponse(call: Call<Message>, response: Response<Message>) {
                    if (response.isSuccessful){  //응답 코드가 200이 아닌 400이어도 onResponse를 호출하기 때문에 if(response.isSuccessful())을 이용해서 성공 처리
                        val sky = response.body()!!.response.body.items.item
                        val arr = arrayListOf<CharSequence>()  // DetailWeather.kt로 데이터 보내기 위해 사용

                        for(i in 0..11){
                            arr.add(sky[i].fcstValue)
                        }

                        binding.sky.text = DetailWeather().skyTV(arr[5] as String)
                        binding.weatherImgView.setImageResource(changeImg(sky[5].fcstValue, sky[6].fcstValue, base_time))
                        binding.tmp.text = sky[0].fcstValue.plus("º")

                        // 낮이면 하늘색, 밤이면 회색 배경
                        if (!isNight){
                            binding.mainCLayout.setBackgroundColor(Color.parseColor("#D6F0FF"))
                        } else{
                            binding.mainCLayout.setBackgroundColor(Color.parseColor("#D8D8D8"))
                        }

                        binding.weatherImgView.setOnClickListener(View.OnClickListener {
                            val intent = Intent(applicationContext, DetailWeather::class.java)
                            intent.putExtra("sky", arr)

                            startActivity(intent)
                        })
                    }
                }
                override fun onFailure(call: Call<Message>, t: Throwable) {
                    println("api fail : " + t.message)
                }
            })
        }

        // 하루에 한 번만 시간별 기온 및 하늘상태 조회
        val pref = getSharedPreferences("visited", MODE_PRIVATE)
        if (pref.getInt("today", 0) != base_date_day){
            DayWeather().main(applicationContext)
        }

        // 저장된 값을 불러와 리사이클러뷰 생성
        val prefTmp = getSharedPreferences("dayTmp", MODE_PRIVATE)
        val prefSky = getSharedPreferences("daySky", MODE_PRIVATE)
        val prefPty = getSharedPreferences("dayPty", MODE_PRIVATE)
        val dayTmpArr = ArrayList<DayItems>()

        for (i in 0..23){
            var dayTmp = prefTmp.getString(i.toString(), "측정불가")
            if (!dayTmp.equals("측정불가")){
                dayTmp = dayTmp.plus("º")
            }

            var time : String
            if(i != 0){
                time = i.toString().plus("시")
            } else{
                time = "자정"
            }

            val daySky = prefSky.getString(i.toString(), "")
            val dayPty = prefPty.getString(i.toString(), "")
            val bTime: String  // changeImg()위해 시간을 규격에 맞게 변경
            if (i.toString().length == 1){
                bTime = "0".plus(i.toString()).plus("00")
            } else{
                bTime = i.toString().plus("00")
            }

            dayTmpArr.add(DayItems(time, changeImg(daySky.toString(), dayPty.toString(), bTime), dayTmp.toString()))
        }
        binding.dayTmp.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.dayTmp.adapter = DayTmpAdapter(dayTmpArr)
    }

    // DetailWeather.kt 위해 시간 변경.. 이거 안하면 일부 시간에서 오류 발생함
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeTime(){
        val btInt = base_time.dropLast(2).toInt()
        when (btInt){
            in 0..1 -> {
                base_date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
                base_time = "2300"
            }
            in 2..4 -> base_time = "0200"
            in 5..7 -> {
                num_of_rows = 302
                base_time = "0500"
            }
            in 8..10 -> base_time = "0800"
            in 11..13 -> base_time = "1100"
            in 14..16 -> {
                num_of_rows = 302
                base_time = "1400"
            }
            in 17..19 -> base_time = "1700"
            in 20..22 -> base_time = "2000"
            in 23..24 -> base_time = "2300"
        }
    }

    // 낮과 밤 판별 및 날씨 이미지 변경
    fun changeImg(sky : String, pty : String, bTime : String) : Int{
        val night = arrayOf("2000", "2100", "2200", "2300", "0000", "0100", "0200", "0300", "0400")
        isNight = night.contains(bTime)

        if (pty.equals("0")){
            if(isNight){
                return when(sky){
                    "1" -> R.drawable.moon
                    "3" -> R.drawable.moon_cloud
                    "4" -> R.drawable.cloud
                    else -> 0
                }
            } else{
                return when(sky){
                    "1" -> R.drawable.sun
                    "3" -> R.drawable.sun_cloud
                    "4" -> R.drawable.cloud
                    else -> 0
                }
            }
        }
        else if (pty.equals("3")){
            return R.drawable.snow
        }
        else{
            return R.drawable.rain
        }
    }
}