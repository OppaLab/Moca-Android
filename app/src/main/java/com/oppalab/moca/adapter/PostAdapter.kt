package com.oppalab.moca.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.oppalab.moca.CommentsActivity
import com.oppalab.moca.MainActivity
import com.oppalab.moca.model.Post
import com.oppalab.moca.model.User
import com.oppalab.moca.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter
    (private val mContext: Context,
     private val mPost: List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>()
{

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.getThumbnail()).into(holder.thumbnail)

        //제목
        if (post.getTitle().equals(""))
        {
            holder.title.visibility = View.GONE
        }
        else
        {
            holder.title.visibility = View.VISIBLE
            holder.title.setText(post.getTitle())
        }

        //내용
        if (post.getContent().equals(""))
        {
            holder.description.visibility = View.GONE
        }
        else
        {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getContent())
        }

        publisherInfo(holder.profileImage, holder.nickName, post.getPublisher())

        isLikes(post.getPostId(), holder.likeButton)

        numberOfLikes(holder.likes, post.getPostId())

        getTotalComments(holder.comments, post.getPostId())

        holder.likeButton.setOnClickListener{
            if (holder.likeButton.tag == "Like")
            {
                Log.d("Likes PostId", post.getPostId())
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostId())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostId())
                    .child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext, MainActivity::class.java)
//                mContext.startActivity(intent)
            }
        }

        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId",post.getPostId())
            intentComment.putExtra("publisherId",post.getPublisher())

            mContext.startActivity(intentComment)
        }

        holder.comments.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId",post.getPostId())
            intentComment.putExtra("publisherId",post.getPublisher())

            mContext.startActivity(intentComment)
        }
    }

    private fun numberOfLikes(likes: TextView, postId: String)
    {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    likes.text = p0.childrenCount.toString() + "명이 공감"
                }
                else {
                    likes.text = ""
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun getTotalComments(comments: TextView, postId: String)
    {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    comments.text = "view all " + p0.childrenCount.toString() + "comments"
                }
                else {
                    comments.text = ""
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


    private fun isLikes(postId: String, likeButton: ImageView)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.child(firebaseUser!!.uid).exists())
                {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "Liked"
                }
                else
                {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


    override fun getItemCount(): Int {
        return mPost.size
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage : CircleImageView
        var thumbnail : ImageView
        var likeButton : ImageView
        var commentButton : ImageView
        var saveButton : ImageView
        var nickName : TextView
        var likes : TextView
        var title : TextView
        var comments: TextView
        var description: TextView
        var publisher : TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_search)
            thumbnail = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_comment_btn)
            nickName = itemView.findViewById(R.id.nickname_post_card)
            title = itemView.findViewById(R.id.title_post_card)
            likes = itemView.findViewById(R.id.likes)
            comments = itemView.findViewById(R.id.comments)
            description = itemView.findViewById(R.id.description)
            publisher = itemView.findViewById(R.id.publisher)
        }
    }


    private fun publisherInfo(profileImage: CircleImageView, nickName: TextView, publisherID: String)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    nickName.text = user!!.getUsername()

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}