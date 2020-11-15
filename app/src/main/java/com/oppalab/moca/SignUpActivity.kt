package com.oppalab.moca

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.view.children
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    var categories = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signup_btn.setOnClickListener {
            if (category_family.isChecked) categories += "Family,"
            if (category_friend.isChecked) categories += "Friend,"
            if (category_parent.isChecked) categories += "Parent,"
            if (category_couple.isChecked) categories += "Couple,"
            if (category_money.isChecked) categories += "Money,"
            if (category_school.isChecked) categories += "School,"
            if (category_study.isChecked) categories += "Study,"
            if (category_sex.isChecked) categories += "Sex,"
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
            userCategoryList = userCategory.split(",")
        ).enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response?.isSuccessful) {
                    PreferenceManager.setLong(applicationContext,"userId", response.body()!!)
                    Log.d("KEY",PreferenceManager.getLong(applicationContext, "userId").toString())
                    Toast.makeText(
                        getApplicationContext(),
                        "SignUp Complete",
                        Toast.LENGTH_LONG
                    ).show();
                }
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