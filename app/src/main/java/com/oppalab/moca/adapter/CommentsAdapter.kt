package com.oppalab.moca.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.oppalab.moca.R
import com.oppalab.moca.model.Comment
import com.oppalab.moca.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comments_item_layout.view.*

class CommentsAdapter(private val mContext: Context,
                      private val mComment: MutableList<Comment>?

) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        var comment = mComment!![position]
        holder.commentTV.text = comment.getComment()
        getUserInfo(holder.imageProfile, holder.userNameTV, comment.getPublisher())

        val childLayoutManager = LinearLayoutManager(holder.itemView.recycler_view_reply.context, VERTICAL, false)

        holder.itemView.recycler_view_reply.apply {
            layoutManager = childLayoutManager
            setRecycledViewPool(viewPool)
        }
    }

    override fun getItemCount(): Int {
        return mComment!!.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageProfile: CircleImageView
        var userNameTV: TextView
        var commentTV: TextView
        var replyBtn: Button

        init {
            imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
            userNameTV = itemView.findViewById(R.id.nickname_comment)
            commentTV = itemView.findViewById(R.id.comment_comment)
            replyBtn = itemView.findViewById(R.id.comment_reply_button)
        }
    }

    private fun getUserInfo(imageProfile: CircleImageView, userNameTV: TextView, publisher: String) {
        val userRef = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(publisher)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageProfile)
                    userNameTV.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}