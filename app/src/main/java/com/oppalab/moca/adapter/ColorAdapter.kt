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

        colorList.add(Color.parseColor("#ffd8d8"))
        colorList.add(Color.parseColor("#fae0d4"))
        colorList.add(Color.parseColor("#faecc5"))
        colorList.add(Color.parseColor("#faf4c0"))
        colorList.add(Color.parseColor("#e4f7ba"))
        colorList.add(Color.parseColor("#cefbc9"))
        colorList.add(Color.parseColor("#d4f4fa"))
        colorList.add(Color.parseColor("#d9e5ff"))
        colorList.add(Color.parseColor("#dad9ff"))
        colorList.add(Color.parseColor("#e8d9ff"))
        colorList.add(Color.parseColor("#ffd9fa"))
        colorList.add(Color.parseColor("#ffd9ec"))
        colorList.add(Color.parseColor("#f6f6f6"))



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