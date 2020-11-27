package com.oppalab.moca.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.ToggleButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oppalab.moca.R
import com.oppalab.moca.`interface`.BrushFragmentListener
import com.oppalab.moca.adapter.ColorAdapter

class BrushFragment : BottomSheetDialogFragment(), ColorAdapter.ColorAdapterClickListener {
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
    var color_recyclerview: RecyclerView? = null
    var seekbar_brush_size: SeekBar? = null
    var seekbar_brush_opacity: SeekBar? = null
    var brush_state_btn: ToggleButton? = null

    internal var listener: BrushFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val itemView = inflater.inflate(R.layout.fragment_brush, container, false)

        color_recyclerview = itemView.findViewById(R.id.brush_color_recyclerview)
        seekbar_brush_size = itemView.findViewById(R.id.seekbar_brush_size)
        seekbar_brush_opacity = itemView.findViewById(R.id.seekbar_brush_opacity)
        brush_state_btn = itemView.findViewById(R.id.brush_state_btn)

        color_recyclerview!!.setHasFixedSize(true)
        color_recyclerview!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        colorAdpater = ColorAdapter(requireContext(), this@BrushFragment)
        color_recyclerview!!.adapter = colorAdpater

        seekbar_brush_size!!.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                listener?.onBrushSizeChangedListener(p1.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        seekbar_brush_opacity!!.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                listener?.onBrushOpacityChangedListener(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        brush_state_btn!!.setOnCheckedChangeListener(object:CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                listener?.onBrushStateChangedListener(p1)
            }

        })

        return itemView
    }

    fun setListener(listener: BrushFragmentListener) {
        this.listener = listener
    }

    override fun onColorItemSelected(color: Int) {
        listener!!.onBrushColorChangedListener(color)
    }
}