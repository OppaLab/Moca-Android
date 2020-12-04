package com.oppalab.moca

import GetCommentsOnPostDTO
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.adapter.CommentsAdapterRetro
import com.oppalab.moca.dto.CommentsOnPost
import com.oppalab.moca.dto.GetReviewDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
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
    private var postTitle = ""
    private var thumbNailImageFilePath = ""
    private var category = ""
    private var reviewUserId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        setSupportActionBar(findViewById(R.id.review_toolbar))


        val intent = intent
        currentUser = PreferenceManager.getLong(applicationContext, "userId")
        postId = intent.getStringExtra("postId")!!
        userId = intent.getStringExtra("userId")!!
        reviewId = intent.getStringExtra("reviewId")!!
        postTitle = intent.getStringExtra("postTitle")!!
        thumbNailImageFilePath = intent.getStringExtra("thumbNailImageFilePath")!!
//        category = intent.getStringExtra("category")!!


        review_detail_subject.text = postTitle

        Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + thumbNailImageFilePath)
            .into(review_thumbnail_detail)


        var comment_linearLayoutManager = LinearLayoutManager(this)
        comment_linearLayoutManager.reverseLayout = true
        review_recycler_view_comments.layoutManager = comment_linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapterRetro(this, commentList)
        review_recycler_view_comments.adapter = commentAdapter


        Log.d("리뷰", reviewId+"|||"+ userId)
        RetrofitConnection.server.getReview(userId = userId, reviewId = reviewId).enqueue(object:
            Callback<GetReviewDTO> {
            override fun onResponse(
                call: Call<GetReviewDTO>,
                response: Response<GetReviewDTO>
            ) {
                Log.d("retrofit", response.body().toString())

                review_text.text = response.body()!!.review
                review_publisher.text = response.body()!!.nickname
                likeCount = response.body()!!.likeCount
                like = response.body()!!.like
                commentCount = response.body()!!.commentCount

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

        //Swipe 추가
        val swipe = object: CommentsAdapterRetro.SwipeHelper(this, review_recycler_view_comments, 200) {
            override fun instantiateDeleteButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<DeleteButton>
            ) {
                buffer.add(
                    DeleteButton(this@ReviewActivity, "Delete", 30, 0,
                        Color.parseColor("#FF3C30"),
                        object:CommentsAdapterRetro.CommentClickListener{
                            override fun onClick(pos: Int) {
                                if(currentUser == commentList!![pos].userId) {
                                    RetrofitConnection.server.deleteComment(
                                        commentId = commentList!![pos].commentId,
                                        userId = commentList!![pos].userId
                                    ).enqueue(object : Callback<Long> {
                                        override fun onResponse(
                                            call: Call<Long>,
                                            response: Response<Long>
                                        ) {
                                            Log.d(
                                                "retrofit",
                                                "Comment 삭제 : comment_id = " + response.body()
                                            )
                                            commentAdapter!!.notifyDataSetChanged()
                                            finish()
                                            startActivity(intent)
                                        }

                                        override fun onFailure(call: Call<Long>, t: Throwable) {
                                            Log.d("retrofit", "Comment 삭제 실패" + t.message.toString())
                                        }
                                    })
                                }
                                else {
                                    Toast.makeText(
                                        this@ReviewActivity,
                                        "본인만 삭제할 수 있습니다.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.d(
                                        "DELETE COMMENT",
                                        "본인만 삭제할 수 있습니다."
                                    )
                                }
                            }
                        })
                )
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        Log.d("menu", "currentUser id:" + currentUser)
        Log.d("menu", "post User id:" + userId)
        if (currentUser.toString() == userId){
            menuInflater.inflate(R.menu.appbar_action, menu)
        }
        else {
            menuInflater.inflate(R.menu.appbar_report_action, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete -> {
                Log.d("retrofit", "review 삭제버튼 동작 = ")
                RetrofitConnection.server.deleteReview(userId = currentUser, reviewId = reviewId.toLong()).enqueue(object : Callback<Long>{
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "review 삭제 : review_id = " + response.body())
                        Toast.makeText(this@ReviewActivity,"게시글이 삭제되었습니다.", Toast.LENGTH_LONG)
                        val intent = Intent(this@ReviewActivity, PostDetailActivity::class.java)
                        intent.putExtra("postId", "postId")
                        startActivity(intent)
                        finish()

                    }
                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", "review 삭제 실패 :  " + t.message.toString())
                    }
                })
                return true
            }
            R.id.action_update -> {
                Log.d("retrofit", "review 수정버튼 동작 = ")
                return true
            }
            R.id.action_report -> {
                Log.d("retrofit", "review 신고버튼 동작 = ")
                return true

            }
        }
        return super.onOptionsItemSelected(item)
    }

}