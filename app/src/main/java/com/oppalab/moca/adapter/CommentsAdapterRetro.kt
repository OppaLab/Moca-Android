package com.oppalab.moca.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R
import com.oppalab.moca.dto.CommentsOnPost
import com.oppalab.moca.util.PreferenceManager
import de.hdodenhof.circleimageview.CircleImageView

class CommentsAdapterRetro(private val mContext: Context,
                           private val mComment: MutableList<CommentsOnPost>?

) : RecyclerView.Adapter<CommentsAdapterRetro.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    private val currentUser = PreferenceManager.getLong(mContext, "userId")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapterRetro.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(holder: CommentsAdapterRetro.ViewHolder, position: Int) {

        var comment = mComment!![position]
        holder.commentTV.text = comment.comment
        holder.userNameTV.text = comment.nickname
        holder.imageProfile.setImageResource(R.drawable.profile)
    }

    override fun getItemCount(): Int {
        return mComment!!.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageProfile: CircleImageView
        var userNameTV: TextView
        var commentTV: TextView

        init {
            imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
            userNameTV = itemView.findViewById(R.id.nickname_comment)
            commentTV = itemView.findViewById(R.id.comment_comment)
        }
    }
}