package com.example.goodgun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.goodgun.databinding.FragmentProfileBinding
import com.example.goodgun.login.CustomSpinnerAdapter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class ProfileFragment : Fragment() {
    private val auth = Firebase.auth
    val currentUser = auth.currentUser

    private lateinit var exTypeSpinner: Spinner
    private lateinit var exFreqSpinner: Spinner
    private lateinit var goalSpinner: Spinner
    private lateinit var exTypeSpinnerAdapter: CustomSpinnerAdapter
    private lateinit var exFreqSpinnerAdapter: CustomSpinnerAdapter
    private lateinit var goalSpinnerAdapter: CustomSpinnerAdapter

    private var selectedTypePosition by Delegates.notNull<Int>()
    private var selectedFreqPosition by Delegates.notNull<Int>()
    private var selectedGoalPosition by Delegates.notNull<Int>()

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

    private fun initLayout() {
        binding.apply {
            loadProfileInfo(currentUser)
        }
        binding.profileFixBtn.setOnClickListener {
            uploadData(currentUser)
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
        userRef.child("u_exercise_freq").setValue((selectedFreqPosition + 1).toString())
        userRef.child("u_exercise_type").setValue((selectedTypePosition + 1).toString())
        userRef.child("u_physical_goals").setValue((selectedGoalPosition + 1).toString())
        ApplicationClass.updateUserInfo()
    }

    private fun initSpinners(typePos: Int, freqPos: Int, goalPos: Int) {
        exTypeList = arrayListOf("무산소 운동", "유산소 운동", "둘 다")
        exTypeSpinnerAdapter = CustomSpinnerAdapter(this.requireContext(), exTypeList)
        exTypeSpinner = binding.profileExTypeSpinner
        exTypeSpinner.adapter = exTypeSpinnerAdapter
        exTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                exTypeSpinner.setSelection(typePos)
            }
        }
        exFreqList = arrayListOf("거의 안함", "1회 이하", "2 ~ 3회", "4 ~ 5 회", "6회 이상")
        exFreqSpinnerAdapter = CustomSpinnerAdapter(this.requireContext(), exFreqList)
        exFreqSpinner = binding.profileExFreqSpinner
        exFreqSpinner.adapter = exFreqSpinnerAdapter
        exFreqSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                exFreqSpinner.setSelection(freqPos)
            }
        }

        goalList = arrayListOf("다이어트", "밸런스", "벌크 업")
        goalSpinnerAdapter = CustomSpinnerAdapter(this.requireContext(), goalList)
        goalSpinner = binding.profileGoalSpinner
        goalSpinner.adapter = goalSpinnerAdapter
        goalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                TODO("Not yet implemented")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
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
                    val uExTypePos = dataSnapshot.child("u_exercise_type").getValue(String::class.java)!!.toInt()
                    val uExFreqPos = dataSnapshot.child("u_exercise_freq").getValue(String::class.java)!!.toInt()
                    val uExGoalPos = dataSnapshot.child("u_physical_goals").getValue(String::class.java)!!.toInt()

                    binding.profileNameInput.setText(uName)
                    binding.profileIdInput.setText(uEmail)
                    binding.profilePwInput.setText(uPW)
                    binding.profileHeightInput.setText(uHeight)
                    binding.profileWeightInput.setText(uWeight)
                    binding.profileAgeInput.setText(uAge)
                    binding.profileAllergy.setText(result)
                    initSpinners(uExTypePos, uExFreqPos, uExGoalPos)
                }
            }
        } else {
            println("현재 사용자를 찾을 수 없습니다.")
        }
        return
    }
}
