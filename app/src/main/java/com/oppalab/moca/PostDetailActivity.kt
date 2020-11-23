//package com.oppalab.moca
//
//import GetCommentsOnPostDTO
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.oppalab.moca.adapter.CommentsAdapterRetro
//import com.oppalab.moca.dto.CommentsOnPost
//import com.oppalab.moca.util.PreferenceManager
//import com.oppalab.moca.util.RetrofitConnection
//import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.activity_post_detail.*
//import kotlinx.android.synthetic.main.thumbnail_item_layout.*
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class PostDetailActivity : AppCompatActivity() {
//    private var postId = 0L
//    private var publisherId = ""
//    private var content = ""
//    private var likeCount = 0L
//    private var like = false
//    private var thumbnailImageFilePath = ""
//    private var subject = ""
//    private var commentId = ""
//    private var commentAdapter: CommentsAdapterRetro? = null
//    private var commentList: MutableList<CommentsOnPost>? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_post_detail)
//
//        val currentUser = PreferenceManager.getLong(applicationContext, "userId")
//
//        val intent = intent
//        postId = intent.getStringExtra("postId")!!.toLong()
//        publisherId = intent.getStringExtra("publisherId")!!
//        subject = intent.getStringExtra("subject")!!
//        thumbnailImageFilePath = intent.getStringExtra("thumbnailImageFilePath")!!
//        if (intent.getStringExtra("like") == "true") like = true
//        likeCount = intent.getStringExtra("likeCount")!!.toLong()
//        content = intent.getStringExtra("content")!!
//
//        var comment_linearLayoutManager = LinearLayoutManager(this)
//        comment_linearLayoutManager.reverseLayout = true
//        post_detail_recycler_view_comments.layoutManager = comment_linearLayoutManager
//
//        commentList = ArrayList()
//        commentAdapter = CommentsAdapterRetro(this, commentList)
//        post_detail_recycler_view_comments.adapter = commentAdapter
//
//        Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + thumbnailImageFilePath)
//            .into(post_thumbnail_detail)
//
//        post_detail_subject.text = intent.getStringExtra("subject")
//        post_detail_publisher.text = publisherId
//
//        RetrofitConnection.server.getCommentOnPost(postId = postId, reviewId = "", page = 0).enqueue(object: Callback<GetCommentsOnPostDTO> {
//            override fun onResponse(
//                call: Call<GetCommentsOnPostDTO>,
//                response: Response<GetCommentsOnPostDTO>
//            ) {
//                Log.d("retrofit", response.body().toString())
//
//                commentList!!.clear()
//                for (comment in response.body()!!.content) {
//                    val curComment = comment
//                    commentList!!.add(comment!!)
//                }
//
//                commentAdapter!!.notifyDataSetChanged()
//            }
//
//            override fun onFailure(call: Call<GetCommentsOnPostDTO>, t: Throwable) {
//                Log.d("retrofit", t.message.toString())
//            }
//
//        })
//        post_comment.setOnClickListener(View.OnClickListener {
//            if (add_comment!!.text.toString() == "") {
//                Toast.makeText(
//                    this@PostDetailActivity,
//                    "Please write comment first.",
//                    Toast.LENGTH_LONG
//                ).show()
//            } else {
//                RetrofitConnection.server.createComment(
//                    postId = postId.toLong(),
//                    reviewId = "", currentUser,
//                    comment = add_comment.text.toString()
//                ).enqueue(object: Callback<Long> {
//                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
//                        Log.d("retrofit", "댓글 생성")
//                        add_comment!!.text.clear()
//                        finish()
//                        startActivity(intent)
//                    }
//
//                    override fun onFailure(call: Call<Long>, t: Throwable) {
//                        Log.d("retrofit", t.message.toString())
//                    }
//
//                })
//            }
//        })
//
//
//    }
//}