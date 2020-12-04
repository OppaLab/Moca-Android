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
import com.oppalab.moca.dto.GetMyPostDTO
import com.oppalab.moca.dto.PostDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDetailActivity : AppCompatActivity() {
    private var postId = 0L
    private var postUserId = ""
    private var reviewId = ""
    private var publisherId = ""
    private var content = ""
    private var like = false
    private var likeTag = ""
    private var likeCount = 0L
    private var thumbnailImageFilePath = ""
    private var subject = ""
    private var commentCount = 0L
    private var commentAdapter: CommentsAdapterRetro? = null
    private var commentList: MutableList<CommentsOnPost>? = null
    private var currentUser = 0L
    private var createdAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)
        setSupportActionBar(findViewById(R.id.post_toolbar))


        val currentUser = PreferenceManager.getLong(applicationContext, "userId")

        val intent = intent

        postId = intent.getStringExtra("postId")!!.toLong()!!
        likeTag = intent.getStringExtra("likeTag")!!

        RetrofitConnection.server.getOnePost(
            userId = currentUser,
            postId = postId,search = "", category = "", page = 0).enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                val mPost = response.body()!!.content[0]

                postUserId = mPost.userId.toString()
                publisherId = mPost.nickname
                reviewId = mPost!!.reviewId.toString()
                subject = mPost.postTitle
                thumbnailImageFilePath = mPost.thumbnailImageFilePath

                likeCount = mPost.likeCount
                commentCount = mPost.commentCount
                content = mPost.postBody
                createdAt = mPost.createdAt

                Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + thumbnailImageFilePath)
                    .into(post_thumbnail_detail)

                Log.d("태크씨발", like.toString() +"||||||||||" +likeTag)

                if (likeTag == "Liked" || like == true) {
                    post_image_like_btn.setImageResource(R.drawable.heart_clicked)
                    post_image_like_btn.tag = "Liked"
                } else {
                    post_image_like_btn.setImageResource(R.drawable.heart_not_clicked)
                    post_image_like_btn.tag = "Like"
                }

                var post_categories = ""
                for (category in mPost.categories) {
                    if (category == "") continue
                    post_categories += category
                    post_categories += ", "
                }
                post_detail_cateogory.text = post_categories.substring(0, post_categories.length - 2)

                post_detail_subject.text = subject
                post_detail_publisher.text = "\"" + publisherId + "\"님이 작성해주신 글이에요."
                if (createdAt <= 60){
                    post_detail_time.text = createdAt.toString() + "초 전에 작성된 글입니다."
                } else if (createdAt <= 60*60){
                    post_detail_time.text = (createdAt/60).toString() + "분 전에 작성된 글입니다."
                } else if (createdAt <= 60*60*24){
                    post_detail_time.text = (createdAt/(60*60)).toString() + "시간 전에 작성된 글입니다."
                } else if (createdAt <= 60*60*24*7){
                    post_detail_time.text = (createdAt/(60*60*7)).toString() + "일 전에 작성된 글입니다."
                } else if (createdAt <= 60*60*24*7*7){
                    post_detail_time.text = (createdAt/(60*60*7*7)).toString() + "주 전에 작성된 글입니다."
                }


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


                post_detail_publisher.setOnClickListener {
                    val intentUserProfile = Intent(this@PostDetailActivity, OtherUserActivity::class.java)
                    intentUserProfile.putExtra("publisherId", postUserId.toString())
                    startActivity(intentUserProfile)
                    finish()
                }
            }

            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

        var comment_linearLayoutManager = LinearLayoutManager(this)
        comment_linearLayoutManager.reverseLayout = true
        post_detail_recycler_view_comments.layoutManager = comment_linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapterRetro(this, commentList)
        post_detail_recycler_view_comments.adapter = commentAdapter



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
                        intent.putExtra("likeTag", post_image_like_btn.tag.toString())
                        finish()
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            }
        })

        Log.d("돌아와3", currentUser.toString() + "||||"  + postUserId +"|||||"+ reviewId )
        if(currentUser.toString() == postUserId && reviewId == "0")
        {
            add_detail_review_btn.visibility = View.VISIBLE
        }
        else
        {
            add_detail_review_btn.visibility = View.GONE
        }

        add_detail_review_btn.setOnClickListener {
            val intentAddReview = Intent(this, AddReviewActivity::class.java)
            intentAddReview.putExtra("postId",postId.toString())
            intentAddReview.putExtra("userId",currentUser.toString())
            intentAddReview.putExtra("postTitle",subject)
            intentAddReview.putExtra("thumbNailImageFilePath",thumbnailImageFilePath)
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
                intentReview.putExtra("postTitle",subject)
                intentReview.putExtra("thumbNailImageFilePath",thumbnailImageFilePath)
                startActivity(intentReview)
            }
        }
        val swipe = object: CommentsAdapterRetro.SwipeHelper(this, post_detail_recycler_view_comments, 200) {
            override fun instantiateDeleteButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<DeleteButton>
            ) {
                buffer.add(
                    DeleteButton(this@PostDetailActivity, "Delete", 30, 0,
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
                                        this@PostDetailActivity,
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
        menuInflater.inflate(R.menu.appbar_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete -> {
                Log.d("retrofit", "post 삭제버튼 동작 = ")
                RetrofitConnection.server.deletePost(postId = postId, userId = currentUser).enqueue(object : Callback<Long>{
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "post 삭제 : post_id = " + response.body())
                        Toast.makeText(this@PostDetailActivity,"게시글이 삭제되었습니다.", Toast.LENGTH_LONG)
//                        val intent = Intent(this@PostDetailActivity, MainActivity::class.java)
//                        intent.putExtra("ProfileFragment","ProfileFragment")
//                        startActivity(intent)
//                        finish()

                    }
                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", "post 삭제 실패 :  " + t.message.toString())
                    }
                })
                return true
            }
            R.id.action_update -> {
                Log.d("retrofit", "post 수정버튼 동작 = ")
                val updatePostIntent = Intent(this, AddPostActivity::class.java)
                updatePostIntent.putExtra("postId",postId)
                updatePostIntent.putExtra("userId", postUserId)
                updatePostIntent.putExtra("flag",true)
                startActivity(updatePostIntent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()

        RetrofitConnection.server.getOnePost(userId = currentUser, postId = postId ,search = "", category = "", page = 0).enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                Log.d("돌아와1", response.body().toString())

                val mPost : MutableList<PostDTO> = ArrayList()
                mPost.add(response.body()!!.content[0])
                if(PreferenceManager.getLong(applicationContext,"userId") == mPost[0].userId && mPost[0].reviewId.toString() == "0")
                {
                    add_detail_review_btn.visibility = View.VISIBLE
                }
                else
                {
                    add_detail_review_btn.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                Log.d("moveToPost", t.message.toString())
            }
        })
    }
}