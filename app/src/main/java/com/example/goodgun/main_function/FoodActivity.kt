package com.example.goodgun.main_function

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.databinding.ActivityFoodBinding

class FoodActivity : AppCompatActivity() {
    lateinit var binding: ActivityFoodBinding
    lateinit var todayAdapter: TodayRVAdapter
    var todayArray: ArrayList<Pair<String, String>> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        temporaryFillArr()
        initTodayRV()
    }

    private fun initTodayRV() {
        todayAdapter = TodayRVAdapter(this, todayArray)
        binding.rvFoodToday.adapter = todayAdapter
        binding.rvFoodToday.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 간격 20으로
        val spaceDecoration = this.VerticalSpaceItemDecoration(20)
        binding.rvFoodToday.addItemDecoration(spaceDecoration)
    }

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    /*테스트용 배열 생성을 위한 임시 함수*/
    private fun temporaryFillArr() {
        todayArray.apply {
            for (i in 0..3) {
                add(Pair(('A' + i).toString(), ('A' + i).toString()))
            }
        }
    }
}
