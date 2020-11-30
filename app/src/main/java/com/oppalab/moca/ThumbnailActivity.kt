package com.oppalab.moca

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.oppalab.moca.`interface`.*
import com.oppalab.moca.fragment.*
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.android.synthetic.main.activity_thumbnail.*
import kotlinx.android.synthetic.main.thumbnail_content.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class ThumbnailActivity : AppCompatActivity(), BackgroundFragmentListener, BrushFragmentListener, EmojiFragmentListener,
    AddTextFragmentListener, AddStickerFragmentListener {
    internal var originalImage:Bitmap? = null
    internal lateinit var finalImage: Bitmap
    internal lateinit var brushFragment: BrushFragment
    internal lateinit var emojiFragment: EmojiFragment
    internal lateinit var addTextFragment: AddTextFragment
    internal lateinit var addSticker1Fragment: Sticker1Fragment
    internal lateinit var addSticker2Fragment: Sticker2Fragment
    internal lateinit var addSticker3Fragment: Sticker3Fragment
    internal lateinit var backgroundFragment: BackgroundFragment

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

//        loadImage()
        backgroundFragment = BackgroundFragment.getInstance()
        brushFragment = BrushFragment.getInstance()
        emojiFragment = EmojiFragment.getInstance()
        addTextFragment = AddTextFragment.getInstance()
        addSticker1Fragment = Sticker1Fragment.getInstance()
        addSticker2Fragment = Sticker2Fragment.getInstance()
        addSticker3Fragment = Sticker3Fragment.getInstance()

        thumbnail_background_btn.setOnClickListener {
            if (backgroundFragment != null) {

                backgroundFragment.setListener(this@ThumbnailActivity)
                backgroundFragment.show(supportFragmentManager, backgroundFragment.tag)

            }
        }

//        thumbnail_brush_btn.setOnClickListener {
//            if (brushFragment != null) {
//
//                photoEditor.setBrushDrawingMode(true)
//
//                brushFragment.setListener(this@ThumbnailActivity)
//                brushFragment.show(supportFragmentManager, brushFragment.tag)
//            }
//        }

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

        thumbnail_sticker1_btn.setOnClickListener {
            if (addSticker1Fragment != null) {
                addSticker1Fragment.setListener(this@ThumbnailActivity)
                addSticker1Fragment.show(supportFragmentManager, addSticker1Fragment.tag)
            }
        }
        thumbnail_sticker2_btn.setOnClickListener {
            if (addSticker2Fragment != null) {
                addSticker2Fragment.setListener(this@ThumbnailActivity)
                addSticker2Fragment.show(supportFragmentManager, addSticker2Fragment.tag)
            }
        }
        thumbnail_sticker3_btn.setOnClickListener {
            if (addSticker3Fragment != null) {
                addSticker3Fragment.setListener(this@ThumbnailActivity)
                addSticker3Fragment.show(supportFragmentManager, addSticker3Fragment.tag)
            }
        }

        thumbnail_save_btn.setOnClickListener{
            var intent = Intent(applicationContext, AddPostActivity::class.java)

            photoEditor.saveAsBitmap(object : OnSaveBitmap {
                override fun onBitmapReady(saveBitmap: Bitmap?) {
                    var stream = ByteArrayOutputStream()
                    saveBitmap!!.compress(Bitmap.CompressFormat.PNG,100,stream)
                    intent.putExtra("image",stream.toByteArray())
                    intent.putExtra("tag","CREATE")
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

    override fun onBackgroundColorChangedListener(color: Int) {
        thumbnail_preview.background = color.toDrawable()
    }


}