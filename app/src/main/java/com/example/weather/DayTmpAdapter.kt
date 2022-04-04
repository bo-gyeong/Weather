package com.example.weather

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class DayTmpAdapter(private val list: ArrayList<DayItems>) : RecyclerView.Adapter<DayTmpAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_tmp, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = list.get(position).time
        holder.weatherImgView.setImageResource(list.get(position).weatherImgView)
        holder.tmp.text = list.get(position).tmp
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val time = v.findViewById<TextView>(R.id.time)
        val weatherImgView = v.findViewById<ImageView>(R.id.weatherImgView)
        val tmp = v.findViewById<TextView>(R.id.tmp)
    }
}

data class DayItems(
    val time : String,
    val weatherImgView : Int,
    val tmp : String
)