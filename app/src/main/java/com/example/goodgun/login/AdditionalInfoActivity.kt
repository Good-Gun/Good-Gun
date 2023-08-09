package com.example.goodgun.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.MainActivity
import com.example.goodgun.databinding.AdditionalInfoLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdditionalInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val database: DatabaseReference = Firebase.database("https://goodgun-4740f-default-rtdb.firebaseio.com/").reference
    lateinit var binding: AdditionalInfoLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdditionalInfoLayoutBinding.inflate(layoutInflater)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        setContentView(binding.root)
        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = database.child("user_list").child(uid)
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataSnapshot = task.result
                    val uWeightValue = dataSnapshot.child("u_weight").getValue(Int::class.java)
                    val uHeightValue = dataSnapshot.child("u_height").getValue(Int::class.java)
                    val uAgeValue = dataSnapshot.child("u_age").getValue(Int::class.java)
                    binding.weightInput.setText(uWeightValue.toString())
                    binding.heightInput.setText(uHeightValue.toString())
                    binding.ageInput.setText(uAgeValue.toString())
                }
            }
        } else {
            println("현재 사용자를 찾을 수 없습니다.")
        }
        // DB에 현재값 업로드
        binding.startBtn.setOnClickListener {
            val uid = currentUser!!.uid
            val userRef = database.child("user_list").child(uid)
            userRef.child("u_weight").setValue(binding.weightInput.text.toString())
            userRef.child("u_height").setValue(binding.heightInput.text.toString())
            userRef.child("u_age").setValue(binding.ageInput.text.toString())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
