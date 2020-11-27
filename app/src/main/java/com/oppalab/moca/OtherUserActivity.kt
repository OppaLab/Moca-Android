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

    private var publisher_profile_Id: Long = 0
    private var postList: MutableList<PostDTO>? = null
    private var otherUserAdapter: OtherUserAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user)

        val currentUser = PreferenceManager.getLong(applicationContext,"userId")
        val intent = intent
        publisher_profile_Id = intent.getLongExtra("publisherId", 0)


        Toast.makeText(this, "OtherUserActivity", Toast.LENGTH_LONG)
            .show()

//        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
//        if (pref != null) {
//            this.profileId = pref.getString("profileId", "none").toString()
//        }

        var recyclerview_mypost_thumbnail: RecyclerView
        recyclerview_mypost_thumbnail = findViewById(R.id.other_user_recyclerview_grid_view)
        recyclerview_mypost_thumbnail.setHasFixedSize(true)
        postList = ArrayList()
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(this, 3)
        recyclerview_mypost_thumbnail.layoutManager = linearLayoutManager

        otherUserAdapter = this?.let { OtherUserAdapter(it, postList as ArrayList<PostDTO>) }
        recyclerview_mypost_thumbnail.adapter = otherUserAdapter

        var circleimageview_thumbmail: CircleImageView
        circleimageview_thumbmail = findViewById(R.id.other_user_profile_profile_image)


        RetrofitConnection.server.getProfile(myUserId = currentUser, userId = publisher_profile_Id).enqueue(object :
            Callback<GetProfileDTO> {
            override fun onResponse(call: Call<GetProfileDTO>, response: Response<GetProfileDTO>) {
                Log.d("retrofit", response.body().toString())
                val profile = response.body()
//                Picasso.get().load(RetrofitConnection.URL+"image/profile/"+response.body()!!.profileImageFilePath).into(circleimageview_thumbmail)
                other_user_profile_fragment_username.text = profile!!.nickname
                other_user_profile_full_name.text = profile!!.nickname
                other_user_total_posts.text = profile!!.numberOfPosts.toString()
                other_user_total_followers.text = profile!!.numberOfFollowers.toString()
                other_user_total_following.text = profile!!.numberOfFollowings.toString()
            }

            override fun onFailure(call: Call<GetProfileDTO>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
            }

        })

        RetrofitConnection.server.getPosts(userId = publisher_profile_Id, page = 0, category = "", search = "").enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                Log.d("retrofit", response.body().toString())
                postList!!.clear()
                for (post in response.body()!!.content) {
                    postList!!.add(post)
                }
                Collections.reverse(postList)
                otherUserAdapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
            }

        })

    }
}