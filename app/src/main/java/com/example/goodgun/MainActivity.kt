package com.example.goodgun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.camera.CameraActivity
import com.example.goodgun.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.ak1.OnBubbleClickListener

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statusBarColor = getColor(R.color.theme1) // 원하는 색상 리소스로 변경
        window.statusBarColor = statusBarColor
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val currentUser = auth.currentUser

        Log.d("Login Check", "uid: ${currentUser?.uid}, email: ${currentUser?.email}")

        /*binding.bottomNavigationMain.run {
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
        }*/
        binding.apply {
            bubbleTabBar.addBubbleListener(object : OnBubbleClickListener {
                override fun onBubbleClick(id: Int) {
                    when (id) {
                        R.id.nav_home -> {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame_main, HomeFragment())
                                .commitAllowingStateLoss()
                        }
                        R.id.nav_option -> {
                        }
                    }
                }
            })

            bubbleTabBar.setSelectedWithId(R.id.nav_home)
        }
        binding.ivAdd.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }
}
