package com.oppalab.moca

import GetCommentsOnPostDTO
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
import kotlinx.android.synthetic.main.activity_comments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsActivityRetro : AppCompatActivity() {

    private var postId = 0L
    private var publisherId = ""
    private var thumbnailImageFilePath = ""
    private var commentId = ""
    private var commentAdapter: CommentsAdapterRetro? = null
    private var commentList: MutableList<CommentsOnPost>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val currentUser = PreferenceManager.getLong(applicationContext, "userId")

        val intent = intent
        postId = intent.getStringExtra("postId")!!.toLong()
        publisherId = intent.getStringExtra("publisherId")!!
        thumbnailImageFilePath = intent.getStringExtra("thumbnailImageFilePath")!!
//        commentId = intent.getStringExtra("commentId")!!


        var comment_recyclerView: RecyclerView
        comment_recyclerView = findViewById(R.id.recycler_view_comments)
        var comment_linearLayoutManager = LinearLayoutManager(this)
        comment_linearLayoutManager.reverseLayout = true
        comment_recyclerView.layoutManager = comment_linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapterRetro(this, commentList)
        comment_recyclerView.adapter = commentAdapter

        profie_image_comment.setImageResource(R.drawable.profile)
        Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + thumbnailImageFilePath)
            .into(post_image_comment)

        RetrofitConnection.server.getCommentOnPost(postId = postId.toString(), reviewId = "", page = 0)
            .enqueue(object : Callback<GetCommentsOnPostDTO> {
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
                }

                override fun onFailure(call: Call<GetCommentsOnPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })


//        FirebaseDatabase.getInstance().reference.child("Users").child(user.get)

        post_comment.setOnClickListener(View.OnClickListener {
            if (add_comment!!.text.toString() == "") {
                Toast.makeText(
                    this@CommentsActivityRetro,
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
                        add_comment!!.text.clear()

//                        layoutInflater.inflate(R.layout.fragment_home, container, false).performClick()

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