package com.oppalab.moca.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R

class TextColorAdapter(
    var context: Context,
    var listener: ColorAdapterClickListener) :
    RecyclerView.Adapter<TextColorAdapter.ColorViewHolder>() {


    internal lateinit var colorList:List<Int>

    init {
        this.colorList = genColorList()!!
    }
    interface ColorAdapterClickListener {
        fun onColorItemSelected(color: Int)
    }

    private fun genColorList(): List<Int>? {
        var colorList = ArrayList<Int>()

        colorList.add(Color.parseColor("#ff0000"))
        colorList.add(Color.parseColor("#ff5e00"))
        colorList.add(Color.parseColor("#ffbb00"))
        colorList.add(Color.parseColor("#ffe400"))
        colorList.add(Color.parseColor("#abf200"))
        colorList.add(Color.parseColor("#1fda11"))
        colorList.add(Color.parseColor("#00d8ff"))
        colorList.add(Color.parseColor("#0055ff"))
        colorList.add(Color.parseColor("#0900ff"))
        colorList.add(Color.parseColor("#6600ff"))
        colorList.add(Color.parseColor("#ff00dd"))
        colorList.add(Color.parseColor("#ff007f"))
        colorList.add(Color.parseColor("#000000"))



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