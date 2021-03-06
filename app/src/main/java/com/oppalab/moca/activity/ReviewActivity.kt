package com.oppalab.moca.activity

import GetCommentsOnPostDTO
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R
import com.oppalab.moca.adapter.CommentsAdapterRetro
import com.oppalab.moca.dto.CommentsOnPost
import com.oppalab.moca.dto.GetReviewDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_review.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.report_popup.view.*
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
    private var likeTag = ""
    private var commentCount = 0L
    private var commentAdapter: CommentsAdapterRetro? = null
    private var commentList: MutableList<CommentsOnPost>? = null
    private var postTitle = ""
    private var thumbNailImageFilePath = ""

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
        likeTag = intent.getStringExtra("likeTag")!!

        review_detail_subject.text = postTitle

        Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + thumbNailImageFilePath)
            .into(review_thumbnail_detail)


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

                review_text.text = response.body()!!.review
                review_publisher.text = response.body()!!.nickname
                likeCount = response.body()!!.likeCount
                like = response.body()!!.like
                commentCount = response.body()!!.commentCount

                likeTag = if (like) "Liked" else "Like"
                if (likeTag == "Liked" || like) {
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

        review_like_btn.setOnClickListener {
            if (review_like_btn.tag == "Like") {
                Log.d("Likes ReviewId", reviewId)

                RetrofitConnection.server.likePost(
                    postId = "",
                    userId = currentUser,
                    reviewId = reviewId
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
                    reviewId = reviewId
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
                review_comments_count.text = response.body()!!.content.size.toString() + "개의 댓글이 있습니다."

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
                        if (commentCount.toInt() == 0) {
                            review_comments_count.text = "댓글이 없어요TT 댓글을 작성해주세요."
                        } else {
                            review_comments_count.text = commentCount.toString() + "개의 댓글이 있습니다."
                        }
                        write_comment_review!!.text.clear()
                        intent.putExtra("likeTag", review_like_btn.tag.toString())
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
                buffer: MutableList<CommentButton>
            ) {
                buffer.add(
                    CommentButton(this@ReviewActivity, "Delete", 30, 0,
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
                buffer.add(
                    CommentButton(this@ReviewActivity, "신고", 30, 0,
                        Color.parseColor("#FF9502"),
                        object:CommentsAdapterRetro.CommentClickListener {
                            override fun onClick(pos: Int) {
                                showCommentReportPopup(pos)
                            }
                        })
                )
            }
        }

    }

    private fun showCommentReportPopup(pos: Int ) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.report_popup, null)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("댓글 신고하기")
            .setPositiveButton("신고하기") { dialog, which ->
                val reportReason = view.editTextReportReason.text.toString()
                Log.d("reportReason", "신고사유는 " + reportReason)
                if (currentUser != commentList!![pos].userId) {
                    RetrofitConnection.server.reportComment(
                        userId = currentUser,
                        reportedUserId = commentList!![pos].userId,
                        commentId = commentList!![pos].commentId,
                        reportReason = reportReason
                    ).enqueue(object : Callback<Void> {
                        override fun onResponse(
                            call: Call<Void>,
                            response: Response<Void>
                        ) {
                            Log.d(
                                "retrofit",
                                "Comment 신고 : comment_id = " + commentList!![pos].commentId
                            )
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.d(
                                "retrofit",
                                "Comment 신고 실패" + t.message.toString()
                            )
                        }
                    })
                } else {
                    Toast.makeText(
                        this@ReviewActivity, "자신을 신고할 수 없습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(
                        "REPORT COMMENT",
                        "자신을 신고할 수 없습니다."
                    )
                }
            }
            .setNeutralButton("취소", null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()
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
                        intent.putExtra("postId", postId)
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
                val updateReviewIntent = Intent (this, AddReviewActivity::class.java)
                updateReviewIntent.putExtra("reviewId",reviewId)
                updateReviewIntent.putExtra("postId",postId)
                updateReviewIntent.putExtra("flag", true)
                updateReviewIntent.putExtra("userId", currentUser.toString())
                updateReviewIntent.putExtra("thumbNailImageFilePath", thumbNailImageFilePath)
                updateReviewIntent.putExtra("postTitle",postTitle)
                startActivity(updateReviewIntent)
                finish()
                return true
            }
            R.id.action_report -> {
                Log.d("retrofit", "post 신고버튼 동작 = ")
                showReviewReportPopup()
                return true

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showReviewReportPopup() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.report_popup, null)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("후기글 신고하기")
            .setPositiveButton("신고하기") {dialog, which ->
                val reportReason = view.editTextReportReason.text.toString()
                Log.d("reportReason","신고사유는 " + reportReason)
                RetrofitConnection.server.reportReview(
                    userId = currentUser,
                    reportedUserId = userId.toLong(),
                    reviewId = reviewId.toLong(),
                    reportReason = reportReason
                ).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Toast.makeText(
                            this@ReviewActivity,
                            "신고가 접수되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("report", "신고가 접수되었습니다. ")
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            this@ReviewActivity,
                            "신고에 실패하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("report", "신고에 실패하였습니다. " + t.message.toString())
                    }
                })
            }
            .setNeutralButton("취소", null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()
    }


    override fun onResume() {
        super.onResume()

        RetrofitConnection.server.getReview(userId = userId, reviewId = reviewId).enqueue(object:
            Callback<GetReviewDTO> {
            override fun onResponse(call: Call<GetReviewDTO>, response: Response<GetReviewDTO>) {

            }

            override fun onFailure(call: Call<GetReviewDTO>, t: Throwable) {
                Log.d("Fail to get review", t.message.toString())
            }
        })
    }
}