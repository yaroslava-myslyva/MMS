package com.example.mms



import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat


class MainActivity : AppCompatActivity(), CvCameraViewListener2 {
    private var mOpenCvCameraView: CameraBridgeViewBase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main)
        Log.d("ttt", "itit" + OpenCVLoader.initDebug())
        OpenCVLoader.initDebug()

        mOpenCvCameraView =
            findViewById<View>(R.id.image_manipulations_activity_surface_view) as CameraBridgeViewBase
        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE
       // mOpenCvCameraView!!.
        mOpenCvCameraView!!.setCvCameraViewListener(this)
        //mOpenCvCameraView!!.setCameraPermissionGranted()
        mOpenCvCameraView!!.enableView()
    }

    public override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                SUCCESS -> {
                    Log.i("OpenCV", "OpenCV loaded successfully")
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }


    public override fun onResume() {
        super.onResume()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            val info = Camera.CameraInfo()
//            for (i in 0 until Camera.getNumberOfCameras()) {
//                Camera.getCameraInfo(i, info)
//                if (info.facing === Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    try {
//                        // Gets to here OK
//                        val camera = Camera.open(i)
//                        camera.release()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        //  throws runtime exception :"Failed to connect to camera service"
//                    }
//                }
//            }
//        }
        if (!OpenCVLoader.initDebug()) {
            Log.d(
                "OpenCV",
                "Internal OpenCV library not found. Using OpenCV Manager for initialization"
            )
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {}
    override fun onCameraViewStopped() {}
    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        Log.d("ttt", "inputframe $inputFrame")
        return inputFrame.rgba()
    }
}
