package com.example.goodgun.camera

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import java.lang.Exception

class Torch(context: Context) {
    private var cameraId: String? = null // cameraId 값을 담을 변수
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

// system service 중 camera 서비스를 받아온다.

    init {
        cameraId = getCameraId() // 생성자에서 카메라 아이디를 받아온다.
    }

    fun flashOn() {
        Log.i("camera", cameraId!!)
        try {
            cameraManager.setTorchMode(cameraId!!, true) // 손전등 On
        } catch (e: Exception) {
            Log.e("camera", e.toString())
        }
    }

    fun flashOff() {
        Log.i("camera", cameraId!!)
        try {
            cameraManager.setTorchMode(cameraId!!, false) // 손전등 Off
        } catch (e: Exception) {
            Log.e("camera", e.toString())
        }
    }

    private fun getCameraId(): String? {
        val cameraIds = cameraManager.cameraIdList // 핸드폰에서 인식가능한 카메라 리스트를 가져온다.
        for (id in cameraIds) { // for문을 통해 순차적으로 접근한다.
            val info = cameraManager.getCameraCharacteristics(id) // 카메라의 세부 정보를 얻는다.
            val flashAvailable = info.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) // 플래시 사용가능 확인
            val lensFacing = info.get(CameraCharacteristics.LENS_FACING) // 카메라 렌즈 방향 확인
            if (flashAvailable != null && // 플래시가 인식가능하고 사용 가능하면
                flashAvailable &&
                lensFacing != null && // 카메라 방향이 인식가능하고 뒤에 있으면
                lensFacing == CameraCharacteristics.LENS_FACING_BACK
            ) {
                Log.i("camera", id.toString())
                return id // id 반환
            }
        }
        return null
    }
}
