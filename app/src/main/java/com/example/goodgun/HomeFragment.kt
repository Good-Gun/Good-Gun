package com.example.goodgun

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.databinding.FragmentHomeBinding
import com.example.goodgun.firebase.FirebaseManager
import com.example.goodgun.main_function.FoodActivity
import com.example.goodgun.main_function.GraphActivity
import com.example.goodgun.main_function.TodayRVAdapter
import com.example.goodgun.main_function.model.Nutrition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    lateinit var todayAdapter: TodayRVAdapter
    lateinit var nutrition: Nutrition
    val food_list: ArrayList<Food> = arrayListOf()

    val todayArray: ArrayList<Pair<String, String>> = arrayListOf()
    var binding: FragmentHomeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        temporaryFillArr()
        initLayout()
        initRV()
        getDataFromFirebase()

        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
    }

    fun initLayout() {
        binding!!.homeBtn2.setOnClickListener {
            val intent = Intent(activity, FoodActivity::class.java)
            startActivity(intent)
        }
        binding!!.homeBtn3.setOnClickListener {
            val intent = Intent(activity, GraphActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRV() {
        todayAdapter = TodayRVAdapter(requireContext(), food_list, 5)
        binding?.rvHomeToday?.adapter = todayAdapter
        binding?.rvHomeToday?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 간격 20으로
        val spaceDecoration = this.VerticalSpaceItemDecoration(20)
        binding?.rvHomeToday?.addItemDecoration(spaceDecoration)
    }

    private fun getDataFromFirebase() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                nutrition = FirebaseManager.getNutritionData(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(" yyyy-MM-dd"))
                )
                food_list.apply {
                    addAll(
                        FirebaseManager.getFoodData(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern(" yyyy-MM-dd"))
                        )
                    )
                }
            }
            todayAdapter.notifyItemRangeInserted(0, food_list.size)
            setProgress()
        }
    }

    private fun setProgress() {
        binding!!.pbHomeCalorie.setProgress((nutrition.calorie / 2000.0 * 100.0).toInt())
        binding!!.tvHomeCalorie.text = nutrition.calorie.toString() + "/2000"
        binding!!.tvHomeCarbohydrates.text = nutrition.carbohydrates.toString() + "/1000"
        binding!!.tvHomeProteins.text = nutrition.protein.toString() + "/600"
        binding!!.tvHomeFat.text = nutrition.fat.toString() + "/400"
    }

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
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
