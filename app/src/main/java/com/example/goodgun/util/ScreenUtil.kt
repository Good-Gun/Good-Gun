package com.example.goodgun.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

class ScreenUtil {
    companion object {
        fun getScreenSize(context: Context): Pair<Int, Int> {
            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            return Pair(screenWidth, screenHeight)
        }

        fun getScreenDensity(context: Context): Float {
            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            return displayMetrics.density
        }
    }
}
