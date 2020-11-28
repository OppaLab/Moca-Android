package com.oppalab.moca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.adapter.OtherUserAdapter
import com.oppalab.moca.dto.GetMyPostDTO
import com.oppalab.moca.dto.GetProfileDTO
import com.oppalab.moca.dto.PostDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_other_user.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OtherUserActivity : AppCompatActivity() {


    private var publisherProfileId: String = ""
    private var currentUser: Long = 0
    private var otherUserProfile: GetProfileDTO? = null
    private var postList: MutableList<PostDTO>? = null
    private var otherUserAdapter: OtherUserAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user)

        val intent = intent
        publisherProfileId = intent.getStringExtra("publisherId")!!
        currentUser = PreferenceManager.getLong(this, "userId")



        Toast.makeText(this, "OtherUserActivity", Toast.LENGTH_LONG)
            .show()

//        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
//        if (pref != null) {
//            this.profileId = pref.getString("profileId", "none").toString()
//        }

        val recyclerview_other_user_profile: RecyclerView
        recyclerview_other_user_profile = findViewById(R.id.other_user_recyclerview_grid_view)
        recyclerview_other_user_profile.setHasFixedSize(true)
        postList = ArrayList()
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(this, 3)
        recyclerview_other_user_profile.layoutManager = linearLayoutManager

        otherUserAdapter = OtherUserAdapter(this, postList as ArrayList<PostDTO>)
        recyclerview_other_user_profile.adapter = otherUserAdapter

//        val circleimageview_thumbmail: CircleImageView
//        circleimageview_thumbmail = findViewById(R.id.other_user_profile_profile_image)



        Log.d("check publisher", publisherProfileId)
        RetrofitConnection.server.getProfile(myUserId = currentUser,userId = publisherProfileId.toLong()).enqueue(object :
            Callback<GetProfileDTO> {
            override fun onResponse(call: Call<GetProfileDTO>, response: Response<GetProfileDTO>) {
                Log.d("retrofit", response.body().toString())
                otherUserProfile = response.body()
//                Picasso.get().load(RetrofitConnection.URL+"image/profile/"+response.body()!!.profileImageFilePath).into(circleimageview_thumbmail)
                other_user_profile_fragment_username.text = otherUserProfile!!.nickname
                other_user_profile_full_name.text = otherUserProfile!!.nickname
                other_user_total_posts.text = otherUserProfile!!.numberOfPosts.toString()
                other_user_total_followers.text = otherUserProfile!!.numberOfFollowers.toString()
                other_user_total_following.text = otherUserProfile!!.numberOfFollowings.toString()

                Log.d("retrofit isFollowed", otherUserProfile!!.IsFollowed.toString())
                if (otherUserProfile!!.IsFollowed) {
                    other_user_follow_button.text = ("UnFollow")
                    other_user_follow_button.tag = "Followed"
                } else {
                    other_user_follow_button.text = ("Follow")
                    other_user_follow_button.tag = "UnFollow"
                }

            }

            override fun onFailure(call: Call<GetProfileDTO>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
            }

        })

        RetrofitConnection.server.getPosts(userId = publisherProfileId.toLong(), page = 0, category = "", search = "", sort = "").enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                Log.d("retrofit", response.body().toString())
                postList!!.clear()
                for (post in response.body()!!.content) {
                    postList!!.add(post)
                }
                Collections.reverse(postList!!)
                otherUserAdapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
            }

        })




        other_user_follow_button.setOnClickListener {
            Log.d("follow", otherUserProfile!!.IsFollowed.toString())
            if (other_user_follow_button.tag == "UnFollow") {
                Log.d("Follow This User", otherUserProfile!!.nickname)

                RetrofitConnection.server.followUser(userId = currentUser, followedUserId = publisherProfileId.toLong()).enqueue(object: Callback<Long>{
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "Follow 생성 : followed_id = " + response.body())
                        other_user_follow_button.tag = "Followed"
                        other_user_follow_button.text = ("UnFollow")
                        other_user_total_followers.text = ((otherUserProfile!!.numberOfFollowers + 1).toString())
                    }
                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }
                })
            } else {
                RetrofitConnection.server.unfollowUser(userId = currentUser, followedUserId = publisherProfileId.toLong()).enqueue(object : Callback<Long>{
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "Follow 삭제 : unfollowed_id = " + response.body())
                        other_user_follow_button.tag = "UnFollow"
                        other_user_follow_button.text = ("Follow")
                        if (otherUserProfile!!.numberOfFollowers - 1 == 0L){
                            other_user_total_followers.text = "0"
                        } else {
                            other_user_total_followers.text = ((otherUserProfile!!.numberOfFollowers - 1).toString())
                            if (otherUserProfile!!.numberOfFollowers == 0L){
                                other_user_total_followers.text = "0"
                            }
                        }
                    }
                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", t.message.toString())
                    }
                })
            }
        }


    }
}