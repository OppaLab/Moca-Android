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

    private lateinit var firebaseUser: FirebaseUser
    private var categories :String = ""
    private var nickname :String = ""
    private var checker = ""
    private var myUrl = ""
    private var storageReference: StorageReference? = null
    private var imageUri: Uri? = null
    private var currentUser : Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        var intent = intent
        categories = intent.getStringExtra("categoriesList")!!
        nickname = intent.getStringExtra("nickname")!!
        currentUser = intent.getStringExtra("currentUser")!!.toLong()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageReference = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        setting_logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        change_image_text_btn.setOnClickListener {
            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@AccountSettingActivity)
        }

        setting_user_name.setText(nickname)
        val categoryArray = categories.split(',')
        Log.d("카테고리 알려줘", categories)
        for(item in categoryArray) {
            if (item == "Family,") setting_category_family.setChecked(true)
            if (item == "Friend,") setting_category_friend.setChecked(true)
            if (item == "Parent,") setting_category_parent.setChecked(true)
            if (item == "Couple,") setting_category_couple.setChecked(true)
            if (item == "Money,") setting_category_money.setChecked(true)
            if (item == "School,") setting_category_school.setChecked(true)
            if (item == "Study,") setting_category_study.setChecked(true)
            if (item == "Sex,") setting_category_sex.setChecked(true)
        }
        save_profile_button.setOnClickListener {
            if (setting_category_family.isChecked) categories += "Family,"
            if (setting_category_friend.isChecked) categories += "Friend,"
            if (setting_category_parent.isChecked) categories += "Parent,"
            if (setting_category_couple.isChecked) categories += "Couple,"
            if (setting_category_money.isChecked) categories += "Money,"
            if (setting_category_school.isChecked) categories += "School,"
            if (setting_category_study.isChecked) categories += "Study,"
            if (setting_category_sex.isChecked) categories += "Sex,"
            categories.substring(0, categories.length-1)

            if (checker == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfoOnly()
            }
        }

    }

    private fun updateUserInfoOnly() {
        when {
            TextUtils.isEmpty(setting_user_name.text.toString()) -> Toast.makeText(this, "수정할 닉네임을 입력해주세요.", Toast.LENGTH_LONG).show()
            categories == "" -> Toast.makeText(this, "카테고리를 골라주세요.", Toast.LENGTH_LONG).show()

            else -> {
                RetrofitConnection.server.updateProfile(
                    userId = currentUser,
                    nickname = setting_user_name.toString(),
                    userCategories = categories.split(","),
                    subscribeToPushNotification = false
                ).enqueue(object :
                    Callback<UpdateProfileDTO> {
                    override fun onResponse(call: Call<UpdateProfileDTO>, response: Response<UpdateProfileDTO>) {
                        Log.d("프로필 수정", response.body().toString())
                    }

                    override fun onFailure(call: Call<UpdateProfileDTO>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
                Toast.makeText(this, "프로필 정보를 수정하였습니다.", Toast.LENGTH_LONG).show()

                val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
    }

    private fun uploadImageAndUpdateInfo() {

        when {
            imageUri == null -> Toast.makeText(this, "이미지를 골라주세요.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(setting_user_name.text.toString()) -> Toast.makeText(this, "수정할 닉네임을 입력해주세요.", Toast.LENGTH_LONG).show()
            categories == "" -> Toast.makeText(this, "카테고리를 골라주세요.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("프로필 변경")
                progressDialog.setMessage("프로필 업로드 중입니다.")
                progressDialog.show()

                val fileRef = storageReference!!.child(firebaseUser!!.uid + "jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWith(Continuation <UploadTask    .TaskSnapshot, Task<Uri>>{
                    if (!it.isSuccessful) {
                        it.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val downloadUrl = it.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap = HashMap<String, Any>()
                        userMap["username"] = setting_user_name.text.toString().toLowerCase()
                        userMap["category"] = categories.toLowerCase()
                        userMap["image"]
                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else {
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_profile_image.setImageURI(imageUri)
        }
    }


}