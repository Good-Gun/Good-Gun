package com.example.goodgun.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.goodgun.ApplicationClass
import com.example.goodgun.add_food.ScanInfomation
import com.example.goodgun.databinding.FragmentHomeBinding
import com.example.goodgun.main_function.*
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Food
import com.example.goodgun.network.model.NutritionResponse
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.util.LoadingDialog
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var loadingDialog: Dialog // 로딩창 클래스
    //lateinit var todayAdapter: TodayRVAdapter // 오늘 섭취한 음식정보 recyclerView

    private lateinit var todayVP: ViewPager2
    private lateinit var adapter: FoodVPAdapter
    private val fragmentFood = mutableListOf<Food>()

    lateinit var nutritionResponse: NutritionResponse // 영양 정보 저장을 위한 클래스
    val food_list: ArrayList<Food> = arrayListOf() // 음식 리스트
    val today: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern(" yyyy-MM-dd"))
    lateinit var date: String // 다른 날짜의 영양정보 탐색을 위한 변수

    var binding: FragmentHomeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        date = today
        loadingDialog = LoadingDialog(requireContext())
        loadingDialog.show()

        initLayout()
        initVP()


        return binding!!.root
    }

    /*일반 레이아웃 초기화*/
    fun initLayout() {
        binding!!.apply {
            tvHomeDate.text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

            /*날짜 탐색 (이전 날짜)*/
            ivHomeLeft.setOnClickListener {
                loadingDialog.show()
                date = LocalDate.parse(date.trim()).minusDays(1).toString()
                tvHomeDate.text = LocalDate.parse(date.trim()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                getNutrition(date)
            }


            /*날짜 탐색 (다음 날짜)*/
            ivHomeRight.setOnClickListener {
                loadingDialog.show()
                if (LocalDate.parse(today.trim()) > LocalDate.parse(date.trim())) {
                    date = LocalDate.parse(date.trim()).plusDays(1).toString()
                    tvHomeDate.text = LocalDate.parse(date.trim()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                    getNutrition(date)
                } else {
                    loadingDialog.dismiss()
                }
            }

            homeBtn1.setOnClickListener {
                val intent = Intent(activity, SolutionActivity::class.java)
                (activity as MainActivity).
                startActivity(intent)
            }

            /*식단 추천 창으로 이동*/
            homeBtn2.setOnClickListener {
                val intent = Intent(activity, FoodActivity::class.java)
                startActivity(intent)
            }

            /*통계 창으로 이동*/
            homeBtn3.setOnClickListener {
                val intent = Intent(activity, GraphActivity::class.java)
                startActivity(intent)
            }

            /*임시 저장된 스캔내역 출력 액티비티로 이동*/
            ivScanNum.setOnClickListener {
                val intent = Intent(requireContext(), ScanInfomation::class.java)
                startActivity(intent)
            }
        }

    }

    /*private fun initRV() {
        todayAdapter = TodayRVAdapter(requireContext(), food_list, 5)
        binding?.rvHomeToday?.adapter = todayAdapter
        binding?.rvHomeToday?.layoutManager = LinearLayoutManagerWrapper(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 간격 20으로
        val spaceDecoration = this.VerticalSpaceItemDecoration(20)
        binding?.rvHomeToday?.addItemDecoration(spaceDecoration)
    }*/

    fun initVP() {
        todayVP = binding!!.vpHomeToday
        adapter = FoodVPAdapter(childFragmentManager, lifecycle)
        todayVP.adapter = adapter
    }

    private var currentPage = 0
    private val DELAY_MS: Long = 3000 // 페이지 전환 간격 (3초)

    private val PERIOD_MS: Long = 5000 // 페이지 전환 주기 (5초)


    private val handler = Handler(Looper.getMainLooper())
    private val runnable = java.lang.Runnable {
        if (currentPage == todayVP.adapter?.itemCount) {
            currentPage = 0
        }
        todayVP.setCurrentItem(currentPage++, true)
    }

    /*날짜를 바꿀 시 파이어베이스 요청*/
    private fun getNutrition(date: String) {
        CoroutineScope(Dispatchers.Main).launch {
            nutritionResponse = NetworkManager.getFoodByDate(date)
            fragmentFood.apply {
                clear()
                //todayAdapter.notifyItemRangeRemoved(0, num)
                addAll(nutritionResponse.food_list)
                //todayAdapter.notifyItemRangeInserted(0, nutritionResponse.food_list.size)
            }

            adapter.setFragmentFood(fragmentFood)
            Handler(Looper.getMainLooper()).post {
                if (fragmentFood.size == 0) {
                    binding!!.tvNoFood.visibility = View.VISIBLE
                } else {
                    binding!!.tvNoFood.visibility = View.GONE

                    val timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            handler.post(runnable)
                        }
                    }, DELAY_MS, PERIOD_MS)
                }
            }
            setData()
        }
    }

    /*프로그래스 및 기타 정보 수정*/
    private fun setData() {
        val max = ApplicationClass.maxNutrition
        val nutrition = nutritionResponse.nutrition
        binding!!.apply {
            /*tvHomeName.text = ApplicationClass.uname*/

            pbHomeCalorie.progress = (nutrition.calorie / max.calorie.toFloat() * 100.0).toFloat()
            pvHomeCarbo.progress = (nutrition.carbohydrates / max.carbohydrates.toFloat() * 100.0).toFloat()
            pvHomeFat.progress = (nutrition.fat / max.fat.toFloat() * 100.0).toFloat()
            pvHomeProtein.progress = (nutrition.protein / max.protein.toFloat() * 100.0).toFloat()

            tvHomeCalorie1.text = nutrition.calorie.toString()
            tvHomeCalorie2.text = "/ " + max.calorie.toString()+" kcal"
            tvHomeCarbohydrates.text = nutrition.carbohydrates.toString() + " / " + max.carbohydrates.toString()
            tvHomeProteins.text = nutrition.protein.toString() + " / " + max.protein.toString()
            tvHomeFat.text = nutrition.fat.toString() + " / " + max.fat.toString()
        }
        loadingDialog.dismiss()
    }

    override fun onResume() {
        super.onResume()

        getNutrition(date)

        /*room DB에 저장되어 있는 음식 개수*/
        val roomdb = DatabaseManager.getDatabaseInstance(ApplicationClass.uid, requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val count = roomdb.foodDao().foodCount()
            withContext(Dispatchers.Main) {
                binding!!.roomDBcount.text = count.toString()
            }
        }
    }

}
