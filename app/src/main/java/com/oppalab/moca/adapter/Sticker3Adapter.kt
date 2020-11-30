package com.oppalab.moca.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R

class Sticker3Adapter(
    internal var context: Context,
    internal var listener: StickerAdapterClickListener
) :
    RecyclerView.Adapter<Sticker3Adapter.StickerViewHolder>() {

    internal var stickerList: List<Int>
    internal var row_selected = -1

    init {
        this.stickerList = getStickerList()
    }

    private fun getStickerList(): List<Int> {
        val result = ArrayList<Int>()

        result.add(R.drawable.parents)
        result.add(R.drawable.money)
        result.add(R.drawable.friend)
        result.add(R.drawable.lover)
        result.add(R.drawable.studying)
        result.add(R.drawable.social_distancing)
        result.add(R.drawable.sick)
        result.add(R.drawable.isolation)
        result.add(R.drawable.employment)
        result.add(R.drawable.learning)
        result.add(R.drawable.school)
        result.add(R.drawable.isolation)
        result.add(R.drawable.high_five)
        result.add(R.drawable.family)
        result.add(R.drawable.family_icon_3)
        result.add(R.drawable.family_icon_4)
        result.add(R.drawable.family_icon_7)
        result.add(R.drawable.family_icon_9)
        result.add(R.drawable.condom)
        result.add(R.drawable.couple1)
        result.add(R.drawable.debt)
        result.add(R.drawable.gender_symbols)
        result.add(R.drawable.marry)
        result.add(R.drawable.physical_abuse)
        result.add(R.drawable.std)




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