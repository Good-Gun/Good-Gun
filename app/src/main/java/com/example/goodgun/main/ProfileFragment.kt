package com.example.goodgun.main

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.goodgun.ApplicationClass
import com.example.goodgun.databinding.FragmentProfileBinding
import com.example.goodgun.login.CustomSpinnerAdapter
import com.example.goodgun.login.LoginActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ProfileFragment : Fragment() {
    private val auth = Firebase.auth
    val currentUser = auth.currentUser

    private lateinit var callback: OnBackPressedCallback

    private lateinit var exTypeSpinner: Spinner
    private lateinit var exFreqSpinner: Spinner
    private lateinit var goalSpinner: Spinner
    private lateinit var exTypeSpinnerAdapter: CustomSpinnerAdapter
    private lateinit var exFreqSpinnerAdapter: CustomSpinnerAdapter
    private lateinit var goalSpinnerAdapter: CustomSpinnerAdapter

    private var selectedTypePosition: Int = 0
    private var selectedFreqPosition: Int = 0
    private var selectedGoalPosition: Int = 0
    private var uExTypePos = 0
    private var uExFreqPos = 0
    private var uExGoalPos = 0

    private var exTypeList: List<String> = ArrayList()
    private var exFreqList: List<String> = ArrayList()
    private var goalList: List<String> = ArrayList()

    val database: DatabaseReference = Firebase.database("https://goodgun-4740f-default-rtdb.firebaseio.com/").reference
    lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        initLayout()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireActivity() as MainActivity).binding.bubbleTabBar.setSelected(com.example.goodgun.R.id.nav_home)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(com.example.goodgun.R.id.frame_main, HomeFragment())
                    .commitAllowingStateLoss()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
    fun getAssetsTextArray(mContext: ProfileFragment, fileName: String): Array<String> {
        val lines = mutableListOf<String>()
        val reader: BufferedReader
        try {
            reader = BufferedReader(
                InputStreamReader(mContext.resources.assets.open("$fileName.txt")),
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

    private fun initLayout() {
        // 알레르기 멀티 검색바
        var items = getAssetsTextArray(this, "allergies")
        var multiAutoCompleteTextView = binding.profileAllergy
        var adapter = ArrayAdapter<String>(requireContext(), R.layout.simple_dropdown_item_1line, items)
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        multiAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                multiAutoCompleteTextView.showDropDown()
            }
        }
        multiAutoCompleteTextView.setOnClickListener {
            multiAutoCompleteTextView.showDropDown()
        }
        binding.apply {
            loadProfileInfo(currentUser)
        }
        // 로그아웃
        binding.profileLogoutBtn.setOnClickListener {
            auth.signOut()

            var logoutIntent = Intent(this.context, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(logoutIntent)
        }

        binding.profileFixBtn.setOnClickListener {
            uploadData(currentUser)
            Toast.makeText(this.requireContext(), "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
        }
        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(com.example.goodgun.R.id.frame_main, HomeFragment())
                .commitAllowingStateLoss()
        }
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
        val uid = currentUser!!.uid
        val userRef = database.child("user_list").child(uid)

        val height = binding.profileHeightInput.text.toString()
        val weight = binding.profileWeightInput.text.toString()
        val age = binding.profileAgeInput.text.toString()

        if (isStringConvertibleToInt(height)) {
            userRef.child("u_height").setValue(height)
        } else {
            Toast.makeText(this.requireContext(), "키를 올바르게 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        if (isStringConvertibleToInt(weight)) {
            userRef.child("u_weight").setValue(weight)
        } else {
            Toast.makeText(this.requireContext(), "몸무게를 올바르게 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        if (isStringConvertibleToInt(age)) {
            userRef.child("u_age").setValue(age)
        } else {
            Toast.makeText(this.requireContext(), "나이를 올바르게 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        var allergies = binding.profileAllergy.text.toString().split(", ")
        allergies = allergies.dropLast(1)
        userRef.child("u_allergy").setValue(allergies)
        userRef.child("u_exercise_type").setValue((selectedTypePosition + 1).toString())
        userRef.child("u_exercise_freq").setValue((selectedFreqPosition + 1).toString())
        userRef.child("u_physical_goals").setValue((selectedGoalPosition + 1).toString())
        ApplicationClass.updateUserInfo()
    }

    private fun initSpinners(typePos: Int, freqPos: Int, goalPos: Int) {
        println("==========" + typePos.toString() + freqPos.toString() + goalPos.toString())
        exTypeList = arrayListOf("무산소 운동", "유산소 운동", "둘 다")
        exTypeSpinnerAdapter = CustomSpinnerAdapter(this.requireContext(), exTypeList)
        exTypeSpinner = binding.profileExTypeSpinner

        selectedTypePosition = typePos
        exTypeSpinner.adapter = exTypeSpinnerAdapter
        exTypeSpinner.setSelection(typePos)
        exTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
                selectedTypePosition = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedTypePosition = typePos
                exTypeSpinner.setSelection(typePos)
            }
        }

        exFreqList = arrayListOf("거의 안함", "1회 이하", "2 ~ 3회", "4 ~ 5 회", "6회 이상")
        exFreqSpinnerAdapter = CustomSpinnerAdapter(this.requireContext(), exFreqList)
        exFreqSpinner = binding.profileExFreqSpinner
        exFreqSpinner.adapter = exFreqSpinnerAdapter
        exFreqSpinner.setSelection(freqPos)
        exFreqSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
                selectedFreqPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedFreqPosition = freqPos
                exFreqSpinner.setSelection(freqPos)
            }
        }

        goalList = arrayListOf("다이어트", "밸런스", "벌크 업")
        goalSpinnerAdapter = CustomSpinnerAdapter(this.requireContext(), goalList)
        goalSpinner = binding.profileGoalSpinner
        goalSpinner.adapter = goalSpinnerAdapter
        goalSpinner.setSelection(goalPos)
        goalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectedGoalPosition = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedGoalPosition = goalPos
                goalSpinner.setSelection(goalPos)
            }
        }
    }

    private fun loadProfileInfo(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = database.child("user_list").child(uid)
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataSnapshot = task.result
                    val uName = dataSnapshot.child("u_name").getValue(String::class.java)
                    val uEmail = dataSnapshot.child("u_email").getValue(String::class.java)
                    val uPW = dataSnapshot.child("u_password").getValue(String::class.java)
                    val uHeight = dataSnapshot.child("u_height").getValue(String::class.java)
                    val uWeight = dataSnapshot.child("u_weight").getValue(String::class.java)
                    val uAge = dataSnapshot.child("u_age").getValue(String::class.java)
                    val typeIndicator = object : GenericTypeIndicator<List<String>>() {}
                    val uAllergies: List<String>? = dataSnapshot.child("u_allergy").getValue(typeIndicator)
                    val result = StringBuilder()
                    if (uAllergies == null) {
                        result.append("")
                    } else {
                        for ((index, allergy) in uAllergies.withIndex()) {
                            result.append(allergy)
                            if (index < uAllergies.size - 1) {
                                result.append(", ")
                            }
                        }
                    }
                    uExTypePos = dataSnapshot.child("u_exercise_type").getValue(String::class.java)!!.toInt()
                    uExFreqPos = dataSnapshot.child("u_exercise_freq").getValue(String::class.java)!!.toInt()
                    uExGoalPos = dataSnapshot.child("u_physical_goals").getValue(String::class.java)!!.toInt()

                    binding.profileNameInput.setText(uName)
                    binding.profileIdInput.setText(uEmail)
                    binding.profilePwInput.setText(uPW)
                    binding.profileHeightInput.setText(uHeight)
                    binding.profileWeightInput.setText(uWeight)
                    binding.profileAgeInput.setText(uAge)
                    binding.profileAllergy.setText(result)

                    initSpinners(uExTypePos - 1, uExFreqPos - 1, uExGoalPos - 1)
                }
            }
        } else {
            println("현재 사용자를 찾을 수 없습니다.")
        }
        return
    }
}
