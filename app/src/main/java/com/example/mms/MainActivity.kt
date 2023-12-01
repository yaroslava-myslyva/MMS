package com.example.mms

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewFrame, CameraBridgeViewBase.CvCameraViewListener2 {

    private lateinit var mOpenCvCameraView: CameraBridgeViewBase
    private lateinit var captureButton: Button
    private lateinit var imageView: ImageView

    private var mGray: Mat? = null
    private var mRgba: Mat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mOpenCvCameraView = findViewById(R.id.yourCameraView)
        mOpenCvCameraView.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView.setCvCameraViewListener(this)

        captureButton = findViewById(R.id.captureButton)
        imageView = findViewById(R.id.imageView)


        // Ініціалізація OpenCV
        if (OpenCVLoader.initDebug()) {
            // OpenCV ініціалізовано успішно
            mOpenCvCameraView.enableView()
        } else {
            // OpenCV ініціалізація не вдалася
            Log.e("OpenCV", "OpenCV initialization failed.")
        }
onCameraFrame(this)
        // Додатковий код ініціалізації тут
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat? {
        val rgba = inputFrame.rgba()
        Log.d("ttt", "onCameraFrame")

        // При натисканні на кнопку ви можете зберегти поточний кадр
        captureImage(rgba)
        return rgba
    }

    private fun captureImage(frame: Mat) {
        // Збереження кадру у форматі Mat або конвертація його у Bitmap та збереження
        val bitmap = Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(frame, bitmap)

        // Далі ви можете використовувати bitmap для відображення або збереження
        // Наприклад, виведення на ImageView
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setImageBitmap(bitmap)
        imageView.visibility = View.VISIBLE

        // Або збереження в файл
        // saveImage(bitmap);
    }

    // Простий метод для збереження Bitmap у файл
    private fun saveImage(bitmap: Bitmap) {
        val filename = "image.jpg"
        val file = File(Environment.getExternalStorageDirectory(), filename)
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(this, "Image saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onPictureTaken(data: ByteArray): Mat? {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setImageBitmap(bitmap)
        imageView.visibility = View.VISIBLE
        mOpenCvCameraView.visibility = View.GONE // Приховати камеру
        return Mat()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        // Ініціалізація матриць при старті камери
        mGray = Mat()
        mRgba = Mat()
    }

    override fun onCameraViewStopped() {
        // Звільнення ресурсів при зупинці камери
        mGray!!.release()
        mRgba!!.release()
    }

    override fun rgba(): Mat {
        return mRgba ?: Mat()
    }

    override fun gray(): Mat {
        return mGray ?: Mat()
    }

    // Додаткові методи імплементації тут
}