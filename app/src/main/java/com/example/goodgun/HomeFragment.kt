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
import com.example.goodgun.main_function.FoodActivity
import com.example.goodgun.main_function.GraphActivity
import com.example.goodgun.main_function.TodayRVAdapter


class HomeFragment : Fragment() {
    lateinit var todayAdapter: TodayRVAdapter

    val todayArray:ArrayList<Pair<String, String>> = arrayListOf()
    var binding: FragmentHomeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        temporaryFillArr()
        initLayout()
        setProgress()

        return binding!!.root
    }

    override fun onResume() {
        super.onResume()

    }

    fun initLayout(){
        binding!!.homeBtn2.setOnClickListener {
            val intent = Intent(activity, FoodActivity::class.java)
            startActivity(intent)
        }
        binding!!.homeBtn3.setOnClickListener {
            val intent = Intent(activity, GraphActivity::class.java)
            startActivity(intent)
        }

        todayAdapter = TodayRVAdapter(requireContext(), todayArray)
        binding?.rvHomeToday?.adapter = todayAdapter
        binding?.rvHomeToday?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 간격 20으로
        val spaceDecoration = VerticalSpaceItemDecoration(20)
        binding?.rvHomeToday?.addItemDecoration(spaceDecoration)

    }

    private fun setProgress(){
        binding!!.pbHomeCalorie.setProgress(40)
    }

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    /*테스트용 배열 생성을 위한 임시 함수*/
    private fun temporaryFillArr(){
        todayArray.apply {
            for(i in 0 .. 3){
                add(Pair(('A'+i).toString(), ('A'+i).toString()))
            }
        }
    }



}

