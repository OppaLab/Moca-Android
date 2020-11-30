package com.oppalab.moca.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oppalab.moca.R
import com.oppalab.moca.`interface`.AddStickerFragmentListener
import com.oppalab.moca.adapter.Sticker3Adapter

class Sticker3Fragment : BottomSheetDialogFragment(), Sticker3Adapter.StickerAdapterClickListener {

    internal lateinit var recycler_sticker: RecyclerView
    internal lateinit var add_sticker_btn: Button
    internal var listener: AddStickerFragmentListener?=null
    internal lateinit var a1Adapter: Sticker3Adapter

    internal var sticker_selected = -1

    companion object {
        internal var instance:Sticker3Fragment?=null

        fun getInstance():Sticker3Fragment {
            if (instance == null) {
                instance = Sticker3Fragment()
            }
            return instance!!
        }
    }

    fun setListener(fragmentListener: AddStickerFragmentListener) {
        this.listener = fragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val itemView =  inflater.inflate(R.layout.fragment_sticker, container, false)

        recycler_sticker = itemView.findViewById(R.id.recycler_stickers)
        recycler_sticker.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false)
        recycler_sticker.setHasFixedSize(true)
        recycler_sticker.adapter = Sticker3Adapter(requireContext(), this)

        add_sticker_btn = itemView.findViewById(R.id.add_sticker_btn)
        add_sticker_btn.setOnClickListener {
            listener!!.onStickerSelected(sticker_selected)
        }

        return itemView
    }

    override fun onStickerItemSelected(sticker: Int) {
        sticker_selected = sticker
    }
}

