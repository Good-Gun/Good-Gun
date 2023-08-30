package com.example.goodgun.main_function

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.doinglab.foodlens.sdk.ui.util.FoodLensDBManager.init
import com.doinglab.foodlens.sdk.ui.util.UnitTokenizer.tokenizeString
import com.example.goodgun.ApplicationClass
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivitySolutionBinding
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Nutrition
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.roomDB.FoodDatabase
import com.example.goodgun.util.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    lateinit var roomdb: FoodDatabase
    private lateinit var auth: FirebaseAuth
    var currentUser: FirebaseUser? = null
    var userid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolutionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this@SolutionActivity, "유효하지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
        } else {
            userid = currentUser!!.uid
        }
        roomdb = DatabaseManager.getDatabaseInstance(userid, applicationContext)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        val statusBarColor = getColor(R.color.red) // 원하는 색상 리소스로 변경
        window.statusBarColor = statusBarColor

        initLayout()
        // initFr()
        init()
    }
    fun initLayout() {
        viewPager = binding.vpSolution
        adapter = SolutionVPAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    fun init() {
        CoroutineScope(Dispatchers.Main).launch {
            /*val solution = roomdb.foodDao().getSolution()
            if(solution.str.isBlank()){
                fragmentTexts.add("솔루션이 준비되지 않았습니다:음식을 등록하고 드신 음식을 기반으로 건강 솔루션을 받아보세요")
                adapter.setFragmentTexts(fragmentTexts)
                loadingDialog.dismiss()
            } else{
                tokenizeString(solution.str)
                adapter.setFragmentTexts(fragmentTexts)
                loadingDialog.dismiss()
            }*/

            val solution = ApplicationClass.sharedPreferences.getString("solution", null)
            if (solution.isNullOrBlank()) {
                fragmentTexts.add("솔루션이 준비되지 않았습니다:음식을 등록하고 드신 음식을 기반으로 건강 솔루션을 받아보세요")
                adapter.setFragmentTexts(fragmentTexts)
                loadingDialog.dismiss()
            } else {
                tokenizeString(solution)
                adapter.setFragmentTexts(fragmentTexts)
                loadingDialog.dismiss()
            }
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
