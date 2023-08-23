package com.example.goodgun.camera

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.BuildConfig
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivityBarcodeScanBinding
import com.example.goodgun.openAPI.BarcodeClient
import com.example.goodgun.openAPI.BarcodeList
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.camera.CameraSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarcodeScanActivity : AppCompatActivity() {
    val binding: ActivityBarcodeScanBinding by lazy {
        ActivityBarcodeScanBinding.inflate(layoutInflater)
    }
    val dialog by lazy { Dialog(this@BarcodeScanActivity, R.style.CustomDialogTheme) }
    var captureFlag = false
    private val cameraSettings = CameraSettings()
    private lateinit var capture: CaptureManager
    private var isFlashOn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        capture = CaptureManager(this, binding.barcodeScannerView)
        capture.initializeFromIntent(this.intent, savedInstanceState)
        capture.decode()

        initLayout()
    }

    private fun initLayout() {
        dialog.setContentView(R.layout.waiting_dialog)
        cameraSettings.requestedCameraId = 1
        binding.apply {
            cameraMode.setOnClickListener {
                finish()
                startActivity(Intent(this@BarcodeScanActivity, CameraActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            phoneLight.setOnClickListener {
                if (isFlashOn) barcodeScannerView.setTorchOff() else barcodeScannerView.setTorchOn()
                isFlashOn = !isFlashOn
            }
            barcodeScannerView.resume()
            barcodeScannerView.decodeContinuous(object : BarcodeCallback {
                override fun barcodeResult(result: BarcodeResult) {
                    try {
                        var result_data = ""
                        var productName: String? = null
                        if (!captureFlag) { // TODO [최초 1번 스캔된 경우]
                            result_data = result.toString()
                            var result_byte: ByteArray
                            // TODO Arrays.toString 형식 바이트 문자열 [104,101,108,108,111]
                            if (result_data.contains("[") && result_data.contains("]") &&
                                result_data.contains(",")
                            ) { // TODO [바이트 값 형식 문자열인 경우 > 한글로 출력]
                                result_byte = result_data.toByteArray()
                                result_data = String(result_byte, Charsets.UTF_8)
                            }

                            dialog.show()
                            captureFlag = true
                            productName = getFoodName(result_data)

//                            val alertTittle = "[QR 스캔 정보 확인]"
//                            val alertMessage = "[정보]" + "\n" + "\n" + result_data
//                            val buttonYes = "다시 스캔"
//                            val buttonNo = "종료"
//                            AlertDialog.Builder(this@BarcodeScanActivity)
//                                .setTitle(alertTittle)
//                                .setMessage(alertMessage)
//                                .setCancelable(false)
//                                .setPositiveButton(buttonYes) { dialog, _ ->
//                                    // TODO Auto-generated method stub
//                                    // TODO [다시 스캔을 하기 위해 플래그값 변경]
//                                    captureFlag = false
//                                    dialog.dismiss()
//                                }
//                                .setNegativeButton(buttonNo) { _, _ ->
//                                    // TODO Auto-generated method stub
//                                    try {
//                                        // TODO [액티비티 종료 실시]
//                                        finish()
//                                        overridePendingTransition(0, 0)
//                                    } catch (e: Exception) {
//                                        e.printStackTrace()
//                                    }
//                                }
//                                .show()

                            // TODO [플래그 값을 변경 실시 : 중복 스캔 방지]
                            captureFlag = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun possibleResultPoints(resultPoints: List<ResultPoint>) {
                }
            })
        }
    }

    private fun getFoodName(barcodeNo: String): String? {
        var productName: String? = null
        BarcodeClient.service.getBarcodeInfo(BuildConfig.KEY_ID, barcodeNo)
            .enqueue(object : Callback<BarcodeList> {
                override fun onResponse(call: Call<BarcodeList>, response: Response<BarcodeList>) {
                    if (response.isSuccessful) {
                        Log.i("barcode", response.body()!!.barcodeResult.barcodeCount)
                        productName = when (response.body()!!.barcodeResult.barcodeCount.toInt()) {
                            0 -> null
                            else -> response.body()!!.barcodeResult.barcodeDto[0].productName
                        }
                        dialog.dismiss()
                        Log.i("barcode", productName.toString())
                        if (productName != null) {
                            Toast.makeText(this@BarcodeScanActivity, productName, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@BarcodeScanActivity, "제품 인식에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        captureFlag = false
                    }
                }
                override fun onFailure(call: Call<BarcodeList>, t: Throwable) {
                }
            })
        return productName
    }
    override fun onDestroy() {
        super.onDestroy()
        binding.barcodeScannerView.pause()
    }
}
