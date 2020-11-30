package com.oppalab.moca.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R

class Sticker1Adapter(
    internal var context: Context,
    internal var listener: StickerAdapterClickListener
) :
    RecyclerView.Adapter<Sticker1Adapter.StickerViewHolder>() {

    internal var stickerList: List<Int>
    internal var row_selected = -1

    init {
        this.stickerList = getStickerList()
    }

    private fun getStickerList(): List<Int> {
        val result = ArrayList<Int>()

        result.add(R.drawable.affection)
        result.add(R.drawable.alms)
        result.add(R.drawable.angry)
        result.add(R.drawable.broke)
        result.add(R.drawable.depression)
        result.add(R.drawable.favourite)
        result.add(R.drawable.fear)
        result.add(R.drawable.love)
        result.add(R.drawable.man)
        result.add(R.drawable.sad_boy)
        result.add(R.drawable.sad_girl)

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