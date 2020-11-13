package com.oppalab.moca

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.thumbnail_content.*
import java.io.ByteArrayOutputStream

class AddPostActivity : AppCompatActivity() {

    private var storagePostRef: StorageReference?=null
    var myUrl = ""
    private var categories = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        var intent: Intent = getIntent();
        var arr = getIntent().getByteArrayExtra("image")!!
        var image = BitmapFactory.decodeByteArray(arr, 0, arr!!.size)
        var thumbnail: ImageView = findViewById(R.id.thumbnail_image);
        thumbnail.setImageBitmap(image);

        storagePostRef = FirebaseStorage.getInstance().reference.child("post_thumbnail/")

        save_new_post_btn.isClickable = true
        save_new_post_btn.setOnClickListener {
            if (post_category_family.isChecked) categories += "Family,"
            if (post_category_friend.isChecked) categories += "Friend,"
            if (post_category_parent.isChecked) categories += "Parent,"
            if (post_category_couple.isChecked) categories += "Couple,"
            if (post_category_money.isChecked) categories += "Money,"
            if (post_category_school.isChecked) categories += "School,"
            if (post_category_study.isChecked) categories += "Study,"
            if (post_category_sex.isChecked) categories += "Sex,"
            categories.substring(0, categories.length-1)
            uploadPost(arr)
        }




    }

    private fun uploadPost(arr : ByteArray)
    {
        val title = post_subject.text.toString()
        val content = post_description.text.toString()

        when{
            TextUtils.isEmpty(post_subject.text.toString()) -> Toast.makeText(this, "제목을 기입해주세요.",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(post_description.text.toString()) -> Toast.makeText(this,"고민을 채워주세요.",Toast.LENGTH_LONG).show()
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

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task <Uri>>{ task ->
                    if(!task.isSuccessful)
                    {
                        task.exception?.let{
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener (OnCompleteListener <Uri>{task->
                        if(task.isSuccessful)
                        {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val postMap = HashMap<String , Any>()
                            postMap["postId"] = postId!!
                            postMap["title"] = title
                            postMap["content"] = content
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["thumbnail"] = myUrl
                            postMap["category"] = categories.toLowerCase()


                            ref.child(postId).updateChildren(postMap)


                            Toast.makeText(this,"고민이 등록되었습니다.", Toast.LENGTH_LONG).show()


                            val intent = Intent(this@AddPostActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            progressDialog.dismiss()
                        }
                        else
                        {
                            progressDialog.dismiss()
                        }
                    })
            }
        }
    }
//    private fun uploadPost(arr: ByteArray) {
//        save_new_post_btn.isClickable = false
//        val title = post_subject.text.toString()
//        val content = post_description.text.toString()
//
//        when {
//            TextUtils.isEmpty(post_subject.text.toString()) -> Toast.makeText(
//                this,
//                "제목을 기입해주세요.",
//                Toast.LENGTH_LONG
//            ).show()
//            TextUtils.isEmpty(post_description.text.toString()) -> Toast.makeText(
//                this,
//                "고민을 채워주세요.",
//                Toast.LENGTH_LONG
//            ).show()
//
//            else -> {
//                val progressDialog = ProgressDialog(this)
//                progressDialog.setTitle("고민글 등록")
//                progressDialog.setMessage("고민을 등록중이에요..")
//                progressDialog.show()
//
//                savePost(title, content, arr)
//
//                progressDialog.dismiss()
//
//            }
//        }
//    }

//    private fun savePost(title: String, content: String, byteArray: ByteArray) {
//        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
//        val postId = ref.push().key
//        val storageReference =
//            FirebaseStorage.getInstance().reference.child("post_thumbnail/" + postId + ".png")
//        val uploadTask: StorageTask<*>
//        uploadTask = storageReference.putBytes(byteArray)
//
//        uploadTask.continueWith(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
//            if (!it.isSuccessful) {
//                it.exception?.let {
//                    throw it
//                }
//            }
//            return@Continuation storageReference.downloadUrl
//        })
//            .addOnCompleteListener(OnCompleteListener <Uri> {task ->
//                if (task.isSuccessful) {
//                    val postMap = HashMap<String, Any>()
//                    postMap["postid"] = postId!!
//                    postMap["title"] = title
//                    postMap["content"] = content
//                    postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
//                    postMap["thumbnail"] = it.result!!.result.toString()
//
//                    ref.child(postId).updateChildren(postMap)
//
//                    Toast.makeText(this, "고민이 등록되었습니다.", Toast.LENGTH_LONG).show()
//
//                    val intent = Intent(this@AddPostActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//            })
//
//    }
}

