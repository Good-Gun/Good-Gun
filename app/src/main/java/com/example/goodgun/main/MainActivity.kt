package com.example.goodgun.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.ApplicationClass
import com.example.goodgun.R
import com.example.goodgun.camera.CameraActivity
import com.example.goodgun.databinding.ActivityMainBinding
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.util.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.ak1.OnBubbleClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: Dialog // 로딩창 클래스

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame_main, ProfileFragment())
                                .commitAllowingStateLoss()
                            supportFragmentManager.beginTransaction().addToBackStack(null)
                            supportFragmentManager.beginTransaction().commit()
                        }
                    }
                }
            })
        }
        binding.ivAdd.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        val statusBarColor = getColor(R.color.theme1) // 원하는 색상 리소스로 변경
        window.statusBarColor = statusBarColor

        loadingDialog = LoadingDialog(this)
        loadingDialog.show()

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser != null) {
            ApplicationClass.uid = currentUser.uid
            ApplicationClass.email = currentUser.email!!
            CoroutineScope(Dispatchers.Main).launch {
                ApplicationClass.user = NetworkManager.getUserData()
                ApplicationClass.uname = ApplicationClass.user.u_name
                ApplicationClass.calculateMaxNut()
                binding.bubbleTabBar.setSelectedWithId(R.id.nav_home)
                loadingDialog.dismiss()
            }
        }
    }

    fun changeNav(id: Int) {
        binding.bubbleTabBar.setSelected(0)
    }
}
