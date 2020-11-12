package com.oppalab.moca.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oppalab.moca.R
import com.oppalab.moca.`interface`.EmojiFragmentListener
import com.oppalab.moca.adapter.EmojiAdapter
import ja.burhanrashid52.photoeditor.PhotoEditor

class EmojiFragment : BottomSheetDialogFragment(), EmojiAdapter.EmojiAdapterListener {
    internal var emojiRecycler: RecyclerView? = null
    internal var listener: EmojiFragmentListener? = null

    fun setListener(listener: EmojiFragmentListener) {
        this.listener = listener
    }

    override fun onEmojiItemSelected(emoji: String) {
        listener!!.onEmojiItemSelected(emoji)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val itemView = inflater.inflate(R.layout.fragment_emoji, container, false)

        emojiRecycler = itemView.findViewById(R.id.emoji_recyclerview) as RecyclerView
        emojiRecycler!!.setHasFixedSize(true)
        emojiRecycler!!.layoutManager = GridLayoutManager(activity, 5)

        val adapter = EmojiAdapter(requireContext(), PhotoEditor.getEmojis(context), this)
        emojiRecycler!!.adapter = adapter

        return itemView
    }

    companion object {
        internal var instance: EmojiFragment?=null

        fun getInstance():EmojiFragment {
            if (instance == null) {
                instance = EmojiFragment()
            }
            return instance!!
        }
    }
}