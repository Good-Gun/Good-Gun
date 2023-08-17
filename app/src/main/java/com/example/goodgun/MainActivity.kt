package com.example.goodgun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.camera.CameraActivity
import com.example.goodgun.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val barcodeLauncher = registerForActivityResult(
        ScanContract(),
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Log.d("camera", "Cancelled scan")
                Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d(
                    "camera",
                    "Cancelled scan due to missing camera permission",
                )
                Toast.makeText(
                    this@MainActivity,
                    "Cancelled due to missing camera permission",
                    Toast.LENGTH_LONG,
                ).show()
            }
        } else {
            Log.d("camera", "Scanned")
            Toast.makeText(
                this@MainActivity,
                "Scanned: " + result.contents,
                Toast.LENGTH_LONG,
            ).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val currentUser = auth.currentUser

        Log.d("Login Check", "uid: ${currentUser?.uid}, email: ${currentUser?.email}")

        binding.bottomNavigationMain.run {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_main, HomeFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.nav_option -> {
                    }
                }
                true
            }
            selectedItemId = R.id.nav_home
        }
        binding.ivAdd.setOnClickListener {
            val options = ScanOptions().setBarcodeImageEnabled(false).setBeepEnabled(false).setCaptureActivity(
                CameraActivity::class.java,
            )
            barcodeLauncher.launch(options)
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }
}
