package com.oppalab.moca

import GetCommentsOnPostDTO
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.adapter.CommentsAdapterRetro
import com.oppalab.moca.dto.CommentsOnPost
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.thumbnail_item_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDetailActivity : AppCompatActivity() {
    private var postId = 0L
    private var postUserId = ""
    private var reviewId = ""
    private var publisherId = ""
    private var content = ""
    private var likeCount = 0L
    private var like = false
    private var thumbnailImageFilePath = ""
    private var subject = ""
    private var commentId = ""
    private var commentCount = 0L
    private var commentAdapter: CommentsAdapterRetro? = null
    private var commentList: MutableList<CommentsOnPost>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val currentUser = PreferenceManager.getLong(applicationContext, "userId")

        val intent = intent

        postId = intent.getStringExtra("postId")!!.toLong()
        postUserId = intent.getStringExtra("postUserId")!!
        publisherId = intent.getStringExtra("publisherId")!!
        reviewId = intent.getStringExtra("reviewId")!!
        Log.d("모지", reviewId)
        subject = intent.getStringExtra("subject")!!
        thumbnailImageFilePath = intent.getStringExtra("thumbnailImageFilePath")!!
        if (intent.getStringExtra("like") == "Liked") like = true
        else if (intent.getStringExtra("like") == "True") like = true
        likeCount = intent.getStringExtra("likeCount")!!.toLong()
        commentCount = intent.getStringExtra("commentCount")!!.toLong()
        content = intent.getStringExtra("content")!!

        var comment_linearLayoutManager = LinearLayoutManager(this)
        comment_linearLayoutManager.reverseLayout = true
        post_detail_recycler_view_comments.layoutManager = comment_linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapterRetro(this, commentList)
        post_detail_recycler_view_comments.adapter = commentAdapter

        Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + thumbnailImageFilePath)
            .into(post_thumbnail_detail)

        if (like) {
            post_image_like_btn.setImageResource(R.drawable.heart_clicked)
            post_image_like_btn.tag = "Liked"
        } else {
            post_image_like_btn.setImageResource(R.drawable.heart_not_clicked)
            post_image_like_btn.tag = "Like"
        }

        post_detail_subject.text = intent.getStringExtra("subject")
        post_detail_publisher.text = publisherId
        post_text.text = content
        if (likeCount.toInt() == 0) {
            post_detail_like_count.text = ""
        } else {
            post_detail_like_count.text = likeCount.toString() + "명이 공감합니다."
        }

        if (commentCount.toInt() == 0) {
            post_detail_comments_count.text = "댓글이 없어요TT 댓글을 작성해주세요."
        } else {
            post_detail_comments_count.text = commentCount.toString() + "개의 댓글이 있습니다."
        }

        post_image_like_btn.setOnClickListener {
            if (post_image_like_btn.tag == "Like") {
                Log.d("Likes PostId", postId.toString())

                RetrofitConnection.server.likePost(
                    postId = postId.toString(),
                    userId = currentUser,
                    reviewId = ""
                ).enqueue(object : Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "Like 생성 : like_id = " + response.body())
                        post_image_like_btn.setImageResource(R.drawable.heart_clicked)
                        post_image_like_btn.tag = "Liked"
                        likeCount += 1

                        post_detail_like_count.text = (likeCount).toString() + "명이 공감합니다."
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            } else {
                RetrofitConnection.server.unlikePost(
                    postId = postId.toString(),
                    userId = currentUser,
                    reviewId = ""
                ).enqueue(object : Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "Like 삭제 : like_id = " + response.body())
                        post_image_like_btn.setImageResource(R.drawable.heart_not_clicked)
                        post_image_like_btn.tag = "Like"
                        likeCount += -1

                        if (likeCount == 0L) {
                            post_detail_like_count.text = ""
                        } else {
                            post_detail_like_count.text = (likeCount).toString() + "명이 공감합니다."
                            if (likeCount == 0L) {
                                post_detail_like_count.text = ""
                            }
                        }
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            }
        }

        RetrofitConnection.server.getCommentOnPost(postId = postId.toString(), reviewId = "", page = 0).enqueue(object: Callback<GetCommentsOnPostDTO> {
            override fun onResponse(
                call: Call<GetCommentsOnPostDTO>,
                response: Response<GetCommentsOnPostDTO>
            ) {
                Log.d("retrofit", response.body().toString())

                commentList!!.clear()
                for (comment in response.body()!!.content) {
                    val curComment = comment
                    commentList!!.add(comment!!)
                }

                commentAdapter!!.notifyDataSetChanged()
                post_detail_comments_count.text = response.body()!!.content.size.toString() + "개의 댓글이 있습니다."
            }

            override fun onFailure(call: Call<GetCommentsOnPostDTO>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
            }

        })
        post_comment.setOnClickListener(View.OnClickListener {
            if (add_comment!!.text.toString() == "") {
                Toast.makeText(
                    this@PostDetailActivity,
                    "Please write comment first.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                RetrofitConnection.server.createComment(
                    postId = postId.toString(),
                    reviewId = "", currentUser,
                    comment = add_comment.text.toString()
                ).enqueue(object: Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "댓글 생성")
                        commentCount++
                        if (commentCount.toInt() == 0) {
                            post_detail_comments_count.text = "댓글이 없어요TT 댓글을 작성해주세요."
                        } else {
                            post_detail_comments_count.text = commentCount.toString() + "개의 댓글이 있습니다."
                        }
                        add_comment!!.text.clear()
                        finish()
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            }
        })

        if(currentUser.toString() == postUserId && reviewId == "0")
        {
            add_detail_review_btn.visibility = View.VISIBLE
        }

        add_detail_review_btn.setOnClickListener {
            val intentAddReview = Intent(this, AddReviewActivity::class.java)
            intentAddReview.putExtra("postId",postId.toString())
            intentAddReview.putExtra("userId",currentUser.toString())
            startActivity(intentAddReview)
        }
        post_detail_review_btn.setOnClickListener{
            if(reviewId == "0")
            {
                Toast.makeText(this@PostDetailActivity, "후기가 아직 없습니다.", Toast.LENGTH_LONG).show()
            } else {
                val intentReview = Intent(this, ReviewActivity::class.java)
                intentReview.putExtra("currentUser", currentUser.toString())
                intentReview.putExtra("userId",postUserId)
                intentReview.putExtra("reviewId", reviewId)
                intentReview.putExtra("postId",postId.toString())
                startActivity(intentReview)
            }
        }


    }
}