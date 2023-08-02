package com.example.goodgun.login

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.databinding.RegistrationLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    lateinit var binding : RegistrationLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationLayoutBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(binding.root)

        // 계정 생성 버튼
        binding.registrationButton
            .setOnClickListener {
                createAccount(binding.registrationIdInput
                    .text.toString(),binding.registrationPasswordInput.text.toString())
//                if(validCheck(binding.idInput.text.toString(), binding.pwInput.text.toString())){
//                    createAccount(binding.idInput.text.toString(),binding.pwInput.text.toString())
//                }else{
//                    Toast.makeText(this, "유효성 검사 통과 실패", Toast.LENGTH_SHORT).show()
//                }

        }

    }

    private fun validCheck(ID: String, password: String): Boolean {
        //규칙을 뭘로 할까
        return true
    }

    private fun createAccount(ID: String, password: String) {

        if (ID.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(ID, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "계정 생성 완료.",
                            Toast.LENGTH_SHORT
                        ).show()
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