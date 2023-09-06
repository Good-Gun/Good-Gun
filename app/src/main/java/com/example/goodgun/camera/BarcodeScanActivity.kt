package com.example.goodgun.camera

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.BuildConfig
import com.example.goodgun.R
import com.example.goodgun.add_food.ScanInfomation
import com.example.goodgun.databinding.ActivityBarcodeScanBinding
import com.example.goodgun.openAPI.BarcodeClient
import com.example.goodgun.openAPI.BarcodeList
import com.example.goodgun.openAPI.FoodClient
import com.example.goodgun.openAPI.FoodList
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.roomDB.FoodDatabase
import com.example.goodgun.roomDB.FoodEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.camera.CameraSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarcodeScanActivity : AppCompatActivity() {
    private val binding: ActivityBarcodeScanBinding by lazy {
        ActivityBarcodeScanBinding.inflate(layoutInflater)
    }
    private val dialog by lazy { Dialog(this@BarcodeScanActivity, R.style.CustomDialogTheme) }
    private var captureFlag = false
    private lateinit var roomdb: FoodDatabase
    private lateinit var capture: CaptureManager
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var userid = ""
    private val cameraSettings = CameraSettings()
    private var isFlashOn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        capture = CaptureManager(this, binding.barcodeScannerView)
        capture.initializeFromIntent(this.intent, savedInstanceState)
        capture.decode()

        initRoomDB()
        initLayout()
    }

    private fun initRoomDB() {
        auth = Firebase.auth
        currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this@BarcodeScanActivity, "유효하지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
        } else {
            userid = currentUser!!.uid
        }
        roomdb = DatabaseManager.getDatabaseInstance(userid, applicationContext)
    }

    private fun initLayout() {
        dialog.setContentView(R.layout.waiting_dialog)
        cameraSettings.requestedCameraId = 1
        binding.apply {
            cameraMode.setOnClickListener {
                finish()
                startActivity(Intent(this@BarcodeScanActivity, CameraActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            cameraShutter.setOnClickListener {
                finish()
                startActivity(Intent(this@BarcodeScanActivity, ScanInfomation::class.java))
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

    private fun getNutrient(productName: String?) {
        FoodClient.foodService.getFoodName(BuildConfig.KEY_ID, "I2790", "json", productName!!.replace(" ", "_"))
            .enqueue(object : Callback<FoodList> {
                override fun onResponse(call: Call<FoodList>, response: Response<FoodList>) {
                    if (response.isSuccessful.not()) {
                        Log.e(ContentValues.TAG, response.toString())
                        return
                    } else {
                        response.body()?.let {
                            val foodDto = response.body()?.list
                            val foodList = foodDto?.food ?: emptyList()
                            if (foodList.isNotEmpty()) {
                                val data = foodList[0]
                                val selectFood = FoodEntity(
                                    data.foodName,

                                    data.calory!!.toDouble(),
                                    data.carbohydrates!!.toDouble(),
                                    data.sugar!!.toDouble(),
                                    data.protein!!.toDouble(),
                                    data.fat!!.toDouble(),
                                    data.trans_fat!!.toDouble(),
                                    data.saturated_fat!!.toDouble(),
                                    data.cholesterol!!.toDouble(),
                                    data.sodium!!.toDouble(),
                                    true,
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    roomdb.foodDao().saveFood(selectFood)
                                }
                                Toast.makeText(this@BarcodeScanActivity, "${productName}이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@BarcodeScanActivity, "\"${productName}\" 제품이 DB에 없습니다. 직접 추가를 이용해주세요.", Toast.LENGTH_SHORT).show()
                            }
                            dialog.dismiss()
                            captureFlag = false
                        }
                    }
                }

                override fun onFailure(call: Call<FoodList>, t: Throwable) {
                    Log.e(ContentValues.TAG, "연결 실패")
                    Log.e(ContentValues.TAG, t.toString())
                }
            })
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
                        Log.i("barcode", productName.toString())
                        if (productName != null) {
                            getNutrient(productName)
                        } else {
                            Toast.makeText(this@BarcodeScanActivity, "제품이 DB에 없습니다. 직접 추가를 이용해주세요.", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            captureFlag = false
                        }
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
