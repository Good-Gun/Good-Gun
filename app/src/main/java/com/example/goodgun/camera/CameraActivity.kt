package com.example.goodgun.camera

import android.content.pm.PackageManager
import android.net.Uri
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
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService

class CameraActivity : AppCompatActivity() {
    val binding: ActivityCameraBinding by lazy {
        ActivityCameraBinding.inflate(layoutInflater)
    }
    var image: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var camera: Camera
    private lateinit var cameraSelector: CameraSelector
    private lateinit var preview: Preview
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraAnimationListener: Animation.AnimationListener

    private var savedUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCameraAnimationListener()
        initLayout()
        checkPermissions()
    }

    private fun initLayout() {
        outputDirectory = getOutputDirectory()
        openCamera()
        setListener()
        binding.apply {
            phoneLight.setOnClickListener {
                toggleTorch()
            }
        }
    }

    private fun savePhoto() {
        image = image ?: return

        val animation = AnimationUtils.loadAnimation(this@CameraActivity, R.anim.shutter_animation)
        animation.setAnimationListener(cameraAnimationListener)
        binding.frameLayoutShutter.animation = animation
        binding.frameLayoutShutter.startAnimation(animation)

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat("yy-mm-dd", Locale.US).format(System.currentTimeMillis()) + ".png"
        )
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        image?.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
                    Log.i("camera", "saved")
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    onBackPressed()
                }
            }
        )
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
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
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

    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)

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

    fun checkPermissions() {
        when {
            (
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                ) &&
                (
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED
                    ) && (
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                ) -> {
                Toast.makeText(this, "모든 권한 승인됨", Toast.LENGTH_SHORT).show()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        builder.setMessage("반드시 READ_EXTERNAL_STORAGE과 CAMERA 권한이 모두 허용되어야 합니다.").setTitle("권한 체크").setPositiveButton("OK") {
            _, _ ->
            multiplePermissionLauncher.launch(permissions)
        }.setNegativeButton("Cancel") {
            dlg, _ ->
            dlg.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun allPermissionGranted() = permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    fun readPermissionGranted() = ActivityCompat.checkSelfPermission(
        this,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    fun cameraPermissionGranted() = ActivityCompat.checkSelfPermission(
        this,
        android.Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
