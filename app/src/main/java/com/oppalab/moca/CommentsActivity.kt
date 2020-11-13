package com.oppalab.moca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.oppalab.moca.adapter.CommentsAdapter
import com.oppalab.moca.adapter.ReplyAdapter
import com.oppalab.moca.model.Comment
import com.oppalab.moca.model.Reply
import com.oppalab.moca.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.comments_item_layout.*

class CommentsActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var commentId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentsAdapter? = null
    private var commentList: MutableList<Comment>? = null
    private var replyAdapter: ReplyAdapter? = null
    private var replyList: MutableList<Reply>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId")!!
        publisherId = intent.getStringExtra("publisherId")!!
//        commentId = intent.getStringExtra("commentId")!!

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var comment_recyclerView: RecyclerView
        comment_recyclerView = findViewById(R.id.recycler_view_comments)
        var comment_linearLayoutManager = LinearLayoutManager(this)
//        linearLayoutManager.reverseLayout = true
        comment_recyclerView.layoutManager = comment_linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapter(this, commentList)
        comment_recyclerView.adapter = commentAdapter

//        var reply_recyclerView: RecyclerView
//        reply_recyclerView = findViewById(R.id.recycler_view_reply)
//        var reply_linearLayoutManager = LinearLayoutManager(this)
//        reply_recyclerView.layoutManager = reply_linearLayoutManager
//
//        replyList = ArrayList()
//        replyAdapter = ReplyAdapter(this, replyList)
//        reply_recyclerView.adapter = replyAdapter

        userInfo()
        readComments()
        getPostImage()
        readReply()

//        FirebaseDatabase.getInstance().reference.child("Users").child(user.get)

        post_comment.setOnClickListener ( View.OnClickListener {
           if (add_comment!!.text.toString() == "") {
               Toast.makeText(this@CommentsActivity, "Please write comment first.", Toast.LENGTH_LONG).show()
           }
           else {
               addComment()
//               add_comment!!.setText("@" + firebaseUser!!.uid)
           }
        } )


//        comment_reply_button.setOnClickListener( View.OnClickListener {
//            add_comment!!.setText("@" + publisherId)
//        })

    }

    private fun addComment() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId!!)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment!!.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        add_comment!!.text.clear()

    }

    private fun addReply() {
        val replyRef = FirebaseDatabase.getInstance().reference
            .child("Reply")
            .child(commentId!!)

        val replyMap = HashMap<String, Any>()
        replyMap["comment"] = add_comment!!.text.toString()
        replyMap["publisher"] = firebaseUser!!

        replyRef.push().setValue(replyMap)

        add_comment!!.text.clear()


    }



    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profie_image_comment)
                }
            }

            override fun onCancelled(error: DatabaseError) { }
        })
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
        val commentsRef = FirebaseDatabase.getInstance()
            .reference.child("Comments")
            .child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    commentList!!.clear()

                    for (snapshot in snapshot.children){
                        val comment = snapshot.getValue(Comment::class.java)
                        commentList!!.add(comment!!)
                    }

                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun readReply() {
        val replyRef = FirebaseDatabase.getInstance()
            .reference.child("Reply")
            .child(postId)

        replyRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    commentList!!.clear()

                    for (snapshot in snapshot.children){
                        val comment = snapshot.getValue(Comment::class.java)
                        commentList!!.add(comment!!)
                    }

                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}