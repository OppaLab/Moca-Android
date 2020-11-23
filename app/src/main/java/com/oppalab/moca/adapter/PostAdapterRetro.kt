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
import com.oppalab.moca.CommentsActivity
import com.oppalab.moca.CommentsActivityRetro
//import com.oppalab.moca.PostDetailActivity
import com.oppalab.moca.R
import com.oppalab.moca.dto.FeedsAtHome
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostAdapterRetro
    (
    private val mContext: Context,
    private val mPost: List<FeedsAtHome>
) : RecyclerView.Adapter<PostAdapterRetro.ViewHolder>() {

    private var currentUser = PreferenceManager.getLong(mContext, "userId")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = mPost[position]

        Picasso.get()
            .load(RetrofitConnection.URL + "/image/thumbnail/" + post.thumbnailImageFilePath)
            .into(holder.thumbnail)

        //제목
        if (post.postTitle.equals("")) {
            holder.title.visibility = View.GONE
        } else {
            holder.title.visibility = View.VISIBLE
            holder.title.setText(post.postTitle)
        }

        //내용
        if (post.postBody.equals("")) {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.postBody)
        }

        Picasso.get().load(RetrofitConnection.URL + "image/profile/" + post.profileImageFilePath)
            .placeholder(R.drawable.profile)
            .into(holder.profileImage)
        holder.nickName.text = post.nickname

        if (post.like) {
            holder.likeButton.setImageResource(R.drawable.heart_clicked)
            holder.likeButton.tag = "Liked"
        } else {
            holder.likeButton.setImageResource(R.drawable.heart_not_clicked)
            holder.likeButton.tag = "Like"
        }

        if (post.likeCount.toInt() == 0) {
            holder.likes.text = ""
        } else {
            holder.likes.text = post.likeCount.toString() + "명이 공감"
        }

        if (post.commentCount == 0L) {
            holder.comments.text = ""
        } else {
            holder.comments.text = "view all " + post.commentCount + " comments"
        }

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like") {
                Log.d("Likes PostId", post.postId.toString())

                RetrofitConnection.server.likePost(
                    postId = post.postId,
                    userId = currentUser,
                    reviewId = ""
                ).enqueue(object : Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "Like 생성 : like_id = " + response.body())
                        holder.likeButton.setImageResource(R.drawable.heart_clicked)
                        holder.likeButton.tag = "Liked"

                        holder.likes.text = (post.likeCount + 1L).toString() + "명이 공감"
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            } else {
                RetrofitConnection.server.unlikePost(
                    postId = post.postId,
                    userId = currentUser,
                    reviewId = ""
                ).enqueue(object : Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "Like 삭제 : like_id = " + response.body())
                        holder.likeButton.setImageResource(R.drawable.heart_not_clicked)
                        holder.likeButton.tag = "Like"

                        if (post.likeCount - 1L == 0L) {
                            holder.likes.text = ""
                        } else {
                            holder.likes.text = (post.likeCount).toString() + "명이 공감"
                            if (post.likeCount == 0L) {
                                holder.likes.text = ""
                            }
                        }
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            }
        }

        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivityRetro::class.java)
            intentComment.putExtra("postId", post.postId.toString())
            intentComment.putExtra("thumbnailImageFilePath", post.thumbnailImageFilePath)
            intentComment.putExtra("publisherId", post.nickname)

            mContext.startActivity(intentComment)
        }

        holder.comments.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivityRetro::class.java)
            intentComment.putExtra("postId", post.postId.toString())
            intentComment.putExtra("thumbnailImageFilePath", post.thumbnailImageFilePath)
            intentComment.putExtra("publisherId", post.nickname)

            mContext.startActivity(intentComment)
        }

//        holder.thumbnail.setOnClickListener {
//            val intentPostDetail = Intent(mContext, PostDetailActivity::class.java)
//            intentPostDetail.putExtra("publisherId", post.nickname)
//            intentPostDetail.putExtra("thumbnailImageFilePath", post.thumbnailImageFilePath)
//            intentPostDetail.putExtra("content", post.postBody)
//            intentPostDetail.putExtra("likeCount", post.likeCount)
//            intentPostDetail.putExtra("like", post.like)
//            intentPostDetail.putExtra("postId", post.postId.toString())
//            intentPostDetail.putExtra("subject", post.postTitle)
//
//            mContext.startActivity(intentPostDetail)
//        }

    }

    override fun getItemCount(): Int {
        return mPost.size
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView
        var thumbnail: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var nickName: TextView
        var likes: TextView
        var title: TextView
        var comments: TextView
        var description: TextView
        var publisher: TextView

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
}
