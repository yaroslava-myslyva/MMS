package com.example.mms

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mms.databinding.ActivityMainBinding
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat

class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 101
    }

    private lateinit var mOpenCvCameraView: CameraBridgeViewBase
    private lateinit var mIntermediateMat: Mat
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // camera permission granted
                activateOpenCVCameraView()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
            }
            else -> {
                // directly ask for the permission.
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // overengineered check for if permission with our request code and permission name was granted
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            val indexOfCameraPermission = permissions.indexOf(android.Manifest.permission.CAMERA)
            if (indexOfCameraPermission != -1) {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[indexOfCameraPermission] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            applicationContext,
                            "Camera permission granted!",
                            Toast.LENGTH_LONG
                        ).show()
                        activateOpenCVCameraView()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Camera permission is required to run this app!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun activateOpenCVCameraView() {
        // everything needed to start a camera preview
        mOpenCvCameraView = binding.yourCameraView
        mOpenCvCameraView.setCameraPermissionGranted()
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_ANY)
        mOpenCvCameraView.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView.setCvCameraViewListener(this)
        mOpenCvCameraView.enableView()
    }


    override fun onResume() {
        super.onResume()
        // there's no need to load the opencv library if there is no camera preview (I think that sounds reasonable (?))
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (!OpenCVLoader.initDebug()) {
                log("Internal OpenCV library not found. Using OpenCV Manager for initialization");
                OpenCVLoader.initAsync(
                    OpenCVLoader.OPENCV_VERSION_3_0_0, this,
                    mLoaderCallback
                )
            } else {
                log("OpenCV library found inside package. Using it!");
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        }
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    log("OpenCV loaded successfully")
                    activateOpenCVCameraView()
                }
                else -> super.onManagerConnected(status)
            }
        }
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        log("onCameraViewStarted")
        mIntermediateMat = Mat()
    }

    override fun onCameraViewStopped() {
        log("onCameraViewStopped")
        // Explicitly deallocate Mats
        mIntermediateMat.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        log("onCameraFrame")
        val rgba = inputFrame!!.rgba()

        return rgba
    }

    override fun onDestroy() {
        mOpenCvCameraView.disableView()
        super.onDestroy()
    }

    private fun log(message: String) {
        Log.i("MainActivity", message)
    }
}