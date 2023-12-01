package com.example.mms

import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewFrame, CameraBridgeViewBase.CvCameraViewListener2 {
    private lateinit var mButton: Button
    private lateinit var mOpenCvCameraView: CameraBridgeViewBase
    private lateinit var mRgba: Mat
    private lateinit var mByte: Mat
    private lateinit var imGgray: Mat
    private lateinit var imgCandy: Mat
    lateinit var imageMat: Mat

    private val mBaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    mOpenCvCameraView.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)

        mButton = findViewById(R.id.captureButton)
        mOpenCvCameraView = findViewById(R.id.yourCameraView)
        mOpenCvCameraView.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView.setCvCameraViewListener(this)

        mButton.setOnClickListener { /* take a picture */ }
    }

    override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (OpenCVLoader.initDebug()) {
            mBaseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
            Log.d(TAG, "onResume: true")
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mBaseLoaderCallback)
            Log.d(TAG, "onResume: false")
        }
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        Log.d(TAG, "onCameraViewStarted: ")
        imageMat = Mat(width, height, CvType.CV_8UC4)
        mRgba = Mat()
        mByte = Mat()
        imGgray = Mat()
        imgCandy = Mat()
    }

    override fun onCameraViewStopped() {
        mRgba.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        Log.d(TAG, "onCameraFrame: ")
//        mRgba = inputFrame.rgba()
//        Imgproc.cvtColor(mRgba, imGgray, Imgproc.COLOR_RGB2GRAY)
//        Imgproc.adaptiveThreshold(imGgray, mByte, 255.0, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 35, 5.0)
//
//        return mByte // this is m Binary image
        imageMat = inputFrame!!.rgba()
        return imageMat
    }

//    override fun onPictureTaken(data: ByteArray, camera: Camera) {
//        // handle the taken picture
//    }

    override fun rgba(): Mat {
        return mRgba
    }

    override fun gray(): Mat {
        return imGgray
    }


}
