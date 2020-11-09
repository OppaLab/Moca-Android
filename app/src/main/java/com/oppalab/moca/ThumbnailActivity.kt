package com.oppalab.moca

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.oppalab.moca.`interface`.BrushFragmentListener
import com.oppalab.moca.fragment.BrushFragment
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.android.synthetic.main.thumbnail_content.*

class ThumbnailActivity : AppCompatActivity(), BrushFragmentListener {
    internal var originalImage:Bitmap? = null
    internal lateinit var finalImage: Bitmap
    internal lateinit var brushFragment: BrushFragment

    lateinit var photoEditor:PhotoEditor

    init {
        System.loadLibrary("NativeImageProcessor")
    }

    object Main {
        val IMAGE_NAME = "moca.png"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thumbnail)

        photoEditor = PhotoEditor.Builder(this@ThumbnailActivity, thumbnail_preview)
            .setPinchTextScalable(true)
            .build()

        loadImage()
        brushFragment = BrushFragment.getInstance()

        thumbnail_brush_btn.setOnClickListener {
            if (brushFragment != null) {

                photoEditor.setBrushDrawingMode(true)

                brushFragment.setListener(this@ThumbnailActivity)
                brushFragment.show(supportFragmentManager, brushFragment.tag)
            }
        }
    }

    private fun loadImage() {

        originalImage = getDrawable(R.drawable.moca)?.toBitmap(300, 300)
        finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        thumbnail_preview.source.setImageBitmap(originalImage)
    }

    override fun onBrushSizeChangedListener(size: Float) {
        photoEditor.brushSize= size
    }

    override fun onBrushOpacityChangedListener(size: Int) {
        photoEditor.setOpacity(size)
    }

    override fun onBrushColorChangedListener(color: Int) {
        photoEditor.brushColor = color
    }

    override fun onBrushStateChangedListener(isEraser: Boolean) {
        if (isEraser) {
            photoEditor.brushEraser()
        } else {
            photoEditor.setBrushDrawingMode(true)
        }
    }
}