package com.example.goodgun.login

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.databinding.LoginLayoutBinding
import com.example.goodgun.main.MainActivity
import com.example.goodgun.network.model.User
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient ? = null
    var GOOGLE_LOGIN_CODE = 9001
    val database: DatabaseReference = Firebase.database("https://goodgun-4740f-default-rtdb.firebaseio.com/").reference

    lateinit var binding: LoginLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginLayoutBinding.inflate(layoutInflater)

//        DB 연결 test
//        val myRef= database.getReference("massage")
//        myRef.setValue("Hello world")

        auth = Firebase.auth
        var currentUser = auth.currentUser
        // 자동 로그인

        val logoutFlag = intent.getIntExtra("logout", 0)

        if (currentUser != null) {
            Toast.makeText(this, currentUser.email + " 로 로그인", Toast.LENGTH_LONG).show()
            // database.child("user_list").child(currentUser.uid).setValue(User(currentUser.email.toString(), currentUser.displayName.toString()))
            startActivity(Intent(this, MainActivity::class.java))
            finish() // 로그인 엑티비티는 종료
        }

        setContentView(binding.root)

        var google_sign_in_button = binding.googleLoginBtn

        setGoogleButtonText(google_sign_in_button, "Google 계정으로 로그인")

        google_sign_in_button.setOnClickListener {
            googleLogin()
        }

        // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
        val default_web_client_id = "757404398715-jm0ar4ksakrsdo1jqqe4i7s8f62hoq79.apps.googleusercontent.com"
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(default_web_client_id)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        if (logoutFlag == 1) {
            // 로그아웃 관련 작업을 수행하는 함수 호출
            auth.signOut()
            googleSignInClient?.signOut()
        }

        // 회원가입으로 이동
        binding.regiBtn.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            signIn(binding.idInput.text.toString(), binding.pwInput.text.toString())
        }
    }

    private fun setGoogleButtonText(loginButton: SignInButton, buttonText: String) {
        var i = 0
        while (i < loginButton.childCount) {
            var v = loginButton.getChildAt(i)
            if (v is TextView) {
                var tv = v
                tv.setText(buttonText)
                tv.setGravity(Gravity.CENTER)
                return
            }
            i++
        }
    }

    private fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        if (signInIntent != null) {
            startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
        }
    } // googleLogin

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if (result.isSuccess) {
                    var account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                }
            }
        } // if
    } // onActivityResult

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    // 로그인 성공 시
                    // 이미 회원인 경우의 action
                    val userId = auth.currentUser?.uid.toString()
                    val userRef = database.child("user_list")
                    userRef.child(userId).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val dataSnapshot = task.result
                            if (dataSnapshot.exists()) {
                                println("uid가 user_list에 포함되어 있습니다.")
                                startActivity(Intent(this, MainActivity::class.java))
                                finish() // 로그인 엑티비티는 종료
                            } else {
                                userRef.child(userId).setValue(User(account?.email.toString(), account?.familyName.toString() + account?.givenName.toString()))
                                startActivity(Intent(this, AdditionalInfoActivity::class.java))
                                println("uid가 user_list에 포함되어 있지 않습니다.")
                                finish() // 로그인 엑티비티는 종료
                            }
                        } else {
                            println("데이터 가져오기 실패: ${task.exception}")
                            finish() // 로그인 엑티비티는 종료
                        }
                    }
                } else {
                    // 로그인 실패 시
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    } // firebaseAuthWithGoogle

    private fun signIn(ID: String, password: String) {
        if (ID.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(ID, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "로그인에 성공 하였습니다.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        moveMainPage(auth?.currentUser)
                    } else {
                        Toast.makeText(
                            baseContext,
                            "로그인에 실패 하였습니다.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, AdditionalInfoActivity::class.java))
            finish() // 로그인 페이지는 종료
        }
    }
}
