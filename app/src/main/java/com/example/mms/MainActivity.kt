package com.example.mms

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.opencv.core.Point

class MainActivity : AppCompatActivity() {

    private val REQUEST_TAKE_PHOTO = 1

    private lateinit var imageView: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image)
        val button: Button = findViewById(R.id.button)

        button.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        val a = Point()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем миниатюру картинки
            val thumbnailBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(thumbnailBitmap)
        }

        // Другой вариант с применением when
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK && data !== null) {
                    imageView.setImageBitmap(data.extras?.get("data") as Bitmap)
                }
            }
            else -> {
                Toast.makeText(this, "Wrong request code", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
