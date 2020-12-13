package com.oppalab.moca.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.oppalab.moca.R
import com.oppalab.moca.dto.GetMyPostDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_post.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class AddPostActivity : AppCompatActivity() {

    private var storagePostRef: StorageReference? = null
    var myUrl = ""
    private var categories = ""
    lateinit var image: Bitmap
    private var flag = false
    private var userId = 0L
    private var postId = 0L
    private var like = false
    private var randomPushCount = 0L
    private var isRandomPush = false
    private lateinit var arr2: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val intent = intent
        val thumbnail: ImageView = findViewById(R.id.thumbnail_image)

        userId = intent.getLongExtra("userId",0L)
        postId = intent.getLongExtra("postId",0L)
        flag = intent.getBooleanExtra("flag",false)

        storagePostRef = FirebaseStorage.getInstance().reference.child("post_thumbnail/")

        if (flag) callPost()
        else{
            val arr = intent.getByteArrayExtra("image")!!
            image = BitmapFactory.decodeByteArray(arr, 0, arr!!.size)
            thumbnail.setImageBitmap(image)
            arr2 = arr
        }

        random_push_switch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                random_push_number_select.visibility = View.VISIBLE
                isRandomPush = true
            }
        }

        plus_button.setOnClickListener {
            randomPushCount++
            random_push_count.text = randomPushCount.toString()
        }

        minus_button.setOnClickListener {
            if (randomPushCount > 0) {
                randomPushCount--
                random_push_count.text = randomPushCount.toString()
            }
        }

        
        save_new_post_btn.isClickable = true

        save_new_post_btn.setOnClickListener {
            if(flag) {
                categoriesCheck()
                updatePost()}
            else {
                categoriesCheck()
                uploadPost(arr2)
            }
        }

    }

    private fun updatePost() {
        RetrofitConnection.server.updatePost(
            postId = postId.toString(),
            postTitle = post_subject.text.toString(),
            postBody = post_description.text.toString(),
            userId = userId,
            postCategories = categories.split(','),
        ).enqueue(object: Callback<Long>{
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("수정된 postId: " ,response.body().toString())
                Log.d("수정수정수정", post_subject.text.toString()+"|||||"+categories)
                val postDetailIntent = Intent(applicationContext, PostDetailActivity::class.java)
                postDetailIntent.putExtra("postId", postId.toString())
//                postDetailIntent.putExtra("categories", categories)
//                postDetailIntent.putExtra("likeTag", if (like) "Liked" else "Like")
                flag = false
                startActivity(postDetailIntent)
                finish()
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("수정실패한 postId: ", postId.toString())
            }

        })
    }

    private fun callPost() {
        RetrofitConnection.server.getOnePost(postId = postId, userId = userId, search = "", category = "", page = 0).enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                val mPost = response.body()!!.content[0]

                post_subject.setText(mPost.postTitle)
                post_description.setText(mPost.postBody)
                Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + mPost.thumbnailImageFilePath)
                    .into(thumbnail_image)
                val getCategories = mPost.categories.toString()
                like = mPost.like
                Log.d("Success to call post(ID", "$postId) at AddPostActivity")
                if (getCategories.contains("가족")) post_category_family.setChecked(true)
                if (getCategories.contains("친구")) post_category_friend.setChecked(true)
                if (getCategories.contains("부모님")) post_category_parent.setChecked(true)
                if (getCategories.contains("연인")) post_category_couple.setChecked(true)
                if (getCategories.contains("금전")) post_category_money.setChecked(true)
                if (getCategories.contains("학교")) post_category_school.setChecked(true)
                if (getCategories.contains("학업")) post_category_study.setChecked(true)
                if (getCategories.contains("성")) post_category_sex.setChecked(true)
                if (getCategories.contains("외모")) post_category_appearence.setChecked(true)
            }

            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                Log.d("Fail to call post at AddPostActivity", t.message.toString())
            }
        })
    }

    private fun uploadPost(arr: ByteArray) {
        val title = post_subject.text.toString()
        val content = post_description.text.toString()

        when {
            TextUtils.isEmpty(post_subject.text.toString()) -> Toast.makeText(
                this,
                "제목을 기입해주세요.",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(post_description.text.toString()) -> Toast.makeText(
                this,
                "고민을 채워주세요.",
                Toast.LENGTH_LONG
            ).show()
            categories == "" -> Toast.makeText(this, "카테고리를 골라주세요.", Toast.LENGTH_LONG).show()


            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("고민글 등록")
                progressDialog.setMessage("고민을 등록중이에요..")
                progressDialog.show()

                val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                val postId = ref.push().key
                val fileRef = storagePostRef!!.child(System.currentTimeMillis().toString() + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putBytes(arr)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val file = File(applicationContext.cacheDir, postId)
                            file.createNewFile()

                            val bos = ByteArrayOutputStream()
                            image.compress(Bitmap.CompressFormat.PNG, 0, bos)
                            val bitmapdata = bos.toByteArray()

                            try {
                                val fos = FileOutputStream(file)
                                fos.write(bitmapdata)
                                fos.flush()
                                fos.close()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            val reqFile =
                                RequestBody.create(MediaType.parse("thumbnailImageFile/*"), file)
                            val body =
                                MultipartBody.Part.createFormData("thumbnailImageFile", file.name, reqFile)

                            RetrofitConnection.server.createPost(
                                thumbnailImageFile = body,
                                postTitle = title,
                                postBody = content,
                                postCategories = categories.split(","),
                                userId = PreferenceManager.getLong(applicationContext,"userId"),
                                numberOfRandomUserPushNotification = randomPushCount,
                                isRandomUserPushNotification = isRandomPush
                            ).enqueue(object : Callback<Long> {
                                override fun onResponse(
                                    call: Call<Long>,
                                    response: Response<Long>
                                ) {
                                    if (response?.isSuccessful) {
                                        Log.d("retrofit addpost userId", PreferenceManager.getLong(applicationContext,"userId").toString())
                                        Toast.makeText(
                                            getApplicationContext(),
                                            "고민이 등록되었습니다.",
                                            Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }

                                override fun onFailure(call: Call<Long>, t: Throwable) {
                                    Log.d("retrofit result", t.message.toString())
                                    Toast.makeText(
                                        getApplicationContext(),
                                        "고민 등록을 실패했습니다.",
                                        Toast.LENGTH_LONG
                                    ).show();
                                }

                            })
                            val intent = Intent(this@AddPostActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            progressDialog.dismiss()
                        } else {
                            progressDialog.dismiss()
                        }
                    })
            }
        }
    }
    private fun categoriesCheck(){
        if (post_category_family.isChecked) categories += "가족,"
        if (post_category_friend.isChecked) categories += "친구,"
        if (post_category_parent.isChecked) categories += "부모님,"
        if (post_category_couple.isChecked) categories += "연인,"
        if (post_category_money.isChecked) categories += "금전,"
        if (post_category_school.isChecked) categories += "학교,"
        if (post_category_study.isChecked) categories += "학업,"
        if (post_category_sex.isChecked) categories += "성,"
        if (post_category_appearence.isChecked) categories += "외모,"

    }
}