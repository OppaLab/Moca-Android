package com.oppalab.moca.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.oppalab.moca.R
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import kotlinx.android.synthetic.main.activity_account_setting.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountSettingActivity : AppCompatActivity() {

    private var categories :String = ""
    private var nickname :String = ""
    private var currentUser : Long = 0L
    private var receiveRandomPush = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        var intent = intent
        categories = intent.getStringExtra("categoriesList")!!
        nickname = intent.getStringExtra("nickname")!!
        currentUser = intent.getStringExtra("currentUser")!!.toLong()
        receiveRandomPush = intent.getBooleanExtra("receiveRandomPush", true)

        if (receiveRandomPush) setting_random_push_switch.isChecked = true
        else setting_random_push_switch.isChecked = false

        setting_logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }


        setting_user_name.setText(nickname)

        Log.d("GetCategories", categories)
        val categoryIndex : String = categories
            if (categoryIndex.contains("가족")) setting_category_family.setChecked(true)
            if (categoryIndex.contains("친구")) setting_category_friend.setChecked(true)
            if (categoryIndex.contains("부모님")) setting_category_parent.setChecked(true)
            if (categoryIndex.contains("연인")) setting_category_couple.setChecked(true)
            if (categoryIndex.contains("금전")) setting_category_money.setChecked(true)
            if (categoryIndex.contains("학교")) setting_category_school.setChecked(true)
            if (categoryIndex.contains("학업")) setting_category_study.setChecked(true)
            if (categoryIndex.contains("성")) setting_category_sex.setChecked(true)
            if (categoryIndex.contains("외모")) setting_category_appearence.setChecked(true)

        setting_random_push_switch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                receiveRandomPush = true
            } else {
                receiveRandomPush = false
            }
        }

        delete_account_btn.setOnClickListener {
            RetrofitConnection.server.signOut(userId = PreferenceManager.getLong(applicationContext, "userId")).enqueue(object: Callback<Long> {
                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    Toast.makeText(applicationContext, "회원탈퇴 완료", Toast.LENGTH_LONG)
                    val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }

        save_profile_button.setOnClickListener {
            categories = ""
            if (setting_category_family.isChecked) categories += "가족,"
            if (setting_category_friend.isChecked) categories += "친구,"
            if (setting_category_parent.isChecked) categories += "부모님,"
            if (setting_category_couple.isChecked) categories += "연인,"
            if (setting_category_money.isChecked) categories += "금전,"
            if (setting_category_school.isChecked) categories += "학교,"
            if (setting_category_study.isChecked) categories += "학업,"
            if (setting_category_sex.isChecked) categories += "성,"
            if (setting_category_appearence.isChecked) categories += "외모,"
            categories.substring(2, categories.length-1)

            Log.d("ToSaveCategories", categories)
            updateUserInfo()
        }

    }

    private fun updateUserInfo() {
        categories = categories.substring(0, categories.length - 1)
        when {
            TextUtils.isEmpty(setting_user_name.text.toString()) -> Toast.makeText(this, "수정할 닉네임을 입력해주세요.", Toast.LENGTH_LONG).show()
            categories == "" -> Toast.makeText(this, "카테고리를 골라주세요.", Toast.LENGTH_LONG).show()
            else -> {
                RetrofitConnection.server.updateProfile(
                    userId = currentUser,
                    nickname=setting_user_name.text.toString(),
                    userCategories = categories.split(','),
                    subscribeToPushNotification = receiveRandomPush
                ).enqueue(object :
                    Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        if (response.isSuccessful) {
                            Log.d("ProfileUpdateSuccess", response.body().toString())
                            Toast.makeText(
                                applicationContext,
                                "프로필 정보를 수정하였습니다.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Log.d("ProfileUpdateFail1", response.body().toString())
                        }
                    }


                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("ProfileUpdateFail2", t.message.toString())
                    }
                })
                val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}