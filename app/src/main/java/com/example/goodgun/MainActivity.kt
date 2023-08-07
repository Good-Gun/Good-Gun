package com.example.goodgun

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.camera.CameraActivity
import com.example.goodgun.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }
}
