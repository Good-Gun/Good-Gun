package com.example.goodgun

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class DottedLineView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private val path = Path()

    init {
        paint.color = resources.getColor(android.R.color.holo_red_light)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f // 선의 두께

        // 점선 효과 추가
        val intervals = floatArrayOf(10f, 10f) // 점선의 길이와 간격을 나타내는 배열
        val phase = 0f // 시작점을 조절할 수 있는 오프셋
        val dashEffect = DashPathEffect(intervals, phase)
        paint.pathEffect = dashEffect
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.moveTo(width / 2f, 0f)
        path.lineTo(width / 2f, height.toFloat())
        canvas?.drawPath(path, paint)
    }
}
