package com.example.goodgun.main
/*
2023.09.03 in FoodActivity - RV to VP
 */

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.example.goodgun.ApplicationClass
import com.example.goodgun.add_food.ScanInfomation
import com.example.goodgun.databinding.FragmentHomeBinding
import com.example.goodgun.main_function.*
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.NutritionResponse
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.roomDB.FoodEntity
import com.example.goodgun.util.LoadingDialog
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Collections.addAll
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private lateinit var loadingDialog: Dialog // 로딩창 클래스
    // lateinit var todayAdapter: TodayRVAdapter // 오늘 섭취한 음식정보 recyclerView

    private lateinit var todayVP: ViewPager2
    private lateinit var adapter: HomeVPAdapter
    private val fragmentFood = mutableListOf<FoodEntity>()

    lateinit var nutritionResponse: NutritionResponse // 영양 정보 저장을 위한 클래스
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
                Log.d("Checking OpenAI", date)
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

            /*날짜 텍스트 클릭시 DatePicker 작동*/
            tvHomeDate.setOnClickListener {
                val callback = SingleDayPickCallback { day ->
                    val str = String.format("%04d", day.year) + "/" + String.format("%02d", day.month + 1) + "/" + String.format("%02d", day.date)
                    date = String.format("%04d", day.year) + "-" + String.format("%02d", day.month + 1) + "-" + String.format("%02d", day.date)
                    binding!!.tvHomeDate.text = str
                    getNutrition(date)
                }

                val today = CivilCalendar()

                val datePicker = PrimeDatePicker.bottomSheetWith(today)
                    .pickSingleDay(callback)
                    .initiallyPickedSingleDay(today)
                    .maxPossibleDate(today)
                    .build()
                datePicker.show(childFragmentManager, "SOME_TAG")
            }

            /*국건 솔루션 창으로 이동*/
            homeBtn1.setOnClickListener {
                val intent = Intent(activity, SolutionActivity::class.java)
                (activity as MainActivity)
                    .startActivity(intent)
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
        adapter = HomeVPAdapter(fragmentFood, childFragmentManager, lifecycle)
        todayVP.adapter = adapter
        binding!!.indicatorToday.attachTo(todayVP)
    }

    /*날짜를 바꿀 시 파이어베이스 요청*/
    private fun getNutrition(date: String) {
        loadingDialog.show()
        CoroutineScope(Dispatchers.Main).launch {
            nutritionResponse = NetworkManager.getFoodByDate(date)
            fragmentFood.apply {
                clear()
                // todayAdapter.notifyItemRangeRemoved(0, num)
                addAll(nutritionResponse.food_list)
                // todayAdapter.notifyItemRangeInserted(0, nutritionResponse.food_list.size)
            }
            if (isAdded()) {
                adapter = HomeVPAdapter(fragmentFood, childFragmentManager, lifecycle)
                todayVP.adapter = adapter
            }
            Handler(Looper.getMainLooper()).post {
                if (fragmentFood.size == 0) {
                    binding!!.tvNoFood.visibility = View.VISIBLE
                } else {
                    binding!!.tvNoFood.visibility = View.GONE
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

            if (pbHomeCalorie.progress >= 100.0) {
                pbHomeCalorie.progressBarColor = Color.RED
            } else {
                pbHomeCalorie.progressBarColor = Color.BLUE
            }

            if (pvHomeCarbo.progress >= 100.0) {
                pvHomeCarbo.highlightView.color = Color.RED
            } else {
                pvHomeCarbo.highlightView.color = Color.BLUE
            }

            if (pvHomeFat.progress >= 100.0) {
                pvHomeFat.highlightView.color = Color.RED
            } else {
                pvHomeFat.highlightView.color = Color.BLUE
            }

            if (pvHomeProtein.progress >= 100.0) {
                pvHomeProtein.highlightView.color = Color.RED
            } else {
                pvHomeProtein.highlightView.color = Color.BLUE
            }

            tvHomeCalorie1.text = nutrition.calorie.roundToInt().toString()
            tvHomeCalorie2.text = "/ " + max.calorie.roundToInt().toString() + " kcal"
            tvHomeCarbohydrates.text = nutrition.carbohydrates.roundToInt().toString() + " / " + max.carbohydrates.roundToInt().toString()
            tvHomeProteins.text = nutrition.protein.roundToInt().toString() + " / " + max.protein.roundToInt().toString()
            tvHomeFat.text = nutrition.fat.roundToInt().toString() + " / " + max.fat.roundToInt().toString()
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
