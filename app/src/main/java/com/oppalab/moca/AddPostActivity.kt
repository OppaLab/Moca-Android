package com.oppalab.moca

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        save_new_post_btn.setOnClickListener{
            uploadPost()}
    }



    private fun uploadPost()
    {
        val title =post_subject.text.toString()
        val content = post_description.text.toString()

        when{
            TextUtils.isEmpty(post_subject.text.toString()) -> Toast.makeText(this, "제목을 기입해주세요.",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(post_description.text.toString()) -> Toast.makeText(this,"고민을 채워주세요.",Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("고민글 등록")
                progressDialog.setMessage("고민을 등록중이에요..")
                progressDialog.show()

                savePost(title, content)

                progressDialog.dismiss()

            }
        }
    }

    private fun savePost(title: String, content: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
        val postId = ref.push().key

        val postMap = HashMap<String, Any>()
        postMap["postid"] = postId!!
        postMap["title"] = title
        postMap["content"] = content
        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
        //썸네일 템플릿 만들어야함
        postMap["thumbnail"] = "https://firebasestorage.googleapis.com/v0/b/moca-80656.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=6b2f3909-59de-41d3-bd69-96e06aab6e23"


        ref.child(postId).updateChildren(postMap)

        Toast.makeText(this,"고민이 등록되었습니다.", Toast.LENGTH_LONG).show()

        val intent = Intent(this@AddPostActivity, MainActivity::class.java)
        startActivity(intent)
        finish()}
}