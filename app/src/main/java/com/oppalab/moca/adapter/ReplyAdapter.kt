package com.oppalab.moca.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.oppalab.moca.R
import com.oppalab.moca.model.Reply
import com.oppalab.moca.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ReplyAdapter(private val mContext: Context,
                   private val mReply: MutableList<Reply>?

) : RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.reply_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (mReply == null)
            return 0;
        else
            return mReply.size
    }

    override fun onBindViewHolder(holder: ReplyAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        var reply = mReply!![position]
        holder.commentTV.text = reply.getComment()
        getUserInfo(holder.imageProfile, holder.userNameTV, reply.getPublisher())
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageProfile: CircleImageView
        var userNameTV: TextView
        var commentTV: TextView

        init {
            imageProfile = itemView.findViewById(R.id.user_profile_image_reply)
            userNameTV = itemView.findViewById(R.id.nickname_reply)
            commentTV = itemView.findViewById(R.id.comment_reply)
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