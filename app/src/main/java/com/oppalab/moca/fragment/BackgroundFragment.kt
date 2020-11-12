package com.oppalab.moca.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.ToggleButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oppalab.moca.R
import com.oppalab.moca.`interface`.BackgroundFragmentListener
import com.oppalab.moca.`interface`.BrushFragmentListener
import com.oppalab.moca.adapter.ColorAdapter
import kotlinx.android.synthetic.main.thumbnail_content.*

class BackgroundFragment : BottomSheetDialogFragment(), ColorAdapter.ColorAdapterClickListener {
    companion object {
        internal var instance: BrushFragment? = null
        fun getInstance():BrushFragment {
            if (instance == null) {
                instance = BrushFragment()
            }
            return instance!!
        }
    }
    var colorAdpater: ColorAdapter? = null
    var background_recycler: RecyclerView? = null

    var set_background_btn: Button? = null

    internal var listener: BackgroundFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val itemView = inflater.inflate(R.layout.fragment_brush, container, false)

        background_recycler = itemView.findViewById(R.id.brush_color_recyclerview)

        set_background_btn = itemView.findViewById(R.id.set_background_btn)

        background_recycler!!.setHasFixedSize(true)
        background_recycler!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        colorAdpater = ColorAdapter(requireContext(), this@BackgroundFragment)
        background_recycler!!.adapter = colorAdpater


        set_background_btn!!.setOnClickListener {
//            thumbnail_preview.
        }
        return itemView
    }

    fun setListener(listener: BackgroundFragmentListener) {
        this.listener = listener
    }

    override fun onColorItemSelected(color: Int) {
        listener!!.onBackgroundColorChangedListener(color)
    }
}