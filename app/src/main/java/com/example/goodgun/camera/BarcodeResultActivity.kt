package com.example.goodgun.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.databinding.ActivityBarcodeResultBinding

class BarcodeResultActivity : AppCompatActivity() {
    val binding: ActivityBarcodeResultBinding by lazy {
        ActivityBarcodeResultBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
    }
}
