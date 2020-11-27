package com.oppalab.moca

import GetCommentsOnPostDTO
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.oppalab.moca.adapter.CommentsAdapterRetro
import com.oppalab.moca.dto.CommentsOnPost
import com.oppalab.moca.dto.GetReviewDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import kotlinx.android.synthetic.main.activity_add_review.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.activity_review.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewActivity : AppCompatActivity() {

    private var currentUser = 0L
    private var postId = ""
    private var userId = ""
    private var reviewId = ""
    private var likeCount = 0L
    private var like = false
    private var commentCount = 0L
    private var commentAdapter: CommentsAdapterRetro? = null
    private var commentList: MutableList<CommentsOnPost>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val intent = intent
        currentUser = PreferenceManager.getLong(applicationContext, "userId")
        postId = intent.getStringExtra("postId")!!
        userId = intent.getStringExtra("userId")!!
        reviewId = intent.getStringExtra("reviewId")!!

        var comment_linearLayoutManager = LinearLayoutManager(this)
        comment_linearLayoutManager.reverseLayout = true
        review_recycler_view_comments.layoutManager = comment_linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapterRetro(this, commentList)
        review_recycler_view_comments.adapter = commentAdapter


        RetrofitConnection.server.getReview(userId = userId, reviewId = reviewId).enqueue(object:
            Callback<GetReviewDTO> {
            override fun onResponse(
                call: Call<GetReviewDTO>,
                response: Response<GetReviewDTO>
            ) {
                Log.d("retrofit", response.body().toString())

                review_text.setText(response.body()!!.review)
                review_publisher.setText(response.body()!!.nickname)
                likeCount = response.body()!!.likeCount
                like = response.body()!!.like
                commentCount = response.body()!!.commentCount

            }

            override fun onFailure(call: Call<GetReviewDTO>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
            }

        })

        if (like) {
            review_like_btn.setImageResource(R.drawable.heart_clicked)
            review_like_btn.tag = "Liked"
        } else {
            review_like_btn.setImageResource(R.drawable.heart_not_clicked)
            review_like_btn.tag = "Like"
        }

        if (likeCount.toInt() == 0) {
            review_like_count.text = ""
        } else {
            review_like_count.text = likeCount.toString() + "명이 공감합니다."
        }

        if (commentCount.toInt() == 0) {
            review_comments_count.text = "댓글이 없어요TT 댓글을 작성해주세요."
        } else {
            review_comments_count.text = commentCount.toString() + "개의 댓글이 있습니다."
        }

        review_like_btn.setOnClickListener {
            if (review_like_btn.tag == "Like") {
                Log.d("Likes ReviewId", postId.toString())

                RetrofitConnection.server.likePost(
                    postId = "",
                    userId = currentUser,
                    reviewId = reviewId.toString()
                ).enqueue(object : Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "Like 생성 : like_id = " + response.body())
                        review_like_btn.setImageResource(R.drawable.heart_clicked)
                        review_like_btn.tag = "Liked"
                        likeCount += 1

                        review_like_count.text = (likeCount).toString() + "명이 공감"
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            } else {
                RetrofitConnection.server.unlikePost(
                    postId = "",
                    userId = currentUser,
                    reviewId = reviewId.toString()
                ).enqueue(object : Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "Like 삭제 : like_id = " + response.body())
                        review_like_btn.setImageResource(R.drawable.heart_not_clicked)
                        review_like_btn.tag = "Like"
                        likeCount += -1

                        if (likeCount == 0L) {
                            review_like_count.text = ""
                        } else {
                            review_like_count.text = (likeCount).toString() + "명이 공감"
                            if (likeCount == 0L) {
                                review_like_count.text = ""
                            }
                        }
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            }
        }

        RetrofitConnection.server.getCommentOnPost(postId = "", reviewId = reviewId, page = 0).enqueue(object:
            Callback<GetCommentsOnPostDTO> {
            override fun onResponse(
                call: Call<GetCommentsOnPostDTO>,
                response: Response<GetCommentsOnPostDTO>
            ) {
                Log.d("retrofit", response.body().toString())

                commentList!!.clear()
                for (comment in response.body()!!.content) {
                    val curComment = comment
                    commentList!!.add(curComment!!)
                }

                commentAdapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<GetCommentsOnPostDTO>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
            }

        })

        post_comment_review.setOnClickListener(View.OnClickListener {
            if (write_comment_review!!.text.toString() == "") {
                Toast.makeText(
                    this@ReviewActivity,
                    "Please write comment first.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                RetrofitConnection.server.createComment(
                    postId = "",
                    reviewId = reviewId, currentUser,
                    comment = write_comment_review.text.toString()
                ).enqueue(object: Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "댓글 생성")
                        commentCount++
                        write_comment_review!!.text.clear()
                        finish()
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            }
        })
    }
}