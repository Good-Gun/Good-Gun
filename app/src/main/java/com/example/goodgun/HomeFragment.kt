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
import com.google.android.play.integrity.internal.m
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private lateinit var loadingDialog: Dialog  //로딩창 클래스
    lateinit var todayAdapter: TodayRVAdapter   //오늘 섭취한 음식정보 recyclerView
    lateinit var  nutrition: Nutrition          //영양 정보 저장을 위한 클래스
    val food_list: ArrayList<Food> = arrayListOf()  //음식 리스트
    lateinit var today:String       //오늘 날짜 저장
    lateinit var date:String        //다른 날짜의 영양정보 탐색을 위한 변수


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

    /*일반 레이아웃 초기화*/
    fun initLayout() {
        binding!!.apply {
            tvHomeName.text = ApplicationClass.uname
            tvHomeDate.text = today

            /*날짜 탐색 (이전 날짜)*/
            ivHomeLeft.setOnClickListener {
                loadingDialog.show()
                date = LocalDate.parse(date.trim()).minusDays(1).toString()
                tvHomeDate.text = date
                getNutrition(date)
            }

            /*날짜 탐색 (다음 날짜)*/
            ivHomeRight.setOnClickListener {
                loadingDialog.show()
                if(LocalDate.parse(today.trim()) > LocalDate.parse(date.trim())) {
                    date = LocalDate.parse(date.trim()).plusDays(1).toString()
                    tvHomeDate.text = date
                    getNutrition(date)
                }
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
        binding?.rvHomeToday?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 간격 20으로
        val spaceDecoration = this.VerticalSpaceItemDecoration(20)
        binding?.rvHomeToday?.addItemDecoration(spaceDecoration)


        /*파이어베이스 요청*/

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

    /*날짜를 바꿀 시 파이어베이스 요청*/
    private fun getNutrition(date:String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                Log.d("Firebase Communication at main", "at main, date: $date")
                nutrition = FirebaseManager.getDayNutrition(date)
            }
            setProgress()
        }
    }

    /*프로그래스 및 기타 정보 수정*/
    private fun setProgress() {
        val max = ApplicationClass.maxNutrition
        binding!!.apply {
            pbHomeCalorie.setProgress((nutrition.calorie/2000.0 * 100.0).toInt())
            tvHomeCalorie.text = nutrition.calorie.toString()+"/"+max.calorie.toString()
            tvHomeCarbohydrates.text = nutrition.carbohydrates.toString()+"/"+max.carbohydrates.toString()
            tvHomeProteins.text = nutrition.protein.toString()+"/"+max.protein.toString()
            tvHomeFat.text = nutrition.fat.toString()+"/"+ max.fat.toString()
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

}
