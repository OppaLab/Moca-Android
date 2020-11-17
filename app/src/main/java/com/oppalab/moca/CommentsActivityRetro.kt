package com.oppalab.moca

import GetCommentsOnPostDTO
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.oppalab.moca.adapter.CommentsAdapterRetro
import com.oppalab.moca.dto.CommentsOnPost
import com.oppalab.moca.model.Comment
import com.oppalab.moca.model.User
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsActivityRetro : AppCompatActivity() {
    private var postId = ""
    private var publisherId = ""
    private var commentId = ""
//    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentsAdapterRetro? = null
    private var commentList: MutableList<CommentsOnPost>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val intent = intent
        postId = intent.getStringExtra("postId")!!
        publisherId = intent.getStringExtra("publisherId")!!
//        commentId = intent.getStringExtra("commentId")!!

//        firebaseUser = FirebaseAuth.getInstance().currentUser

        var comment_recyclerView: RecyclerView
        comment_recyclerView = findViewById(R.id.recycler_view_comments)
        var comment_linearLayoutManager = LinearLayoutManager(this)
//        linearLayoutManager.reverseLayout = true
        comment_recyclerView.layoutManager = comment_linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapterRetro(this, commentList)
        comment_recyclerView.adapter = commentAdapter

        userInfo()
        readComments()
        getPostImage()

//        FirebaseDatabase.getInstance().reference.child("Users").child(user.get)

        post_comment.setOnClickListener ( View.OnClickListener {
            if (add_comment!!.text.toString() == "") {
                Toast.makeText(this@CommentsActivityRetro, "Please write comment first.", Toast.LENGTH_LONG).show()
            }
            else {
                addComment()
            }
        } )

    }

    private fun addComment() {
        var currentUser = PreferenceManager.getLong(applicationContext, "userId")

        RetrofitConnection.server.createComment(
            postId = postId.toLong(),
            reviewId = 0,
            userId = currentUser,
            comment = add_comment!!.text.toString()
            ).enqueue(object : Callback<Long>{
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response?.isSuccessful) {
                    Log.d(
                        "retrofit addcomment userId",
                        PreferenceManager.getLong(applicationContext, "userId").toString()
                    )
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit addcomment fail", t.message.toString())
            }

        })
        add_comment!!.text.clear()

    }


    private fun userInfo() {
//        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
//
//        usersRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val user = snapshot.getValue<User>(User::class.java)
//                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profie_image_comment)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) { }
//        })
        val currentUser = PreferenceManager.getLong(applicationContext, "userId")

        Picasso.get().load(R.drawable.profile).into(profie_image_comment)

    }

    private fun getPostImage() {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postId!!).child("thumbnail")

        postRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val image = snapshot.value.toString()
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(post_image_comment)
                }
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun readComments() {

        RetrofitConnection.server.getCommentOnPost(
            postId = postId.toLong(),
            page = 0
        ).enqueue(object : Callback<GetCommentsOnPostDTO>{
            override fun onResponse(
                call: Call<GetCommentsOnPostDTO>,
                response: Response<GetCommentsOnPostDTO>
            ) {
                Log.d("retrofit Comments", response.body().toString())

                commentList?.clear()

                for (comment in response.body()!!.content) {
                    val curComment = comment
                    commentList!!.add(curComment)
                    commentAdapter!!.notifyDataSetChanged()
                }

                if (response.body()!!.last == true) {
                    Toast.makeText(applicationContext, "마지막 댓글 입니다.", Toast.LENGTH_LONG)

                }

            }
            override fun onFailure(call: Call<GetCommentsOnPostDTO>, t: Throwable) {
                Log.d("retrofit comment fail", t.message.toString())
            }

        })
    }

}