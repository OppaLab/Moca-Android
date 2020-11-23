package com.oppalab.moca.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R
import com.oppalab.moca.dto.MyPostDTO
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MyThumbnailAdapter(private val mContext: Context, mPost: List<MyPostDTO>):
    RecyclerView.Adapter<MyThumbnailAdapter.ViewHolder>() {

    private var mPost: List<MyPostDTO>? = null

    init {
        this.mPost = mPost
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.thumbnail_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: MyPostDTO = mPost!![position]
        Picasso.get().load(RetrofitConnection.URL+"/image/thumbnail/"+post.thumbnailImageFilePath).into(holder.postThumbnail)

    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {
        var postThumbnail: ImageView
//        var profileThumbnail: CircleImageView

        init {
            postThumbnail = itemView.findViewById(R.id.post_thumbnail)
//            profileThumbnail = itemView.findViewById(R.id.profile_profile_image)
        }
    }
}