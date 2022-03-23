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

    fun compressImage(imageFile: File, limitSize: Int): Bitmap {

        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)


        val stream = ByteArrayOutputStream()
        var quality = START_QUALITY
        bitmap.compress(FORMAT, quality, stream) //сжатая Bitmap пишется в ByteArrayOutputStream
        Log.e(TAG, "first compress: size = ${stream.size()} ")

        while (stream.size() > limitSize && quality > 10) {
            stream.reset()
            quality -= QUALITY_DECREASE_STEP
            bitmap.compress(FORMAT, quality, stream)
            Log.e(TAG, "compressImage: new quality = $quality size = ${stream.size()}")
        }
        val compressedBitmap =
            BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().size)
        val waterMarkBitmap = mark(compressedBitmap, "TEST TEST TEST aklsd;alskd;alksd")

//          Перегоняем в Base64 string и обратно
//        val toBase64String = Base64.encodeToString(waterMarkBitmap.convertToByteArray(), Base64.DEFAULT)
//        val fromBase64 = Base64.decode(toBase64String.toByteArray(), Base64.DEFAULT)
//        return BitmapFactory.decodeByteArray(fromBase64, 0, fromBase64.size)


//        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().size)

        return waterMarkBitmap
    }

    fun mark(source: Bitmap, watermark: String): Bitmap {
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

    fun Bitmap.convertToByteArray(): ByteArray {
        //minimum number of bytes that can be used to store this bitmap's pixels
        val size = this.byteCount

        //allocate new instances which will hold bitmap
        val buffer = ByteBuffer.allocate(size)
        val bytes = ByteArray(size)

        //copy the bitmap's pixels into the specified buffer
        this.copyPixelsToBuffer(buffer)

        //rewinds buffer (buffer position is set to zero and the mark is discarded)
        buffer.rewind()

        //transfer bytes from buffer into the given destination array
        buffer.get(bytes)

        //return bitmap's pixels
        return bytes
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
