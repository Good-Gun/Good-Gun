package com.example.goodgun.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import com.example.goodgun.ApplicationClass
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
    private lateinit var selectedFreqPosition: String
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
        multiAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
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
        exTypeList = arrayListOf("무산소 운동", "유산소 운동", "둘 다")
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
        exFreqList = arrayListOf("거의 안함", "1회 이하", "2 ~ 3회", "4 ~ 5 회", "6회 이상")
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
                selectedFreqPosition = position.toString()
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

    fun isStringConvertibleToInt(input: String): Boolean {
        return try {
            input.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun uploadData(currentUser: FirebaseUser?) {
        binding.startBtn.setOnClickListener {
            val uid = currentUser!!.uid
            val userRef = database.child("user_list").child(uid)
            val weight = binding.weightInput.text.toString()
            val height = binding.heightInput.text.toString()
            val age = binding.ageInput.text.toString()
            if(isStringConvertibleToInt(weight))
                userRef.child("u_weight").setValue(weight)
            else
                Toast.makeText(this, "몸무게를 올바르게 입력해주세요", Toast.LENGTH_SHORT).show()
            if(isStringConvertibleToInt(height))
                userRef.child("u_height").setValue(height)
            else
                Toast.makeText(this, "키를 올바르게 입력해주세요", Toast.LENGTH_SHORT).show()
            if(isStringConvertibleToInt(age))
                userRef.child("u_age").setValue(age)
            else
                Toast.makeText(this, "나이를 올바르게 입력해주세요", Toast.LENGTH_SHORT).show()
            var allergies = binding.allergyInput.text.toString().split(", ")
            allergies = allergies.dropLast(1)
            userRef.child("u_allergy").setValue(allergies)
            userRef.child("u_exercise_freq").setValue(selectedFreqPosition + 1)
            userRef.child("u_exercise_type").setValue(selectedType)
            ApplicationClass.updateUserInfo()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
