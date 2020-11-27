package com.oppalab.moca.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.*
//import com.oppalab.moca.PostDetailActivity
import com.oppalab.moca.dto.FeedsAtHome
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.posts_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostAdapterRetro
    (
    private val mContext: Context,
    private val mPost: List<FeedsAtHome>
) : RecyclerView.Adapter<PostAdapterRetro.ViewHolder>() {

    companion object {
        private const val TYPE_POST = 0
        private const val TYPE_LOADING = 1
    }

    private var currentUser = PreferenceManager.getLong(mContext, "userId")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = mPost[position]
        var curLikeCount = post.likeCount
        var curCommentCount = post.commentCount

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

        if (curLikeCount.toInt() == 0) {
            holder.likes.text = ""
        } else {
            holder.likes.text = curLikeCount.toString() + "명이 공감"
        }

        if (curCommentCount == 0L) {
            holder.comments.text = ""
        } else {
            holder.comments.text = "view all " + curCommentCount + " comments"
        }

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like") {
                Log.d("Likes PostId", post.postId.toString())

                RetrofitConnection.server.likePost(
                    postId = post.postId.toString(),
                    userId = currentUser,
                    reviewId = ""
                ).enqueue(object : Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        curLikeCount++

                        Log.d("retrofit", "Like 생성 : like_id = " + response.body())
                        holder.likeButton.setImageResource(R.drawable.heart_clicked)
                        holder.likeButton.tag = "Liked"

                        holder.likes.text = (curLikeCount).toString() + "명이 공감"
                    }

                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }

                })
            } else {
                RetrofitConnection.server.unlikePost(
                    postId = post.postId.toString(),
                    userId = currentUser,
                    reviewId = ""
                ).enqueue(object : Callback<Long> {
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "Like 삭제 : like_id = " + response.body())
                        holder.likeButton.setImageResource(R.drawable.heart_not_clicked)
                        holder.likeButton.tag = "Like"

                        curLikeCount += -1

                        if (curLikeCount == 0L) {
                            holder.likes.text = ""
                        } else {
                            holder.likes.text = (curLikeCount).toString() + "명이 공감"
                            if (curLikeCount == 0L) {
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

        holder.nickName.setOnClickListener{
            val intentUserProfile = Intent(mContext, OtherUserActivity::class.java)
            intentUserProfile.putExtra("publisherId", post.userId)
            Log.d("publisherId",post.userId.toString())
            mContext.startActivity(intentUserProfile)
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

        holder.thumbnail.setOnClickListener {
            val intentPostDetail = Intent(mContext, PostDetailActivity::class.java)
            intentPostDetail.putExtra("publisherId", post.nickname)
            intentPostDetail.putExtra("thumbnailImageFilePath", post.thumbnailImageFilePath)
            intentPostDetail.putExtra("content", post.postBody)
            if (holder.likeButton.tag == "Liked") {
                intentPostDetail.putExtra("likeCount", (curLikeCount).toString())
            } else {
                intentPostDetail.putExtra("likeCount", curLikeCount.toString())
            }

            intentPostDetail.putExtra("commentCount", post.commentCount.toString())
            intentPostDetail.putExtra("likeTag", holder.likeButton.tag.toString())
            intentPostDetail.putExtra("like", if (holder.likeButton.tag == "Liked") true else false)
            intentPostDetail.putExtra("postId", post.postId.toString())
            intentPostDetail.putExtra("subject", post.postTitle)
            intentPostDetail.putExtra("postUserId",post.userId.toString())
            intentPostDetail.putExtra("reviewId",post.reviewId.toString())

            mContext.startActivity(intentPostDetail)
        }

        holder.review.setOnClickListener {
            if(post.reviewId == 0L)
            {
                Toast.makeText(mContext, "후기가 아직 없습니다.", Toast.LENGTH_LONG).show()
            } else {
                val intentReview = Intent(mContext, ReviewActivity::class.java)
                intentReview.putExtra("currentUser", currentUser.toString())
                intentReview.putExtra("userId", post.userId.toString())
                intentReview.putExtra("reviewId", post.reviewId.toString())
                intentReview.putExtra("postId",post.postId.toString())
                mContext.startActivity(intentReview)
            }
        }

    }

    override fun getItemCount(): Int {
        return mPost.size
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView
        var thumbnail: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var review: ImageView
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
            review = itemView.findViewById(R.id.post_review_btn)
            nickName = itemView.findViewById(R.id.nickname_post_card)
            title = itemView.findViewById(R.id.title_post_card)
            likes = itemView.findViewById(R.id.likes)
            comments = itemView.findViewById(R.id.comments)
            description = itemView.findViewById(R.id.description)
            publisher = itemView.findViewById(R.id.publisher)
        }
    }
}
