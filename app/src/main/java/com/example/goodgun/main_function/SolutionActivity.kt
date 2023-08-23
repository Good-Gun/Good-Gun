package com.example.goodgun.main_function

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivitySolutionBinding
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Nutrition
import com.example.goodgun.util.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SolutionActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: SolutionVPAdapter
    private lateinit var binding: ActivitySolutionBinding
    private lateinit var loadingDialog: Dialog
    private val fragmentTexts = mutableListOf<String>()
    private var response: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolutionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        val statusBarColor = getColor(R.color.red) // 원하는 색상 리소스로 변경
        window.statusBarColor = statusBarColor

        initLayout()
        initFr()
    }
    fun initLayout() {
        viewPager = binding.vpSolution
        adapter = SolutionVPAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
    fun initFr() {
        val question = Nutrition().getQuestion(2)!!
        CoroutineScope(Dispatchers.Main).launch {
            response = NetworkManager.callAI(question)
            tokenizeString(response)
            adapter.setFragmentTexts(fragmentTexts)
            loadingDialog.dismiss()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun tokenizeString(str: String) {
        str.split("1.", "2.", "3.", "4.", "5.", "6.", "7.").toCollection(fragmentTexts)
        fragmentTexts.removeAt(0)
        Log.d("Checking OPENAI", "Hi from SolutionActivity: $str")
    }
}
