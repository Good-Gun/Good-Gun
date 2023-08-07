package com.example.goodgun.login

import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.goodgun.User
import com.example.goodgun.databinding.RegistrationLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class RegistrationActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    lateinit var binding : RegistrationLayoutBinding
    private var PWValid = false
    private var PWChecked = false
    val database: DatabaseReference = Firebase.database("https://goodgun-4740f-default-rtdb.firebaseio.com/").reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationLayoutBinding.inflate(layoutInflater)
        auth = Firebase.auth


        binding.registrationPasswordInput.doAfterTextChanged {
            if(isValidPassword(binding.registrationPasswordInput.text.toString())){
                binding.pwRuleTextView.setTextColor(Color.GREEN)
                PWValid = true
            }else{
                binding.pwRuleTextView.setTextColor(Color.BLACK)
                PWValid = true
            }
        }
        binding.registrationPWCheckInput.doAfterTextChanged {
            if(binding.registrationPasswordInput.text.toString() == binding.registrationPWCheckInput.text.toString()){
                binding.pwCheckTextView2.setTextColor(Color.GREEN)
                PWChecked = true
            }else{
                binding.pwCheckTextView2.setTextColor(Color.BLACK)
                PWChecked = false
            }
        }




        setContentView(binding.root)
        // 계정 생성 버튼
        binding.registrationButton
            .setOnClickListener {
                createAccount(binding.registrationIdInput
                    .text.toString(),binding.registrationPasswordInput.text.toString(),binding.nameInput.text.toString())

        }

    }

    fun isValidPassword(password: String): Boolean {
        // 최소 길이 체크
        if (password.length < 6) {
            return false
        }
        // 6~12자의 영문 숫자, 혼합
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}\$")
        return regex.matches(password)
    }

    private fun createAccount(ID: String, password: String, name:String) {
        if (ID.isNotEmpty() && password.isNotEmpty() && PWChecked && PWValid) {
            auth?.createUserWithEmailAndPassword(ID, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "계정 생성 완료.",
                            Toast.LENGTH_SHORT
                        ).show()
                        database.child("user_list").child(ID).setValue(User(ID, password, name))
                        finish() // 가입창 종료
                    } else {
                        Toast.makeText(
                            this, "계정 생성 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}