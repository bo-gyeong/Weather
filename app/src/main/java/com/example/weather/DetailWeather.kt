package com.example.weather

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import com.example.weather.databinding.ActivityDetailWeatherBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailWeather : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 액션바 변경
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_bar)
        val barTitle =findViewById<TextView>(R.id.title)
        barTitle.text = LocalDate.now().format(DateTimeFormatter.ofPattern("MM월 dd일"))

        val intent = getIntent()
        val skyArr = intent.getCharSequenceArrayListExtra("sky")

        binding.sky.text = skyTV(skyArr?.get(5) as String)
        binding.tmp.text = "${skyArr.get(0)}º"
        binding.pty.text = ptyTV(skyArr.get(6) as String).plus("(${skyArr.get(7)} %)")
        binding.pcp.text = pcpTV(skyArr.get(9) as String)

        val pref = getSharedPreferences("MNTmp", MODE_PRIVATE)
        val min = pref.getString("TMN", "최저기온 측정불가")
        val max = pref.getString("TMX", "최고기온 측정불가")

        if (!min.equals("최저기온 측정불가")){
            binding.tmn.text = min.plus("º")
        } else{
            binding.tmn.text = min
        }

        if (!max.equals("최고기온 측정불가")){
            binding.tmx.text = max.plus("º")
        } else{
            binding.tmx.text = max
        }

        binding.wsd.text = wsdTV(skyArr.get(4) as String)
        binding.vec.text = vecTV(skyArr.get(3) as String)

        val snoStr = snoTV(skyArr.get(11) as String)
        if (!snoStr.equals("적설없음")){
            binding.sno.visibility
            binding.sno.text = snoStr
        }
    }

    // 하늘상태 코드 값 변경
    fun skyTV(sky : String) : String {
        var skyStr = "하늘상태 측정불가"

        if(sky.equals("1")){
            skyStr = "맑음"
        }
        else if (sky.equals("3")){
            skyStr = "구름많음"
        }
        else if (sky.equals("4")){
            skyStr = "흐림"
        }

        return skyStr
    }

    // 강수형태 코드 값 변경
    fun ptyTV(pty : String) : String {
        when(pty){
            "0" -> return "없음"
            "1" -> return "비"
            "2" -> return "비/눈"
            "3" -> return "소나기"
        }
        return "관측불가"
    }

    // 강수량 표시
    fun pcpTV(pcp : String) : String {
        if (pcp.equals("강수없음")){
            return pcp
        }

        val pcpFloat = pcp.toFloat()
        return when(pcpFloat){
            in 0.1..0.9 -> "1.0mm 미만"
            in 1.0..29.9 -> "$pcp mm"
            in 30.0..49.9 -> "30.0~50.0mm"
            else -> "50.0mm 이상"
        }
    }

    // 신적설 표시
    fun snoTV(sno : String) : String {
        if (sno.equals("적설없음")){
            return sno
        }

        val snoFloat = sno.toFloat()
        return when(snoFloat){
            in 0.1..0.9 -> "1.0cm 미만"
            in 1.0..4.9 -> "$sno cm"
            else -> "5.0cm 이상"
        }
    }

    // 풍속 용어 표현
    fun wsdTV(wsd : String) : String {
        val wsdFloat = wsd.toFloat()

        return when(wsdFloat){
            in 0.0..3.9 -> "약함($wsd m/s)"
            in 4.0..8.9 -> "약간강함($wsd m/s)"
            in 9.0..13.9 -> "강함($wsd m/s)"
            else -> "매우강함($wsd m/s)"
        }
    }

    // 풍향 표현 단위
    fun vecTV(vec : String) : String {
        val vecInt = vec.toInt()

        return when(vecInt){
            in 0..44 -> "N-NE"
            in 45..89 -> "NE-E"
            in 90..134 -> "E-SE"
            in 135..179 -> "SE-S"
            in 180..224 -> "S-SW"
            in 225..269 -> "SW-W"
            in 270..314 -> "W-NW"
            in 315..360 -> "NW-N"
            else -> "방향 감지불가"
        }
    }
}