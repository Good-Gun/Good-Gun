package com.example.goodgun.login

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.goodgun.databinding.RegistrationLayoutBinding
import com.example.goodgun.network.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    lateinit var binding: RegistrationLayoutBinding
    private var PWValid = false
    private var PWChecked = false
    private var emailValid = false
    val database: DatabaseReference = Firebase.database("https://goodgun-4740f-default-rtdb.firebaseio.com/").reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationLayoutBinding.inflate(layoutInflater)
        auth = Firebase.auth

        binding.registrationPasswordInput.doAfterTextChanged {
            if (isValidPassword(binding.registrationPasswordInput.text.toString())) {
                binding.pwRuleTextView.setTextColor(Color.GREEN)
                PWValid = true
            } else {
                binding.pwRuleTextView.setTextColor(Color.BLACK)
                PWValid = true
            }
        }
        binding.registrationPWCheckInput.doAfterTextChanged {
            if (binding.registrationPasswordInput.text.toString() == binding.registrationPWCheckInput.text.toString()) {
                binding.pwCheckTextView2.setTextColor(Color.GREEN)
                PWChecked = true
            } else {
                binding.pwCheckTextView2.setTextColor(Color.BLACK)
                PWChecked = false
            }
        }
        binding.registrationIdInput.doAfterTextChanged {
            if (isValidEmail(binding.registrationIdInput.text.toString())) {
                binding.emailValid.setTextColor(Color.GREEN)
            } else {
                binding.emailValid.setTextColor(Color.BLACK)
            }
        }
        binding.duplicateCheckButton.setOnClickListener {
            checkEmailDuplicate(
                binding.registrationIdInput.text.toString(),
                onSuccess = {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                },
            )
        }
        binding.backBtn.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
        // 계정 생성 버튼
        binding.registrationButton
            .setOnClickListener {
                createAccount(
                    binding.registrationIdInput
                        .text.toString(),
                    binding.registrationPasswordInput.text.toString(),
                    binding.nameInput.text.toString(),
                )
            }
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")
        return emailPattern.matches(email)
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

    fun checkEmailDuplicate(email: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        auth?.fetchSignInMethodsForEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods ?: emptyList()
                    if (signInMethods.isEmpty()) {
                        // 이메일이 중복되지 않음
                        onSuccess.invoke("사용 가능한 이메일 주소입니다.")
                    } else {
                        // 이메일이 이미 사용 중
                        onFailure.invoke("이미 사용 중인 이메일 주소입니다.")
                    }
                } else {
                    onFailure.invoke("중복 확인 중 오류가 발생했습니다.")
                }
            }
    }

    private fun createAccount(ID: String, password: String, name: String) {
        if (ID.isNotEmpty() && password.isNotEmpty() && PWChecked && PWValid && isValidEmail(ID)) {
            auth?.createUserWithEmailAndPassword(ID, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "계정이 생성되었습니다!",
                            Toast.LENGTH_SHORT,
                        ).show()
                        database.child("user_list").child(auth!!.uid!!).setValue(User(ID, password, name))
                        finish() // 가입창 종료
                    } else {
                        Toast.makeText(
                            this,
                            "계정 생성에 실패했습니다.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "아직 조건이 만족하지 않은 입력창이 존재합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
