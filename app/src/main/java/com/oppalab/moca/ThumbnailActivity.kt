package com.oppalab.moca

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.oppalab.moca.`interface`.AddStickerFragmentListener
import com.oppalab.moca.`interface`.AddTextFragmentListener
import com.oppalab.moca.`interface`.BrushFragmentListener
import com.oppalab.moca.`interface`.EmojiFragmentListener
import com.oppalab.moca.fragment.AddTextFragment
import com.oppalab.moca.fragment.BrushFragment
import com.oppalab.moca.fragment.EmojiFragment
import com.oppalab.moca.fragment.StickerFragment
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.android.synthetic.main.activity_thumbnail.*
import kotlinx.android.synthetic.main.thumbnail_content.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class ThumbnailActivity : AppCompatActivity(), BrushFragmentListener, EmojiFragmentListener,
    AddTextFragmentListener, AddStickerFragmentListener {
    internal var originalImage:Bitmap? = null
    internal lateinit var finalImage: Bitmap
    internal lateinit var brushFragment: BrushFragment
    internal lateinit var emojiFragment: EmojiFragment
    internal lateinit var addTextFragment: AddTextFragment
    internal lateinit var addStickerFragment: StickerFragment

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
            .setDefaultEmojiTypeface(Typeface.createFromAsset(assets, "noto_color_emoji.ttf"))
            .build()

        loadImage()
        brushFragment = BrushFragment.getInstance()
        emojiFragment = EmojiFragment.getInstance()
        addTextFragment = AddTextFragment.getInstance()
        addStickerFragment = StickerFragment.getInstance()

        thumbnail_brush_btn.setOnClickListener {
            if (brushFragment != null) {

                photoEditor.setBrushDrawingMode(true)

                brushFragment.setListener(this@ThumbnailActivity)
                brushFragment.show(supportFragmentManager, brushFragment.tag)
            }
        }

        thumbnail_emoji_btn.setOnClickListener {
            if (emojiFragment != null) {
                emojiFragment.setListener(this@ThumbnailActivity)
                emojiFragment.show(supportFragmentManager, emojiFragment.tag)
            }
        }

        thumbnail_text_btn.setOnClickListener {
            if (addTextFragment != null) {
                addTextFragment.setListener(this@ThumbnailActivity)
                addTextFragment.show(supportFragmentManager, addTextFragment.tag)
            }
        }

        thumbnail_sticker_btn.setOnClickListener {
            if (addStickerFragment != null) {
                addStickerFragment.setListener(this@ThumbnailActivity)
                addStickerFragment.show(supportFragmentManager, addStickerFragment.tag)
            }
        }

        thumbnail_save_btn.setOnClickListener{
            var intent = Intent(applicationContext, AddPostActivity::class.java)

            photoEditor.saveAsBitmap(object : OnSaveBitmap {
                override fun onBitmapReady(saveBitmap: Bitmap?) {
                    var stream = ByteArrayOutputStream()
                    saveBitmap!!.compress(Bitmap.CompressFormat.PNG,100,stream)
                    intent.putExtra("image",stream.toByteArray())
                    startActivity(intent)
                }
                override fun onFailure(e: Exception?) {
                    Log.d("Siball", "jot god")
                }
            })
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

    override fun onEmojiItemSelected(emoji: String) {
        photoEditor.addEmoji(emoji)
    }

    override fun onAddTextListener(typeFace: Typeface, text: String, color: Int) {
        photoEditor.addText(typeFace, text, color)
    }

    override fun onStickerSelected(sticker: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, sticker)
        photoEditor.addImage(bitmap)
    }


}