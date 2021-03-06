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
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.*
import com.oppalab.moca.activity.OtherUserActivity
import com.oppalab.moca.activity.PostDetailActivity
import com.oppalab.moca.activity.ReviewActivity
//import com.oppalab.moca.activity.PostDetailActivity
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

        val createdAt = post.createdAt
        if (createdAt <= 60){
            holder.createdAt.text = createdAt.toString() + "초 전에 작성된 글입니다."
        } else if (createdAt <= 60*60){
            holder.createdAt.text = (createdAt/60).toString() + "분 전에 작성된 글입니다."
        } else if (createdAt <= 60*60*24){
            holder.createdAt.text = (createdAt/(60*60)).toString() + "시간 전에 작성된 글입니다."
        } else if (createdAt <= 60*60*24*7){
            holder.createdAt.text = (createdAt/(60*60*7)).toString() + "일 전에 작성된 글입니다."
        } else if (createdAt <= 60*60*24*7*7){
            holder.createdAt.text = (createdAt/(60*60*7*7)).toString() + "주 전에 작성된 글입니다."
        }

        var post_categories = ""
        for (category in post.categories) {
            if (category == "") continue
            post_categories += category
            post_categories += ", "
        }
        holder.category.text = post_categories.substring(0, post_categories.length - 2)

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
            intentUserProfile.putExtra("publisherId", post.userId.toString())
            Log.d("publisherId",post.userId.toString())
            mContext.startActivity(intentUserProfile)
        }


        holder.thumbnail.setOnClickListener {
            val intentPostDetail = Intent(mContext, PostDetailActivity::class.java)
//            intentPostDetail.putExtra("publisherId", post.nickname)
//            intentPostDetail.putExtra("thumbnailImageFilePath", post.thumbnailImageFilePath)
//            intentPostDetail.putExtra("content", post.postBody)
//            if (holder.likeButton.tag == "Liked") {
//                intentPostDetail.putExtra("likeCount", (curLikeCount).toString())
//            } else {
//                intentPostDetail.putExtra("likeCount", curLikeCount.toString())
//            }

//            intentPostDetail.putExtra("commentCount", post.commentCount.toString())
//            intentPostDetail.putExtra("likeTag", holder.likeButton.tag.toString())
//            intentPostDetail.putExtra("like", if (holder.likeButton.tag == "Liked") true else false)
            intentPostDetail.putExtra("postId", post.postId.toString())
//            intentPostDetail.putExtra("subject", post.postTitle)
//            intentPostDetail.putExtra("postUserId",post.userId.toString())
//            intentPostDetail.putExtra("reviewId",post.reviewId.toString())
//            intentPostDetail.putExtra("createdAt", post.createdAt.toString())
//            var categoryString = ""
//            for (category in post.categories) {
//                if (category == "") continue
//                categoryString += category
//                categoryString += ", "
//            }
//            intentPostDetail.putExtra("categories",categoryString.substring(0, categoryString.length-2))

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
                intentReview.putExtra("postTitle", post.postTitle)
                intentReview.putExtra("thumbNailImageFilePath", post.thumbnailImageFilePath)
                intentReview.putExtra("likeTag", "")
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
        var review: ImageView
        var nickName: TextView
        var likes: TextView
        var title: TextView
        var description:TextView
        var publisher: TextView
        var category: TextView
        var createdAt : TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_search)
            thumbnail = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            review = itemView.findViewById(R.id.post_review_btn)
            nickName = itemView.findViewById(R.id.nickname_post_card)
            title = itemView.findViewById(R.id.title_post_card)
            likes = itemView.findViewById(R.id.likes)
            description = itemView.findViewById(R.id.description)
            publisher = itemView.findViewById(R.id.publisher)
            category = itemView.findViewById(R.id.category_post_card)
            createdAt = itemView.findViewById(R.id.time_post_card)
        }
    }
}
