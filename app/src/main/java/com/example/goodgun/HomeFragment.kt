package com.example.goodgun

import android.app.Dialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.add_food.ScanInfomation
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    private lateinit var loadingDialog: Dialog
    lateinit var todayAdapter: TodayRVAdapter
    lateinit var  nutrition: Nutrition
    val food_list: ArrayList<Food> = arrayListOf()
    lateinit var today:String
    lateinit var date:String

    var binding: FragmentHomeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        today = LocalDateTime.now().format(DateTimeFormatter.ofPattern(" yyyy-MM-dd"))
        date = today
        loadingDialog = LoadingDialog(requireContext())
        loadingDialog.show()

        initLayout()
        initRV()

        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
    }

    fun initLayout() {
        binding!!.apply {
            tvHomeName.text = "이석준"
            tvHomeDate.text = today

            ivHomeLeft.setOnClickListener {
                loadingDialog.show()
                date = LocalDate.parse(date.trim()).minusDays(1).toString()
                tvHomeDate.text = date
                getNutrition(date)
            }
            ivHomeRight.setOnClickListener {
                loadingDialog.show()
                if(LocalDate.parse(today.trim()) > LocalDate.parse(date.trim())) {
                    date = LocalDate.parse(date.trim()).plusDays(1).toString()
                    tvHomeDate.text = date
                    getNutrition(date)
                }
            }

            homeBtn2.setOnClickListener {
                val intent = Intent(activity, FoodActivity::class.java)
                startActivity(intent)
            }
            homeBtn3.setOnClickListener {
                val intent = Intent(activity, GraphActivity::class.java)
                startActivity(intent)
            }

            ivScanNum.setOnClickListener {
                /*임시 저장된 스캔내역 출력 액티비티로 이동*/
                val intent = Intent(requireContext(), ScanInfomation::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initRV() {
        todayAdapter = TodayRVAdapter(requireContext(), food_list, 5)
        binding?.rvHomeToday?.adapter = todayAdapter
        binding?.rvHomeToday?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 간격 20으로
        val spaceDecoration = this.VerticalSpaceItemDecoration(20)
        binding?.rvHomeToday?.addItemDecoration(spaceDecoration)

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                nutrition = FirebaseManager.getDayNutrition(today)
                food_list.apply {
                    addAll(FirebaseManager.getFoodData(today))
                }
            }
            todayAdapter.notifyItemRangeInserted(0, food_list.size)
            setProgress()
        }
    }

    private fun getNutrition(date:String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                Log.d("Firebase Communication at main", "at main, date: $date")
                nutrition = FirebaseManager.getDayNutrition(date)
            }
            setProgress()
        }
    }

    private fun setProgress() {
        binding!!.apply {
            pbHomeCalorie.setProgress((nutrition.calorie/2000.0 * 100.0).toInt())
            tvHomeCalorie.text = nutrition.calorie.toString()+"/2000"
            tvHomeCarbohydrates.text = nutrition.carbohydrates.toString()+"/1000"
            tvHomeProteins.text = nutrition.protein.toString()+"/600"
            tvHomeFat.text = nutrition.fat.toString()+"/400"
        }
        loadingDialog.dismiss()
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

}
