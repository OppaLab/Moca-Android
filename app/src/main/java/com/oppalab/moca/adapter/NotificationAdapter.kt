package com.oppalab.moca.adapter

import android.content.Context
import android.content.Intent
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.OtherUserActivity
import com.oppalab.moca.PostDetailActivity
import com.oppalab.moca.R
import com.oppalab.moca.ReviewActivity
import com.oppalab.moca.dto.GetMyPostDTO
import com.oppalab.moca.dto.GetReviewDTO
import com.oppalab.moca.dto.NotificationsDTO
import com.oppalab.moca.dto.PostDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_review.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter (
    private val mContext: Context,
    private val mNotification: List<NotificationsDTO>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{

    private var currentUser = PreferenceManager.getLong(mContext,"userId")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var notification = mNotification[position]

        //썸네일을 넣을것인지 않넣을것인지 안정해서 일단 없앰
        holder.postThumbnail.visibility= View.GONE
        holder.profileImage.setImageResource(R.drawable.profile)
        if(notification.activity == "post")
        {
            holder.username.text= notification.nickname
            holder.text.text="새로운 고민글을 작성했어요."

            holder.text.setOnClickListener {
                moveToPost(notification.userId, notification.postId)
            }
        }
        else if(notification.activity == "review")
        {
            holder.username.text= notification.nickname
            holder.text.text="관심있었던 고민글에 후기글이 등록됬어요."
            holder.text.setOnClickListener {
                moveToReview(notification.userId.toString(), notification.reviewId.toString(), notification.postId)
            }
        }
        else if(notification.activity == "like")
        {
            holder.username.text= notification.nickname
            if(notification.reviewId == 0L)
            {
                holder.text.text="당신 고민글에 공감했어요."
                holder.text.setOnClickListener {

                    moveToPost(currentUser, notification.postId)
                }
            }
            else
            {
                holder.text.text="당신 후기에 공감했어요."
                holder.text.setOnClickListener {
                    moveToReview(currentUser.toString(), notification.reviewId.toString(), notification.postId)
                }
            }
        }
        else if(notification.activity == "follow")
        {
            holder.username.text= notification.nickname
            holder.text.text="당신을 팔로우합니다."

            holder.text.setOnClickListener{
                moveToProfile(notification.userId)
            }
        }
        else if(notification.activity == "comment")
        {
            holder.username.text= notification.nickname
            if(notification.reviewId == 0L)
            {
                Log.d("notificationcomment", currentUser.toString()+notification.postId.toString())
                holder.text.text="당신 고민글에 댓글이 등록됬어요."
                holder.text.setOnClickListener {
                    moveToPost(currentUser, notification.postId)
                }
            }
            else
            {
                holder.text.text="당신 후기에 댓글이 등록됬어요."
                holder.text.setOnClickListener {
                    moveToReview(currentUser.toString(), notification.reviewId.toString(), notification.postId)
                }
            }
        }
        else
        {
            holder.username.text= notification.nickname
            holder.text.text="어떤 유저가 고민을 푸시했습니다."
            /*
            * holder.text.setOnClickListener {
                moveToPost(notification.userId, notification.postId)
            }
            * 고민글 푸시기능 개발후 주석해제
            * */
        }

    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var postThumbnail: ImageView
        var profileImage: CircleImageView
        var username: TextView
        var text: TextView

        init{
            postThumbnail = itemView.findViewById(R.id.notification_post_thumnail)
            profileImage = itemView.findViewById(R.id.notification_profile_image)
            username = itemView.findViewById(R.id.username_notification)
            text = itemView.findViewById(R.id.content_notification)
        }
    }

    private fun moveToPost(userId: Long, postId : Long)
    {
        RetrofitConnection.server.getOnePost(userId = userId, postId = postId ,search = "", category = "", page = 0).enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                Log.d("moveToPost", response.body().toString())
                val intentPostDetail = Intent(mContext, PostDetailActivity::class.java)

                var mPost : MutableList<PostDTO> = ArrayList()

                mPost.add(response.body()!!.content[0])

                intentPostDetail.putExtra("publisherId", mPost[0].nickname)
                intentPostDetail.putExtra("thumbnailImageFilePath", mPost[0].thumbnailImageFilePath)
                intentPostDetail.putExtra("content", mPost[0].postBody)
                intentPostDetail.putExtra("likeCount", mPost[0].likeCount.toString())
                intentPostDetail.putExtra("commentCount", mPost[0].commentCount.toString())
                intentPostDetail.putExtra("like", mPost[0].like)
                intentPostDetail.putExtra("postId", mPost[0].postId.toString())
                intentPostDetail.putExtra("subject", mPost[0].postTitle)
                intentPostDetail.putExtra("postUserId",mPost[0].userId.toString())
                intentPostDetail.putExtra("reviewId",mPost[0].reviewId.toString())

                mContext.startActivity(intentPostDetail)
            }

            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                Log.d("moveToPost", t.message.toString())
            }
        })
    }

    private fun moveToReview(userId: String, reviewId: String, postId: Long)
    {
        val intentReview = Intent(mContext, ReviewActivity::class.java)
        intentReview.putExtra("userId",userId)
        intentReview.putExtra("postId",postId)
        intentReview.putExtra("reviewId",reviewId)
        mContext.startActivity(intentReview)
    }

    private fun moveToProfile(userId: Long) {
        val intentProfile = Intent(mContext, OtherUserActivity::class.java)
        intentProfile.putExtra("publisherId",userId)
        mContext.startActivity(intentProfile)
    }
}