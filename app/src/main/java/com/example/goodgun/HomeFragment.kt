package com.example.goodgun

import android.animation.ValueAnimator
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
import com.example.goodgun.add_food.ScanInfomation
import com.example.goodgun.databinding.FragmentHomeBinding
import com.example.goodgun.main_function.FoodActivity
import com.example.goodgun.main_function.GraphActivity
import com.example.goodgun.main_function.SolutionActivity
import com.example.goodgun.main_function.TodayRVAdapter
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Food
import com.example.goodgun.network.model.NutritionResponse
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.util.LoadingDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private lateinit var loadingDialog: Dialog // 로딩창 클래스
    lateinit var todayAdapter: TodayRVAdapter // 오늘 섭취한 음식정보 recyclerView
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
        initRV()

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

    private fun initRV() {
        todayAdapter = TodayRVAdapter(requireContext(), food_list, 5)
        binding?.rvHomeToday?.adapter = todayAdapter
        binding?.rvHomeToday?.layoutManager = LinearLayoutManagerWrapper(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 간격 20으로
        val spaceDecoration = this.VerticalSpaceItemDecoration(20)
        binding?.rvHomeToday?.addItemDecoration(spaceDecoration)
    }

    /*날짜를 바꿀 시 파이어베이스 요청*/
    private fun getNutrition(date: String) {
        CoroutineScope(Dispatchers.Main).launch {
            nutritionResponse = NetworkManager.getFoodByDate(date)
            food_list.apply {
                val num = food_list.size
                clear()
                todayAdapter.notifyItemRangeRemoved(0, num)
                addAll(nutritionResponse.food_list)
                todayAdapter.notifyItemRangeInserted(0, nutritionResponse.food_list.size)
            }
            Handler(Looper.getMainLooper()).post {
                if (food_list.size == 0) {
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

            //pbHomeCalorie.progress = (nutrition.calorie / 2000.0 * 100.0).toFloat()
            tvHomeCalorie1.text = nutrition.calorie.toString()
            tvHomeCalorie2.text = "/" + max.calorie.toString()
            tvHomeCarbohydrates.text = nutrition.carbohydrates.toString() + " / " + max.carbohydrates.toString()
            tvHomeProteins.text = nutrition.protein.toString() + " / " + max.protein.toString()
            tvHomeFat.text = nutrition.fat.toString() + " / " + max.fat.toString()
        }
        loadingDialog.dismiss()
    }

    /*리사이클러뷰에서 아이템 간격 조정*/
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
    inner class LinearLayoutManagerWrapper : LinearLayoutManager {
        constructor(context: Context) : super(context) {}

        constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {}

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

        override fun supportsPredictiveItemAnimations(): Boolean {
            return false
        }
    }
}
