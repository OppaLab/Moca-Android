package com.oppalab.moca

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_review.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class AddReviewActivity : AppCompatActivity() {

    private var postId= 0L
    private var userId= 0L
    private var postTitle = ""
    private var thumbNailImageFilePath = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        val intent = intent
        postId = intent.getStringExtra("postId")!!.toLong()
        userId = intent.getStringExtra("userId")!!.toLong()
        postTitle = intent.getStringExtra("postTitle")!!
        thumbNailImageFilePath = intent.getStringExtra("thumbNailImageFilePath")!!

        add_review_detail_subject.text = postTitle

        Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + thumbNailImageFilePath)
            .into(add_review_thumbnail_detail)


        save_new_review_btn.isClickable = true
        save_new_review_btn.setOnClickListener {
            uploadReview()
        }
    }

    private fun uploadReview() {
        val content = review_description.text.toString()

        when {
            TextUtils.isEmpty(review_description.text.toString()) -> Toast.makeText(
                this,
                "후기를 채워주세요.",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("후기글 등록")
                progressDialog.setMessage("후기를 등록중이에요..")
                progressDialog.show()

                RetrofitConnection.server.createReview(
                    postId = postId,
                    userId = userId,
                    review = content
                ).enqueue(object : Callback<Long> {
                    override fun onResponse(
                        call: Call<Long>,
                        response: Response<Long>
                    ) {
                        if (response?.isSuccessful) {
                            Toast.makeText(
                                getApplicationContext(),
                                "Review Uploaded Successfully...",
                                Toast.LENGTH_LONG
                            ).show();

                            val intentReview = Intent(this@AddReviewActivity, ReviewActivity::class.java)
                            intentReview.putExtra("userId",userId.toString())
                            intentReview.putExtra("postId",postId.toString())
                            intentReview.putExtra("reviewId",response.body().toString())
                            intentReview.putExtra("postTitle", postTitle)
                            intentReview.putExtra("thumbNailImageFilePath", thumbNailImageFilePath)
                            startActivity(intentReview)
                            finish()
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

                Toast.makeText(this, "후기가 등록되었습니다.", Toast.LENGTH_LONG).show()



                progressDialog.dismiss()
            }
        }
    }
}