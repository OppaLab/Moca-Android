package com.oppalab.moca.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.fonts.Font
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oppalab.moca.R
import com.oppalab.moca.`interface`.AddTextFragmentListener
import com.oppalab.moca.`interface`.BrushFragmentListener
import com.oppalab.moca.adapter.FontAdapter
import com.oppalab.moca.adapter.TextColorAdapter
import java.lang.StringBuilder

class AddTextFragment : BottomSheetDialogFragment(), TextColorAdapter.ColorAdapterClickListener,
    FontAdapter.FontAdapterClickListener {

    var colorSelcted:Int = Color.parseColor("#000000") //default
    var typeFace = Typeface.DEFAULT

    internal var listener: AddTextFragmentListener?=null

    fun setListener(listener: AddTextFragmentListener)
    {
        this.listener = listener
    }

    var edt_add_text:EditText?=null
    var recycler_color:RecyclerView?=null
    var recycler_font:RecyclerView?=null
    var btn_done:Button?=null
    var colorAdapter:TextColorAdapter?=null
    var fontAdapter:FontAdapter?=null

    companion object {
        internal  var instance:AddTextFragment?=null

        fun getInstance():AddTextFragment{
            if(instance == null)
                instance = AddTextFragment()
            return instance!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var itemView =  inflater.inflate(R.layout.fragment_add_text, container, false)

        edt_add_text = itemView.findViewById<EditText>(R.id.edt_add_text)
        btn_done = itemView.findViewById<Button>(R.id.btn_done)
        recycler_color = itemView.findViewById<RecyclerView>(R.id.recycler_color)
        recycler_color!!.setHasFixedSize(true)
        recycler_color!!.layoutManager = LinearLayoutManager(activity,
            LinearLayoutManager.HORIZONTAL,false)

        recycler_font = itemView.findViewById<RecyclerView>(R.id.recycler_font)
        recycler_font!!.setHasFixedSize(true)
        recycler_font!!.layoutManager = LinearLayoutManager(activity,
            LinearLayoutManager.HORIZONTAL,false)

        colorAdapter = TextColorAdapter(requireContext(), this@AddTextFragment)
        recycler_color!!.adapter = colorAdapter

        fontAdapter = FontAdapter(requireContext(), this@AddTextFragment)
        recycler_font!!.adapter = fontAdapter

        //event
        btn_done!!.setOnClickListener {
            listener!!.onAddTextListener(typeFace, edt_add_text!!.text.toString(),colorSelcted)
        }
        return itemView
    }


    override fun onColorItemSelected(color: Int) {
        colorSelcted = color
    }

    override fun onFontSelected(fontName: String) {
        typeFace = Typeface.createFromAsset(requireContext().assets,StringBuilder("font/")
            .append(fontName).toString())
    }
}