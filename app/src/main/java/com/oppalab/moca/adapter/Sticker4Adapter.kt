package com.oppalab.moca.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R

class Sticker4Adapter(
    internal var context: Context,
    internal var listener: StickerAdapterClickListener
) :
    RecyclerView.Adapter<Sticker4Adapter.StickerViewHolder>() {

    internal var stickerList: List<Int>
    internal var row_selected = -1

    init {
        this.stickerList = getStickerList()
    }

    private fun getStickerList(): List<Int> {
        val result = ArrayList<Int>()

        result.add(R.drawable.barbell)
        result.add(R.drawable.beer)
        result.add(R.drawable.cigarrete)
        result.add(R.drawable.diet)
        result.add(R.drawable.drug)
        result.add(R.drawable.drugs)
        result.add(R.drawable.gambling)
        result.add(R.drawable.lifting)
        result.add(R.drawable.liquor)
        result.add(R.drawable.money)
        result.add(R.drawable.scalpel)
        result.add(R.drawable.fast_food)
        result.add(R.drawable.stock_market)
        result.add(R.drawable.strong)
        result.add(R.drawable.syringe)

        return result
    }


    inner class StickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var sticker_image: ImageView
        internal var sticker_check: ImageView

        init {
            sticker_image = itemView.findViewById(R.id.thumbnail_sticker_image)
            sticker_check = itemView.findViewById(R.id.thumbnail_sticker_check)
            itemView.setOnClickListener {
                listener.onStickerItemSelected(stickerList.get(adapterPosition))
                row_selected = adapterPosition
                notifyDataSetChanged()
            }
        }
    }

    interface StickerAdapterClickListener {
        fun onStickerItemSelected(sticker: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.sticker_item, parent, false)
        return StickerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        holder.sticker_image.setImageResource(stickerList.get(position))

        if (row_selected == position) {
            holder.sticker_check.visibility = View.VISIBLE
        } else {
            holder.sticker_check.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return stickerList.size
    }
}