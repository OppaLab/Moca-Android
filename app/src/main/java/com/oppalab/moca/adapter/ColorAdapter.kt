package com.oppalab.moca.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R

class ColorAdapter(
    var context: Context,
    var listener: ColorAdapterClickListener) :
    RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {


    internal lateinit var colorList:List<Int>

    init {
        this.colorList = genColorList()!!
    }
    interface ColorAdapterClickListener {
        fun onColorItemSelected(color: Int)
    }

    private fun genColorList(): List<Int>? {
        var colorList = ArrayList<Int>()

        colorList.add(Color.parseColor("#ffffff"))
        colorList.add(Color.parseColor("#131722"))
        colorList.add(Color.parseColor("#ff545e"))
        colorList.add(Color.parseColor("#57bb82"))
        colorList.add(Color.parseColor("#dbeeff"))
        colorList.add(Color.parseColor("#ba5796"))
        colorList.add(Color.parseColor("#bb349b"))
        colorList.add(Color.parseColor("#6e557c"))
        colorList.add(Color.parseColor("#5e40b2"))

        colorList.add(Color.parseColor("#8051cf"))
        colorList.add(Color.parseColor("#895adc"))
        colorList.add(Color.parseColor("#935da0"))
        colorList.add(Color.parseColor("#7a5e93"))
        colorList.add(Color.parseColor("#6c4475"))
        colorList.add(Color.parseColor("#403890"))
        colorList.add(Color.parseColor("#1b36eb"))
        colorList.add(Color.parseColor("#10d6a2"))

        colorList.add(Color.parseColor("#45b9d3"))
        colorList.add(Color.parseColor("#0c6483"))
        colorList.add(Color.parseColor("#487995"))
        colorList.add(Color.parseColor("#428fb9"))
        colorList.add(Color.parseColor("#a183b3"))
        colorList.add(Color.parseColor("#210333"))
        colorList.add(Color.parseColor("#99ffcc"))
        colorList.add(Color.parseColor("#b2b2b2"))

        colorList.add(Color.parseColor("#c0fff4"))
        colorList.add(Color.parseColor("#97ffff"))
        colorList.add(Color.parseColor("#ff1493"))
        colorList.add(Color.parseColor("#caff70"))
        colorList.add(Color.parseColor("#dab420"))

        return colorList
    }

    inner class ColorViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        internal var color_section:CardView
        init {
            color_section = itemView.findViewById(R.id.color_section) as CardView

            itemView.setOnClickListener{
                listener.onColorItemSelected(colorList[adapterPosition])
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.color_item,parent,false)
        return ColorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.color_section.setCardBackgroundColor(colorList[position])
    }

    override fun getItemCount(): Int {
        return colorList.size
    }
}