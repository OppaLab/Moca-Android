package com.oppalab.moca

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.oppalab.moca.dto.GetProfileDTO
import com.oppalab.moca.dto.UpdateProfileDTO
import com.oppalab.moca.fragment.ProfileFragment
import com.oppalab.moca.fragment.ProfileFragmentRetro
import com.oppalab.moca.model.User
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_account_setting.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountSettingActivity : AppCompatActivity() {

    private var categories :String = ""
    private var nickname :String = ""
    private var currentUser : Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        var intent = intent
        categories = intent.getStringExtra("categoriesList")!!
        nickname = intent.getStringExtra("nickname")!!
        currentUser = intent.getStringExtra("currentUser")!!.toLong()

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
            if (categoryIndex.contains("Family")) setting_category_family.setChecked(true)
            if (categoryIndex.contains("Friend")) setting_category_friend.setChecked(true)
            if (categoryIndex.contains("Parent")) setting_category_parent.setChecked(true)
            if (categoryIndex.contains("Couple")) setting_category_couple.setChecked(true)
            if (categoryIndex.contains("Money")) setting_category_money.setChecked(true)
            if (categoryIndex.contains("School")) setting_category_school.setChecked(true)
            if (categoryIndex.contains("Study")) setting_category_study.setChecked(true)
            if (categoryIndex.contains("Sex")) setting_category_sex.setChecked(true)

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
            if (setting_category_family.isChecked) categories += "Family,"
            if (setting_category_friend.isChecked) categories += "Friend,"
            if (setting_category_parent.isChecked) categories += "Parent,"
            if (setting_category_couple.isChecked) categories += "Couple,"
            if (setting_category_money.isChecked) categories += "Money,"
            if (setting_category_school.isChecked) categories += "School,"
            if (setting_category_study.isChecked) categories += "Study,"
            if (setting_category_sex.isChecked) categories += "Sex,"
            categories.substring(0, categories.length-1)

            Log.d("ToSaveCategories", categories)
            updateUserInfo()
        }
    }

    private fun updateUserInfo() {
        when {
            TextUtils.isEmpty(setting_user_name.text.toString()) -> Toast.makeText(this, "수정할 닉네임을 입력해주세요.", Toast.LENGTH_LONG).show()
            categories == "" -> Toast.makeText(this, "카테고리를 골라주세요.", Toast.LENGTH_LONG).show()

            else -> {
                RetrofitConnection.server.updateProfile(
                    userId = currentUser,
                    nickname=setting_user_name.text.toString(),
                    userCategories = categories.split(','),
                    subscribeToPushNotification = true
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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}