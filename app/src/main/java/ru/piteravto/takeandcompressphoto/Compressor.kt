package ru.piteravto.takeandcompressphoto

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File

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
// это попытка перевести всё в Base64 и обратно, пока неудачная.
//        val toBase64 = Base64.encode(stream.toByteArray(), Base64.DEFAULT)
//        val string = toBase64.toString()
//        val byteArray = string.toByteArray()
//        val fromBase64 = Base64.decode(byteArray, Base64.DEFAULT)
//        return BitmapFactory.decodeByteArray(fromBase64, 0, fromBase64.size)
        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().size)
    }
}
