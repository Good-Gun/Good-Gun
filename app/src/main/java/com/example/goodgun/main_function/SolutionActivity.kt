package com.example.goodgun.main_function

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goodgun.R

class SolutionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solution)

        val statusBarColor = getColor(R.color.red) // 원하는 색상 리소스로 변경
        window.statusBarColor = statusBarColor
    }
}