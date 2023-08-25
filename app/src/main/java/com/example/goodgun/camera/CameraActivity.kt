package com.example.goodgun.camera

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import com.example.goodgun.R
import com.example.goodgun.add_food.FoodPhotoCheck
import com.example.goodgun.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService

class CameraActivity : AppCompatActivity() {
    private val binding: ActivityCameraBinding by lazy {
        ActivityCameraBinding.inflate(layoutInflater)
    }
    private var image: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var camera: Camera
    private lateinit var cameraSelector: CameraSelector
    private lateinit var preview: Preview
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraAnimationListener: Animation.AnimationListener
    private var savedUri: Uri? = null

    private val permissions = arrayOf(android.Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListener()
        initLayout()
        checkPermissions()
//        permissionCheck()
    }

    private fun initLayout() {
        outputDirectory = getOutputDirectory()
        setCameraAnimationListener()
        openCamera()
        binding.apply {
            phoneLight.setOnClickListener {
                toggleTorch()
            }
            cameraMode.setOnClickListener {
                val i = Intent(this@CameraActivity, BarcodeScanActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
                startActivity(i)
//                mode = !mode
//                if (mode == false) { // 바코드 촬영 모드
//                    cameraShutter.isEnabled = false
// //                    zxingBarcodeSurface.resume()
//                    cameraMode.setImageResource(R.drawable.food_mode)
//                    cameraModeText.text = "제품 바코드 촬영"
//                    cameraFrame.setImageResource(R.drawable.barcode_layout)
//                } else {
//                    cameraShutter.isEnabled = true
// //                    zxingBarcodeSurface.pause()
//                    cameraMode.setImageResource(R.drawable.ic_barcode_20dp)
//                    cameraModeText.text = "음식 사진 촬영"
//                    cameraFrame.setImageResource(R.drawable.food_camera)
//                }
            }
            cameraShutter.isEnabled = false
//            zxingBarcodeSurface.decodeContinuous {
//                Toast.makeText(this@CameraActivity, it.text, Toast.LENGTH_SHORT)
//            }
        }
    }

    private fun setListener() {
        binding.cameraShutter.setOnClickListener {
            savePhoto()
        }
    }

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            image = ImageCapture.Builder().build()

            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, image)
                binding.cameraShutter.isEnabled = true
            } catch (e: Exception) {
                Log.e("camera", e.toString())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun toggleTorch() {
//        // 카메라가 사용 중인지 확인
        Log.i("camera", cameraProvider.isBound(preview).toString())
        if (cameraProvider.isBound(preview) == true) {
            val torchState = camera.cameraInfo.torchState
            val newTorchState =
                if (torchState.value == TorchState.ON) TorchState.OFF else TorchState.ON
            camera.cameraControl.enableTorch(newTorchState != 0)
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            filesDir
        }
    }

    private fun savePhoto() {
        image = image ?: return

        val animation = AnimationUtils.loadAnimation(this, R.anim.shutter_animation)
        animation.setAnimationListener(cameraAnimationListener)
        binding!!.frameLayout.animation = animation
        binding!!.frameLayout.startAnimation(animation)

        val child = SimpleDateFormat("yy-mm-dd", Locale.US).format(System.currentTimeMillis()) + ".png"

        val photoFile = File(
            outputDirectory,
            child,
        )
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        image?.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
                    val i = Intent(this@CameraActivity, FoodPhotoCheck::class.java)
                    i.putExtra("food", savedUri.toString())
                    Log.i("camera", "saved")
                    startActivity(i)
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
//                    onBackPressed()
                }
            },
        )
    }

    private fun setCameraAnimationListener() {
        cameraAnimationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        }
    }

    val multiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val resultPermission = it.all { map ->
                map.value
            }
            if (!resultPermission) {
                // finish()
                Toast.makeText(this, "모든 권한 승인되어야 함", Toast.LENGTH_SHORT).show()
            }
        }

    fun permissionCheck() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        for (permission: String in permissions) {
            var chk = checkCallingOrSelfPermission(permission)
            if (chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permissions, 0)
                break
            }
        }
    }

    fun checkPermissions() {
        when {
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            -> {
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA,
            ) -> {
                permissionCheckAlertDialog()
            }
            else -> {
                multiplePermissionLauncher.launch(permissions)
            }
        }
    }

    fun permissionCheckAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("반드시 CAMERA 권한이 모두 허용되어야 합니다.").setTitle("권한 체크").setPositiveButton("OK") {
                _, _ ->
            multiplePermissionLauncher.launch(permissions)
        }.setNegativeButton("Cancel") {
                dlg, _ ->
            dlg.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider.unbindAll()
    }
}
