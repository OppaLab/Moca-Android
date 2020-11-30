package com.oppalab.moca

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.view.children
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    var categories = ""
    var access_token = ""

    override fun onStart() {
        super.onStart()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            access_token = token!!
            Log.d("token", token!!)
            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signup_btn.setOnClickListener {
            if (category_family.isChecked) categories += "가족,"
            if (category_friend.isChecked) categories += "친구,"
            if (category_parent.isChecked) categories += "부모님,"
            if (category_couple.isChecked) categories += "연인,"
            if (category_money.isChecked) categories += "금전,"
            if (category_school.isChecked) categories += "학교,"
            if (category_study.isChecked) categories += "학업,"
            if (category_sex.isChecked) categories += "성,"
            categories.substring(0, categories.length - 1)
            CreatAccount()
        }

        signin_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun CreatAccount() {
        val email = signup_email.text.toString()
        val password = signup_password.text.toString()
        val userName = signup_user_name.text.toString()
        val userCategory: String = categories

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "이메일 입력이 필요합니다.", Toast.LENGTH_LONG)
                .show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "비밀번호 입력이 필요합니다.",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "유저 네임이 필요합니다.", Toast.LENGTH_LONG)
                .show()
            TextUtils.isEmpty(userCategory) -> Toast.makeText(
                this,
                "카테고리 설정이 필요합니다..",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("회원가입")
                progressDialog.setMessage("잠시만 기다려 주십시오.")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val auth: FirebaseAuth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            saveUserInfo(email, userName, userCategory, progressDialog)
                        } else {
                            val message = it.exception!!.toString()
                            Toast.makeText(this, "Error $message", Toast.LENGTH_LONG).show()
                            auth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(
        email: String,
        userName: String,
        userCategory: String,
        progressDialog: ProgressDialog
    ) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["email"] = email
        userMap["username"] = userName.toLowerCase()
        userMap["category"] = userCategory
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/moca-a7445.appspot.com/o/Default_Images%2Ffriend.png?alt=media&token=3e162428-277f-4514-add5-751c2ec3b5ae"

        RetrofitConnection.server.signUp(
            nickname = userName.toLowerCase(),
            email = email,
            userCategoryList = userCategory.split(","),
            registrationToken = access_token
        ).enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("아이디값", response.body().toString())
                PreferenceManager.setLong(applicationContext, "userId", response.body()!!)
                Log.d(
                    "retrofit signup userId",
                    PreferenceManager.getLong(applicationContext, "userId").toString()
                )
                Toast.makeText(
                    getApplicationContext(),
                    "SignUp Complete",
                    Toast.LENGTH_LONG
                ).show();
                progressDialog.dismiss()

                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit result", t.message.toString())
                Toast.makeText(
                    getApplicationContext(),
                    "Fail",
                    Toast.LENGTH_LONG
                ).show();
            }

        })

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "계정이 생성되었습니다.", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)
//                    finish()
                } else {
                    val message = it.exception!!.toString()
                    Toast.makeText(this, "Error $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}