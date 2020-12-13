package com.oppalab.moca.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.oppalab.moca.R
import com.oppalab.moca.dto.GetReviewDTO
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
    private var flag = false
    private var reviewId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        val intent = intent
        postId = intent.getStringExtra("postId")!!.toLong()
        userId = intent.getStringExtra("userId")!!.toLong()
        postTitle = intent.getStringExtra("postTitle")!!
        reviewId = intent.getStringExtra("reviewId")!!
        flag = intent.getBooleanExtra("flag", false)
        thumbNailImageFilePath = intent.getStringExtra("thumbNailImageFilePath")!!

        add_review_detail_subject.text = postTitle

        Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + thumbNailImageFilePath)
            .into(add_review_thumbnail_detail)

        if(flag) callReview()
        save_new_review_btn.isClickable = true
        save_new_review_btn.setOnClickListener {
            if(flag) {updateReview() } else{ uploadReview()}
        }
    }

    private fun updateReview() {
        RetrofitConnection.server.updateReview(reviewId = reviewId, review = review_description.text.toString()).enqueue(object : Callback<Long>{
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("Success to update review : review_id", response.body().toString())
                Toast.makeText(
                    this@AddReviewActivity,
                    "후기가 수정되었습니다.",
                    Toast.LENGTH_LONG
                ).show()
                flag = false

                val intentReview = Intent(this@AddReviewActivity, ReviewActivity::class.java)
                intentReview.putExtra("userId",userId.toString())
                intentReview.putExtra("postId",postId.toString())
                intentReview.putExtra("reviewId",reviewId)
                intentReview.putExtra("postTitle", postTitle)
                intentReview.putExtra("thumbNailImageFilePath", thumbNailImageFilePath)
                intentReview.putExtra("likeTag", "")
                startActivity(intentReview)
                finish()
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("Fail to update review  : review_id", reviewId)
                Toast.makeText(
                    this@AddReviewActivity,
                    "후기 수정을 실패 했습니다..",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun callReview(){
        RetrofitConnection.server.getReview(userId = userId.toString(), reviewId = reviewId).enqueue(object: Callback<GetReviewDTO>{
            override fun onResponse(call: Call<GetReviewDTO>, response: Response<GetReviewDTO>) {
                Log.d("리뷰다",response.body()!!.review)
                review_description.setText(response.body()!!.review)
            }

            override fun onFailure(call: Call<GetReviewDTO>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
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

                            val intentReview = Intent(this@AddReviewActivity, PostDetailActivity::class.java)
//                            intentReview.putExtra("userId",userId.toString())
                            intentReview.putExtra("postId",postId.toString())
                            intentReview.putExtra("reviewId",response.body().toString())
                            intentReview.putExtra("postTitle", postTitle)
                            intentReview.putExtra("thumbNailImageFilePath", thumbNailImageFilePath)
                            intentReview.putExtra("likeTag", "")

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