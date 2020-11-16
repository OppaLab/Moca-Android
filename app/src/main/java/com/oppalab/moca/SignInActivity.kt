package com.oppalab.moca

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.signup_btn

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signup_btn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        login_btn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = login_email.text.toString()
        val password = login_password.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "이메일 입력이 필요합니다.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "비밀번호 입력이 필요합니다.", Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("로그인")
                progressDialog.setMessage("잠시만 기다려 주십시오.")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val auth: FirebaseAuth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        progressDialog.dismiss()

                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        val message = it.exception!!.toString()
                        Toast.makeText(this, "Error $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}