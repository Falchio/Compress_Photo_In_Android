package ru.piteravto.takeandcompressphoto

import android.graphics.*
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer

private const val TAG = "Compressor"
private const val START_QUALITY = 100
private const val QUALITY_DECREASE_STEP = 10


object Compressor {
    private val FORMAT = Bitmap.CompressFormat.JPEG

    fun compressImage(imageFile: File, limitSize: Int, watermark: String? = null): Bitmap {

        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val waterMarkBitmap = mark(bitmap, watermark)
        val stream = ByteArrayOutputStream()
        var quality = START_QUALITY
        waterMarkBitmap.compress(
            FORMAT, quality, stream
        ) //сжатая Bitmap пишется в ByteArrayOutputStream

        while (stream.size() > limitSize && quality > 10) {
            stream.reset()
            quality -= QUALITY_DECREASE_STEP
            waterMarkBitmap.compress(FORMAT, quality, stream)
        }

        val toBase64String = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
        val fromBase64 = Base64.decode(toBase64String.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(fromBase64, 0, fromBase64.size)
    }

    fun mark(source: Bitmap, watermark: String? = null): Bitmap {
        if (watermark == null) return source
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(source, 0f, 0f, null)
        val paint = getPaint(18)
        val (widthPaint, heightPaint) = paint.getTextWidthAndHeight(watermark)
        canvas.drawText(
            watermark,
            width - widthPaint,
            height - heightPaint,
            paint
        )
        return result
    }

    private fun dpToPx(dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            App.resources.displayMetrics
        )
    }

    /** Подготовка холста */
    private fun getPaint(textSize: Int, isShadowEnable: Boolean = false): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            setTextSize(dpToPx(textSize))
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            if (isShadowEnable) {
                setShadowLayer(2f, 2f, 2f, Color.BLACK)
            }

            color = Color.RED
            textAlign = Paint.Align.LEFT
        }
    }

    private fun Paint.getTextWidthAndHeight(text: String): Pair<Float, Float> {
        val baseline = -this.ascent() // ascent() is negative
        val width: Float = this.measureText(text) + dpToPx(8)
        val height: Float = baseline + this.descent() + dpToPx(4)
        return Pair(width, height)
    }
}
