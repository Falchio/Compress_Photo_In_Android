package ru.piteravto.takeandcompressphoto

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "TakePhoto"
private const val LIMIT_FILE_SIZE  = 512_000

class MainActivity : AppCompatActivity() {
    private lateinit var imageFile: File

    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showToast("Permission Granted.")
            } else {
                showToast("Permission Denied.")
            }
        }

    private val getCameraImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Log.e(TAG, "file = ${imageFile.absolutePath}")
                val compressedFile = Compressor.compressImage(imageFile, LIMIT_FILE_SIZE, "TEST TEST TEST")
                val imageView = findViewById<ImageView>(R.id.image)
                imageView.setImageBitmap(compressedFile)
            } else {
                this.showToast("Не удалось сделать фотографию")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestSinglePermissionLauncher.launch(Manifest.permission.CAMERA)
        val button: Button = findViewById<Button?>(R.id.button).apply {
            setOnClickListener {
                val file = createTempFile()
                imageFile = file
                val uri = fileUri(file)
                getCameraImage.launch(uri)
            }
        }

    }

    private fun createTempFile(): File {
        val timePattern = "dd.MM.yyyy_HH.mm.ss"
        val timeStump = SimpleDateFormat(timePattern, Locale.getDefault()).format(Date())
        val cacheDir = File(this.filesDir, "images").also {
            if (!it.exists()) it.mkdir()
        }
        val imageFile = File(cacheDir, "image_$timeStump.jpg").also {
            if (!it.exists()) it.createNewFile()
        }
        return imageFile
    }

    private fun fileUri(file: File): Uri {
        return FileProvider.getUriForFile(
            this,
            this.applicationContext.packageName + ".provider",
            file
        )
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}