package com.example.goodgun.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.MainActivity
import com.example.goodgun.databinding.AdditionalInfoLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class AdditionalInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val database: DatabaseReference = Firebase.database("https://goodgun-4740f-default-rtdb.firebaseio.com/").reference
    private lateinit var binding: AdditionalInfoLayoutBinding

    private lateinit var exTypeSpinner: Spinner
    private lateinit var exFreqSpinner: Spinner
    private lateinit var exTypeSpinnerAdapter: CustomSpinnerAdapter
    private lateinit var exFreqSpinnerAdapter: CustomSpinnerAdapter

    private var exTypeList: List<String> = ArrayList()
    private var exFreqList: List<String> = ArrayList()

    private lateinit var selectedType: String
    private lateinit var selectedFreq: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdditionalInfoLayoutBinding.inflate(layoutInflater)
        auth = Firebase.auth
        val currentUser = auth.currentUser

        var items = getAssetsTextArray(this, "allergies")
        var multiAutoCompleteTextView = binding.allergyInput
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, items)
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(CommaTokenizer())
        multiAutoCompleteTextView.setOnClickListener {
            multiAutoCompleteTextView.showDropDown()
        }
        // 운동 스피너 초기화
        initSpinners()
        // 유저의 기존 데이터 불러오기
        loadAdditionalInfo(currentUser)
        // DB에 현재값 업로드
        uploadData(currentUser)

        setContentView(binding.root)
    }

    private fun initSpinners() {
        exTypeList = arrayListOf("무산소 운동", "유산소 운동")
        exTypeSpinnerAdapter = CustomSpinnerAdapter(this, exTypeList)
        exTypeSpinner = binding.exTypeSpinner
        exTypeSpinner.adapter = exTypeSpinnerAdapter
        exTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedType = exTypeSpinner.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        exFreqList = arrayListOf("1회 이하", "2 ~ 3회", "4 ~ 5 회", "6회 이상")
        exFreqSpinnerAdapter = CustomSpinnerAdapter(this, exFreqList)
        exFreqSpinner = binding.exFreqSpinner
        exFreqSpinner.adapter = exFreqSpinnerAdapter
        exFreqSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedFreq = exFreqSpinner.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    fun getAssetsTextArray(mContext: Context, fileName: String): Array<String> {
        val lines = mutableListOf<String>()
        val reader: BufferedReader
        try {
            reader = BufferedReader(
                InputStreamReader(mContext.resources.assets.open("$fileName.txt"))
            )
            var str: String?
            while (reader.readLine().also { str = it } != null) {
                lines.add(str!!)
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return lines.toTypedArray()
    }
    private fun loadAdditionalInfo(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = database.child("user_list").child(uid)
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataSnapshot = task.result
                    val uWeightValue = dataSnapshot.child("u_weight").getValue(String::class.java)
                    val uHeightValue = dataSnapshot.child("u_height").getValue(String::class.java)
                    val uAgeValue = dataSnapshot.child("u_age").getValue(String::class.java)
                    binding.weightInput.setText(uWeightValue.toString())
                    binding.heightInput.setText(uHeightValue.toString())
                    binding.ageInput.setText(uAgeValue.toString())
                }
            }
        } else {
            println("현재 사용자를 찾을 수 없습니다.")
        }
        return
    }
    private fun uploadData(currentUser: FirebaseUser?) {
        binding.startBtn.setOnClickListener {
            val uid = currentUser!!.uid
            val userRef = database.child("user_list").child(uid)
            userRef.child("u_weight").setValue(binding.weightInput.text.toString())
            userRef.child("u_height").setValue(binding.heightInput.text.toString())
            userRef.child("u_age").setValue(binding.ageInput.text.toString())
            var allergies = binding.allergyInput.text.toString().split(", ")
            allergies = allergies.dropLast(1)

            userRef.child("u_allergy").setValue(allergies)

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
