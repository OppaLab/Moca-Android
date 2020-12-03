package com.oppalab.moca.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.PostDetailActivity
import com.oppalab.moca.R
import com.oppalab.moca.dto.PostDTO
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso

class OtherUserAdapter(private val mContext: Context, mPost: List<PostDTO>):
    RecyclerView.Adapter<OtherUserAdapter.ViewHolder>() {

    private var mPost: List<PostDTO>? = null

    init {
        this.mPost = mPost
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.thumbnail_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: PostDTO = mPost!![position]
        Picasso.get().load(RetrofitConnection.URL+"/image/thumbnail/"+post.thumbnailImageFilePath).into(holder.postThumbnail)

        holder.postThumbnail.setOnClickListener {
            val intentPostDetail = Intent(mContext, PostDetailActivity::class.java)
            intentPostDetail.putExtra("publisherId", post.nickname)
            intentPostDetail.putExtra("thumbnailImageFilePath", post.thumbnailImageFilePath)
            intentPostDetail.putExtra("content", post.postBody)
            intentPostDetail.putExtra("likeCount", post.likeCount.toString())
            intentPostDetail.putExtra("commentCount", post.commentCount.toString())
            intentPostDetail.putExtra("like", post.like)
            intentPostDetail.putExtra("postId", post.postId.toString())
            intentPostDetail.putExtra("subject", post.postTitle)
            intentPostDetail.putExtra("postUserId", post.userId.toString())
            intentPostDetail.putExtra("reviewId",post.reviewId.toString())
            intentPostDetail.putExtra("likeTag", if (post.like) "Liked" else "Like")
            intentPostDetail.putExtra("categories",post.categories.toString())
            intentPostDetail.putExtra("createdAt",post.createdAt.toString())
            mContext.startActivity(intentPostDetail)
        }
    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }



    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {
        var postThumbnail: ImageView

        init {
            postThumbnail = itemView.findViewById(R.id.post_thumbnail)
        }
    }
}